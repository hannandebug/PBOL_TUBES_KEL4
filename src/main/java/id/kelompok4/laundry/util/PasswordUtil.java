package id.kelompok4.laundry.util;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

// Fitur: Hash dan Verifikasi Kata Sandi
public class PasswordUtil {

    private static final String HASH_VERSION = "{bcrypt-sha256}";

    // Fitur: Meng-hash SHA-256 terlebih dahulu agar kata sandi panjang tidak terpotong oleh batas BCrypt 72 byte
    public static String hashPassword(String passwordPlain) {
        if (passwordPlain == null || passwordPlain.isEmpty()) {
            throw new IllegalArgumentException("Kata sandi tidak boleh kosong.");
        }
        return HASH_VERSION + BCrypt.hashpw(sha256Hex(passwordPlain), BCrypt.gensalt(12));
    }

    // Fitur: Memeriksa kecocokan kata sandi yang dimasukkan pengguna dengan hash di database
    public static boolean checkPassword(String passwordPlain, String passwordHash) {
        if (passwordPlain == null || passwordHash == null || passwordHash.isEmpty()) {
            return false;
        }
        try {
            if (passwordHash.startsWith(HASH_VERSION)) {
                return BCrypt.checkpw(sha256Hex(passwordPlain), passwordHash.substring(HASH_VERSION.length()));
            }
            return BCrypt.checkpw(passwordPlain, passwordHash);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean perluUpgrade(String passwordHash) {
        return passwordHash != null && !passwordHash.startsWith(HASH_VERSION);
    }

    private static String sha256Hex(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritma SHA-256 tidak tersedia.", e);
        }
    }
}
