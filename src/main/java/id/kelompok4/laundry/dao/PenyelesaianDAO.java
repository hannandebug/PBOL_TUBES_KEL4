package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.model.PenyelesaianPengaduan;

import java.sql.*;

// Fitur: Penyelesaian Pengaduan
public class PenyelesaianDAO {

    // Fitur: Tambah Penyelesaian
    public boolean tambahPenyelesaian(PenyelesaianPengaduan p) throws SQLException {
        String insertSql = "INSERT INTO penyelesaian_pengaduan " +
                           "(id_pengaduan, id_pelanggan, jenis_penyelesaian, alasan_penyelesaian, nominal_disepakati, " +
                           " status_persetujuan, id_admin_penetap, status_pelaksanaan, catatan) " +
                           "VALUES (?, ?, ?, ?, ?, 'MENUNGGU', ?, 'BELUM_DILAKSANAKAN', ?)";

        try (Connection conn = KoneksiDatabase.getKoneksi()) {
            conn.setAutoCommit(false);
            try {
                String statusPengaduan;
                String tanggungJawab;
                String lockPengaduanSql = "SELECT status_pengaduan, status_tanggung_jawab FROM pengaduan " +
                                          "WHERE id_pengaduan = ? FOR UPDATE";
                try (PreparedStatement ps = conn.prepareStatement(lockPengaduanSql)) {
                    ps.setLong(1, p.getIdPengaduan());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Pengaduan tidak ditemukan.");
                        }
                        statusPengaduan = rs.getString("status_pengaduan");
                        tanggungJawab = rs.getString("status_tanggung_jawab");
                    }
                }

                if (!"DIVERIFIKASI".equals(statusPengaduan) && !"SOLUSI_DITOLAK".equals(statusPengaduan)) {
                    throw new SQLException("Pengaduan belum siap menerima solusi.");
                }
                if (!"TANGGUNG_JAWAB_PENUH".equals(tanggungJawab)
                    && !"TANGGUNG_JAWAB_SEBAGIAN".equals(tanggungJawab)
                    && !"TANGGUNG_JAWAB_LAUNDRY".equals(tanggungJawab)) {
                    throw new SQLException("Tanggung jawab laundry belum ditetapkan.");
                }

                String solusiTerakhirSql = "SELECT status_persetujuan FROM penyelesaian_pengaduan " +
                                           "WHERE id_pengaduan = ? ORDER BY id_penyelesaian DESC LIMIT 1 FOR UPDATE";
                try (PreparedStatement ps = conn.prepareStatement(solusiTerakhirSql)) {
                    ps.setLong(1, p.getIdPengaduan());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && !"DITOLAK_PELANGGAN".equals(rs.getString("status_persetujuan"))) {
                            throw new SQLException("Solusi sebelumnya masih menunggu tanggapan atau sudah disetujui.");
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, p.getIdPengaduan());
                    ps.setLong(2, p.getIdPelanggan());
                    ps.setString(3, p.getJenisPenyelesaian());
                    ps.setString(4, p.getAlasanPenyelesaian());
                    ps.setBigDecimal(5, p.getNomorDisepakati());
                    ps.setLong(6, p.getIdAdminPenetap());
                    ps.setString(7, p.getCatatan());

                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) p.setIdPenyelesaian(rs.getLong(1));
                    }
                }

                String updatePengaduanSql = "UPDATE pengaduan SET status_pengaduan = 'MENUNGGU_PERSETUJUAN', " +
                                            "id_admin_pemeriksa = ?, id_pengguna_terakhir_memperbarui = ? " +
                                            "WHERE id_pengaduan = ?";
                try (PreparedStatement ps = conn.prepareStatement(updatePengaduanSql)) {
                    ps.setLong(1, p.getIdAdminPenetap());
                    ps.setLong(2, p.getIdAdminPenetap());
                    ps.setLong(3, p.getIdPengaduan());
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Status pengaduan tidak berhasil diperbarui.");
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    // Fitur: Cari Berdasarkan Pengaduan ID
    public PenyelesaianPengaduan cariBerdasarkanPengaduanId(Long idPengaduan) throws SQLException {
        String sql = "SELECT p.*, pg.nomor_pengaduan, u.nama_lengkap AS nama_admin " +
                     "FROM penyelesaian_pengaduan p " +
                     "INNER JOIN pengaduan pg ON pg.id_pengaduan = p.id_pengaduan " +
                     "INNER JOIN pengguna u ON u.id_pengguna = p.id_admin_penetap " +
                     "WHERE p.id_pengaduan = ? ORDER BY p.id_penyelesaian DESC LIMIT 1";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPengaduan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PenyelesaianPengaduan p = new PenyelesaianPengaduan();
                    p.setIdPenyelesaian(rs.getLong("id_penyelesaian"));
                    p.setIdPengaduan(rs.getLong("id_pengaduan"));
                    p.setIdPelanggan(rs.getLong("id_pelanggan"));
                    p.setJenisPenyelesaian(rs.getString("jenis_penyelesaian"));
                    p.setAlasanPenyelesaian(rs.getString("alasan_penyelesaian"));
                    p.setNominalDisepakati(rs.getBigDecimal("nominal_disepakati"));
                    p.setStatusPersetujuan(rs.getString("status_persetujuan"));
                    p.setWaktuPelangganMerespons(rs.getTimestamp("waktu_pelanggan_merespons"));
                    p.setIdAdminPenetap(rs.getLong("id_admin_penetap"));
                    p.setStatusPelaksanaan(rs.getString("status_pelaksanaan"));
                    p.setDilaksanakanOleh(rs.getLong("dilaksanakan_oleh"));
                    p.setWaktuPelaksanaan(rs.getTimestamp("waktu_pelaksanaan"));
                    p.setCatatan(rs.getString("catatan"));
                    p.setNomorPengaduan(rs.getString("nomor_pengaduan"));
                    p.setNamaAdminPenetap(rs.getString("nama_admin"));
                    return p;
                }
            }
        }
        return null;
    }

    // Fitur: Update Respon Pelanggan
    public boolean updateResponPelanggan(Long idPenyelesaian, Long idPengaduan,
                                         String statusPersetujuan, String statusPengaduan) throws SQLException {
        String sql = "UPDATE penyelesaian_pengaduan SET status_persetujuan = ?, waktu_pelanggan_merespons = CURRENT_TIMESTAMP " +
                     "WHERE id_penyelesaian = ? AND id_pengaduan = ? AND status_persetujuan = 'MENUNGGU'";

        try (Connection conn = KoneksiDatabase.getKoneksi()) {
            conn.setAutoCommit(false);
            try {
                String lockPengaduanSql = "SELECT status_pengaduan FROM pengaduan WHERE id_pengaduan = ? FOR UPDATE";
                try (PreparedStatement ps = conn.prepareStatement(lockPengaduanSql)) {
                    ps.setLong(1, idPengaduan);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !"MENUNGGU_PERSETUJUAN".equals(rs.getString("status_pengaduan"))) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, statusPersetujuan);
                    ps.setLong(2, idPenyelesaian);
                    ps.setLong(3, idPengaduan);
                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }

                String updatePengaduanSql = "UPDATE pengaduan SET status_pengaduan = ? " +
                                            "WHERE id_pengaduan = ? AND status_pengaduan = 'MENUNGGU_PERSETUJUAN'";
                try (PreparedStatement ps = conn.prepareStatement(updatePengaduanSql)) {
                    ps.setString(1, statusPengaduan);
                    ps.setLong(2, idPengaduan);
                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    // Fitur: Tandai Selesai
    public boolean tandaiSelesai(Long idPengaduan, Long idAdmin) throws SQLException {
        try (Connection conn = KoneksiDatabase.getKoneksi()) {
            conn.setAutoCommit(false);
            try {
                String lockPengaduanSql = "SELECT status_pengaduan FROM pengaduan WHERE id_pengaduan = ? FOR UPDATE";
                try (PreparedStatement ps = conn.prepareStatement(lockPengaduanSql)) {
                    ps.setLong(1, idPengaduan);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !"DALAM_PENYELESAIAN".equals(rs.getString("status_pengaduan"))) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                Long idPenyelesaian = null;
                String lockPenyelesaianSql = "SELECT id_penyelesaian, status_persetujuan, status_pelaksanaan " +
                                              "FROM penyelesaian_pengaduan WHERE id_pengaduan = ? " +
                                              "ORDER BY id_penyelesaian DESC LIMIT 1 FOR UPDATE";
                try (PreparedStatement ps = conn.prepareStatement(lockPenyelesaianSql)) {
                    ps.setLong(1, idPengaduan);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()
                            && "DISETUJUI_PELANGGAN".equals(rs.getString("status_persetujuan"))
                            && ("BELUM_DILAKSANAKAN".equals(rs.getString("status_pelaksanaan"))
                                || "DALAM_PROSES".equals(rs.getString("status_pelaksanaan")))) {
                            idPenyelesaian = rs.getLong("id_penyelesaian");
                        } else {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                String updatePenyelesaianSql = "UPDATE penyelesaian_pengaduan " +
                                               "SET status_pelaksanaan = 'SELESAI', dilaksanakan_oleh = ?, " +
                                               "waktu_pelaksanaan = CURRENT_TIMESTAMP " +
                                               "WHERE id_penyelesaian = ? AND status_persetujuan = 'DISETUJUI_PELANGGAN' " +
                                               "AND status_pelaksanaan IN ('BELUM_DILAKSANAKAN', 'DALAM_PROSES')";
                try (PreparedStatement ps = conn.prepareStatement(updatePenyelesaianSql)) {
                    ps.setLong(1, idAdmin);
                    ps.setLong(2, idPenyelesaian);
                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }

                String updatePengaduanSql = "UPDATE pengaduan SET status_pengaduan = 'SELESAI', " +
                                            "id_pengguna_terakhir_memperbarui = ? " +
                                            "WHERE id_pengaduan = ? AND status_pengaduan = 'DALAM_PENYELESAIAN'";
                try (PreparedStatement ps = conn.prepareStatement(updatePengaduanSql)) {
                    ps.setLong(1, idAdmin);
                    ps.setLong(2, idPengaduan);
                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }
}
