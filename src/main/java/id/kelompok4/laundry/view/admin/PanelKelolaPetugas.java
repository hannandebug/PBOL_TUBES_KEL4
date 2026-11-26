package id.kelompok4.laundry.view.admin;

import id.kelompok4.laundry.dao.PenggunaDAO;
import id.kelompok4.laundry.model.Petugas;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.util.PasswordUtil;
import id.kelompok4.laundry.view.komponen.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Fitur: Kelola Petugas
// OOP: Inheritance - PanelKelolaPetugas mewarisi JPanel.
public class PanelKelolaPetugas extends JPanel {

    private final PenggunaDAO penggunaDAO = new PenggunaDAO();

    private final DefaultTableModel modelPetugas = new DefaultTableModel(
        new String[]{"ID", "Nama Petugas", "Username", "No. HP", "Status", "Terdaftar"}, 0
    );
    private final TabelKustom tabelPetugas = new TabelKustom(modelPetugas);

    public PanelKelolaPetugas() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        muatDataPetugas();
    }

    private void initUI() {
        PanelKartu kartu = new PanelKartu(new BorderLayout(0, 16), 20);

        // Fitur: Tambah dan Ubah Petugas
        JPanel pnlAtas = new JPanel(new BorderLayout(0, 10));
        pnlAtas.setOpaque(false);

        JLabel lblJudul = TemaWarna.buatJudulBagian("Daftar Petugas & Kasir");
        pnlAtas.add(lblJudul, BorderLayout.NORTH);

        JPanel pnlToolbar = new JPanel(new GridLayout(2, 1, 0, 8));
        pnlToolbar.setOpaque(false);
        JPanel pnlToolbarAtas = new JPanel(new GridLayout(1, 3, 8, 0));
        JPanel pnlToolbarBawah = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlToolbarAtas.setOpaque(false);
        pnlToolbarBawah.setOpaque(false);

        TombolKustom btnTambah = TombolKustom.buatAksiUtama("+ Tambah Petugas");
        btnTambah.addActionListener(e -> dialogTambahPetugas());

        TombolKustom btnUbah = TombolKustom.buatPrimer("Edit Data");
        btnUbah.addActionListener(e -> dialogUbahPetugas());

        TombolKustom btnToggleAktif = TombolKustom.buatSekunder("Aktif / Nonaktif");
        btnToggleAktif.addActionListener(e -> prosesToggleStatus());

        TombolKustom btnResetPass = TombolKustom.buatSekunder("Reset Sandi");
        btnResetPass.addActionListener(e -> prosesResetPassword());

        TombolKustom btnRefresh = TombolKustom.buatSekunder("Muat Ulang");
        btnRefresh.addActionListener(e -> muatDataPetugas());

        pnlToolbarAtas.add(btnTambah);
        pnlToolbarAtas.add(btnUbah);
        pnlToolbarAtas.add(btnToggleAktif);
        pnlToolbarBawah.add(btnResetPass);
        pnlToolbarBawah.add(btnRefresh);
        pnlToolbar.add(pnlToolbarAtas);
        pnlToolbar.add(pnlToolbarBawah);

        pnlAtas.add(pnlToolbar, BorderLayout.CENTER);
        kartu.add(pnlAtas, BorderLayout.NORTH);

        // Fitur: Lihat Daftar Petugas
        tabelPetugas.sembunyikanKolom(0);
        tabelPetugas.getColumnModel().getColumn(4).setCellRenderer(new BadgeStatusRenderer());
        kartu.add(TemaWarna.bungkusTabel(tabelPetugas), BorderLayout.CENTER);

        add(kartu, BorderLayout.CENTER);
    }

    public void muatDataPetugas() {
        modelPetugas.setRowCount(0);
        try {
            List<Petugas> list = penggunaDAO.ambilSemuaPetugas();
            for (Petugas p : list) {
                modelPetugas.addRow(new Object[]{
                    p.getIdPengguna(),
                    p.getNamaLengkap(),
                    p.getNamaPengguna(),
                    p.getNomorTelepon() != null ? p.getNomorTelepon() : "-",
                    p.isStatusAktif() ? "AKTIF" : "NONAKTIF",
                    FormatUtil.formatTanggal(p.getDibuatPada())
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data petugas: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Fitur: Dialog Tambah Petugas
    private void dialogTambahPetugas() {
        JTextField txtNama = new JTextField(18);
        JTextField txtUser = new JTextField(18);
        JTextField txtTelp = new JTextField(18);
        JPasswordField txtPass = new JPasswordField(18);

        TemaWarna.aturPlaceholder(txtNama, "Nama lengkap petugas");
        TemaWarna.aturPlaceholder(txtUser, "Username untuk login");
        TemaWarna.aturPlaceholder(txtTelp, "08xxxxxxxxxx");
        TemaWarna.aturPlaceholder(txtPass, "Minimal 6 karakter");

        JPanel pnlForm = buatFormPanel(
            new String[]{"Nama Lengkap", "Username", "No. HP", "Kata Sandi"},
            new JComponent[]{txtNama, txtUser, txtTelp, new FieldKataSandi(txtPass)}
        );

        int ok = JOptionPane.showConfirmDialog(this, pnlForm, "Tambah Petugas Baru", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            String nama = txtNama.getText().trim();
            String user = txtUser.getText().trim();
            String telp = txtTelp.getText().trim();
            String pass = new String(txtPass.getPassword());

            if (nama.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama, username, dan kata sandi wajib diisi.", "Data Belum Lengkap", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (penggunaDAO.cariBerdasarkanUsername(user) != null) {
                    JOptionPane.showMessageDialog(this, "Username sudah dipakai, silakan gunakan yang lain.", "Username Terpakai", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Petugas pet = new Petugas();
                pet.setNamaLengkap(nama);
                pet.setNamaPengguna(user);
                pet.setKataSandiHash(PasswordUtil.hashPassword(pass));
                pet.setNomorTelepon(telp.isEmpty() ? null : telp);
                pet.setStatusAktif(true);

                penggunaDAO.tambahPetugas(pet);
                JOptionPane.showMessageDialog(this, "Akun petugas berhasil ditambahkan.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                muatDataPetugas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menambah petugas: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Fitur: Dialog Ubah Petugas
    private void dialogUbahPetugas() {
        int baris = tabelPetugas.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih petugas pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idPengguna = (Long) modelPetugas.getValueAt(baris, 0);
        String namaLama = (String) modelPetugas.getValueAt(baris, 1);
        String user = (String) modelPetugas.getValueAt(baris, 2);
        String telpLama = (String) modelPetugas.getValueAt(baris, 3);
        String statusSaatIni = (String) modelPetugas.getValueAt(baris, 4);

        JTextField txtNama = new JTextField(namaLama, 18);
        JTextField txtTelp = new JTextField("-".equals(telpLama) ? "" : telpLama, 18);
        JLabel lblUser = new JLabel(user);
        lblUser.setFont(TemaWarna.FONT_BOLD);
        lblUser.setForeground(TemaWarna.ABU_TEKS);

        JPanel pnl = buatFormPanel(
            new String[]{"Username", "Nama Lengkap", "No. HP"},
            new JComponent[]{lblUser, txtNama, txtTelp}
        );

        int ok = JOptionPane.showConfirmDialog(this, pnl, "Edit Data Petugas", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            String namaBaru = txtNama.getText().trim();
            String telpBaru = txtTelp.getText().trim();

            if (namaBaru.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama lengkap tidak boleh kosong.", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Petugas pet = new Petugas();
                pet.setIdPengguna(idPengguna);
                pet.setNamaLengkap(namaBaru);
                pet.setNomorTelepon(telpBaru.isEmpty() ? null : telpBaru);
                pet.setStatusAktif("AKTIF".equalsIgnoreCase(statusSaatIni));

                if (penggunaDAO.ubahPengguna(pet)) {
                    JOptionPane.showMessageDialog(this, "Data petugas berhasil diperbarui.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    muatDataPetugas();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Fitur: Proses Toggle Status
    private void prosesToggleStatus() {
        int baris = tabelPetugas.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih petugas pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idPengguna = (Long) modelPetugas.getValueAt(baris, 0);
        String nama = (String) modelPetugas.getValueAt(baris, 1);
        String statusSaatIni = (String) modelPetugas.getValueAt(baris, 4);

        boolean aktifBaru = !"AKTIF".equalsIgnoreCase(statusSaatIni);
        int konf = JOptionPane.showConfirmDialog(this,
            String.format("%s akun %s?", aktifBaru ? "Aktifkan" : "Nonaktifkan", nama),
            "Konfirmasi Status",
            JOptionPane.YES_NO_OPTION
        );

        if (konf == JOptionPane.YES_OPTION) {
            try {
                penggunaDAO.setStatusAktif(idPengguna, aktifBaru);
                muatDataPetugas();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah status: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Fitur: Proses Reset Password
    private void prosesResetPassword() {
        int baris = tabelPetugas.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih petugas pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idPengguna = (Long) modelPetugas.getValueAt(baris, 0);
        String nama = (String) modelPetugas.getValueAt(baris, 1);

        JPasswordField txtPassBaru = new JPasswordField(18);
        JPanel pnlForm = buatFormPanel(
            new String[]{"Kata Sandi Baru"},
            new JComponent[]{new FieldKataSandi(txtPassBaru)}
        );

        int ok = JOptionPane.showConfirmDialog(this, pnlForm,
            "Reset Kata Sandi untuk " + nama,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (ok == JOptionPane.OK_OPTION) {
            String passBaru = new String(txtPassBaru.getPassword()).trim();
            if (passBaru.length() < 6) {
                JOptionPane.showMessageDialog(this, "Kata sandi minimal 6 karakter.", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String hash = PasswordUtil.hashPassword(passBaru);
                if (penggunaDAO.ubahKataSandi(idPengguna, hash)) {
                    JOptionPane.showMessageDialog(this, "Kata sandi berhasil diperbarui.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Kata sandi tidak dapat diperbarui karena akun petugas tidak ditemukan.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal mereset sandi: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel buatFormPanel(String[] labels, JComponent[] fields) {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBorder(new EmptyBorder(6, 4, 6, 4));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.35;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(TemaWarna.FONT_BOLD);
            lbl.setForeground(TemaWarna.TEKS_GELAP);
            pnl.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.65;
            fields[i].setPreferredSize(new Dimension(220, 34));
            pnl.add(fields[i], gbc);
        }
        return pnl;
    }
}
