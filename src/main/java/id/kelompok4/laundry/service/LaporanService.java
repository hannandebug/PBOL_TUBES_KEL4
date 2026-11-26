package id.kelompok4.laundry.service;

import id.kelompok4.laundry.dao.LaporanDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Service Laporan
 * Mengelola data analitik, statistik dashboard ketiga role, dan laporan finansial.
 */
public class LaporanService {

    private final LaporanDAO laporanDAO = new LaporanDAO();

    public Map<String, Object> ambilRingkasanAdmin() throws SQLException {
        return laporanDAO.ambilRingkasanAdmin();
    }

    public Map<String, Object> ambilRingkasanPetugas() throws SQLException {
        return laporanDAO.ambilRingkasanPetugas();
    }

    public Map<String, Object> ambilRingkasanPelanggan(Long idPelanggan) throws SQLException {
        return laporanDAO.ambilRingkasanPelanggan(idPelanggan);
    }

    public List<Map<String, Object>> ambilLaporanArusKas() throws SQLException {
        return laporanDAO.ambilLaporanArusKas();
    }
}
