package id.kelompok4.laundry;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.view.komponen.TemaWarna;
import id.kelompok4.laundry.view.login.FormLogin;

import javax.swing.*;
import java.awt.*;

// Fitur: Inisialisasi Aplikasi
public class Main {

    public static void main(String[] args) {
        inisialisasiLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            boolean terhubung = KoneksiDatabase.ujiKoneksi();
            if (!terhubung) {
                JOptionPane.showMessageDialog(
                    null,
                    "Aplikasi belum bisa terhubung ke database MySQL (" +
                    KoneksiDatabase.getHost() + ":" + KoneksiDatabase.getPort() + ").\n\n" +
                    "Coba cek langkah berikut:\n" +
                    "1. Jalankan MySQL di Laragon atau XAMPP.\n" +
                    "2. Pastikan database 'laundry_kelompok4' sudah dibuat/diimpor.\n" +
                    "3. Cek pengaturan di file 'database.properties'.",
                    "Koneksi Database Bermasalah",
                    JOptionPane.WARNING_MESSAGE
                );
            }

            FormLogin loginFrame = new FormLogin();
            loginFrame.setVisible(true);
        });
    }

    // Fitur: Mengatur tema FlatLaf dan styling global komponen Swing agar tampak modern dan rapi
    private static void inisialisasiLookAndFeel() {
        try {
            Class<?> flatLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            Object flatLafInstance = flatLafClass.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel((LookAndFeel) flatLafInstance);

            UIManager.put("Component.arc", 10);
            UIManager.put("Button.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("CheckBox.arc", 6);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.innerFocusWidth", 0);
            UIManager.put("Component.focusColor", TemaWarna.SEA_BREEZE);
            UIManager.put("Component.borderColor", TemaWarna.ABU_BORDER);

            UIManager.put("TextField.margin", new Insets(6, 10, 6, 10));
            UIManager.put("PasswordField.margin", new Insets(6, 10, 6, 10));
            UIManager.put("ComboBox.padding", new Insets(5, 10, 5, 10));
            UIManager.put("TextArea.margin", new Insets(8, 10, 8, 10));

            UIManager.put("ScrollBar.width", 8);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.trackArc", 999);
            UIManager.put("ScrollBar.thumb", new Color(0xCB, 0xD5, 0xE1));
            UIManager.put("ScrollBar.hoverThumbColor", new Color(0x94, 0xA3, 0xB8));

            UIManager.put("TabbedPane.selectedBackground", TemaWarna.PUTIH);
            UIManager.put("TabbedPane.underlineColor", TemaWarna.AMALFI_TILE);
            UIManager.put("TabbedPane.tabHeight", 38);
            UIManager.put("TabbedPane.tabInsets", new Insets(6, 16, 6, 16));
            UIManager.put("TabbedPane.showTabSeparators", false);
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }
    }
}
