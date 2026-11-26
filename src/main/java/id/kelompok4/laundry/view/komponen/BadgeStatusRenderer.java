package id.kelompok4.laundry.view.komponen;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

// Fitur: Tampilkan Badge Status
// OOP: Inheritance - BadgeStatusRenderer mewarisi DefaultTableCellRenderer.
public class BadgeStatusRenderer extends DefaultTableCellRenderer {

    private Color bgPill = new Color(0xE2, 0xE8, 0xF0);
    private Color fgPill = TemaWarna.TEKS_GELAP;
    private String teksTampil = "-";

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            setBackground(row % 2 == 0 ? TemaWarna.PUTIH : TemaWarna.PERMUKAAN_SOFT);
        }

        if (value == null || value.toString().trim().isEmpty()) {
            teksTampil = "-";
            bgPill = new Color(0xF1, 0xF5, 0xF9);
            fgPill = TemaWarna.ABU_TEKS;
        } else {
            String kode = value.toString().trim().toUpperCase();
            teksTampil = terjemahkanStatus(kode, value.toString().trim());
            tentukanWarna(kode);
        }

        setText("");
        return this;
    }

    private String terjemahkanStatus(String kode, String asli) {
        switch (kode) {
            case "MENUNGGU_PEMBAYARAN": return "Belum Bayar";
            case "BELUM_DIBAYAR":       return "Belum Bayar";
            case "DITERIMA":            return "Diterima";
            case "DIPROSES":            return "Sedang Dicuci";
            case "SIAP_DIAMBIL":        return "Siap Diambil";
            case "SELESAI":             return "Selesai";
            case "DIBATALKAN":          return "Dibatalkan";
            case "LUNAS":               return "Lunas";
            case "AKTIF":               return "Aktif";
            case "NONAKTIF":            return "Nonaktif";
            case "DIAJUKAN":            return "Menunggu";
            case "DISETUJUI":           return "Disetujui";
            case "DITOLAK":             return "Ditolak";
            case "MENUNGGU":            return "Menunggu";
            case "DIBAYAR":             return "Sudah Cair";
            case "DILAPORKAN":          return "Baru Masuk";
            case "DALAM_PENELUSURAN":   return "Sedang Dicek";
            case "DIVERIFIKASI":        return "Sudah Dicek";
            case "MENUNGGU_PERSETUJUAN": return "Menunggu Respon";
            case "MENUNGGU_PERSETUJUAN_PELANGGAN": return "Menunggu Respon";
            case "SOLUSI_DITOLAK":      return "Solusi Ditolak";
            case "DISETUJUI_PELANGGAN": return "Disetujui";
            case "DITOLAK_PELANGGAN":   return "Ditolak";
            case "BELUM_DITENTUKAN":    return "Belum Diputuskan";
            case "TANGGUNG_JAWAB_LAUNDRY": return "Tanggung Jawab Laundry";
            case "TANGGUNG_JAWAB_PENUH":    return "Tanggung Jawab Penuh";
            case "TANGGUNG_JAWAB_SEBAGIAN": return "Sebagian";
            case "BUKAN_TANGGUNG_JAWAB_LAUNDRY": return "Bukan Kesalahan Toko";
            case "PEMASUKAN":           return "Uang Masuk";
            case "PENGELUARAN":         return "Uang Keluar";
            default:                    return asli.replace("_", " ");
        }
    }

    private void tentukanWarna(String kode) {
        switch (kode) {
            case "AKTIF":
            case "SELESAI":
            case "LUNAS":
            case "DISETUJUI":
            case "DIBAYAR":
            case "DISETUJUI_PELANGGAN":
            case "PEMASUKAN":
                bgPill = new Color(0xDC, 0xFC, 0xE7);
                fgPill = new Color(0x16, 0x65, 0x34);
                break;
            case "MENUNGGU_PEMBAYARAN":
            case "MENUNGGU":
            case "DIAJUKAN":
            case "BELUM_DIBAYAR":
            case "MENUNGGU_PERSETUJUAN":
            case "MENUNGGU_PERSETUJUAN_PELANGGAN":
            case "SOLUSI_DITOLAK":
            case "DILAPORKAN":
            case "BELUM_DITENTUKAN":
                bgPill = new Color(0xFE, 0xF3, 0xC7);
                fgPill = new Color(0x92, 0x40, 0x0E);
                break;
            case "DITERIMA":
            case "DIPROSES":
            case "DALAM_PENELUSURAN":
            case "DIVERIFIKASI":
            case "TANGGUNG_JAWAB_SEBAGIAN":
                bgPill = new Color(0xDB, 0xEA, 0xFE);
                fgPill = new Color(0x1E, 0x40, 0xAF);
                break;
            case "SIAP_DIAMBIL":
                bgPill = new Color(0xFF, 0xED, 0xD5);
                fgPill = new Color(0x9A, 0x34, 0x12);
                break;
            case "NONAKTIF":
            case "DIBATALKAN":
            case "DITOLAK":
            case "DITOLAK_PELANGGAN":
            case "PENGELUARAN":
            case "TANGGUNG_JAWAB_PENUH":
            case "TANGGUNG_JAWAB_LAUNDRY":
                bgPill = new Color(0xFE, 0xE2, 0xE2);
                fgPill = new Color(0x99, 0x1B, 0x1B);
                break;
            default:
                bgPill = new Color(0xF1, 0xF5, 0xF9);
                fgPill = TemaWarna.TEKS_GELAP;
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(TemaWarna.FONT_BADGE);

        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(teksTampil);
        int pillW = Math.min(getWidth() - 12, textW + 18);
        int pillH = 24;
        int x = (getWidth() - pillW) / 2;
        int y = (getHeight() - pillH) / 2;

        g2.setColor(bgPill);
        g2.fillRoundRect(x, y, pillW, pillH, 12, 12);

        g2.setColor(fgPill);
        int textX = (getWidth() - textW) / 2;
        int textY = y + ((pillH - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(teksTampil, textX, textY);

        g2.dispose();
    }
}
