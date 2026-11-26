package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.model.Pelanggan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Pelanggan
 * Menangani transaksi pendaftaran mandiri pelanggan dan query data pelanggan.
 * Menerapkan Database Transaction (Commit & Rollback) untuk insert atomik
 * ke tabel pengguna dan tabel pelanggan.
 */
public class PelangganDAO {

    /**
     * Mendaftarkan pelanggan baru secara atomik.
     * Jika insert ke salah satu tabel gagal, seluruh operasi di-rollback.
     */
    public boolean registrasiPelanggan(Pelanggan pelanggan, String kataSandiHash) throws SQLException {
        String sqlPengguna = "INSERT INTO pengguna (nama_lengkap, nama_pengguna, kata_sandi_hash, peran, nomor_telepon, status_aktif) " +
                             "VALUES (?, ?, ?, 'PELANGGAN', ?, 1)";
        String sqlPelanggan = "INSERT INTO pelanggan (id_pengguna, kode_pelanggan, alamat, catatan) " +
                              "VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = KoneksiDatabase.getKoneksi();
            // Matikan autocommit untuk memulai transaksi database
            conn.setAutoCommit(false);

            // 1. Insert ke tabel pengguna
            Long idPenggunaBaru;
            try (PreparedStatement psUser = conn.prepareStatement(sqlPengguna, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, pelanggan.getNamaLengkap());
                psUser.setString(2, pelanggan.getNamaPengguna());
                psUser.setString(3, kataSandiHash);
                psUser.setString(4, pelanggan.getNomorTelepon());
                psUser.executeUpdate();

                try (ResultSet rs = psUser.getGeneratedKeys()) {
                    if (rs.next()) {
                        idPenggunaBaru = rs.getLong(1);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Buat kode pelanggan unik jika belum ada
            String kode = pelanggan.getKodePelanggan();
            if (kode == null || kode.trim().isEmpty()) {
                kode = generateKodePelanggan(conn);
            }

            // 3. Insert ke tabel pelanggan
            try (PreparedStatement psPel = conn.prepareStatement(sqlPelanggan, Statement.RETURN_GENERATED_KEYS)) {
                psPel.setLong(1, idPenggunaBaru);
                psPel.setString(2, kode);
                psPel.setString(3, pelanggan.getAlamat());
                psPel.setString(4, pelanggan.getCatatan());
                psPel.executeUpdate();

                try (ResultSet rsPel = psPel.getGeneratedKeys()) {
                    if (rsPel.next()) {
                        pelanggan.setIdPelanggan(rsPel.getLong(1));
                    }
                }
            }

            pelanggan.setIdPengguna(idPenggunaBaru);
            pelanggan.setKodePelanggan(kode);

            // Commit transaksi jika seluruh langkah sukses
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Menghasilkan kode pelanggan baru (misal: PLG-0001).
     */
    private String generateKodePelanggan(Connection conn) throws SQLException {
        String sql = "SELECT MAX(id_pelanggan) FROM pelanggan";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            long maxId = 0;
            if (rs.next()) {
                maxId = rs.getLong(1);
            }
            return String.format("PLG-%04d", maxId + 1);
        }
    }

    /**
     * Menghasilkan kode pelanggan baru menggunakan koneksi default.
     */
    public String generateKodePelangganBaru() throws SQLException {
        try (Connection conn = KoneksiDatabase.getKoneksi()) {
            return generateKodePelanggan(conn);
        }
    }

    /**
     * Mencari profil pelanggan berdasarkan ID Pelanggan.
     */
    public Pelanggan cariBerdasarkanId(Long idPelanggan) throws SQLException {
        String sql = "SELECT p.*, u.nama_lengkap, u.nama_pengguna, u.kata_sandi_hash, u.nomor_telepon, u.status_aktif " +
                     "FROM pelanggan p " +
                     "INNER JOIN pengguna u ON u.id_pengguna = p.id_pengguna " +
                     "WHERE p.id_pelanggan = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPelanggan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowKePelanggan(rs);
                }
            }
        }
        return null;
    }

    /**
     * Mencari profil pelanggan berdasarkan ID Pengguna (akun login).
     */
    public Pelanggan cariBerdasarkanIdPengguna(Long idPengguna) throws SQLException {
        String sql = "SELECT p.*, u.nama_lengkap, u.nama_pengguna, u.kata_sandi_hash, u.nomor_telepon, u.status_aktif " +
                     "FROM pelanggan p " +
                     "INNER JOIN pengguna u ON u.id_pengguna = p.id_pengguna " +
                     "WHERE p.id_pengguna = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPengguna);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowKePelanggan(rs);
                }
            }
        }
        return null;
    }

    /**
     * Mencari pelanggan berdasarkan kata kunci (kode, nama, atau nomor telepon).
     */
    public List<Pelanggan> cariPelanggan(String keyword) throws SQLException {
        List<Pelanggan> hasil = new ArrayList<>();
        String sql = "SELECT p.*, u.nama_lengkap, u.nama_pengguna, u.kata_sandi_hash, u.nomor_telepon, u.status_aktif " +
                     "FROM pelanggan p " +
                     "INNER JOIN pengguna u ON u.id_pengguna = p.id_pengguna " +
                     "WHERE p.kode_pelanggan LIKE ? OR u.nama_lengkap LIKE ? OR u.nomor_telepon LIKE ? " +
                     "ORDER BY u.nama_lengkap ASC";

        String p = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(mapRowKePelanggan(rs));
                }
            }
        }
        return hasil;
    }

    /**
     * Mengambil seluruh daftar pelanggan.
     */
    public List<Pelanggan> ambilSemuaPelanggan() throws SQLException {
        List<Pelanggan> hasil = new ArrayList<>();
        String sql = "SELECT p.*, u.nama_lengkap, u.nama_pengguna, u.kata_sandi_hash, u.nomor_telepon, u.status_aktif " +
                     "FROM pelanggan p " +
                     "INNER JOIN pengguna u ON u.id_pengguna = p.id_pengguna " +
                     "ORDER BY p.id_pelanggan DESC";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(mapRowKePelanggan(rs));
            }
        }
        return hasil;
    }

    private Pelanggan mapRowKePelanggan(ResultSet rs) throws SQLException {
        Pelanggan p = new Pelanggan();
        p.setIdPelanggan(rs.getLong("id_pelanggan"));
        p.setIdPengguna(rs.getLong("id_pengguna"));
        p.setKodePelanggan(rs.getString("kode_pelanggan"));
        p.setAlamat(rs.getString("alamat"));
        p.setCatatan(rs.getString("catatan"));
        p.setNamaLengkap(rs.getString("nama_lengkap"));
        p.setNamaPengguna(rs.getString("nama_pengguna"));
        p.setKataSandiHash(rs.getString("kata_sandi_hash"));
        p.setNomorTelepon(rs.getString("nomor_telepon"));
        p.setStatusAktif(rs.getInt("status_aktif") == 1);
        p.setPeran("PELANGGAN");
        return p;
    }
}
