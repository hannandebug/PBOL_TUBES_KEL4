package id.kelompok4.laundry.view.petugas;

import id.kelompok4.laundry.dao.LayananDAO;
import id.kelompok4.laundry.dao.PaketDAO;
import id.kelompok4.laundry.dao.PelangganDAO;
import id.kelompok4.laundry.dao.TarifDAO;
import id.kelompok4.laundry.model.*;
import id.kelompok4.laundry.service.TransaksiService;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.util.SessionManager;
import id.kelompok4.laundry.view.komponen.PanelKartu;
import id.kelompok4.laundry.view.komponen.TemaWarna;
import id.kelompok4.laundry.view.komponen.TombolKustom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * PanelTransaksiBaru
 * Formulir pencatatan pesanan cucian baru di meja kasir.
 */
public class PanelTransaksiBaru extends JPanel {

    private final PelangganDAO pelangganDAO = new PelangganDAO();
    private final LayananDAO layananDAO = new LayananDAO();
    private final PaketDAO paketDAO = new PaketDAO();
    private final TarifDAO tarifDAO = new TarifDAO();
    private final TransaksiService transaksiService = new TransaksiService();

    private final JTextField txtCariPelanggan = new JTextField(15);
    private final JComboBox<Pelanggan> cmbPelanggan = new JComboBox<>();
    private final JComboBox<JenisLayanan> cmbLayanan = new JComboBox<>();
    private final JComboBox<PaketLaundry> cmbPaket = new JComboBox<>();

    private final JTextField txtBeratAktual = new JTextField("0.00", 10);
    private final JCheckBox chkSetujuMinimum = new JCheckBox("Pelanggan setuju aturan minimal berat 2 kg");
    private final JTextArea txtCatatan = new JTextArea(3, 20);

    private final JLabel lblTarifPerKg = new JLabel("Rp0 / kg");
    private final JLabel lblBeratTagihan = new JLabel("0.00 kg");
    private final JLabel lblTotalBiaya = new JLabel("Rp0");

    private BigDecimal tarifAktif = BigDecimal.ZERO;

