package id.kelompok4.laundry.view.pelanggan;

import id.kelompok4.laundry.dao.TransaksiDAO;
import id.kelompok4.laundry.model.Pelanggan;
import id.kelompok4.laundry.model.Pengaduan;
import id.kelompok4.laundry.model.PenyelesaianPengaduan;
import id.kelompok4.laundry.model.TransaksiLaundry;
import id.kelompok4.laundry.service.PengaduanService;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.util.SessionManager;
import id.kelompok4.laundry.view.komponen.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Fitur: Ajukan dan Tanggapi Pengaduan
// OOP: Inheritance - PanelPengaduanPelanggan mewarisi JPanel.
public class PanelPengaduanPelanggan extends JPanel {

    private final PengaduanService pengaduanService = new PengaduanService();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    private final DefaultTableModel modelPengaduan = new DefaultTableModel(
        new String[]{"ID", "No. Komplain", "No. Transaksi", "Masalah", "Pakaian", "Tgl Lapor", "Status"}, 0
    );
    private final TabelKustom tabelPengaduan = new TabelKustom(modelPengaduan);

    public PanelPengaduanPelanggan() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        muatPengaduan();
    }

    private void initUI() {
        PanelKartu kartu = new PanelKartu(new BorderLayout(0, 16), 20);

        JPanel pnlAtas = new JPanel(new BorderLayout());
        pnlAtas.setOpaque(false);
        pnlAtas.add(TemaWarna.buatJudulBagian("Riwayat Pengaduan Anda"), BorderLayout.WEST);

        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlToolbar.setOpaque(false);

        TombolKustom btnTambah = TombolKustom.buatAksiUtama("+ Buat Pengaduan");
        btnTambah.addActionListener(e -> dialogBuatPengaduan());

        TombolKustom btnRespon = TombolKustom.buatPrimer("Tanggapi Solusi");
        btnRespon.addActionListener(e -> dialogTanggapiSolusi());

        TombolKustom btnRefresh = TombolKustom.buatSekunder("Muat Ulang");
        btnRefresh.addActionListener(e -> muatPengaduan());

        pnlToolbar.add(btnTambah);
        pnlToolbar.add(btnRespon);
        pnlToolbar.add(btnRefresh);
        pnlAtas.add(pnlToolbar, BorderLayout.EAST);

        kartu.add(pnlAtas, BorderLayout.NORTH);
        tabelPengaduan.sembunyikanKolom(0);
        tabelPengaduan.getColumnModel().getColumn(6).setCellRenderer(new BadgeStatusRenderer());
        kartu.add(TemaWarna.bungkusTabel(tabelPengaduan), BorderLayout.CENTER);

        add(kartu, BorderLayout.CENTER);
    }

    // Fitur: Muat Pengaduan
    public void muatPengaduan() {
        modelPengaduan.setRowCount(0);
        if (!(SessionManager.getPenggunaAktif() instanceof Pelanggan)) return;

        Pelanggan p = (Pelanggan) SessionManager.getPenggunaAktif();
        if (p.getIdPelanggan() == null) return;

        try {
            List<Pengaduan> list = pengaduanService.ambilPengaduanPelanggan(p.getIdPelanggan());
            for (Pengaduan item : list) {
                String statusTampilan = item.getStatusPengaduan();
                if ("MENUNGGU_PERSETUJUAN".equalsIgnoreCase(statusTampilan)
                    || "MENUNGGU_PERSETUJUAN_PELANGGAN".equalsIgnoreCase(statusTampilan)) {
                    statusTampilan = "DIVERIFIKASI";
                }

                modelPengaduan.addRow(new Object[]{
                    item.getIdPengaduan(),
                    item.getNomorPengaduan(),
                    item.getNomorTransaksi(),
                    item.getJenisPengaduan(),
                    item.getDeskripsiPakaian(),
                    FormatUtil.formatTanggalWaktu(item.getTanggalLapor()),
                    statusTampilan
                });
            }
        } catch (Exception e) {
            System.err.println("[PanelPengaduanPelanggan] Gagal muat komplain: " + e.getMessage());
        }
    }

    // Fitur: Dialog Buat Pengaduan
    private void dialogBuatPengaduan() {
        Pelanggan pel = (Pelanggan) SessionManager.getPenggunaAktif();
        try {
            List<TransaksiLaundry> daftarTrx = transaksiDAO.ambilTransaksiPelanggan(pel.getIdPelanggan());
            if (daftarTrx.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Anda belum memiliki pesanan untuk dilaporkan.", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<TransaksiLaundry> cmbTrx = new JComboBox<>();
            for (TransaksiLaundry t : daftarTrx) {
                if (!"MENUNGGU_PEMBAYARAN".equalsIgnoreCase(t.getStatusTransaksi())
                    && !"DIBATALKAN".equalsIgnoreCase(t.getStatusTransaksi())) {
                    cmbTrx.addItem(t);
                }
            }
            cmbTrx.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof TransaksiLaundry) {
                        TransaksiLaundry transaksi = (TransaksiLaundry) value;
                        setText(transaksi.getNomorTransaksi());
                    }
                    return this;
                }
            });

            if (cmbTrx.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "Belum ada pesanan yang sudah dibayar untuk dilaporkan.", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<String> cmbJenis = new JComboBox<>(new String[]{"HILANG", "RUSAK", "TERTUKAR"});
            JTextField txtPakaian = new JTextField(18);
            JTextField txtJumlah = new JTextField("1", 5);
            JTextArea txtKronologi = new JTextArea(4, 18);
            txtKronologi.setLineWrap(true);
            txtKronologi.setWrapStyleWord(true);
            TemaWarna.aturPlaceholder(txtPakaian, "Contoh: Kemeja putih lengan panjang");

            JPanel pnl = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(6, 6, 6, 6);

            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
            pnl.add(new JLabel("Pesanan"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.65;
            cmbTrx.setPreferredSize(new Dimension(240, 34));
            pnl.add(cmbTrx, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            pnl.add(new JLabel("Jenis Masalah"), gbc);
            gbc.gridx = 1;
            cmbJenis.setPreferredSize(new Dimension(240, 34));
            pnl.add(cmbJenis, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            pnl.add(new JLabel("Ciri Pakaian *"), gbc);
            gbc.gridx = 1;
            txtPakaian.setPreferredSize(new Dimension(240, 34));
            pnl.add(txtPakaian, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            pnl.add(new JLabel("Jumlah (pcs)"), gbc);
            gbc.gridx = 1;
            txtJumlah.setPreferredSize(new Dimension(240, 34));
            pnl.add(txtJumlah, gbc);

            gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.NORTHWEST;
            pnl.add(new JLabel("Ceritakan Kejadian *"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.CENTER;
            JScrollPane scrollKronologi = new JScrollPane(txtKronologi);
            scrollKronologi.setPreferredSize(new Dimension(240, 80));
            pnl.add(scrollKronologi, gbc);

            int ok = JOptionPane.showConfirmDialog(this, pnl, "Buat Pengaduan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok == JOptionPane.OK_OPTION) {
                TransaksiLaundry trxTerpilih = (TransaksiLaundry) cmbTrx.getSelectedItem();
                String jenis = (String) cmbJenis.getSelectedItem();
                String desc = txtPakaian.getText().trim();
                String kronologi = txtKronologi.getText().trim();
                int jml = Integer.parseInt(txtJumlah.getText().trim());

                if (desc.isEmpty() || kronologi.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ciri pakaian dan kronologi wajib diisi.", "Data Belum Lengkap", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                pengaduanService.ajukanPengaduan(
                    trxTerpilih.getIdTransaksi(),
                    pel.getIdPelanggan(),
                    pel.getIdPengguna(),
                    jenis, desc, jml, kronologi
                );

                JOptionPane.showMessageDialog(this,
                    "Pengaduan berhasil dikirim.\nTim kami akan segera memeriksa cucian Anda.",
                    "Terkirim",
                    JOptionPane.INFORMATION_MESSAGE
                );
                muatPengaduan();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengirim pengaduan: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dialogTanggapiSolusi() {
        int baris = tabelPengaduan.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pengaduan pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idPengaduan = (Long) modelPengaduan.getValueAt(baris, 0);
        try {
            PenyelesaianPengaduan solusi = pengaduanService.cariPenyelesaian(idPengaduan);
            if (solusi == null) {
                JOptionPane.showMessageDialog(this, "Belum ada solusi yang dikirim oleh pemilik gerai.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (!"MENUNGGU".equalsIgnoreCase(solusi.getStatusPersetujuan())) {
                JOptionPane.showMessageDialog(this,
                    "Belum ada solusi baru yang perlu ditanggapi. Pemilik gerai akan mengirimkan solusi berikutnya jika diperlukan.",
                    "Tidak Ada Tanggapan Baru",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            String pesan = String.format(
                "Solusi dari Pemilik Gerai:\n\n" +
                "Bentuk Solusi  : %s\n" +
                "Penjelasan     : %s\n" +
                "Ganti Rugi     : %s\n" +
                "Status         : %s\n\n" +
                "Apakah Anda setuju dengan solusi di atas?",
                solusi.getJenisPenyelesaian().replace("_", " "),
                solusi.getAlasanPenyelesaian(),
                FormatUtil.formatRupiah(solusi.getNomorDisepakati()),
                solusi.getStatusPersetujuan()
            );

            int opsi = JOptionPane.showConfirmDialog(this, pesan, "Tanggapi Solusi", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opsi == JOptionPane.YES_OPTION) {
                pengaduanService.responPelanggan(solusi.getIdPenyelesaian(), idPengaduan, true);
                JOptionPane.showMessageDialog(this, "Anda menyetujui solusi ini. Tim kami akan segera menindaklanjuti.", "Disetujui", JOptionPane.INFORMATION_MESSAGE);
                muatPengaduan();
            } else if (opsi == JOptionPane.NO_OPTION) {
                pengaduanService.responPelanggan(solusi.getIdPenyelesaian(), idPengaduan, false);
                JOptionPane.showMessageDialog(this,
                    "Anda menolak usulan ini. Pengaduan dikirim kembali ke pemilik gerai untuk ditindaklanjuti.",
                    "Solusi Ditolak",
                    JOptionPane.INFORMATION_MESSAGE
                );
                muatPengaduan();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat solusi: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }
}
