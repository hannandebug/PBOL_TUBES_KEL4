package id.kelompok4.laundry.view.pelanggan;

import id.kelompok4.laundry.model.Pelanggan;
import id.kelompok4.laundry.service.LaporanService;
import id.kelompok4.laundry.util.SessionManager;
import id.kelompok4.laundry.view.komponen.KartuStatistik;
import id.kelompok4.laundry.view.komponen.PanelKartu;
import id.kelompok4.laundry.view.komponen.TemaWarna;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

// Fitur: Ringkasan Pelanggan
// OOP: Inheritance - PanelRingkasanPelanggan mewarisi JPanel.
public class PanelRingkasanPelanggan extends JPanel {

    private final JLabel lblSapaan = new JLabel();
    private final JLabel lblKodePelanggan = new JLabel();

    private final KartuStatistik kSedangDicuci = new KartuStatistik("Sedang Dicuci", "0", TemaWarna.SEA_BREEZE);
    private final KartuStatistik kSiapAmbil = new KartuStatistik("Siap Diambil", "0", TemaWarna.CITRUS_ZEST);
    private final KartuStatistik kTotalPesanan = new KartuStatistik("Total Pesanan", "0", TemaWarna.AMALFI_TILE);
    private final KartuStatistik kPengaduan = new KartuStatistik("Pengaduan Aktif", "0", TemaWarna.MERAH_BAHAYA);

    private final LaporanService laporanService = new LaporanService();

    public PanelRingkasanPelanggan() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        muatData();
    }

    private void initUI() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        // Fitur: Tampilkan Sapaan Pelanggan
        PanelKartu pnlBanner = new PanelKartu(new BorderLayout(0, 6), 22);
        pnlBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));
        pnlBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSapaan.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblSapaan.setForeground(TemaWarna.TEKS_GELAP);

        lblKodePelanggan.setFont(TemaWarna.FONT_REGULER);
        lblKodePelanggan.setForeground(TemaWarna.ABU_TEKS);

        pnlBanner.add(lblSapaan, BorderLayout.NORTH);
        pnlBanner.add(lblKodePelanggan, BorderLayout.SOUTH);
        container.add(pnlBanner);
        container.add(Box.createVerticalStrut(20));

        // Fitur: Lihat Ringkasan Statistik Pelanggan
        JPanel pnlKartu = new JPanel(new GridLayout(1, 4, 16, 16));
        pnlKartu.setOpaque(false);
        pnlKartu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 114));
        pnlKartu.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlKartu.add(kSedangDicuci);
        pnlKartu.add(kSiapAmbil);
        pnlKartu.add(kTotalPesanan);
        pnlKartu.add(kPengaduan);
        container.add(pnlKartu);
        container.add(Box.createVerticalStrut(20));

        // Fitur: Lihat Informasi Pengambilan
        PanelKartu pnlInfo = new PanelKartu(new BorderLayout(0, 8), 20);
        pnlInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        pnlInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblJudulInfo = TemaWarna.buatJudulBagian("Informasi Pengambilan Cucian");
        JLabel lblIsiInfo = new JLabel("<html><body style='color:#64748B; font-size:12px; line-height:1.5;'>" +
                                       "• Jika status cucian sudah <b>Siap Diambil</b>, Anda bisa langsung datang ke gerai.<br>" +
                                       "• Pembayaran sudah lunas di awal, jadi tinggal ambil pakaian tanpa bayar lagi.<br>" +
                                       "• Buka menu <b>Cucian & Struk</b> untuk melihat struk pembayaran atau mengajukan batal." +
                                       "</body></html>");

        pnlInfo.add(lblJudulInfo, BorderLayout.NORTH);
        pnlInfo.add(lblIsiInfo, BorderLayout.CENTER);
        container.add(pnlInfo);
        container.add(Box.createVerticalGlue());

        add(container, BorderLayout.CENTER);
    }

    public void muatData() {
        Pelanggan p = null;
        if (SessionManager.getPenggunaAktif() instanceof Pelanggan) {
            p = (Pelanggan) SessionManager.getPenggunaAktif();
        }

        if (p != null) {
            lblSapaan.setText("Halo, " + p.getNamaLengkap() + "!");
            lblKodePelanggan.setText("Kode Pelanggan: " + (p.getKodePelanggan() != null ? p.getKodePelanggan() : "-") +
                                     "  •  No. HP: " + (p.getNomorTelepon() != null ? p.getNomorTelepon() : "-"));

            try {
                if (p.getIdPelanggan() != null) {
                    Map<String, Object> data = laporanService.ambilRingkasanPelanggan(p.getIdPelanggan());
                    kSedangDicuci.setNilai((Integer) data.getOrDefault("sedang_dikerjakan", 0));
                    kSiapAmbil.setNilai((Integer) data.getOrDefault("siap_diambil", 0));
                    kTotalPesanan.setNilai((Integer) data.getOrDefault("total_pesanan", 0));
                    kPengaduan.setNilai((Integer) data.getOrDefault("pengaduan_berjalan", 0));
                }
            } catch (Exception e) {
                System.err.println("[PanelRingkasanPelanggan] Gagal memuat ringkasan: " + e.getMessage());
            }
        }
    }
}
