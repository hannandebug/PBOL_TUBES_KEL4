package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.model.Admin;
import id.kelompok4.laundry.model.Pelanggan;
import id.kelompok4.laundry.model.Pengguna;
import id.kelompok4.laundry.model.Petugas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Pengguna
 * Menangani operasi database untuk akun (Admin, Petugas, Pelanggan).
 * Menggunakan prinsip Polymorphism: mengembalikan objek Pengguna
 * yang secara dinamis diinstansiasi sebagai Admin, Petugas, atau Pelanggan.
 */
public class PenggunaDAO {

    /**
     * Memetakan baris ResultSet ke objek turunan Pengguna yang tepat.
     */
    private Pengguna mapRowKePengguna(ResultSet rs) throws SQLException {
        String peran = rs.getString("peran");
        Pengguna pengguna;

        if ("ADMIN".equalsIgnoreCase(peran)) {
            pengguna = new Admin();
        } else if ("PETUGAS".equalsIgnoreCase(peran)) {
            pengguna = new Petugas();
        } else {
            Pelanggan p = new Pelanggan();
            // Coba ambil field pelanggan jika di-join
            try {
                p.setIdPelanggan(rs.getLong("id_pelanggan"));
                p.setKodePelanggan(rs.getString("kode_pelanggan"));
                p.setAlamat(rs.getString("alamat"));
                p.setCatatan(rs.getString("catatan"));
            } catch (SQLException ignored) {}
            pengguna = p;
        }

        pengguna.setIdPengguna(rs.getLong("id_pengguna"));
        pengguna.setNamaLengkap(rs.getString("nama_lengkap"));
        pengguna.setNamaPengguna(rs.getString("nama_pengguna"));
        pengguna.setKataSandiHash(rs.getString("kata_sandi_hash"));
        pengguna.setPeran(peran);
        pengguna.setNomorTelepon(rs.getString("nomor_telepon"));
        pengguna.setStatusAktif(rs.getInt("status_aktif") == 1);
        pengguna.setDibuatPada(rs.getTimestamp("dibuat_pada"));
        pengguna.setDiperbaruiPada(rs.getTimestamp("diperbarui_pada"));

        return pengguna;
    }

    /**
     * Mencari pengguna berdasarkan username login.
     * Melakukan join dengan tabel pelanggan jika pengguna adalah PELANGGAN.
     */
    public Pengguna cariBerdasarkanUsername(String username) throws SQLException {
        String sql = "SELECT u.*, p.id_pelanggan, p.kode_pelanggan, p.alamat, p.catatan " +
                     "FROM pengguna u " +
                     "LEFT JOIN pelanggan p ON p.id_pengguna = u.id_pengguna " +
                     "WHERE u.nama_pengguna = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowKePengguna(rs);
                }
            }
        }
        return null;
    }

    /**
     * Mencari pengguna berdasarkan identifier login: bisa berupa username ATAU nomor telepon terdaftar.
     * Sesuai ketentuan Flowchart Login: 'Masukkan nama atau nomor telepon dan password'.
     */
    public Pengguna cariBerdasarkanIdentifierLogin(String identifier) throws SQLException {
        String sql = "SELECT u.*, p.id_pelanggan, p.kode_pelanggan, p.alamat, p.catatan " +
                     "FROM pengguna u " +
                     "LEFT JOIN pelanggan p ON p.id_pengguna = u.id_pengguna " +
                     "WHERE u.nama_pengguna = ? OR u.nomor_telepon = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identifier);
            ps.setString(2, identifier);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowKePengguna(rs);
                }
            }
        }
        return null;
    }

    /**
     * Mencari pengguna berdasarkan ID pengguna.
     */
    public Pengguna cariBerdasarkanId(Long idPengguna) throws SQLException {
        String sql = "SELECT u.*, p.id_pelanggan, p.kode_pelanggan, p.alamat, p.catatan " +
                     "FROM pengguna u " +
                     "LEFT JOIN pelanggan p ON p.id_pengguna = u.id_pengguna " +
                     "WHERE u.id_pengguna = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPengguna);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowKePengguna(rs);
                }
            }
        }
        return null;
    }

    /**
     * Menambahkan akun Admin baru (biasanya saat setup awal aplikasi).
     */
    public boolean tambahAdmin(Admin admin) throws SQLException {
        String sql = "INSERT INTO pengguna (nama_lengkap, nama_pengguna, kata_sandi_hash, peran, nomor_telepon, status_aktif) " +
                     "VALUES (?, ?, ?, 'ADMIN', ?, 1)";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, admin.getNamaLengkap());
            ps.setString(2, admin.getNamaPengguna());
            ps.setString(3, admin.getKataSandiHash());
            ps.setString(4, admin.getNomorTelepon());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        admin.setIdPengguna(rs.getLong(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Menambahkan akun Petugas baru oleh Admin.
     */
    public boolean tambahPetugas(Petugas petugas) throws SQLException {
        String sql = "INSERT INTO pengguna (nama_lengkap, nama_pengguna, kata_sandi_hash, peran, nomor_telepon, status_aktif) " +
                     "VALUES (?, ?, ?, 'PETUGAS', ?, ?)";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, petugas.getNamaLengkap());
            ps.setString(2, petugas.getNamaPengguna());
            ps.setString(3, petugas.getKataSandiHash());
            ps.setString(4, petugas.getNomorTelepon());
            ps.setInt(5, petugas.isStatusAktif() ? 1 : 0);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        petugas.setIdPengguna(rs.getLong(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Mengubah data pengguna (nama lengkap dan nomor telepon).
     */
    public boolean ubahPengguna(Pengguna pengguna) throws SQLException {
        String sql = "UPDATE pengguna SET nama_lengkap = ?, nomor_telepon = ?, status_aktif = ? WHERE id_pengguna = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pengguna.getNamaLengkap());
            ps.setString(2, pengguna.getNomorTelepon());
            ps.setInt(3, pengguna.isStatusAktif() ? 1 : 0);
            ps.setLong(4, pengguna.getIdPengguna());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mengubah kata sandi pengguna.
     */
    public boolean ubahKataSandi(Long idPengguna, String hashKataSandiBaru) throws SQLException {
        String sql = "UPDATE pengguna SET kata_sandi_hash = ? WHERE id_pengguna = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashKataSandiBaru);
            ps.setLong(2, idPengguna);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mengaktifkan atau menonaktifkan akun (soft delete).
     */
    public boolean setStatusAktif(Long idPengguna, boolean aktif) throws SQLException {
        String sql = "UPDATE pengguna SET status_aktif = ? WHERE id_pengguna = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, aktif ? 1 : 0);
            ps.setLong(2, idPengguna);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mengambil seluruh daftar Petugas/Kasir untuk menu Admin.
     */
    public List<Petugas> ambilSemuaPetugas() throws SQLException {
        List<Petugas> daftar = new ArrayList<>();
        String sql = "SELECT * FROM pengguna WHERE peran = 'PETUGAS' ORDER BY dibuat_pada DESC";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                daftar.add((Petugas) mapRowKePengguna(rs));
            }
        }
        return daftar;
    }

    /**
     * Menghitung total akun terdaftar (untuk verifikasi setup Admin pertama).
     */
    public int hitungTotalPengguna() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pengguna";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Menghitung jumlah akun Admin aktif.
     */
    public int hitungJumlahAdmin() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pengguna WHERE peran = 'ADMIN' AND status_aktif = 1";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}
