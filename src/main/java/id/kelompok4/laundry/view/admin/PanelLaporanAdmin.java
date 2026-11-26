package id.kelompok4.laundry.view.admin;

import id.kelompok4.laundry.dao.PelangganDAO;
import id.kelompok4.laundry.dao.TransaksiDAO;
import id.kelompok4.laundry.model.Pelanggan;
import id.kelompok4.laundry.model.TransaksiLaundry;
import id.kelompok4.laundry.service.LaporanService;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.view.komponen.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * PanelLaporanAdmin
 * Monitoring transaksi, cucian belum diambil, pelanggan, serta arus kas keuangan.
 */
public class PanelLaporanAdmin extends JPanel {

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final PelangganDAO pelangganDAO = new PelangganDAO();
    private final LaporanService laporanService = new LaporanService();

    private final DefaultTableModel modelTransaksi = new DefaultTableModel(
        new String[]{"No. Transaksi", "Pelanggan", "Layanan", "Paket", "Berat", "Total Biaya", "Status Cucian", "Pembayaran", "Tanggal"}, 0
    );
    private final TabelKustom tabelTransaksi = new TabelKustom(modelTransaksi);

    private final DefaultTableModel modelBelumDiambil = new DefaultTableModel(
        new String[]{"No. Transaksi", "Pelanggan", "No. HP", "Layanan", "Paket", "Total Biaya", "Status", "No. Struk"}, 0
    );
    private final TabelKustom tabelBelumDiambil = new TabelKustom(modelBelumDiambil);

    private final DefaultTableModel modelPelanggan = new DefaultTableModel(
        new String[]{"Kode", "Nama Lengkap", "Username", "No. HP", "Alamat"}, 0
    );
    private final TabelKustom tabelPelanggan = new TabelKustom(modelPelanggan);

    private final DefaultTableModel modelKeuangan = new DefaultTableModel(
        new String[]{"Arus Kas", "Waktu", "Keterangan", "Metode", "Nominal", "Kasir / Petugas"}, 0
    );
    private final TabelKustom tabelKeuangan = new TabelKustom(modelKeuangan);

    private final KartuStatistik kKasMasuk = new KartuStatistik("Total Uang Masuk", "Rp0", TemaWarna.HIJAU_SUKSES);
    private final KartuStatistik kKasKeluar = new KartuStatistik("Total Uang Keluar", "Rp0", TemaWarna.MERAH_BAHAYA);
    private final KartuStatistik kSaldoBersih = new KartuStatistik("Saldo Bersih", "Rp0", TemaWarna.AMALFI_TILE);

    private final JComboBox<String> cmbFilterStatus = new JComboBox<>(new String[]{
        "SEMUA", "MENUNGGU_PEMBAYARAN", "DITERIMA", "DIPROSES", "SIAP_DIAMBIL", "SELESAI", "DIBATALKAN"
    });
    private final JTextField txtCari = new JTextField(16);

