package id.kelompok4.laundry.service;

import id.kelompok4.laundry.dao.LaporanDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

// Fitur: Laporan dan Ringkasan
public class LaporanService {

    private final LaporanDAO laporanDAO = new LaporanDAO();

    // Fitur: Ambil Ringkasan Admin
    public Map<String, Object> ambilRingkasanAdmin() throws SQLException {
        return laporanDAO.ambilRingkasanAdmin();
    }

    // Fitur: Ambil Ringkasan Petugas
    public Map<String, Object> ambilRingkasanPetugas() throws SQLException {
        return laporanDAO.ambilRingkasanPetugas();
    }

    // Fitur: Ambil Ringkasan Pelanggan
    public Map<String, Object> ambilRingkasanPelanggan(Long idPelanggan) throws SQLException {
        return laporanDAO.ambilRingkasanPelanggan(idPelanggan);
    }

    // Fitur: Ambil Laporan Arus Kas
    public List<Map<String, Object>> ambilLaporanArusKas() throws SQLException {
        return laporanDAO.ambilLaporanArusKas();
    }
}
