package id.kelompok4.laundry.konektor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class KoneksiDatabase
 * Mengelola pembentukan koneksi JDBC ke database MySQL (laundry_kelompok4).
 * Menggunakan prinsip Singleton sederhana dan membaca konfigurasi eksternal
 * dari file database.properties agar kredensial tidak di-hardcode.
 *
 * Kelompok 4 - Pemrograman Berorientasi Objek Lanjutan (PBOL)
 */
public class KoneksiDatabase {

    private static final Properties properti = new Properties();
    private static boolean terinisialisasi = false;

    // Nilai default jika file konfigurasi tidak ditemukan
    private static String host = "localhost";
    private static String port = "3306";
    private static String dbName = "laundry_kelompok4";
    private static String username = "root";
    private static String password = "";
    private static String useSSL = "false";
    private static String serverTimezone = "Asia/Jakarta";
    private static String allowPublicKeyRetrieval = "true";

    // Static block untuk memuat driver dan konfigurasi saat class pertama kali dipanggil
    static {
        muatKonfigurasi();
        try {
            // Memastikan driver MySQL Connector/J terdaftar
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[KoneksiDatabase] Driver MySQL JDBC tidak ditemukan: " + e.getMessage());
        }
    }

    /**
     * Membaca file database.properties dari classpath atau direktori kerja.
     */
    private static void muatKonfigurasi() {
        if (terinisialisasi) return;

        try {
            InputStream input = null;

            // Prioritas 1: Baca dari resource classpath
            input = KoneksiDatabase.class.getClassLoader().getResourceAsStream("database.properties");

            // Prioritas 2: Baca dari file lokal database.properties jika ada di root project
            if (input == null) {
                File fileLokal = new File("database.properties");
                if (fileLokal.exists()) {
                    input = new FileInputStream(fileLokal);
                }
            }

            // Prioritas 3: Direktori database/
            if (input == null) {
                File fileFolder = new File("database/database.properties");
                if (fileFolder.exists()) {
                    input = new FileInputStream(fileFolder);
                }
            }

            if (input != null) {
                try (InputStream is = input) {
                    properti.load(is);
                    host = properti.getProperty("db.host", host);
                    port = properti.getProperty("db.port", port);
                    dbName = properti.getProperty("db.name", dbName);
                    username = properti.getProperty("db.username", username);
                    password = properti.getProperty("db.password", password);
                    useSSL = properti.getProperty("db.useSSL", useSSL);
                    serverTimezone = properti.getProperty("db.serverTimezone", serverTimezone);
                    allowPublicKeyRetrieval = properti.getProperty("db.allowPublicKeyRetrieval", allowPublicKeyRetrieval);
                }
            }
            terinisialisasi = true;
        } catch (Exception e) {
            System.err.println("[KoneksiDatabase] Menggunakan konfigurasi default karena: " + e.getMessage());
        }
    }

    /**
     * Membuka dan mengembalikan koneksi baru ke database MySQL.
     * Menggunakan try-with-resources pada pemanggilnya.
     *
     * @return Connection objek JDBC
     * @throws SQLException jika koneksi gagal dibuka
     */
    public static Connection getKoneksi() throws SQLException {
        String url = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=%s&serverTimezone=%s&allowPublicKeyRetrieval=%s&characterEncoding=utf8",
            host, port, dbName, useSSL, serverTimezone, allowPublicKeyRetrieval
        );
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Menguji apakah koneksi ke database berhasil terhubung.
     *
     * @return true jika terhubung, false jika gagal
     */
    public static boolean ujiKoneksi() {
        try (Connection conn = getKoneksi()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[KoneksiDatabase] Uji koneksi gagal: " + e.getMessage());
            return false;
        }
    }

    // Getter untuk informasi konfigurasi jika dibutuhkan GUI/Status
    public static String getDbName() { return dbName; }
    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getUsername() { return username; }
}
