package id.kelompok4.laundry.view.komponen;

import javax.swing.*;
import java.awt.*;

/**
 * TemaWarna
 * Palet warna resmi Kelompok 4 dan skala tipografi yang konsisten.
 */
public class TemaWarna {

    // Palet Utama
    public static final Color AMALFI_TILE    = new Color(0x2E, 0x5A, 0xA7); // #2E5AA7
    public static final Color AMALFI_GELAP   = new Color(0x22, 0x45, 0x84);
    public static final Color CITRUS_ZEST    = new Color(0xFF, 0xA6, 0x2B); // #FFA62B
    public static final Color SEA_BREEZE     = new Color(0x86, 0xC5, 0xFF); // #86C5FF
    public static final Color CREAM_GELATO   = new Color(0xF8, 0xE6, 0xA0); // #F8E6A0
    public static final Color LATAR_BELAKANG = new Color(0xF5, 0xF8, 0xFC); // #F5F8FC
    public static final Color TEKS_GELAP     = new Color(0x17, 0x32, 0x4D); // #17324D

    // Warna Netral & Status
    public static final Color PUTIH          = Color.WHITE;
    public static final Color PERMUKAAN_SOFT = new Color(0xF8, 0xFA, 0xFD);
    public static final Color ABU_BORDER     = new Color(0xE2, 0xE8, 0xF0);
    public static final Color ABU_TEKS       = new Color(0x64, 0x74, 0x8B);
    public static final Color HIJAU_SUKSES   = new Color(0x16, 0xA3, 0x4A);
    public static final Color MERAH_BAHAYA   = new Color(0xDC, 0x26, 0x26);

    // Tipografi Konsisten
    public static final Font FONT_HEADER_BESAR = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_TITLE        = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBTITLE     = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULER      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD         = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_KECIL        = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_KECIL_BOLD   = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_BADGE        = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_MONO         = new Font("Consolas", Font.PLAIN, 12);

    /**
     * Mengatur teks placeholder pada JTextField bila FlatLaf aktif.
     */
    public static void aturPlaceholder(JComponent field, String placeholder) {
        field.putClientProperty("JTextField.placeholderText", placeholder);
    }

    /**
     * Membuat label judul bagian dengan jarak bawah rapi.
     */
    public static JLabel buatJudulBagian(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(FONT_SUBTITLE);
        lbl.setForeground(TEKS_GELAP);
        return lbl;
    }

    /**
     * Membungkus JTable ke dalam JScrollPane bergaya kartu bersih tanpa border kaku.
     */
    public static JScrollPane bungkusTabel(JTable tabel) {
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBackground(PUTIH);
        scroll.getViewport().setBackground(PUTIH);
        scroll.setBorder(BorderFactory.createLineBorder(ABU_BORDER, 1));
        return scroll;
    }
}
