package id.kelompok4.laundry.view.komponen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Fitur: Komponen Kartu Antarmuka
// OOP: Inheritance - PanelKartu mewarisi JPanel.
public class PanelKartu extends JPanel {

    private int sudut = 14;
    private Color warnaLatar = TemaWarna.PUTIH;
    private Color warnaBorder = TemaWarna.ABU_BORDER;

    public PanelKartu() {
        this(new BorderLayout(12, 12), 20);
    }

    public PanelKartu(LayoutManager layout, int padding) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(padding, padding, padding, padding));
    }

    public PanelKartu(LayoutManager layout, int padV, int padH) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(padV, padH, padV, padH));
    }

    public void setWarnaLatar(Color warnaLatar) {
        this.warnaLatar = warnaLatar;
        repaint();
    }

    public void setWarnaBorder(Color warnaBorder) {
        this.warnaBorder = warnaBorder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(warnaLatar);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, sudut, sudut);

        if (warnaBorder != null) {
            g2.setColor(warnaBorder);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, sudut, sudut);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