    public PanelTransaksiBaru() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        muatDataMaster();
    }

    private void initUI() {
        JPanel rootForm = new JPanel(new GridLayout(1, 2, 20, 0));
        rootForm.setOpaque(false);

        // Kartu Kiri: Data Pesanan
        PanelKartu pnlKiri = new PanelKartu(new BorderLayout(0, 14), 22);
        pnlKiri.add(TemaWarna.buatJudulBagian("Data Cucian Pelanggan"), BorderLayout.NORTH);

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        int row = 0;

        // Cari Pelanggan
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        formFields.add(buatLabel("Cari Pelanggan"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        JPanel pnlCari = new JPanel(new BorderLayout(8, 0));
        pnlCari.setOpaque(false);
        TemaWarna.aturPlaceholder(txtCariPelanggan, "Ketik nama atau no. HP...");
        txtCariPelanggan.setPreferredSize(new Dimension(150, 36));
        pnlCari.add(txtCariPelanggan, BorderLayout.CENTER);
        TombolKustom btnCari = TombolKustom.buatSekunder("Cari");
        btnCari.addActionListener(e -> cariPelanggan());
        pnlCari.add(btnCari, BorderLayout.EAST);
        formFields.add(pnlCari, gbc);
        row++;

        // Pilih Pelanggan
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        formFields.add(buatLabel("Pelanggan *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        cmbPelanggan.setPreferredSize(new Dimension(200, 36));
        formFields.add(cmbPelanggan, gbc);
        row++;

        // Layanan
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        formFields.add(buatLabel("Jenis Layanan *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        cmbLayanan.setPreferredSize(new Dimension(200, 36));
        cmbLayanan.addActionListener(e -> updateTarifDanHitung());
        formFields.add(cmbLayanan, gbc);
        row++;

        // Paket
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        formFields.add(buatLabel("Paket *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        cmbPaket.setPreferredSize(new Dimension(200, 36));
        cmbPaket.addActionListener(e -> updateTarifDanHitung());
        formFields.add(cmbPaket, gbc);
        row++;

        // Berat
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        formFields.add(buatLabel("Berat Timbang (kg) *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtBeratAktual.setFont(TemaWarna.FONT_BOLD);
        txtBeratAktual.setPreferredSize(new Dimension(200, 36));
        txtBeratAktual.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateTarifDanHitung(); }
            public void removeUpdate(DocumentEvent e) { updateTarifDanHitung(); }
            public void changedUpdate(DocumentEvent e) { updateTarifDanHitung(); }
        });
        formFields.add(txtBeratAktual, gbc);
        row++;

        // Checkbox Minimum
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        chkSetujuMinimum.setFont(TemaWarna.FONT_KECIL);
        chkSetujuMinimum.setForeground(TemaWarna.TEKS_GELAP);
        chkSetujuMinimum.setOpaque(false);
        chkSetujuMinimum.setEnabled(false);
        chkSetujuMinimum.addActionListener(e -> updateTarifDanHitung());
        formFields.add(chkSetujuMinimum, gbc);
        gbc.gridwidth = 1;
        row++;

        // Catatan
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblCat = buatLabel("Catatan");
        lblCat.setBorder(new EmptyBorder(6, 0, 0, 0));
        formFields.add(lblCat, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65; gbc.anchor = GridBagConstraints.CENTER;
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        TemaWarna.aturPlaceholder(txtCatatan, "Misal: pisahkan baju putih, ada kancing lepas...");
        JScrollPane scrollCat = new JScrollPane(txtCatatan);
        scrollCat.setPreferredSize(new Dimension(200, 74));
        formFields.add(scrollCat, gbc);

        pnlKiri.add(formFields, BorderLayout.CENTER);
        rootForm.add(pnlKiri);

        // Kartu Kanan: Ringkasan Biaya
        PanelKartu pnlKanan = new PanelKartu(new BorderLayout(0, 16), 24);
        pnlKanan.add(TemaWarna.buatJudulBagian("Ringkasan Biaya"), BorderLayout.NORTH);

        JPanel pnlRincian = new JPanel();
        pnlRincian.setLayout(new BoxLayout(pnlRincian, BoxLayout.Y_AXIS));
        pnlRincian.setOpaque(false);

        lblTarifPerKg.setFont(TemaWarna.FONT_BOLD);
        lblTarifPerKg.setForeground(TemaWarna.TEKS_GELAP);
        lblBeratTagihan.setFont(TemaWarna.FONT_BOLD);
        lblBeratTagihan.setForeground(TemaWarna.TEKS_GELAP);

        pnlRincian.add(buatBarisRincian("Harga per Kg", lblTarifPerKg));
        pnlRincian.add(Box.createVerticalStrut(12));
        pnlRincian.add(buatBarisRincian("Berat Tagihan", lblBeratTagihan));
        pnlRincian.add(Box.createVerticalStrut(16));

        JSeparator sep = new JSeparator();
        sep.setForeground(TemaWarna.ABU_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnlRincian.add(sep);
        pnlRincian.add(Box.createVerticalStrut(16));

        lblTotalBiaya.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTotalBiaya.setForeground(TemaWarna.AMALFI_TILE);
        pnlRincian.add(buatBarisRincian("Total Bayar", lblTotalBiaya));
        pnlRincian.add(Box.createVerticalStrut(24));

        // Info kotak lembut
        JPanel pnlInfo = new JPanel(new BorderLayout());
        pnlInfo.setBackground(TemaWarna.LATAR_BELAKANG);
        pnlInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TemaWarna.ABU_BORDER, 1),
            new EmptyBorder(12, 14, 12, 14)
        ));
        pnlInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        JLabel lblInfoMin = new JLabel("<html><body style='color:#64748B; font-size:11px; line-height:1.4;'>" +
                                       "• Minimal tagihan cucian adalah <b>2 kg</b>.<br>" +
                                       "• Pembayaran dilakukan di awal sebelum cucian diproses." +
                                       "</body></html>");
        pnlInfo.add(lblInfoMin, BorderLayout.CENTER);
        pnlRincian.add(pnlInfo);

        pnlKanan.add(pnlRincian, BorderLayout.CENTER);

        TombolKustom btnSimpanTrx = TombolKustom.buatAksiUtama("Buat Pesanan");
        btnSimpanTrx.setPreferredSize(new Dimension(200, 44));
        btnSimpanTrx.addActionListener(e -> prosesSimpanTransaksi());
        pnlKanan.add(btnSimpanTrx, BorderLayout.SOUTH);

        rootForm.add(pnlKanan);
        add(rootForm, BorderLayout.CENTER);
    }

    private JLabel buatLabel(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(TemaWarna.FONT_BOLD);
        l.setForeground(TemaWarna.TEKS_GELAP);
        return l;
    }

    private JPanel buatBarisRincian(String label, JLabel nilai) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JLabel lbl = new JLabel(label);
        lbl.setFont(TemaWarna.FONT_REGULER);
        lbl.setForeground(TemaWarna.ABU_TEKS);
        p.add(lbl, BorderLayout.WEST);
        nilai.setHorizontalAlignment(SwingConstants.RIGHT);
        p.add(nilai, BorderLayout.EAST);
        return p;
    }

    public void muatDataMaster() {
        try {
            cmbLayanan.removeAllItems();
            for (JenisLayanan l : layananDAO.ambilSemuaAktif()) {
                cmbLayanan.addItem(l);
            }

            cmbPaket.removeAllItems();
            for (PaketLaundry p : paketDAO.ambilSemuaAktif()) {
                cmbPaket.addItem(p);
            }

            cariPelanggan();
            updateTarifDanHitung();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data pelanggan, layanan, atau tarif: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cariPelanggan() {
        try {
            String kw = txtCariPelanggan.getText();
            List<Pelanggan> list = pelangganDAO.cariPelanggan(kw);
            cmbPelanggan.removeAllItems();
            for (Pelanggan p : list) {
                cmbPelanggan.addItem(p);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari pelanggan: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTarifDanHitung() {
        JenisLayanan jl = (JenisLayanan) cmbLayanan.getSelectedItem();
        PaketLaundry pl = (PaketLaundry) cmbPaket.getSelectedItem();

        String teksBerat = txtBeratAktual.getText().trim().replace(",", ".");
        BigDecimal beratAktual;
        try {
            if (teksBerat.isEmpty()) throw new NumberFormatException();
            beratAktual = new BigDecimal(teksBerat);
        } catch (NumberFormatException e) {
            tampilkanBeratTidakValid("Masukkan berat yang valid");
            return;
        }
        if (beratAktual.compareTo(BigDecimal.ZERO) <= 0) {
            tampilkanBeratTidakValid("Berat harus lebih dari 0 kg");
            return;
        }
        if (beratAktual.stripTrailingZeros().scale() > 2) {
            tampilkanBeratTidakValid("Maksimal 2 angka desimal");
            return;
        }

        boolean diBawahMinimum = beratAktual.compareTo(TransaksiService.MINIMUM_BERAT_KG) < 0;
        chkSetujuMinimum.setEnabled(diBawahMinimum);
        if (!diBawahMinimum) chkSetujuMinimum.setSelected(false);
        lblBeratTagihan.setText(FormatUtil.formatBerat(
            diBawahMinimum ? TransaksiService.MINIMUM_BERAT_KG : beratAktual
        ) + (diBawahMinimum ? " (Min. 2 kg)" : ""));

        if (jl == null || pl == null) {
            tarifAktif = BigDecimal.ZERO;
            lblTarifPerKg.setText("Tarif belum diatur");
            lblTotalBiaya.setText(FormatUtil.formatRupiah(BigDecimal.ZERO));
            return;
        }

        try {
            TarifLayanan t = tarifDAO.cariTarif(jl.getIdJenisLayanan(), pl.getIdPaket());
            if (t != null && t.isStatusAktif()) {
                tarifAktif = t.getHargaPerKg();
                lblTarifPerKg.setText(FormatUtil.formatRupiah(tarifAktif) + " / kg");
            } else {
                tarifAktif = BigDecimal.ZERO;
                lblTarifPerKg.setText("Tarif belum diatur");
            }

            BigDecimal total = transaksiService.hitungBiaya(beratAktual, tarifAktif);
            lblTotalBiaya.setText(FormatUtil.formatRupiah(total));
        } catch (Exception e) {
            System.err.println("[PanelTransaksiBaru] Gagal hitung: " + e.getMessage());
            tarifAktif = BigDecimal.ZERO;
            lblTarifPerKg.setText("Tarif gagal dimuat");
            lblTotalBiaya.setText(FormatUtil.formatRupiah(BigDecimal.ZERO));
        }
    }

    private void tampilkanBeratTidakValid(String pesan) {
        lblBeratTagihan.setText(pesan);
        lblTotalBiaya.setText(FormatUtil.formatRupiah(BigDecimal.ZERO));
        chkSetujuMinimum.setEnabled(false);
        chkSetujuMinimum.setSelected(false);
    }

    private void prosesSimpanTransaksi() {
        Pelanggan pel = (Pelanggan) cmbPelanggan.getSelectedItem();
        if (pel == null) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan terlebih dahulu.", "Pilih Pelanggan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JenisLayanan jl = (JenisLayanan) cmbLayanan.getSelectedItem();
        PaketLaundry pl = (PaketLaundry) cmbPaket.getSelectedItem();
        if (jl == null || pl == null) {
            JOptionPane.showMessageDialog(this, "Pilih layanan dan paket yang memiliki tarif aktif.", "Data Layanan Belum Tersedia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String teksBerat = txtBeratAktual.getText().trim().replace(",", ".");
        BigDecimal berat;
        try {
            berat = new BigDecimal(teksBerat);
            if (berat.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Masukkan berat cucian yang valid (lebih dari 0 kg).", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (berat.stripTrailingZeros().scale() > 2) {
            JOptionPane.showMessageDialog(this, "Berat cucian maksimal menggunakan 2 angka desimal.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean setuju = chkSetujuMinimum.isSelected();
        if (berat.compareTo(TransaksiService.MINIMUM_BERAT_KG) < 0 && !setuju) {
            int tanya = JOptionPane.showConfirmDialog(
                this,
                "Berat timbang di bawah 2 kg (" + FormatUtil.formatBerat(berat) + ").\n" +
                "Biaya akan dihitung minimal 2 kg. Apakah pelanggan setuju?",
                "Konfirmasi Berat Minimal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (tanya == JOptionPane.YES_OPTION) {
                setuju = true;
                chkSetujuMinimum.setSelected(true);
            } else {
                return;
            }
        }

        try {
            TransaksiLaundry trx = transaksiService.buatTransaksiBaru(
                pel.getIdPelanggan(),
                SessionManager.getIdPenggunaAktif(),
                jl.getIdJenisLayanan(),
                pl.getIdPaket(),
                berat,
                setuju,
                txtCatatan.getText().trim()
            );

            JOptionPane.showMessageDialog(this,
                "Pesanan berhasil dicatat.\n" +
                "No. Transaksi : " + trx.getNomorTransaksi() + "\n" +
                "Total Tagihan : " + FormatUtil.formatRupiah(trx.getTotalBiaya()) + "\n\n" +
                "Lanjut ke menu 'Pembayaran' untuk memproses pelunasan.",
                "Pesanan Tercatat",
                JOptionPane.INFORMATION_MESSAGE
            );

            txtBeratAktual.setText("0.00");
            chkSetujuMinimum.setSelected(false);
            txtCatatan.setText("");
            updateTarifDanHitung();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal membuat pesanan: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }
}