    public PanelLaporanAdmin() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        muatSemuaData();
    }

    private void initUI() {
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(TemaWarna.FONT_BOLD);

        // Tab 1: Semua Transaksi
        PanelKartu kartu1 = new PanelKartu(new BorderLayout(0, 14), 18);
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlFilter.setOpaque(false);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(TemaWarna.FONT_BOLD);
        pnlFilter.add(lblStatus);
        cmbFilterStatus.setPreferredSize(new Dimension(170, 34));
        pnlFilter.add(cmbFilterStatus);

        TemaWarna.aturPlaceholder(txtCari, "Cari nama atau no. transaksi...");
        txtCari.setPreferredSize(new Dimension(210, 34));
        pnlFilter.add(txtCari);

        TombolKustom btnCari = TombolKustom.buatPrimer("Cari");
        btnCari.addActionListener(e -> muatDataTransaksi());
        pnlFilter.add(btnCari);

        TombolKustom btnRefreshTrx = TombolKustom.buatSekunder("Reset");
        btnRefreshTrx.addActionListener(e -> {
            txtCari.setText("");
            cmbFilterStatus.setSelectedIndex(0);
            muatDataTransaksi();
        });
        pnlFilter.add(btnRefreshTrx);

        kartu1.add(pnlFilter, BorderLayout.NORTH);
        tabelTransaksi.getColumnModel().getColumn(6).setCellRenderer(new BadgeStatusRenderer());
        tabelTransaksi.getColumnModel().getColumn(7).setCellRenderer(new BadgeStatusRenderer());
        kartu1.add(TemaWarna.bungkusTabel(tabelTransaksi), BorderLayout.CENTER);
        tab.addTab("Semua Transaksi", kartu1);

        // Tab 2: Belum Diambil
        PanelKartu kartu2 = new PanelKartu(new BorderLayout(0, 14), 18);
        JPanel pnlAtas2 = new JPanel(new BorderLayout());
        pnlAtas2.setOpaque(false);
        pnlAtas2.add(TemaWarna.buatJudulBagian("Cucian Selesai yang Belum Diambil"), BorderLayout.WEST);
        TombolKustom btnRef2 = TombolKustom.buatSekunder("Muat Ulang");
        btnRef2.addActionListener(e -> muatDataBelumDiambil());
        pnlAtas2.add(btnRef2, BorderLayout.EAST);

        kartu2.add(pnlAtas2, BorderLayout.NORTH);
        tabelBelumDiambil.getColumnModel().getColumn(6).setCellRenderer(new BadgeStatusRenderer());
        kartu2.add(TemaWarna.bungkusTabel(tabelBelumDiambil), BorderLayout.CENTER);
        tab.addTab("Siap Diambil", kartu2);

        // Tab 3: Daftar Pelanggan
        PanelKartu kartu3 = new PanelKartu(new BorderLayout(0, 14), 18);
        JPanel pnlAtas3 = new JPanel(new BorderLayout());
        pnlAtas3.setOpaque(false);
        pnlAtas3.add(TemaWarna.buatJudulBagian("Pelanggan Terdaftar"), BorderLayout.WEST);
        TombolKustom btnRef3 = TombolKustom.buatSekunder("Muat Ulang");
        btnRef3.addActionListener(e -> muatDataPelanggan());
        pnlAtas3.add(btnRef3, BorderLayout.EAST);

        kartu3.add(pnlAtas3, BorderLayout.NORTH);
        kartu3.add(TemaWarna.bungkusTabel(tabelPelanggan), BorderLayout.CENTER);
        tab.addTab("Pelanggan", kartu3);

        // Tab 4: Keuangan & Kas
        JPanel pnlTab4 = new JPanel(new BorderLayout(0, 16));
        pnlTab4.setBackground(TemaWarna.LATAR_BELAKANG);
        pnlTab4.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel pnlRingkasKeuangan = new JPanel(new GridLayout(1, 3, 16, 16));
        pnlRingkasKeuangan.setOpaque(false);
        pnlRingkasKeuangan.add(kKasMasuk);
        pnlRingkasKeuangan.add(kKasKeluar);
        pnlRingkasKeuangan.add(kSaldoBersih);
        pnlTab4.add(pnlRingkasKeuangan, BorderLayout.NORTH);

        PanelKartu kartuKas = new PanelKartu(new BorderLayout(0, 14), 18);
        JPanel pnlAtasKas = new JPanel(new BorderLayout());
        pnlAtasKas.setOpaque(false);
        pnlAtasKas.add(TemaWarna.buatJudulBagian("Riwayat Keluar Masuk Kas"), BorderLayout.WEST);
        TombolKustom btnRefreshKeuangan = TombolKustom.buatSekunder("Muat Ulang");
        btnRefreshKeuangan.addActionListener(e -> muatDataKeuangan());
        pnlAtasKas.add(btnRefreshKeuangan, BorderLayout.EAST);

        kartuKas.add(pnlAtasKas, BorderLayout.NORTH);
        tabelKeuangan.getColumnModel().getColumn(0).setCellRenderer(new BadgeStatusRenderer());
        kartuKas.add(TemaWarna.bungkusTabel(tabelKeuangan), BorderLayout.CENTER);
        pnlTab4.add(kartuKas, BorderLayout.CENTER);

        tab.addTab("Keuangan & Kas", pnlTab4);

        add(tab, BorderLayout.CENTER);
    }

    public void muatSemuaData() {
        muatDataTransaksi();
        muatDataBelumDiambil();
        muatDataPelanggan();
        muatDataKeuangan();
    }

    public void muatDataTransaksi() {
        modelTransaksi.setRowCount(0);
        try {
            String status = (String) cmbFilterStatus.getSelectedItem();
            String kw = txtCari.getText();
            List<TransaksiLaundry> list = transaksiDAO.ambilSemuaTransaksi(status, kw);
            for (TransaksiLaundry t : list) {
                modelTransaksi.addRow(new Object[]{
                    t.getNomorTransaksi(),
                    t.getNamaPelanggan(),
                    t.getNamaLayanan(),
                    t.getNamaPaket(),
                    FormatUtil.formatBerat(t.getBeratTagihan()),
                    FormatUtil.formatRupiah(t.getTotalBiaya()),
                    t.getStatusTransaksi(),
                    t.getStatusPembayaran(),
                    FormatUtil.formatTanggalWaktu(t.getTanggalTransaksi())
                });
            }
        } catch (Exception e) {
            System.err.println("[PanelLaporanAdmin] Gagal muat transaksi: " + e.getMessage());
        }
    }

    public void muatDataBelumDiambil() {
        modelBelumDiambil.setRowCount(0);
        try {
            List<TransaksiLaundry> list = transaksiDAO.ambilCucianBelumDiambil();
            for (TransaksiLaundry t : list) {
                modelBelumDiambil.addRow(new Object[]{
                    t.getNomorTransaksi(),
                    t.getNamaPelanggan(),
                    t.getNomorTeleponPelanggan() != null ? t.getNomorTeleponPelanggan() : "-",
                    t.getNamaLayanan(),
                    t.getNamaPaket(),
                    FormatUtil.formatRupiah(t.getTotalBiaya()),
                    t.getStatusTransaksi(),
                    t.getNomorStruk() != null ? t.getNomorStruk() : "-"
                });
            }
        } catch (Exception e) {
            System.err.println("[PanelLaporanAdmin] Gagal muat cucian belum diambil: " + e.getMessage());
        }
    }

    public void muatDataPelanggan() {
        modelPelanggan.setRowCount(0);
        try {
            List<Pelanggan> list = pelangganDAO.ambilSemuaPelanggan();
            for (Pelanggan p : list) {
                modelPelanggan.addRow(new Object[]{
                    p.getKodePelanggan(),
                    p.getNamaLengkap(),
                    p.getNamaPengguna(),
                    p.getNomorTelepon() != null ? p.getNomorTelepon() : "-",
                    p.getAlamat() != null ? p.getAlamat() : "-"
                });
            }
        } catch (Exception e) {
            System.err.println("[PanelLaporanAdmin] Gagal muat pelanggan: " + e.getMessage());
        }
    }

    public void muatDataKeuangan() {
        modelKeuangan.setRowCount(0);
        try {
            List<Map<String, Object>> list = laporanService.ambilLaporanArusKas();
            BigDecimal totalMasuk = BigDecimal.ZERO;
            BigDecimal totalKeluar = BigDecimal.ZERO;

            for (Map<String, Object> m : list) {
                String tipe = (String) m.get("tipe");
                Timestamp tgl = (Timestamp) m.get("tanggal");
                String desc = (String) m.get("deskripsi");
                String metode = (String) m.get("metode");
                BigDecimal nominal = (BigDecimal) m.get("nominal");
                String operator = (String) m.get("operator");

                if ("PEMASUKAN".equalsIgnoreCase(tipe)) {
                    totalMasuk = totalMasuk.add(nominal != null ? nominal : BigDecimal.ZERO);
                } else {
                    totalKeluar = totalKeluar.add(nominal != null ? nominal : BigDecimal.ZERO);
                }

                modelKeuangan.addRow(new Object[]{
                    tipe,
                    tgl != null ? FormatUtil.formatTanggalWaktu(tgl) : "-",
                    desc,
                    metode != null ? metode : "-",
                    FormatUtil.formatRupiah(nominal),
                    operator != null ? operator : "-"
                });
            }

            kKasMasuk.setNilai(FormatUtil.formatRupiah(totalMasuk));
            kKasKeluar.setNilai(FormatUtil.formatRupiah(totalKeluar));
            kSaldoBersih.setNilai(FormatUtil.formatRupiah(totalMasuk.subtract(totalKeluar)));
        } catch (Exception e) {
            System.err.println("[PanelLaporanAdmin] Gagal muat laporan keuangan: " + e.getMessage());
        }
    }
}
