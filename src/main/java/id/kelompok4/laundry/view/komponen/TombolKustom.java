package id.kelompok4.laundry.view.komponen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * TombolKustom
 * Tombol modern dengan sudut membulat 10px, status hover/tekan yang responsif,
 * serta kontras warna yang jelas.
 */
public class TombolKustom extends JButton {

    private final Color warnaLatar;
    private Color warnaHover;
    private final Color warnaTekan;
    private final int sudutLengkung = 10;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public TombolKustom(String teks, Color latar, Color teksColor) {
        super(teks);
        this.warnaLatar = latar;
        this.warnaHover = tentukanWarnaHover(latar);
        this.warnaTekan = this.warnaHover.darker();

        setForeground(teksColor);
        setFont(TemaWarna.FONT_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(9, 16, 9, 16));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public static TombolKustom buatPrimer(String teks) {
        return new TombolKustom(teks, TemaWarna.AMALFI_TILE, TemaWarna.PUTIH);
    }

    public static TombolKustom buatAksiUtama(String teks) {
        return new TombolKustom(teks, TemaWarna.CITRUS_ZEST, TemaWarna.TEKS_GELAP);
    }

    public static TombolKustom buatSukses(String teks) {
        return new TombolKustom(teks, TemaWarna.HIJAU_SUKSES, TemaWarna.PUTIH);
    }

    public static TombolKustom buatBahaya(String teks) {
        return new TombolKustom(teks, TemaWarna.MERAH_BAHAYA, TemaWarna.PUTIH);
    }

    public static TombolKustom buatSekunder(String teks) {
        TombolKustom btn = new TombolKustom(teks, TemaWarna.PUTIH, TemaWarna.TEKS_GELAP);
        btn.warnaHover = new Color(0xF1, 0xF5, 0xF9);
        return btn;
    }

    private Color tentukanWarnaHover(Color c) {
        if (c.equals(TemaWarna.CITRUS_ZEST)) {
            return new Color(0xF5, 0x97, 0x14);
        } else if (c.equals(TemaWarna.AMALFI_TILE)) {
            return TemaWarna.AMALFI_GELAP;
        } else if (c.equals(TemaWarna.HIJAU_SUKSES)) {
            return new Color(0x15, 0x80, 0x3D);
        } else if (c.equals(TemaWarna.MERAH_BAHAYA)) {
            return new Color(0xB9, 0x1C, 0x1C);
        }
        return c.darker();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!isEnabled()) {
            g2.setColor(new Color(0xE2, 0xE8, 0xF0));
        } else if (isPressed) {
            g2.setColor(warnaTekan);
        } else if (isHovered) {
            g2.setColor(warnaHover);
        } else {
            g2.setColor(warnaLatar);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), sudutLengkung, sudutLengkung);

        if (warnaLatar.equals(TemaWarna.PUTIH)) {
            g2.setColor(new Color(0xCB, 0xD5, 0xE1));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, sudutLengkung, sudutLengkung);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
