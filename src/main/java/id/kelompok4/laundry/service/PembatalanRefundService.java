package id.kelompok4.laundry.service;

import id.kelompok4.laundry.dao.PembatalanDAO;
import id.kelompok4.laundry.dao.PengembalianDanaDAO;
import id.kelompok4.laundry.dao.TransaksiDAO;
import id.kelompok4.laundry.model.PengembalianDana;
import id.kelompok4.laundry.model.PermohonanPembatalan;
import id.kelompok4.laundry.model.TransaksiLaundry;

import java.sql.SQLException;
import java.util.List;

/**
 * Service Pembatalan dan Refund
 * Mengatur alur pembatalan transaksi berbayar dan pencairan refund 100%.
 */
public class PembatalanRefundService {

    private final PembatalanDAO pembatalanDAO = new PembatalanDAO();
    private final PengembalianDanaDAO pengembalianDanaDAO = new PengembalianDanaDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    /**
     * Pelanggan mengajukan pembatalan transaksi yang sudah dibayar.
     * Aturan: Hanya boleh diajukan saat transaksi masih DITERIMA (belum DIPROSES).
     */
    public PermohonanPembatalan ajukanPembatalan(Long idTransaksi, Long idPelanggan,
                                                 Long idPenggunaPemohon, String alasan) throws Exception {
        if (idTransaksi == null) throw new IllegalArgumentException("Pilih transaksi yang ingin dibatalkan.");
        if (alasan == null || alasan.trim().isEmpty()) {
            throw new IllegalArgumentException("Alasan pembatalan wajib diisi.");
        }

        TransaksiLaundry trx = transaksiDAO.cariBerdasarkanId(idTransaksi);
        if (trx == null) throw new IllegalArgumentException("Transaksi tidak ditemukan.");

        if (!"DITERIMA".equalsIgnoreCase(trx.getStatusTransaksi())) {
            throw new IllegalArgumentException("Permohonan pembatalan biasa hanya dapat diajukan sebelum proses pencucian dimulai (status DITERIMA).");
        }

        if (pembatalanDAO.cariBerdasarkanTransaksiId(idTransaksi) != null) {
            throw new IllegalArgumentException("Transaksi ini sudah pernah diajukan pembatalan.");
        }

        PermohonanPembatalan pb = new PermohonanPembatalan();
        pb.setNomorPermohonan(pembatalanDAO.generateNomorPermohonanBaru());
        pb.setIdTransaksi(idTransaksi);
        pb.setIdPelanggan(idPelanggan);
        pb.setIdPenggunaPemohon(idPenggunaPemohon);
        pb.setAlasanPembatalan(alasan.trim());

        boolean ok = pembatalanDAO.ajukanPembatalan(pb);
        if (!ok) throw new SQLException("Gagal mengajukan permohonan pembatalan.");

        return pb;
    }

    /**
     * Admin memutuskan permohonan pembatalan.
     * Jika disetujui, otomatis dibuat catatan pengembalian_dana sebesar 100% total biaya.
     */
    public boolean putuskanPembatalanAdmin(Long idPermohonan, boolean disetujui, Long idAdmin, String catatan) throws Exception {
        PermohonanPembatalan pb = null;
        for (PermohonanPembatalan item : pembatalanDAO.ambilSemuaPermohonan()) {
            if (item.getIdPermohonanPembatalan().equals(idPermohonan)) {
                pb = item;
                break;
            }
        }
        if (pb == null) throw new IllegalArgumentException("Data permohonan tidak ditemukan.");

        String statusBaru = disetujui ? "DISETUJUI" : "DITOLAK";
        boolean ok = pembatalanDAO.putuskanAdmin(idPermohonan, statusBaru, idAdmin, catatan);

        if (ok && disetujui) {
            TransaksiLaundry trx = transaksiDAO.cariBerdasarkanId(pb.getIdTransaksi());
            PengembalianDana refund = new PengembalianDana();
            refund.setNomorPengembalian(pengembalianDanaDAO.generateNomorPengembalianBaru());
            refund.setIdTransaksi(pb.getIdTransaksi());
            refund.setIdPermohonanPembatalan(idPermohonan);
            refund.setJumlahPengembalian(trx.getTotalBiaya());
            refund.setDibuatOlehAdmin(idAdmin);
            refund.setAlasan("Refund penuh atas persetujuan pembatalan pesanan (" + pb.getAlasanPembatalan() + ")");
            refund.setCatatan(catatan);

            pengembalianDanaDAO.buatPengembalianDana(refund);
        }

        return ok;
    }

    /**
     * Petugas kasir menyerahkan uang refund kepada pelanggan dan mencatat metode penyerahan.
     * Trigger database trg_pengembalian_setelah_ubah otomatis membatalkan transaksi.
     */
    public boolean eksekusiBayarRefundKasir(Long idPengembalian, String metode, Long idPetugas) throws SQLException {
        if (metode == null || metode.trim().isEmpty()) {
            throw new IllegalArgumentException("Metode pengembalian dana harus dipilih (TUNAI / TRANSFER / QRIS).");
        }
        return pengembalianDanaDAO.eksekusiBayarRefundKasir(idPengembalian, metode, idPetugas);
    }

    public List<PermohonanPembatalan> ambilSemuaPermohonan() throws SQLException {
        return pembatalanDAO.ambilSemuaPermohonan();
    }

    public List<PermohonanPembatalan> ambilPermohonanPelanggan(Long idPelanggan) throws SQLException {
        return pembatalanDAO.ambilPermohonanPelanggan(idPelanggan);
    }

    public List<PengembalianDana> ambilRefundMenunggu() throws SQLException {
        return pengembalianDanaDAO.ambilRefundMenunggu();
    }

    public List<PengembalianDana> ambilSemuaRefund() throws SQLException {
        return pengembalianDanaDAO.ambilSemuaRefund();
    }
}
