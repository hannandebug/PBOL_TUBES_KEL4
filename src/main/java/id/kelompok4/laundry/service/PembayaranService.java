package id.kelompok4.laundry.service;

import id.kelompok4.laundry.dao.PembayaranDAO;
import id.kelompok4.laundry.dao.TransaksiDAO;
import id.kelompok4.laundry.model.PembayaranTransaksi;
import id.kelompok4.laundry.model.TransaksiLaundry;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Service Pembayaran
 * Menangani aturan verifikasi pembayaran dan approval oleh Petugas/Kasir:
 * - Pembayaran harus lunas 100% (tidak boleh ada pembayaran sebagian)
 * - Nominal bayar harus sama persis dengan total biaya
 * - Menerbitkan nomor struk resmi setelah di-approve
 * - Trigger database otomatis memajukan transaksi menjadi DITERIMA
 */
public class PembayaranService {

    private final PembayaranDAO pembayaranDAO = new PembayaranDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    /**
     * Memproses approval pembayaran setelah kasir memverifikasi fisik uang / mutasi rekening / QRIS.
     */
    public PembayaranTransaksi prosesApprovePembayaran(Long idTransaksi, Long idPetugas, String metode,
                                                       BigDecimal jumlahBayar, String catatan) throws Exception {
        if (idTransaksi == null) {
            throw new IllegalArgumentException("ID transaksi tidak valid.");
        }
        if (idPetugas == null) {
            throw new IllegalArgumentException("Identitas petugas penerima tidak valid.");
        }
        String metodeValid = validasiMetode(metode);

        TransaksiLaundry trx = transaksiDAO.cariBerdasarkanId(idTransaksi);
        if (trx == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }

        if (!"MENUNGGU_PEMBAYARAN".equalsIgnoreCase(trx.getStatusTransaksi())) {
            throw new IllegalArgumentException("Pembayaran hanya dapat diproses untuk transaksi berstatus MENUNGGU_PEMBAYARAN.");
        }

        // Cek apakah sudah pernah dibayar sebelumnya
        if (pembayaranDAO.cariBerdasarkanTransaksiId(idTransaksi) != null) {
            throw new IllegalArgumentException("Transaksi ini sudah memiliki pembayaran yang tercatat.");
        }
        if (pembayaranDAO.adaPercobaanMenungguVerifikasi(idTransaksi)) {
            throw new IllegalArgumentException("Ada percobaan pembayaran yang masih menunggu keputusan. Tolak atau selesaikan percobaan tersebut terlebih dahulu.");
        }

        // Validasi nominal bayar harus pas 100%
        if (jumlahBayar == null || jumlahBayar.compareTo(trx.getTotalBiaya()) != 0) {
            throw new IllegalArgumentException(String.format(
                "Nominal pembayaran tidak sesuai! Harus lunas tepat sebesar Rp %s (tidak boleh kurang atau lebih).",
                trx.getTotalBiaya()
            ));
        }

        PembayaranTransaksi bayar = new PembayaranTransaksi();
        bayar.setIdTransaksi(idTransaksi);
        bayar.setNomorStruk(pembayaranDAO.generateNomorStrukBaru());
        bayar.setJumlahBayar(jumlahBayar);
        bayar.setMetodePembayaran(metodeValid);
        bayar.setDiterimaOleh(idPetugas);
        bayar.setCatatan(catatan);

        boolean sukses = pembayaranDAO.simpanPembayaran(bayar);
        if (!sukses) {
            throw new SQLException("Gagal menyimpan data pembayaran ke database.");
        }

        return bayar;
    }

    public boolean tolakPembayaran(Long idTransaksi, Long idPetugas, String metode, String alasan) throws Exception {
        if (idTransaksi == null) {
            throw new IllegalArgumentException("ID transaksi tidak valid.");
        }
        if (idPetugas == null) {
            throw new IllegalArgumentException("Identitas petugas verifikasi tidak valid.");
        }
        String metodeValid = validasiMetode(metode);
        if (alasan == null || alasan.trim().isEmpty()) {
            throw new IllegalArgumentException("Alasan penolakan pembayaran wajib diisi.");
        }

        TransaksiLaundry trx = transaksiDAO.cariBerdasarkanId(idTransaksi);
        if (trx == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }
        if (!"MENUNGGU_PEMBAYARAN".equalsIgnoreCase(trx.getStatusTransaksi())) {
            throw new IllegalArgumentException("Pembayaran hanya dapat ditolak untuk transaksi yang masih menunggu pembayaran.");
        }
        if (pembayaranDAO.cariBerdasarkanTransaksiId(idTransaksi) != null) {
            throw new IllegalArgumentException("Transaksi ini sudah memiliki pembayaran yang disetujui.");
        }

        PembayaranTransaksi pembayaran = new PembayaranTransaksi();
        pembayaran.setIdTransaksi(idTransaksi);
        pembayaran.setJumlahBayar(trx.getTotalBiaya());
        pembayaran.setMetodePembayaran(metodeValid);
        pembayaran.setDiterimaOleh(idPetugas);
        pembayaran.setCatatan(alasan.trim());
        return pembayaranDAO.tolakPembayaran(pembayaran);
    }

    public PembayaranTransaksi ambilPembayaranByTransaksi(Long idTransaksi) throws SQLException {
        return pembayaranDAO.cariBerdasarkanTransaksiId(idTransaksi);
    }

    public PembayaranTransaksi ambilPembayaranByStruk(String nomorStruk) throws SQLException {
        return pembayaranDAO.cariBerdasarkanNomorStruk(nomorStruk);
    }

    private String validasiMetode(String metode) {
        if (metode == null) {
            throw new IllegalArgumentException("Metode pembayaran harus dipilih (TUNAI, TRANSFER, atau QRIS).");
        }
        String metodeValid = metode.trim().toUpperCase(Locale.ROOT);
        if (!metodeValid.equals("TUNAI") && !metodeValid.equals("TRANSFER") && !metodeValid.equals("QRIS")) {
            throw new IllegalArgumentException("Metode pembayaran harus TUNAI, TRANSFER, atau QRIS.");
        }
        return metodeValid;
    }
}
