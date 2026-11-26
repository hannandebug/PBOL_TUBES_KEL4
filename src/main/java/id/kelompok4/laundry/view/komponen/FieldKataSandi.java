package id.kelompok4.laundry.view.komponen;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Field kata sandi dengan tombol mata untuk mengubah visibilitas teks.
 */
public class FieldKataSandi extends JPanel {

    public FieldKataSandi(JPasswordField field) {
        super(new BorderLayout());
        setOpaque(false);
        setFont(TemaWarna.FONT_REGULER);
        field.setFont(TemaWarna.FONT_REGULER);

        char echoChar = field.getEchoChar();
        JButton tombolVisibilitas = new JButton(new IkonMata(false));
        tombolVisibilitas.setToolTipText("Tampilkan kata sandi");
        tombolVisibilitas.getAccessibleContext().setAccessibleName("Tampilkan kata sandi");
        tombolVisibilitas.setPreferredSize(new Dimension(34, field.getPreferredSize().height));
        tombolVisibilitas.setMargin(new Insets(0, 4, 0, 4));
        tombolVisibilitas.setBorderPainted(false);
        tombolVisibilitas.setContentAreaFilled(false);
        tombolVisibilitas.setFocusPainted(false);
        tombolVisibilitas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tombolVisibilitas.addActionListener(e -> {
            boolean tampilkan = field.getEchoChar() != 0;
            field.setEchoChar(tampilkan ? (char) 0 : echoChar);

            String aksi = tampilkan ? "Sembunyikan kata sandi" : "Tampilkan kata sandi";
            tombolVisibilitas.setIcon(new IkonMata(tampilkan));
            tombolVisibilitas.setToolTipText(aksi);
            tombolVisibilitas.getAccessibleContext().setAccessibleName(aksi);
        });

        add(field, BorderLayout.CENTER);
        add(tombolVisibilitas, BorderLayout.EAST);
    }

    private static final class IkonMata implements Icon {
        private final boolean terlihat;

        private IkonMata(boolean terlihat) {
            this.terlihat = terlihat;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color warna = UIManager.getColor("TextField.foreground");
            g2.setColor(warna != null ? warna : TemaWarna.ABU_TEKS);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Path2D mata = new Path2D.Float();
            mata.moveTo(1, 8);
            mata.quadTo(8, 1, 15, 8);
            mata.quadTo(8, 15, 1, 8);
            g2.draw(mata);
            g2.drawOval(6, 6, 4, 4);
            g2.fillOval(7, 7, 2, 2);

            if (!terlihat) {
                g2.drawLine(3, 13, 13, 3);
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}
