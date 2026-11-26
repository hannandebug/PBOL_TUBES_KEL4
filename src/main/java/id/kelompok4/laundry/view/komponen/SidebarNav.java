package id.kelompok4.laundry.view.komponen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * SidebarNav
 * Panel navigasi kiri terpadu dengan penanda menu aktif yang jelas,
 * logo sederhana berdesain bersih, dan efek hover halus.
 */
public class SidebarNav extends JPanel {

    private final List<TombolMenu> daftarMenu = new ArrayList<>();
    private TombolMenu menuAktif;

    public SidebarNav(String namaAplikasi, String subPeran) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(TemaWarna.AMALFI_TILE);
        setPreferredSize(new Dimension(236, 700));
        setBorder(new EmptyBorder(24, 14, 20, 14));

        // Area Brand
        JPanel pnlBrand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlBrand.setOpaque(false);
        pnlBrand.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBrand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JLabel ikonBrand = new JLabel(LogoLaundry.buatIkon(36, 36));
        ikonBrand.setPreferredSize(new Dimension(36, 36));

        JPanel pnlBrandTeks = new JPanel();
        pnlBrandTeks.setLayout(new BoxLayout(pnlBrandTeks, BoxLayout.Y_AXIS));
        pnlBrandTeks.setOpaque(false);

        JLabel lblBrand = new JLabel(namaAplikasi);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBrand.setForeground(TemaWarna.PUTIH);

        JLabel lblSub = new JLabel(subPeran);
        lblSub.setFont(TemaWarna.FONT_KECIL);
        lblSub.setForeground(TemaWarna.SEA_BREEZE);

        pnlBrandTeks.add(lblBrand);
        pnlBrandTeks.add(lblSub);

        pnlBrand.add(ikonBrand);
        pnlBrand.add(pnlBrandTeks);

        add(pnlBrand);
        add(Box.createVerticalStrut(24));

        // Garis pemisah tipis
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        sep.setBackground(new Color(255, 255, 255, 40));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        add(sep);
        add(Box.createVerticalStrut(16));
    }

    public void tambahMenu(String label, Runnable aksiKlik) {
        TombolMenu btn = new TombolMenu(label);
        btn.addActionListener(e -> {
            setAktif(btn);
            if (aksiKlik != null) {
                aksiKlik.run();
            }
        });

        daftarMenu.add(btn);
        add(btn);
        add(Box.createVerticalStrut(6));

        if (menuAktif == null) {
            setAktif(btn);
        }
    }

    public void tambahSpacerBawah() {
        add(Box.createVerticalGlue());
        JLabel lblFooter = new JLabel("RapiWash LMS • 2026");
        lblFooter.setFont(TemaWarna.FONT_BADGE);
        lblFooter.setForeground(new Color(255, 255, 255, 130));
        lblFooter.setBorder(new EmptyBorder(8, 10, 0, 10));
        lblFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblFooter);
    }

    private void setAktif(TombolMenu target) {
        this.menuAktif = target;
        for (TombolMenu m : daftarMenu) {
            m.setAktif(m == target);
        }
    }

    private static class TombolMenu extends JButton {
        private boolean aktif = false;
        private boolean hover = false;

        public TombolMenu(String teks) {
            super(teks);
            setFont(TemaWarna.FONT_BOLD);
            setForeground(new Color(255, 255, 255, 210));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 16, 10, 14));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setAlignmentX(Component.LEFT_ALIGNMENT);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        public void setAktif(boolean aktif) {
            this.aktif = aktif;
            setForeground(aktif ? TemaWarna.PUTIH : new Color(255, 255, 255, 200));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (aktif) {
                g2.setColor(new Color(255, 255, 255, 38));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // Garis aksen kiri
                g2.setColor(TemaWarna.CITRUS_ZEST);
                g2.fillRoundRect(0, 8, 4, getHeight() - 16, 4, 4);
            } else if (hover) {
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
