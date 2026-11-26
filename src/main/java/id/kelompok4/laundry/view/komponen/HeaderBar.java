package id.kelompok4.laundry.view.komponen;

import id.kelompok4.laundry.model.Pengguna;
import id.kelompok4.laundry.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Fitur: Navigasi dan Sesi Pengguna
// OOP: Inheritance - HeaderBar mewarisi JPanel.
public class HeaderBar extends JPanel {

    private final JLabel lblJudulLayar;

    public HeaderBar(String judulAwal, Runnable onLogout) {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.PUTIH);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, TemaWarna.ABU_BORDER),
            new EmptyBorder(14, 24, 14, 24)
        ));

        lblJudulLayar = new JLabel(judulAwal);
        lblJudulLayar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblJudulLayar.setForeground(TemaWarna.TEKS_GELAP);
        add(lblJudulLayar, BorderLayout.WEST);

        JPanel panelKanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        panelKanan.setOpaque(false);

        String tanggal = LocalDate.now().format(
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("id-ID"))
        );
        JLabel lblTanggal = new JLabel(tanggal);
        lblTanggal.setFont(TemaWarna.FONT_KECIL);
        lblTanggal.setForeground(TemaWarna.ABU_TEKS);
        panelKanan.add(lblTanggal);

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 22));
        sep.setForeground(TemaWarna.ABU_BORDER);
        panelKanan.add(sep);

        Pengguna p = SessionManager.getPenggunaAktif();
        String nama = p != null ? p.getNamaLengkap() : "Pengguna";
        String peran = p != null ? p.getNamaPeran() : "-";
        String inisial = ambilInisial(nama);

        JLabel lblAvatar = new JLabel(inisial, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xDB, 0xEA, 0xFE));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblAvatar.setFont(TemaWarna.FONT_KECIL_BOLD);
        lblAvatar.setForeground(TemaWarna.AMALFI_TILE);
        lblAvatar.setPreferredSize(new Dimension(32, 32));
        panelKanan.add(lblAvatar);

        JPanel pnlUserInfo = new JPanel();
        pnlUserInfo.setLayout(new BoxLayout(pnlUserInfo, BoxLayout.Y_AXIS));
        pnlUserInfo.setOpaque(false);

        JLabel lblUser = new JLabel(nama);
        lblUser.setFont(TemaWarna.FONT_BOLD);
        lblUser.setForeground(TemaWarna.TEKS_GELAP);

        JLabel lblPeran = new JLabel(peran);
        lblPeran.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPeran.setForeground(TemaWarna.ABU_TEKS);

        pnlUserInfo.add(lblUser);
        pnlUserInfo.add(lblPeran);
        panelKanan.add(pnlUserInfo);

        // Fitur: Logout Pengguna
        TombolKustom btnLogout = TombolKustom.buatSekunder("Keluar");
        btnLogout.setFont(TemaWarna.FONT_KECIL_BOLD);
        btnLogout.setBorder(new EmptyBorder(7, 14, 7, 14));
        btnLogout.addActionListener(e -> {
            int tanya = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Keluar dari akun ini?",
                "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (tanya == JOptionPane.YES_OPTION) {
                SessionManager.logout();
                if (onLogout != null) {
                    onLogout.run();
                }
            }
        });
        panelKanan.add(btnLogout);

        add(panelKanan, BorderLayout.EAST);
    }

    private String ambilInisial(String nama) {
        if (nama == null || nama.trim().isEmpty()) return "?";
        String[] bagian = nama.trim().split("\\s+");
        if (bagian.length == 1) {
            return bagian[0].substring(0, Math.min(2, bagian[0].length())).toUpperCase();
        }
        return ("" + bagian[0].charAt(0) + bagian[1].charAt(0)).toUpperCase();
    }

    public void setJudulLayar(String judul) {
        lblJudulLayar.setText(judul);
    }
}
