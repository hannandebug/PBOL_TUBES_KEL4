package id.kelompok4.laundry.view.admin;

import id.kelompok4.laundry.service.LaporanService;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.view.komponen.KartuStatistik;
import id.kelompok4.laundry.view.komponen.TemaWarna;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;

// Fitur: Ringkasan Admin
// OOP: Inheritance - PanelRingkasanAdmin mewarisi JPanel.
public class PanelRingkasanAdmin extends JPanel {

    private final KartuStatistik kTotalPelanggan = new KartuStatistik("Pelanggan Terdaftar", "0", TemaWarna.AMALFI_TILE);
    private final KartuStatistik kTotalPetugas = new KartuStatistik("Petugas Aktif", "0", TemaWarna.AMALFI_TILE);
    private final KartuStatistik kTotalTransaksi = new KartuStatistik("Total Transaksi", "0", TemaWarna.AMALFI_TILE);
    private final KartuStatistik kPenerimaanJasa = new KartuStatistik("Total Pemasukan", "Rp0", TemaWarna.HIJAU_SUKSES);

    private final KartuStatistik kMenungguBayar = new KartuStatistik("Belum Bayar", "0", TemaWarna.CITRUS_ZEST);
    private final KartuStatistik kDiproses = new KartuStatistik("Sedang Dicuci", "0", TemaWarna.SEA_BREEZE);
    private final KartuStatistik kSiapAmbil = new KartuStatistik("Siap Diambil", "0", TemaWarna.CITRUS_ZEST);
    private final KartuStatistik kSelesai = new KartuStatistik("Pesanan Selesai", "0", TemaWarna.HIJAU_SUKSES);

    private final KartuStatistik kPengaduan = new KartuStatistik("Komplain Aktif", "0", TemaWarna.MERAH_BAHAYA);
    private final KartuStatistik kPembatalan = new KartuStatistik("Pengajuan Batal", "0", TemaWarna.MERAH_BAHAYA);
    private final KartuStatistik kRefund = new KartuStatistik("Refund Menunggu", "0", TemaWarna.CITRUS_ZEST);
    private final KartuStatistik kTotalRefund = new KartuStatistik("Total Refund Cair", "Rp0", TemaWarna.MERAH_BAHAYA);

    private final LaporanService laporanService = new LaporanService();

    public PanelRingkasanAdmin() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);

        initUI();
        muatData();
    }

    private void initUI() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(TemaWarna.LATAR_BELAKANG);
        container.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Fitur: Ringkasan Umum
        container.add(buatLabelBagian("Gambaran Umum Usaha"));
        container.add(Box.createVerticalStrut(10));
        container.add(buatBarisKartu(kTotalPelanggan, kTotalPetugas, kTotalTransaksi, kPenerimaanJasa));
        container.add(Box.createVerticalStrut(24));

        // Fitur: Status Cucian
        container.add(buatLabelBagian("Pantauan Cucian Hari Ini"));
        container.add(Box.createVerticalStrut(10));
        container.add(buatBarisKartu(kMenungguBayar, kDiproses, kSiapAmbil, kSelesai));
        container.add(Box.createVerticalStrut(24));

        // Fitur: Tindak Lanjut
        container.add(buatLabelBagian("Perlu Tindak Lanjut"));
        container.add(Box.createVerticalStrut(10));
        container.add(buatBarisKartu(kPengaduan, kPembatalan, kRefund, kTotalRefund));
        container.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JLabel buatLabelBagian(String teks) {
        JLabel lbl = TemaWarna.buatJudulBagian(teks);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel buatBarisKartu(KartuStatistik... kartu) {
        JPanel pnl = new JPanel(new GridLayout(1, kartu.length, 16, 16));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));
        for (KartuStatistik k : kartu) {
            pnl.add(k);
        }
        return pnl;
    }

    public void muatData() {
        try {
            Map<String, Object> data = laporanService.ambilRingkasanAdmin();
            kTotalPelanggan.setNilai((Integer) data.getOrDefault("total_pelanggan", 0));
            kTotalPetugas.setNilai((Integer) data.getOrDefault("total_petugas", 0));
            kTotalTransaksi.setNilai((Integer) data.getOrDefault("total_transaksi", 0));

            BigDecimal penerimaan = (BigDecimal) data.getOrDefault("total_penerimaan_jasa", BigDecimal.ZERO);
            kPenerimaanJasa.setNilai(FormatUtil.formatRupiah(penerimaan));

            kMenungguBayar.setNilai((Integer) data.getOrDefault("trx_menunggu_bayar", 0));
            kDiproses.setNilai((Integer) data.getOrDefault("trx_diproses", 0));
            kSiapAmbil.setNilai((Integer) data.getOrDefault("trx_siap_diambil", 0));
            kSelesai.setNilai((Integer) data.getOrDefault("trx_selesai", 0));

            kPengaduan.setNilai((Integer) data.getOrDefault("pengaduan_aktif", 0));
            kPembatalan.setNilai((Integer) data.getOrDefault("pembatalan_menunggu", 0));
            kRefund.setNilai((Integer) data.getOrDefault("refund_menunggu", 0));

            BigDecimal refund = (BigDecimal) data.getOrDefault("total_refund_dibayar", BigDecimal.ZERO);
            kTotalRefund.setNilai(FormatUtil.formatRupiah(refund));
        } catch (Exception e) {
            System.err.println("[PanelRingkasanAdmin] Gagal memuat ringkasan: " + e.getMessage());
        }
    }
}
