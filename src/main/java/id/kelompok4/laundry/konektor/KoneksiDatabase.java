package id.kelompok4.laundry.konektor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Fitur: Koneksi Database MySQL
public class KoneksiDatabase {

    private static final Properties properti = new Properties();
    private static boolean terinisialisasi = false;

    private static String host = "localhost";
    private static String port = "3306";
    private static String dbName = "laundry_kelompok4";
    private static String username = "root";
    private static String password = "";
    private static String useSSL = "false";
    private static String serverTimezone = "Asia/Jakarta";
    private static String allowPublicKeyRetrieval = "true";

    static {
        muatKonfigurasi();
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[KoneksiDatabase] Driver MySQL JDBC tidak ditemukan: " + e.getMessage());
        }
    }

    // Fitur: Membaca file database.properties dari classpath atau direktori kerja
    private static void muatKonfigurasi() {
        if (terinisialisasi) return;

        try {
            InputStream input = null;

            input = KoneksiDatabase.class.getClassLoader().getResourceAsStream("database.properties");

            if (input == null) {
                File fileLokal = new File("database.properties");
                if (fileLokal.exists()) {
                    input = new FileInputStream(fileLokal);
                }
            }

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

    // Fitur: Membuka dan mengembalikan koneksi baru ke database MySQL
    public static Connection getKoneksi() throws SQLException {
        String url = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=%s&serverTimezone=%s&allowPublicKeyRetrieval=%s&characterEncoding=utf8",
            host, port, dbName, useSSL, serverTimezone, allowPublicKeyRetrieval
        );
        return DriverManager.getConnection(url, username, password);
    }

    // Fitur: Menguji apakah koneksi ke database berhasil terhubung
    public static boolean ujiKoneksi() {
        try (Connection conn = getKoneksi()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[KoneksiDatabase] Uji koneksi gagal: " + e.getMessage());
            return false;
        }
    }

    public static String getDbName() { return dbName; }
    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getUsername() { return username; }
}
