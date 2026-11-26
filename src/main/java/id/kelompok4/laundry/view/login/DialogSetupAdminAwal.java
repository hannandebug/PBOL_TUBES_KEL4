package id.kelompok4.laundry.view.login;

import id.kelompok4.laundry.service.AutentikasiService;
import id.kelompok4.laundry.view.komponen.FieldKataSandi;
import id.kelompok4.laundry.view.komponen.TemaWarna;
import id.kelompok4.laundry.view.komponen.TombolKustom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * DialogSetupAdminAwal
 * Ditampilkan secara otomatis saat aplikasi pertama kali dijalankan
 * jika tabel pengguna belum memiliki akun Admin sama sekali.
 */
public class DialogSetupAdminAwal extends JDialog {

    private final JTextField txtNamaLengkap = new JTextField(20);
    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JPasswordField txtKonfirmasi = new JPasswordField(20);
    private final JTextField txtNoTelp = new JTextField(20);

    private final AutentikasiService authService = new AutentikasiService();
    private boolean berhasil = false;

    public DialogSetupAdminAwal(Frame parent) {
        super(parent, "Setup Akun Admin / Pemilik Pertama", true);
        setSize(480, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(TemaWarna.PUTIH);

        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(TemaWarna.AMALFI_TILE);
        pnlHeader.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel lblJudul = new JLabel("Inisialisasi Sistem Laundry");
        lblJudul.setFont(TemaWarna.FONT_TITLE);
        lblJudul.setForeground(TemaWarna.PUTIH);

        JLabel lblSub = new JLabel("Buat akun Admin / Pemilik pertama untuk memulai operasional.");
        lblSub.setFont(TemaWarna.FONT_KECIL);
        lblSub.setForeground(TemaWarna.SEA_BREEZE);

        pnlHeader.add(lblJudul, BorderLayout.NORTH);
        pnlHeader.add(lblSub, BorderLayout.SOUTH);
        content.add(pnlHeader, BorderLayout.NORTH);

        // Form Fields
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(TemaWarna.PUTIH);
        pnlForm.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        int baris = 0;
        tambahBarisForm(pnlForm, gbc, baris++, "Nama Lengkap Pemilik *", txtNamaLengkap);
        tambahBarisForm(pnlForm, gbc, baris++, "Username Admin *", txtUsername);
        tambahBarisForm(pnlForm, gbc, baris++, "Nomor Telepon", txtNoTelp);
        tambahBarisForm(pnlForm, gbc, baris++, "Kata Sandi * (min 6 karakter)", new FieldKataSandi(txtPassword));
        tambahBarisForm(pnlForm, gbc, baris++, "Ulangi Kata Sandi *", new FieldKataSandi(txtKonfirmasi));

        content.add(pnlForm, BorderLayout.CENTER);

        // Tombol Aksi
        JPanel pnlBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        pnlBawah.setBackground(TemaWarna.LATAR_BELAKANG);
        pnlBawah.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, TemaWarna.ABU_BORDER));

        TombolKustom btnSimpan = TombolKustom.buatAksiUtama("Buat Akun Admin");
        btnSimpan.addActionListener(e -> prosesSimpan());

        pnlBawah.add(btnSimpan);
        content.add(pnlBawah, BorderLayout.SOUTH);

        setContentPane(content);
    }

    private void tambahBarisForm(JPanel pnl, GridBagConstraints gbc, int y, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(TemaWarna.FONT_BOLD);
        lbl.setForeground(TemaWarna.TEKS_GELAP);
        pnl.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        field.setFont(TemaWarna.FONT_REGULER);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 32));
        pnl.add(field, gbc);
    }

    private void prosesSimpan() {
        String nama = txtNamaLengkap.getText();
        String user = txtUsername.getText();
        String telp = txtNoTelp.getText();
        String pass = new String(txtPassword.getPassword());
        String konf = new String(txtKonfirmasi.getPassword());

        if (nama.trim().isEmpty() || user.trim().isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seluruh field bertanda bintang (*) wajib diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(konf)) {
            JOptionPane.showMessageDialog(this, "Konfirmasi kata sandi tidak cocok.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean ok = authService.setupAdminPertama(nama, user, pass, telp);
            if (ok) {
                berhasil = true;
                JOptionPane.showMessageDialog(this, "Akun Admin pertama berhasil dibuat!\nSilakan login menggunakan username dan password tersebut.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal membuat akun: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isBerhasil() {
        return berhasil;
    }
}
