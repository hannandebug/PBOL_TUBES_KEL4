package id.kelompok4.laundry.service;

import id.kelompok4.laundry.dao.PengaduanDAO;
import id.kelompok4.laundry.dao.PenyelesaianDAO;
import id.kelompok4.laundry.dao.TransaksiDAO;
import id.kelompok4.laundry.model.Pengaduan;
import id.kelompok4.laundry.model.PenyelesaianPengaduan;
import id.kelompok4.laundry.model.TransaksiLaundry;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Service Pengaduan
 * Mengelola komplain pelanggan (Hilang, Rusak, Tertukar), penugasan penelusuran,
 * penetapan tanggung jawab, dan musyawarah penyelesaian.
 */
public class PengaduanService {

    private final PengaduanDAO pengaduanDAO = new PengaduanDAO();
    private final PenyelesaianDAO penyelesaianDAO = new PenyelesaianDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    /**
     * Pelanggan mengajukan komplain cucian.
     */
    public Pengaduan ajukanPengaduan(Long idTransaksi, Long idPelanggan, Long idPenggunaPelapor,
                                     String jenisPengaduan, String deskripsiPakaian,
                                     int jumlahPakaian, String uraianKejadian) throws Exception {
        if (idTransaksi == null) throw new IllegalArgumentException("Pilih transaksi yang bermasalah.");
        if (jenisPengaduan == null || jenisPengaduan.trim().isEmpty()) {
            throw new IllegalArgumentException("Jenis pengaduan harus dipilih (HILANG, RUSAK, atau TERTUKAR).");
        }
        if (deskripsiPakaian == null || deskripsiPakaian.trim().isEmpty()) {
            throw new IllegalArgumentException("Deskripsi pakaian wajib diisi secara jelas.");
        }
        if (jumlahPakaian <= 0) {
            throw new IllegalArgumentException("Jumlah pakaian minimal 1.");
        }
        if (uraianKejadian == null || uraianKejadian.trim().isEmpty()) {
            throw new IllegalArgumentException("Kronologi / uraian kejadian wajib diisi.");
        }

        TransaksiLaundry trx = transaksiDAO.cariBerdasarkanId(idTransaksi);
        if (trx == null) throw new IllegalArgumentException("Transaksi tidak ditemukan.");

        if ("MENUNGGU_PEMBAYARAN".equalsIgnoreCase(trx.getStatusTransaksi())
            || "DIBATALKAN".equalsIgnoreCase(trx.getStatusTransaksi())) {
            throw new IllegalArgumentException("Pengaduan hanya dapat dikaitkan dengan transaksi yang sudah diterima dan tidak dibatalkan.");
        }

        Pengaduan p = new Pengaduan();
        p.setNomorPengaduan(pengaduanDAO.generateNomorPengaduanBaru());
        p.setIdTransaksi(idTransaksi);
        p.setIdPelanggan(idPelanggan);
        p.setIdPenggunaPelapor(idPenggunaPelapor);
        p.setJenisPengaduan(jenisPengaduan.toUpperCase());
        p.setDeskripsiPakaian(deskripsiPakaian.trim());
        p.setJumlahPakaian(jumlahPakaian);
        p.setUraianKejadian(uraianKejadian.trim());

        boolean sukses = pengaduanDAO.tambahPengaduan(p);
        if (!sukses) throw new SQLException("Gagal mengirimkan pengaduan.");

        return p;
    }

    public List<Pengaduan> ambilSemuaPengaduan() throws SQLException {
        return pengaduanDAO.ambilSemuaPengaduan();
    }

    public List<Pengaduan> ambilPengaduanPelanggan(Long idPelanggan) throws SQLException {
        return pengaduanDAO.ambilPengaduanPelanggan(idPelanggan);
    }

    public List<Pengaduan> ambilTugasPenelusuranPetugas() throws SQLException {
        return pengaduanDAO.ambilTugasPenelusuranPetugas();
    }

    public boolean tugaskanPenelusuran(Long idPengaduan, Long idAdmin, Long idPetugas, Timestamp batasWaktu) throws SQLException {
        return pengaduanDAO.updateTugaskanPenelusuran(idPengaduan, idAdmin, idPetugas, batasWaktu);
    }

    public boolean isiHasilPenelusuran(Long idPengaduan, String hasil, Long idPetugas) throws SQLException {
        return pengaduanDAO.updateHasilPenelusuranPetugas(idPengaduan, hasil, idPetugas);
    }

    public boolean tetapkanTanggungJawab(Long idPengaduan, String statusTanggungJawab, String statusPengaduan, Long idAdmin) throws SQLException {
        return pengaduanDAO.updateKeputusanAdmin(idPengaduan, statusTanggungJawab, statusPengaduan, idAdmin);
    }

    public boolean ajukanPenyelesaian(Long idPengaduan, Long idPelanggan, String jenisPenyelesaian,
                                      String alasan, BigDecimal nominal, Long idAdmin, String catatan) throws SQLException {
        PenyelesaianPengaduan p = new PenyelesaianPengaduan();
        p.setIdPengaduan(idPengaduan);
        p.setIdPelanggan(idPelanggan);
        p.setJenisPenyelesaian(jenisPenyelesaian);
        p.setAlasanPenyelesaian(alasan);
        p.setNominalDisepakati(nominal != null ? nominal : BigDecimal.ZERO);
        p.setIdAdminPenetap(idAdmin);
        p.setCatatan(catatan);

        return penyelesaianDAO.tambahPenyelesaian(p);
    }

    public PenyelesaianPengaduan cariPenyelesaian(Long idPengaduan) throws SQLException {
        return penyelesaianDAO.cariBerdasarkanPengaduanId(idPengaduan);
    }

    public boolean responPelanggan(Long idPenyelesaian, Long idPengaduan, boolean setuju) throws SQLException {
        String statusPersetujuan = setuju ? "DISETUJUI_PELANGGAN" : "DITOLAK_PELANGGAN";
        String statusPengaduan = setuju ? "DALAM_PENYELESAIAN" : "SOLUSI_DITOLAK";
        boolean ok = penyelesaianDAO.updateResponPelanggan(
            idPenyelesaian, idPengaduan, statusPersetujuan, statusPengaduan
        );
        if (!ok) {
            throw new SQLException("Tanggapan tidak dapat diproses karena solusi sudah ditanggapi atau pengaduan berubah.");
        }
        return ok;
    }

    public boolean tandaiPenyelesaianSelesai(Long idPengaduan, Long idAdmin) throws SQLException {
        boolean ok = penyelesaianDAO.tandaiSelesai(idPengaduan, idAdmin);
        if (!ok) {
            throw new SQLException("Pengaduan hanya dapat diselesaikan setelah pelanggan menyetujui solusi yang belum dilaksanakan.");
        }
        return true;
    }
}
