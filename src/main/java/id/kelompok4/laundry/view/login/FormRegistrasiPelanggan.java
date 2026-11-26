package id.kelompok4.laundry.view.login;

import id.kelompok4.laundry.service.AutentikasiService;
import id.kelompok4.laundry.view.komponen.FieldKataSandi;
import id.kelompok4.laundry.view.komponen.TemaWarna;
import id.kelompok4.laundry.view.komponen.TombolKustom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * FormRegistrasiPelanggan
 * Formulir pendaftaran mandiri khusus Pelanggan baru.
 */
public class FormRegistrasiPelanggan extends JDialog {

    private final JTextField txtNamaLengkap = new JTextField(20);
    private final JTextField txtUsername = new JTextField(20);
    private final JTextField txtNoTelp = new JTextField(20);
    private final JTextArea txtAlamat = new JTextArea(4, 20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JPasswordField txtKonfirmasi = new JPasswordField(20);

    private final AutentikasiService authService = new AutentikasiService();

    public FormRegistrasiPelanggan(Frame parent) {
        super(parent, "Daftar Akun Pelanggan", true);
        setSize(500, 610);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(TemaWarna.PUTIH);

        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout(0, 4));
        pnlHeader.setBackground(TemaWarna.AMALFI_TILE);
        pnlHeader.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel lblJudul = new JLabel("Buat Akun Pelanggan");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblJudul.setForeground(TemaWarna.PUTIH);

        JLabel lblSub = new JLabel("Daftar agar Anda bisa memantau status cucian dan struk.");
        lblSub.setFont(TemaWarna.FONT_KECIL);
        lblSub.setForeground(new Color(219, 234, 254));

        pnlHeader.add(lblJudul, BorderLayout.NORTH);
        pnlHeader.add(lblSub, BorderLayout.SOUTH);
        content.add(pnlHeader, BorderLayout.NORTH);

        // Form Fields
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(TemaWarna.PUTIH);
        pnlForm.setBorder(new EmptyBorder(20, 24, 16, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        TemaWarna.aturPlaceholder(txtNamaLengkap, "Contoh: Budi Santoso");
        TemaWarna.aturPlaceholder(txtUsername, "Minimal 4 huruf/angka");
        TemaWarna.aturPlaceholder(txtNoTelp, "Contoh: 081234567890");
        TemaWarna.aturPlaceholder(txtPassword, "Minimal 6 karakter");
        TemaWarna.aturPlaceholder(txtKonfirmasi, "Ketik ulang kata sandi");

        int baris = 0;
        tambahBaris(pnlForm, gbc, baris++, "Nama Lengkap *", txtNamaLengkap);
        tambahBaris(pnlForm, gbc, baris++, "Username *", txtUsername);
        tambahBaris(pnlForm, gbc, baris++, "No. HP / WhatsApp *", txtNoTelp);

        // Alamat
        gbc.gridx = 0; gbc.gridy = baris; gbc.weightx = 0.35; gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblAlamat = new JLabel("Alamat");
        lblAlamat.setFont(TemaWarna.FONT_BOLD);
        lblAlamat.setForeground(TemaWarna.TEKS_GELAP);
        lblAlamat.setBorder(new EmptyBorder(6, 0, 0, 0));
        pnlForm.add(lblAlamat, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65; gbc.anchor = GridBagConstraints.CENTER;
        txtAlamat.setFont(TemaWarna.FONT_REGULER);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        scrollAlamat.setPreferredSize(new Dimension(220, 100));
        pnlForm.add(scrollAlamat, gbc);
        baris++;

        tambahBaris(pnlForm, gbc, baris++, "Kata Sandi *", new FieldKataSandi(txtPassword));
        tambahBaris(pnlForm, gbc, baris++, "Ulangi Sandi *", new FieldKataSandi(txtKonfirmasi));

        content.add(pnlForm, BorderLayout.CENTER);

        // Tombol Aksi
        JPanel pnlBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        pnlBawah.setBackground(TemaWarna.LATAR_BELAKANG);
        pnlBawah.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, TemaWarna.ABU_BORDER));

        TombolKustom btnBatal = TombolKustom.buatSekunder("Batal");
        btnBatal.addActionListener(e -> dispose());

        TombolKustom btnDaftar = TombolKustom.buatAksiUtama("Daftar Sekarang");
        btnDaftar.addActionListener(e -> prosesDaftar());

        pnlBawah.add(btnBatal);
        pnlBawah.add(btnDaftar);
        content.add(pnlBawah, BorderLayout.SOUTH);

        setContentPane(content);
    }

    private void tambahBaris(JPanel pnl, GridBagConstraints gbc, int y, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0.35;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(TemaWarna.FONT_BOLD);
        lbl.setForeground(TemaWarna.TEKS_GELAP);
        pnl.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        if (field instanceof JTextField) {
            field.setFont(TemaWarna.FONT_REGULER);
        }
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 36));
        pnl.add(field, gbc);
    }

    private void prosesDaftar() {
        String nama = txtNamaLengkap.getText();
        String user = txtUsername.getText();
        String telp = txtNoTelp.getText();
        String alamat = txtAlamat.getText();
        String pass = new String(txtPassword.getPassword());
        String konf = new String(txtKonfirmasi.getPassword());

        if (nama.trim().isEmpty() || user.trim().isEmpty() || telp.trim().isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi nama, username, nomor HP, dan kata sandi.", "Data Belum Lengkap", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(konf)) {
            JOptionPane.showMessageDialog(this, "Kata sandi dan ulangi sandi tidak sama.", "Cek Kata Sandi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean sukses = authService.registrasiPelanggan(nama, user, pass, telp, alamat);
            if (sukses) {
                JOptionPane.showMessageDialog(this,
                    "Akun berhasil dibuat!\nSilakan masuk menggunakan username atau nomor HP Anda.",
                    "Pendaftaran Berhasil",
                    JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Pendaftaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}
