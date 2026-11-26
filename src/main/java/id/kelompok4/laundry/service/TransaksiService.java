package id.kelompok4.laundry.service;

import id.kelompok4.laundry.dao.TarifDAO;
import id.kelompok4.laundry.dao.TransaksiDAO;
import id.kelompok4.laundry.model.TarifLayanan;
import id.kelompok4.laundry.model.TransaksiLaundry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

/**
 * Service Transaksi
 * Mengelola aturan bisnis pencatatan pesanan laundry baru oleh Petugas/Kasir:
 * - Validasi berat aktual > 0
 * - Validasi minimum tagihan 2.00 Kg
 * - Mengambil snapshot tarif dan durasi aktif saat transaksi dibuat
 * - Menerbitkan nomor transaksi resmi
 */
public class TransaksiService {

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final TarifDAO tarifDAO = new TarifDAO();

    public static final BigDecimal MINIMUM_BERAT_KG = new BigDecimal("2.00");
    private static final BigDecimal BERAT_MAKSIMUM_KG = new BigDecimal("999999.99");

    /**
     * Menghitung total perkiraan biaya di layar GUI kasir sebelum disimpan.
     */
    public BigDecimal hitungBiaya(BigDecimal beratAktual, BigDecimal tarifPerKg) {
        if (beratAktual == null || tarifPerKg == null) return BigDecimal.ZERO;
        BigDecimal beratTagihan = beratAktual.compareTo(MINIMUM_BERAT_KG) < 0 ? MINIMUM_BERAT_KG : beratAktual;
        return beratTagihan.multiply(tarifPerKg).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Membuat transaksi baru di kasir.
     */
    public TransaksiLaundry buatTransaksiBaru(Long idPelanggan, Long idPetugas, Long idJenisLayanan,
                                             Long idPaket, BigDecimal beratAktual, boolean setujuMinimum,
                                             String catatan) throws Exception {
        if (idPelanggan == null) {
            throw new IllegalArgumentException("Pelanggan harus dipilih terlebih dahulu.");
        }
        if (idPetugas == null) {
            throw new IllegalArgumentException("Identitas petugas pencatat tidak valid.");
        }
        if (idJenisLayanan == null || idPaket == null) {
            throw new IllegalArgumentException("Jenis layanan dan paket laundry harus dipilih.");
        }
        if (beratAktual == null || beratAktual.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Berat cucian aktual harus lebih besar dari 0.");
        }
        if (beratAktual.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Berat cucian maksimal menggunakan 2 angka desimal.");
        }
        if (beratAktual.compareTo(BERAT_MAKSIMUM_KG) > 0) {
            throw new IllegalArgumentException("Berat cucian melebihi batas maksimum sistem.");
        }

        // Validasi berat minimum 2 KG
        if (beratAktual.compareTo(MINIMUM_BERAT_KG) < 0 && !setujuMinimum) {
            throw new IllegalArgumentException("Berat cucian di bawah 2 Kg wajib mendapatkan persetujuan biaya minimum.");
        }

        // Ambil snapshot tarif resmi yang aktif saat ini
        TarifLayanan tarif = tarifDAO.cariTarif(idJenisLayanan, idPaket);
        if (tarif == null || !tarif.isStatusAktif()) {
            throw new IllegalArgumentException("Kombinasi layanan dan paket ini belum memiliki tarif aktif.");
        }

        TransaksiLaundry trx = new TransaksiLaundry();
        trx.setNomorTransaksi(transaksiDAO.generateNomorTransaksiBaru());
        trx.setIdPelanggan(idPelanggan);
        trx.setIdPetugasPencatat(idPetugas);
        trx.setIdJenisLayanan(idJenisLayanan);
        trx.setIdPaket(idPaket);
        trx.setBeratAktual(beratAktual);
        trx.setPelangganSetujuMinimum(
            beratAktual.compareTo(MINIMUM_BERAT_KG) < 0 && setujuMinimum
        );
        trx.setTarifPerKgSaatTransaksi(tarif.getHargaPerKg());
        trx.setDurasiJamSaatTransaksi(tarif.getDurasiJam());
        trx.setCatatan(catatan);

        boolean sukses = transaksiDAO.tambahTransaksi(trx);
        if (!sukses) {
            throw new SQLException("Gagal menyimpan transaksi ke database.");
        }

        return trx;
    }

    public List<TransaksiLaundry> ambilTransaksiPelanggan(Long idPelanggan) throws SQLException {
        return transaksiDAO.ambilTransaksiPelanggan(idPelanggan);
    }

    public List<TransaksiLaundry> ambilSemuaTransaksi(String status, String keyword) throws SQLException {
        return transaksiDAO.ambilSemuaTransaksi(status, keyword);
    }

    public List<TransaksiLaundry> ambilAntreanBayar() throws SQLException {
        return transaksiDAO.ambilTransaksiMenungguBayar();
    }

    public List<TransaksiLaundry> ambilCucianBelumDiambil() throws SQLException {
        return transaksiDAO.ambilCucianBelumDiambil();
    }

    public TransaksiLaundry cariBerdasarkanId(Long id) throws SQLException {
        return transaksiDAO.cariBerdasarkanId(id);
    }
}
