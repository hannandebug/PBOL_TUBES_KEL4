package id.kelompok4.laundry.view.admin;

import id.kelompok4.laundry.dao.PenggunaDAO;
import id.kelompok4.laundry.model.Pengaduan;
import id.kelompok4.laundry.model.Petugas;
import id.kelompok4.laundry.service.PengaduanService;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.util.SessionManager;
import id.kelompok4.laundry.view.komponen.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * PanelKelolaPengaduanAdmin
 * Penanganan pengaduan pelanggan oleh pemilik gerai.
 */
public class PanelKelolaPengaduanAdmin extends JPanel {

    private final PengaduanService pengaduanService = new PengaduanService();
    private final PenggunaDAO penggunaDAO = new PenggunaDAO();

    private final DefaultTableModel modelPengaduan = new DefaultTableModel(
        new String[]{"ID", "No. Komplain", "No. Transaksi", "Pelanggan", "Masalah", "Tgl Lapor", "Tanggung Jawab", "Status"}, 0
    );
    private final TabelKustom tabelPengaduan = new TabelKustom(modelPengaduan);

    public PanelKelolaPengaduanAdmin() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        muatData();
    }

    private void initUI() {
        PanelKartu kartu = new PanelKartu(new BorderLayout(0, 16), 20);

        JPanel pnlAtas = new JPanel(new BorderLayout(0, 10));
        pnlAtas.setOpaque(false);

        pnlAtas.add(TemaWarna.buatJudulBagian("Daftar Pengaduan Pelanggan"), BorderLayout.NORTH);

        JPanel pnlToolbar = new JPanel(new GridLayout(2, 3, 8, 8));
        pnlToolbar.setOpaque(false);

        TombolKustom btnDetail = TombolKustom.buatSekunder("Detail");
        btnDetail.addActionListener(e -> lihatDetail());

        TombolKustom btnTugaskan = TombolKustom.buatAksiUtama("Tugaskan Kasir");
        btnTugaskan.addActionListener(e -> dialogTugaskanPenelusuran());

        TombolKustom btnPutusan = TombolKustom.buatPrimer("Tentukan Tanggung Jawab");
        btnPutusan.addActionListener(e -> dialogPutusanTanggungJawab());

        TombolKustom btnSolusi = TombolKustom.buatSukses("Beri Solusi");
        btnSolusi.addActionListener(e -> dialogUsulkanPenyelesaian());

        TombolKustom btnSelesaikan = TombolKustom.buatSukses("Tandai Selesai");
        btnSelesaikan.addActionListener(e -> dialogTandaiPenyelesaianSelesai());

        TombolKustom btnRefresh = TombolKustom.buatSekunder("Muat Ulang");
        btnRefresh.addActionListener(e -> muatData());

        pnlToolbar.add(btnDetail);
        pnlToolbar.add(btnTugaskan);
        pnlToolbar.add(btnPutusan);
        pnlToolbar.add(btnSolusi);
        pnlToolbar.add(btnSelesaikan);
        pnlToolbar.add(btnRefresh);

        pnlAtas.add(pnlToolbar, BorderLayout.CENTER);
        kartu.add(pnlAtas, BorderLayout.NORTH);

        tabelPengaduan.sembunyikanKolom(0);
        int[] lebarMinimumKolom = {0, 150, 150, 135, 80, 145, 175, 110};
        int[] lebarAwalKolom = {0, 180, 180, 185, 110, 180, 230, 145};
        for (int kolom = 1; kolom < lebarMinimumKolom.length; kolom++) {
            tabelPengaduan.getColumnModel().getColumn(kolom).setMinWidth(lebarMinimumKolom[kolom]);
            tabelPengaduan.getColumnModel().getColumn(kolom).setPreferredWidth(lebarAwalKolom[kolom]);
        }
        tabelPengaduan.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabelPengaduan.getColumnModel().getColumn(6).setCellRenderer(new BadgeStatusRenderer());
        tabelPengaduan.getColumnModel().getColumn(7).setCellRenderer(new BadgeStatusRenderer());
        JScrollPane scrollTabel = TemaWarna.bungkusTabel(tabelPengaduan);
        scrollTabel.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                aturUkuranTabel(scrollTabel);
            }
        });
        kartu.add(scrollTabel, BorderLayout.CENTER);

        add(kartu, BorderLayout.CENTER);
    }

    private void aturUkuranTabel(JScrollPane scrollTabel) {
        int lebarViewport = scrollTabel.getViewport().getWidth();
        if (lebarViewport <= 0) {
            return;
        }

        int lebarMinimum = 0;
        for (int kolom = 1; kolom < tabelPengaduan.getColumnModel().getColumnCount(); kolom++) {
            lebarMinimum += tabelPengaduan.getColumnModel().getColumn(kolom).getMinWidth();
        }

        boolean perluGulir = lebarViewport < lebarMinimum;
        int modeUbahUkuran = perluGulir ? JTable.AUTO_RESIZE_OFF : JTable.AUTO_RESIZE_ALL_COLUMNS;
        if (tabelPengaduan.getAutoResizeMode() == modeUbahUkuran) {
            return;
        }

        tabelPengaduan.setAutoResizeMode(modeUbahUkuran);
        if (perluGulir) {
            for (int kolom = 1; kolom < tabelPengaduan.getColumnModel().getColumnCount(); kolom++) {
                tabelPengaduan.getColumnModel().getColumn(kolom).setWidth(
                    tabelPengaduan.getColumnModel().getColumn(kolom).getPreferredWidth()
                );
            }
        }
        tabelPengaduan.revalidate();
        scrollTabel.revalidate();
    }

    public void muatData() {
        modelPengaduan.setRowCount(0);
        try {
            List<Pengaduan> list = pengaduanService.ambilSemuaPengaduan();
            for (Pengaduan p : list) {
                modelPengaduan.addRow(new Object[]{
                    p.getIdPengaduan(),
                    p.getNomorPengaduan(),
                    p.getNomorTransaksi(),
                    p.getNamaPelanggan(),
                    p.getJenisPengaduan(),
                    FormatUtil.formatTanggalWaktu(p.getTanggalLapor()),
                    p.getStatusTanggungJawab(),
                    p.getStatusPengaduan()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data pengaduan: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Pengaduan getPengaduanTerpilih() {
        int baris = tabelPengaduan.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu pengaduan pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Long id = (Long) modelPengaduan.getValueAt(baris, 0);
        try {
            for (Pengaduan p : pengaduanService.ambilSemuaPengaduan()) {
                if (p.getIdPengaduan().equals(id)) return p;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void lihatDetail() {
        Pengaduan p = getPengaduanTerpilih();
        if (p == null) return;

        String pesan = String.format(
            "No. Komplain     : %s\n" +
            "No. Transaksi    : %s\n" +
            "Pelanggan        : %s (%s)\n" +
            "Masalah          : %s (%d pcs)\n" +
            "Barang           : %s\n\n" +
            "Kronologi Kejadian:\n%s\n\n" +
            "Hasil Pengecekan Kasir:\n%s\n",
            p.getNomorPengaduan(), p.getNomorTransaksi(), p.getNamaPelanggan(),
            p.getNomorTeleponPelanggan() != null ? p.getNomorTeleponPelanggan() : "-",
            p.getJenisPengaduan(), p.getJumlahPakaian(), p.getDeskripsiPakaian(),
            p.getUraianKejadian(),
            p.getHasilPenelusuran() != null ? p.getHasilPenelusuran() : "(Belum ada catatan pengecekan)"
        );

        JTextArea ta = new JTextArea(pesan, 13, 38);
        ta.setFont(TemaWarna.FONT_REGULER);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Rincian Pengaduan", JOptionPane.PLAIN_MESSAGE);
    }

    private void dialogTugaskanPenelusuran() {
        Pengaduan p = getPengaduanTerpilih();
        if (p == null) return;

        try {
            List<Petugas> petugasList = penggunaDAO.ambilSemuaPetugas();
            if (petugasList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Belum ada petugas aktif untuk ditugaskan.", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<Petugas> cmbPetugas = new JComboBox<>(petugasList.toArray(new Petugas[0]));
            JTextField txtBatasJam = new JTextField("24", 5);

            JPanel pnl = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(6, 6, 6, 6);

            gbc.gridx = 0; gbc.gridy = 0;
            pnl.add(new JLabel("Pilih Kasir / Petugas"), gbc);
            gbc.gridx = 1;
            cmbPetugas.setPreferredSize(new Dimension(200, 34));
            pnl.add(cmbPetugas, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            pnl.add(new JLabel("Batas Waktu Cek (Jam)"), gbc);
            gbc.gridx = 1;
            txtBatasJam.setPreferredSize(new Dimension(200, 34));
            pnl.add(txtBatasJam, gbc);

            int ok = JOptionPane.showConfirmDialog(this, pnl, "Tugaskan Pengecekan ke Kasir", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok == JOptionPane.OK_OPTION) {
                Petugas pet = (Petugas) cmbPetugas.getSelectedItem();
                int jam = Integer.parseInt(txtBatasJam.getText().trim());
                Timestamp deadline = Timestamp.valueOf(LocalDateTime.now().plusHours(jam));

                pengaduanService.tugaskanPenelusuran(p.getIdPengaduan(), SessionManager.getIdPenggunaAktif(), pet.getIdPengguna(), deadline);
                JOptionPane.showMessageDialog(this, "Tugas pengecekan berhasil dikirim ke " + pet.getNamaLengkap() + ".", "Terkirim", JOptionPane.INFORMATION_MESSAGE);
                muatData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menugaskan: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dialogPutusanTanggungJawab() {
        Pengaduan p = getPengaduanTerpilih();
        if (p == null) return;

        String[] opsiTanggungJawab = {
            "BUKAN_TANGGUNG_JAWAB_LAUNDRY",
            "TANGGUNG_JAWAB_PENUH",
            "TANGGUNG_JAWAB_SEBAGIAN"
        };
        JComboBox<String> cmbTj = new JComboBox<>(opsiTanggungJawab);
        cmbTj.setSelectedItem(p.getStatusTanggungJawab());

        JPanel pnl = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0;
        pnl.add(new JLabel("Tanggung Jawab"), gbc);
        gbc.gridx = 1;
        cmbTj.setPreferredSize(new Dimension(250, 34));
        pnl.add(cmbTj, gbc);

        int ok = JOptionPane.showConfirmDialog(this, pnl, "Tentukan Tanggung Jawab", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                String tanggungJawab = (String) cmbTj.getSelectedItem();
                String statusPengaduan = "BUKAN_TANGGUNG_JAWAB_LAUNDRY".equals(tanggungJawab)
                    ? "SELESAI"
                    : "DIVERIFIKASI";
                boolean sukses = pengaduanService.tetapkanTanggungJawab(
                    p.getIdPengaduan(),
                    tanggungJawab,
                    statusPengaduan,
                    SessionManager.getIdPenggunaAktif()
                );

                if (sukses) {
                    JOptionPane.showMessageDialog(this, "Keputusan berhasil disimpan.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    muatData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan keputusan: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dialogUsulkanPenyelesaian() {
        Pengaduan p = getPengaduanTerpilih();
        if (p == null) return;

        if (!"DIVERIFIKASI".equalsIgnoreCase(p.getStatusPengaduan())
            && !"SOLUSI_DITOLAK".equalsIgnoreCase(p.getStatusPengaduan())) {
            JOptionPane.showMessageDialog(this,
                "Solusi hanya dapat diberikan setelah hasil pemeriksaan tersedia atau pelanggan menolak solusi sebelumnya.",
                "Belum Siap Diberi Solusi",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String tanggungJawab = p.getStatusTanggungJawab();
        if (!"TANGGUNG_JAWAB_PENUH".equalsIgnoreCase(tanggungJawab)
            && !"TANGGUNG_JAWAB_SEBAGIAN".equalsIgnoreCase(tanggungJawab)
            && !"TANGGUNG_JAWAB_LAUNDRY".equalsIgnoreCase(tanggungJawab)) {
            JOptionPane.showMessageDialog(this,
                "Tetapkan tanggung jawab laundry terlebih dahulu sebelum memberikan solusi.",
                "Tanggung Jawab Belum Ditetapkan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            var sebelumnya = pengaduanService.cariPenyelesaian(p.getIdPengaduan());
            if (sebelumnya != null && !"DITOLAK_PELANGGAN".equalsIgnoreCase(sebelumnya.getStatusPersetujuan())) {
                JOptionPane.showMessageDialog(this,
                    "Pengaduan ini masih memiliki solusi yang menunggu tanggapan atau sudah disetujui pelanggan.",
                    "Solusi Sudah Dikirim",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memeriksa solusi sebelumnya: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] opsi = {
            "PAKAIAN_DITEMUKAN", "CUCI_ULANG_GRATIS", "SETRIKA_ULANG_GRATIS",
            "PENGEMBALIAN_UANG", "PENGGANTIAN_BARANG", "KOMPENSASI_TUNAI"
        };
        JComboBox<String> cmbOpsi = new JComboBox<>(opsi);
        JTextField txtAlasan = new JTextField(20);
        JTextField txtNominal = new JTextField("0", 15);
        TemaWarna.aturPlaceholder(txtAlasan, "Contoh: Pakaian luntur saat dicuci");

        JPanel pnl = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0;
        pnl.add(new JLabel("Bentuk Solusi"), gbc);
        gbc.gridx = 1;
        cmbOpsi.setPreferredSize(new Dimension(230, 34));
        pnl.add(cmbOpsi, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnl.add(new JLabel("Penjelasan ke Pelanggan"), gbc);
        gbc.gridx = 1;
        txtAlasan.setPreferredSize(new Dimension(230, 34));
        pnl.add(txtAlasan, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pnl.add(new JLabel("Nominal Ganti Rugi (Rp)"), gbc);
        gbc.gridx = 1;
        txtNominal.setPreferredSize(new Dimension(230, 34));
        pnl.add(txtNominal, gbc);

        int ok = JOptionPane.showConfirmDialog(this, pnl, "Beri Solusi ke Pelanggan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                String jenis = (String) cmbOpsi.getSelectedItem();
                String alasan = txtAlasan.getText().trim();
                BigDecimal nominal = new BigDecimal(txtNominal.getText().trim());

                if (alasan.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Penjelasan ke pelanggan wajib diisi.", "Data Belum Lengkap", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                pengaduanService.ajukanPenyelesaian(
                    p.getIdPengaduan(), p.getIdPelanggan(), jenis, alasan, nominal, SessionManager.getIdPenggunaAktif(), null
                );

                JOptionPane.showMessageDialog(this,
                    "Solusi berhasil dikirim ke pelanggan.\nPelanggan bisa menyetujui atau menolaknya dari akun masing-masing.",
                    "Solusi Terkirim",
                    JOptionPane.INFORMATION_MESSAGE
                );
                muatData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengirim solusi: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dialogTandaiPenyelesaianSelesai() {
        Pengaduan p = getPengaduanTerpilih();
        if (p == null) return;

        if (!"DALAM_PENYELESAIAN".equalsIgnoreCase(p.getStatusPengaduan())) {
            JOptionPane.showMessageDialog(this,
                "Pengaduan hanya dapat ditandai selesai setelah pelanggan menyetujui solusi.",
                "Belum Dapat Diselesaikan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            var solusi = pengaduanService.cariPenyelesaian(p.getIdPengaduan());
            if (solusi == null || !"DISETUJUI_PELANGGAN".equalsIgnoreCase(solusi.getStatusPersetujuan())) {
                JOptionPane.showMessageDialog(this,
                    "Belum ada solusi yang disetujui pelanggan untuk pengaduan ini.",
                    "Belum Dapat Diselesaikan",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            if ("SELESAI".equalsIgnoreCase(solusi.getStatusPelaksanaan())) {
                JOptionPane.showMessageDialog(this, "Solusi untuk pengaduan ini sudah ditandai selesai.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String pesan = "Pastikan solusi berikut benar-benar sudah dilaksanakan:\n\n"
                + solusi.getJenisPenyelesaian().replace("_", " ") + "\n"
                + solusi.getAlasanPenyelesaian() + "\n\n"
                + "Tandai pengaduan sebagai selesai?";
            int konfirmasi = JOptionPane.showConfirmDialog(
                this, pesan, "Konfirmasi Penyelesaian", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );
            if (konfirmasi != JOptionPane.YES_OPTION) return;

            pengaduanService.tandaiPenyelesaianSelesai(
                p.getIdPengaduan(), SessionManager.getIdPenggunaAktif()
            );
            JOptionPane.showMessageDialog(this,
                "Solusi dan pengaduan berhasil ditandai selesai.",
                "Berhasil",
                JOptionPane.INFORMATION_MESSAGE
            );
            muatData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal menyelesaikan pengaduan: " + ex.getMessage(),
                "Kesalahan",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
