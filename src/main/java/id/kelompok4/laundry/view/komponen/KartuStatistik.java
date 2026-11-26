package id.kelompok4.laundry.view.komponen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Fitur: Ringkasan Statistik
// OOP: Inheritance - KartuStatistik mewarisi PanelKartu.
public class KartuStatistik extends PanelKartu {

    private final JLabel lblNilai;
    private final JLabel lblJudul;
    private final Color aksen;

    public KartuStatistik(String judul, String nilaiAwal, Color warnaAksen) {
        super(new BorderLayout(0, 6), 16, 18);
        this.aksen = warnaAksen != null ? warnaAksen : TemaWarna.AMALFI_TILE;

        JPanel pnlAtas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlAtas.setOpaque(false);

        JPanel titik = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(aksen);
                g2.fillOval(0, 2, 10, 10);
                g2.dispose();
            }
        };
        titik.setOpaque(false);
        titik.setPreferredSize(new Dimension(10, 14));

        lblJudul = new JLabel(judul);
        lblJudul.setFont(TemaWarna.FONT_KECIL_BOLD);
        lblJudul.setForeground(TemaWarna.ABU_TEKS);

        pnlAtas.add(titik);
        pnlAtas.add(lblJudul);
        add(pnlAtas, BorderLayout.NORTH);

        // Fitur: Lihat Nilai Statistik
        lblNilai = new JLabel(nilaiAwal);
        lblNilai.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNilai.setForeground(TemaWarna.TEKS_GELAP);
        lblNilai.setBorder(new EmptyBorder(4, 0, 0, 0));
        add(lblNilai, BorderLayout.CENTER);
    }

    public void setNilai(String nilai) {
        lblNilai.setText(nilai);
    }

    public void setNilai(int nilai) {
        lblNilai.setText(String.valueOf(nilai));
    }
}
