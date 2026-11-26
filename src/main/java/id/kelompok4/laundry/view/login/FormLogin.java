package id.kelompok4.laundry.view.login;

import id.kelompok4.laundry.model.Pengguna;
import id.kelompok4.laundry.service.AutentikasiService;
import id.kelompok4.laundry.view.admin.DashboardAdminFrame;
import id.kelompok4.laundry.view.komponen.FieldKataSandi;
import id.kelompok4.laundry.view.komponen.LogoLaundry;
import id.kelompok4.laundry.view.komponen.TemaWarna;
import id.kelompok4.laundry.view.komponen.TombolKustom;
import id.kelompok4.laundry.view.pelanggan.DashboardPelangganFrame;
import id.kelompok4.laundry.view.petugas.DashboardPetugasFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * FormLogin
 * Layar masuk utama aplikasi RapiWash Laundry Management System.
 */
public class FormLogin extends JFrame {

    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final TombolKustom btnLogin = TombolKustom.buatPrimer("Masuk");
    private final JButton btnDaftar = new JButton("Belum punya akun? Daftar sebagai pelanggan");

    private final AutentikasiService authService = new AutentikasiService();

    public FormLogin() {
        super("RapiWash — Masuk");
        setSize(880, 540);
        setMinimumSize(new Dimension(820, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(LogoLaundry.ambilGambar());

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new GridBagLayout());

        // ==========================================
        // PANEL KIRI: VISUAL BRAND & KEUNGGULAN
        // ==========================================
        JPanel pnlKiri = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Dekorasi gelembung lembut di latar
                g2.setColor(new Color(255, 255, 255, 16));
                g2.fillOval(-60, getHeight() - 180, 260, 260);
                g2.fillOval(getWidth() - 120, -50, 200, 200);
                g2.setColor(new Color(255, 166, 43, 35));
                g2.fillOval(getWidth() - 90, getHeight() - 90, 60, 60);
                g2.dispose();
            }
        };
        pnlKiri.setBackground(TemaWarna.AMALFI_TILE);
        pnlKiri.setBorder(new EmptyBorder(40, 40, 32, 40));

        // Logo & Nama Brand
        JPanel pnlBrand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlBrand.setOpaque(false);

        JLabel ikon = new JLabel(LogoLaundry.buatIkon(40, 40));
        ikon.setPreferredSize(new Dimension(40, 40));

        JLabel lblLogo = new JLabel("RapiWash");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(TemaWarna.PUTIH);

        pnlBrand.add(ikon);
        pnlBrand.add(lblLogo);

        // Konten Tengah Kiri
        JPanel pnlHero = new JPanel();
        pnlHero.setLayout(new BoxLayout(pnlHero, BoxLayout.Y_AXIS));
        pnlHero.setOpaque(false);
        pnlHero.setBorder(new EmptyBorder(32, 0, 0, 0));

        JLabel lblHeadline = new JLabel("<html><body style='width:300px;'>Clean Clothes,<br>Better Days.</body></html>");
        lblHeadline.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeadline.setForeground(TemaWarna.PUTIH);
        lblHeadline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubHero = new JLabel("<html><body style='width:300px; line-height:1.45;'>Aplikasi manajemen laundry untuk mengelola pelanggan, transaksi, pembayaran, proses pengerjaan, dan pengaduan secara terintegrasi.</body></html>");
        lblSubHero.setFont(TemaWarna.FONT_REGULER);
        lblSubHero.setForeground(new Color(219, 234, 254));
        lblSubHero.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlHero.add(lblHeadline);
        pnlHero.add(Box.createVerticalStrut(12));
        pnlHero.add(lblSubHero);
        pnlHero.add(Box.createVerticalStrut(24));

        pnlHero.add(buatPoinKeunggulan("Status Cucian Real-Time", "Cek kapan pakaian dicuci hingga siap diambil."));
        pnlHero.add(Box.createVerticalStrut(12));
        pnlHero.add(buatPoinKeunggulan("Struk Digital Resmi", "Rincian berat, tarif, dan pembayaran tercatat rapi."));
        pnlHero.add(Box.createVerticalStrut(12));
        pnlHero.add(buatPoinKeunggulan("Layanan Komplain Cepat", "Pakaian bermasalah langsung ditangani tim gerai."));

        JLabel lblFooter = new JLabel("© 2026 RapiWash Laundry Management System");
        lblFooter.setFont(TemaWarna.FONT_KECIL);
        lblFooter.setForeground(new Color(191, 219, 254));

        pnlKiri.add(pnlBrand, BorderLayout.NORTH);
        pnlKiri.add(pnlHero, BorderLayout.CENTER);
        pnlKiri.add(lblFooter, BorderLayout.SOUTH);

        // ==========================================
        // PANEL KANAN: FORM LOGIN BERSIH
        // ==========================================
        JPanel pnlKanan = new JPanel(new GridBagLayout());
        pnlKanan.setBackground(TemaWarna.PUTIH);
        pnlKanan.setBorder(new EmptyBorder(36, 40, 36, 40));

        JPanel pnlForm = new JPanel();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setOpaque(false);
        pnlForm.setPreferredSize(new Dimension(360, 380));
        pnlForm.setMaximumSize(new Dimension(440, 380));

