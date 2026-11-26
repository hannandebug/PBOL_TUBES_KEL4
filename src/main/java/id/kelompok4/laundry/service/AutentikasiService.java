package id.kelompok4.laundry.service;

import id.kelompok4.laundry.dao.PelangganDAO;
import id.kelompok4.laundry.dao.PenggunaDAO;
import id.kelompok4.laundry.model.Admin;
import id.kelompok4.laundry.model.Pelanggan;
import id.kelompok4.laundry.model.Pengguna;
import id.kelompok4.laundry.util.PasswordUtil;
import id.kelompok4.laundry.util.SessionManager;

import java.sql.SQLException;

/**
 * Service Autentikasi
 * Mengelola proses login, registrasi pelanggan mandiri, setup Admin awal,
 * serta validasi hak akses peran (Role-Based Access Control).
 */
public class AutentikasiService {

    private final PenggunaDAO penggunaDAO = new PenggunaDAO();
    private final PelangganDAO pelangganDAO = new PelangganDAO();

    /**
     * Memeriksa apakah database masih kosong dari akun Admin.
     */
    public boolean apakahPerluSetupAdminAwal() {
        try {
            return penggunaDAO.hitungJumlahAdmin() == 0;
        } catch (SQLException e) {
            System.err.println("[AutentikasiService] Gagal memeriksa setup admin: " + e.getMessage());
            return false;
        }
    }

    /**
     * Membuat akun Admin pertama kali.
     */
    public boolean setupAdminPertama(String namaLengkap, String namaPengguna, String password, String noTelp) throws Exception {
        if (namaLengkap == null || namaLengkap.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama lengkap wajib diisi.");
        }
        if (namaPengguna == null || namaPengguna.trim().length() < 4) {
            throw new IllegalArgumentException("Username minimal 4 karakter.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter.");
        }

        // Cek duplikasi username
        if (penggunaDAO.cariBerdasarkanUsername(namaPengguna.trim()) != null) {
            throw new IllegalArgumentException("Username sudah digunakan. Silakan pilih username lain.");
        }

        String hash = PasswordUtil.hashPassword(password);
        Admin admin = new Admin();
        admin.setNamaLengkap(namaLengkap.trim());
        admin.setNamaPengguna(namaPengguna.trim());
        admin.setKataSandiHash(hash);
        admin.setNomorTelepon(noTelp != null ? noTelp.trim() : null);
        admin.setStatusAktif(true);

        return penggunaDAO.tambahAdmin(admin);
    }

    /**
     * Memproses login pengguna (Admin, Petugas, atau Pelanggan).
     * Menerima username ATAU nomor telepon sesuai flowchart login.
     */
    public Pengguna login(String usernameAtauTelp, String password) throws Exception {
        if (usernameAtauTelp == null || usernameAtauTelp.trim().isEmpty()) {
            throw new IllegalArgumentException("Username atau Nomor Telepon tidak boleh kosong.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong.");
        }

        Pengguna p = penggunaDAO.cariBerdasarkanIdentifierLogin(usernameAtauTelp.trim());
        if (p == null) {
            throw new IllegalArgumentException("Username/Nomor Telepon atau password salah.");
        }

        if (!p.isStatusAktif()) {
            throw new IllegalArgumentException("Akun Anda telah dinonaktifkan oleh administrator. Silakan hubungi admin.");
        }

        boolean cocok = PasswordUtil.checkPassword(password, p.getKataSandiHash());
        if (!cocok) {
            throw new IllegalArgumentException("Username/Nomor Telepon atau password salah.");
        }

        if (PasswordUtil.perluUpgrade(p.getKataSandiHash())) {
            String hashBaru = PasswordUtil.hashPassword(password);
            if (!penggunaDAO.ubahKataSandi(p.getIdPengguna(), hashBaru)) {
                throw new SQLException("Gagal memperbarui penyimpanan kata sandi akun.");
            }
            p.setKataSandiHash(hashBaru);
        }

        // Simpan ke sesi aktif
        SessionManager.setPenggunaAktif(p);
        return p;
    }

    /**
     * Registrasi mandiri pelanggan baru.
     * Role secara otomatis ditetapkan menjadi 'PELANGGAN'.
     */
    public boolean registrasiPelanggan(String namaLengkap, String username, String password, String noTelp, String alamat) throws Exception {
        if (namaLengkap == null || namaLengkap.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama lengkap wajib diisi.");
        }
        if (username == null || username.trim().length() < 4) {
            throw new IllegalArgumentException("Username minimal 4 karakter.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter.");
        }
        if (noTelp == null || noTelp.trim().length() < 10) {
            throw new IllegalArgumentException("Nomor telepon tidak valid (minimal 10 digit).");
        }

        if (penggunaDAO.cariBerdasarkanUsername(username.trim()) != null) {
            throw new IllegalArgumentException("Username sudah terdaftar. Gunakan username lain.");
        }

        String hash = PasswordUtil.hashPassword(password);
        Pelanggan pelanggan = new Pelanggan();
        pelanggan.setNamaLengkap(namaLengkap.trim());
        pelanggan.setNamaPengguna(username.trim());
        pelanggan.setNomorTelepon(noTelp.trim());
        pelanggan.setAlamat(alamat != null ? alamat.trim() : null);
        pelanggan.setStatusAktif(true);

        return pelangganDAO.registrasiPelanggan(pelanggan, hash);
    }
}