        JLabel lblMasuk = new JLabel("Masuk ke Akun");
        lblMasuk.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblMasuk.setForeground(TemaWarna.TEKS_GELAP);
        lblMasuk.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblInstruksi = new JLabel("Gunakan username atau nomor HP yang terdaftar.");
        lblInstruksi.setFont(TemaWarna.FONT_REGULER);
        lblInstruksi.setForeground(TemaWarna.ABU_TEKS);
        lblInstruksi.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlForm.add(lblMasuk);
        pnlForm.add(Box.createVerticalStrut(6));
        pnlForm.add(lblInstruksi);
        pnlForm.add(Box.createVerticalStrut(28));

        // Field Username / No. HP
        JLabel lblUser = new JLabel("Username atau No. HP");
        lblUser.setFont(TemaWarna.FONT_BOLD);
        lblUser.setForeground(TemaWarna.TEKS_GELAP);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername.setFont(TemaWarna.FONT_REGULER);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUsername.setPreferredSize(new Dimension(360, 40));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        TemaWarna.aturPlaceholder(txtUsername, "Contoh: budi01 atau 0812xxxx");

        pnlForm.add(lblUser);
        pnlForm.add(Box.createVerticalStrut(6));
        pnlForm.add(txtUsername);
        pnlForm.add(Box.createVerticalStrut(16));

        // Field Password
        JLabel lblPass = new JLabel("Kata Sandi");
        lblPass.setFont(TemaWarna.FONT_BOLD);
        lblPass.setForeground(TemaWarna.TEKS_GELAP);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword.setFont(TemaWarna.FONT_REGULER);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setPreferredSize(new Dimension(360, 40));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        TemaWarna.aturPlaceholder(txtPassword, "Masukkan kata sandi");

        JPanel pnlPassword = new FieldKataSandi(txtPassword);
        pnlPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnlPassword.setPreferredSize(new Dimension(360, 40));
        pnlPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlForm.add(lblPass);
        pnlForm.add(Box.createVerticalStrut(6));
        pnlForm.add(pnlPassword);
        pnlForm.add(Box.createVerticalStrut(24));

        // Tombol Login
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setPreferredSize(new Dimension(360, 42));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> prosesLogin());

        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    prosesLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);

        pnlForm.add(btnLogin);
        pnlForm.add(Box.createVerticalStrut(16));

        // Tombol Registrasi Pelanggan
        btnDaftar.setFont(TemaWarna.FONT_BOLD);
        btnDaftar.setForeground(TemaWarna.AMALFI_TILE);
        btnDaftar.setContentAreaFilled(false);
        btnDaftar.setBorderPainted(false);
        btnDaftar.setBorder(new EmptyBorder(4, 0, 4, 0));
        btnDaftar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDaftar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDaftar.addActionListener(e -> {
            FormRegistrasiPelanggan reg = new FormRegistrasiPelanggan(this);
            reg.setVisible(true);
        });

        pnlForm.add(btnDaftar);

        GridBagConstraints formConstraints = new GridBagConstraints();
        formConstraints.fill = GridBagConstraints.HORIZONTAL;
        formConstraints.weightx = 1.0;
        pnlKanan.add(pnlForm, formConstraints);

        GridBagConstraints kiriConstraints = new GridBagConstraints();
        kiriConstraints.gridx = 0;
        kiriConstraints.gridy = 0;
        kiriConstraints.weightx = 0.48;
        kiriConstraints.weighty = 1.0;
        kiriConstraints.fill = GridBagConstraints.BOTH;
        root.add(pnlKiri, kiriConstraints);

        GridBagConstraints kananConstraints = new GridBagConstraints();
        kananConstraints.gridx = 1;
        kananConstraints.gridy = 0;
        kananConstraints.weightx = 0.52;
        kananConstraints.weighty = 1.0;
        kananConstraints.fill = GridBagConstraints.BOTH;
        root.add(pnlKanan, kananConstraints);
        setContentPane(root);
    }

    private JPanel buatPoinKeunggulan(String judul, String ket) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(320, 44));

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TemaWarna.CITRUS_ZEST);
                g2.fillOval(0, 4, 8, 8);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(10, 16));

        JPanel teks = new JPanel();
        teks.setLayout(new BoxLayout(teks, BoxLayout.Y_AXIS));
        teks.setOpaque(false);

        JLabel lJudul = new JLabel(judul);
        lJudul.setFont(TemaWarna.FONT_BOLD);
        lJudul.setForeground(TemaWarna.PUTIH);

        JLabel lKet = new JLabel(ket);
        lKet.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lKet.setForeground(new Color(219, 234, 254));

        teks.add(lJudul);
        teks.add(lKet);

        p.add(dot, BorderLayout.WEST);
        p.add(teks, BorderLayout.CENTER);
        return p;
    }

    private void prosesLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try {
            Pengguna pengguna = authService.login(username, password);

            String peran = pengguna.getPeran();
            if ("ADMIN".equalsIgnoreCase(peran)) {
                new DashboardAdminFrame().setVisible(true);
            } else if ("PETUGAS".equalsIgnoreCase(peran)) {
                new DashboardPetugasFrame().setVisible(true);
            } else if ("PELANGGAN".equalsIgnoreCase(peran)) {
                new DashboardPelangganFrame().setVisible(true);
            } else {
                throw new IllegalStateException("Peran akun tidak dikenali: " + peran);
            }

            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Masuk", JOptionPane.ERROR_MESSAGE);
        }
    }
}
