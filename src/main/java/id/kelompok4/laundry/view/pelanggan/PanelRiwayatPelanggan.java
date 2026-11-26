package id.kelompok4.laundry.view.pelanggan;

import id.kelompok4.laundry.dao.PembayaranDAO;
import id.kelompok4.laundry.dao.TransaksiDAO;
import id.kelompok4.laundry.model.Pelanggan;
import id.kelompok4.laundry.model.PembayaranTransaksi;
import id.kelompok4.laundry.model.TransaksiLaundry;
import id.kelompok4.laundry.service.PembatalanRefundService;
import id.kelompok4.laundry.util.CetakStrukUtil;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.util.SessionManager;
import id.kelompok4.laundry.view.komponen.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * PanelRiwayatPelanggan
 * Menampilkan daftar cucian pelanggan, struk pembayaran, dan ajukan pembatalan.
 */
public class PanelRiwayatPelanggan extends JPanel {

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final PembayaranDAO pembayaranDAO = new PembayaranDAO();
    private final PembatalanRefundService pembatalanService = new PembatalanRefundService();

    private final DefaultTableModel modelRiwayat = new DefaultTableModel(
        new String[]{"ID", "No. Transaksi", "Layanan", "Paket", "Berat", "Total Biaya", "Status", "Estimasi Selesai", "No. Struk"}, 0
    );
    private final TabelKustom tabelRiwayat = new TabelKustom(modelRiwayat);

    public PanelRiwayatPelanggan() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        muatRiwayat();
    }

    private void initUI() {
        PanelKartu kartu = new PanelKartu(new BorderLayout(0, 16), 20);

        JPanel pnlAtas = new JPanel(new BorderLayout());
        pnlAtas.setOpaque(false);
        pnlAtas.add(TemaWarna.buatJudulBagian("Riwayat Cucian Anda"), BorderLayout.WEST);

        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlToolbar.setOpaque(false);

        TombolKustom btnLihatStruk = TombolKustom.buatAksiUtama("Lihat Struk");
        btnLihatStruk.addActionListener(e -> prosesLihatStruk());

        TombolKustom btnBatal = TombolKustom.buatBahaya("Ajukan Batal");
        btnBatal.addActionListener(e -> prosesAjukanBatal());

        TombolKustom btnRefresh = TombolKustom.buatSekunder("Muat Ulang");
        btnRefresh.addActionListener(e -> muatRiwayat());

        pnlToolbar.add(btnLihatStruk);
        pnlToolbar.add(btnBatal);
        pnlToolbar.add(btnRefresh);
        pnlAtas.add(pnlToolbar, BorderLayout.EAST);

        kartu.add(pnlAtas, BorderLayout.NORTH);
        tabelRiwayat.sembunyikanKolom(0);
        tabelRiwayat.getColumnModel().getColumn(6).setCellRenderer(new BadgeStatusRenderer());
        kartu.add(TemaWarna.bungkusTabel(tabelRiwayat), BorderLayout.CENTER);

        add(kartu, BorderLayout.CENTER);
    }

    public void muatRiwayat() {
        modelRiwayat.setRowCount(0);
        if (!(SessionManager.getPenggunaAktif() instanceof Pelanggan)) return;

        Pelanggan p = (Pelanggan) SessionManager.getPenggunaAktif();
        if (p.getIdPelanggan() == null) return;

        try {
            List<TransaksiLaundry> list = transaksiDAO.ambilTransaksiPelanggan(p.getIdPelanggan());
            for (TransaksiLaundry t : list) {
                modelRiwayat.addRow(new Object[]{
                    t.getIdTransaksi(),
                    t.getNomorTransaksi(),
                    t.getNamaLayanan(),
                    t.getNamaPaket(),
                    FormatUtil.formatBerat(t.getBeratTagihan()),
                    FormatUtil.formatRupiah(t.getTotalBiaya()),
                    t.getStatusTransaksi(),
                    t.getEstimasiSelesai() != null ? FormatUtil.formatTanggalWaktu(t.getEstimasiSelesai()) : "Menunggu Bayar",
                    t.getNomorStruk() != null ? t.getNomorStruk() : "-"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat riwayat cucian: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prosesLihatStruk() {
        int baris = tabelRiwayat.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan pada tabel untuk melihat struk.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idTrx = (Long) modelRiwayat.getValueAt(baris, 0);
        try {
            TransaksiLaundry trx = transaksiDAO.cariBerdasarkanId(idTrx);
            PembayaranTransaksi bayar = pembayaranDAO.cariBerdasarkanTransaksiId(idTrx);

            if (bayar == null) {
                JOptionPane.showMessageDialog(this,
                    "Struk belum tersedia karena pesanan ini belum lunas di kasir.",
                    "Struk Belum Terbit",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            JTextArea ta = new JTextArea(CetakStrukUtil.susunTeksStruk(trx, bayar), 18, 38);
            ta.setFont(TemaWarna.FONT_MONO);
            ta.setEditable(false);

            JOptionPane.showMessageDialog(
                this,
                new JScrollPane(ta),
                "Struk Pembayaran",
                JOptionPane.PLAIN_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat struk: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prosesAjukanBatal() {
        int baris = tabelRiwayat.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan yang ingin dibatalkan.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idTrx = (Long) modelRiwayat.getValueAt(baris, 0);
        String noTrx = (String) modelRiwayat.getValueAt(baris, 1);
        String status = (String) modelRiwayat.getValueAt(baris, 6);

        if ("DIPROSES".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                "Cucian Anda sudah mulai dicuci sehingga tidak bisa dibatalkan.\nPembatalan hanya berlaku sebelum proses cuci dimulai.",
                "Tidak Bisa Dibatalkan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!"DITERIMA".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                "Hanya pesanan berstatus 'Diterima' yang bisa diajukan pembatalan.",
                "Info",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Pelanggan p = (Pelanggan) SessionManager.getPenggunaAktif();
        String alasan = JOptionPane.showInputDialog(this,
            "Alasan membatalkan pesanan " + noTrx + ":\n(Dana akan dikembalikan penuh jika disetujui admin)",
            "Ajukan Pembatalan",
            JOptionPane.PLAIN_MESSAGE
        );

        if (alasan != null && !alasan.trim().isEmpty()) {
            try {
                pembatalanService.ajukanPembatalan(idTrx, p.getIdPelanggan(), p.getIdPengguna(), alasan.trim());
                JOptionPane.showMessageDialog(this,
                    "Pengajuan batal berhasil dikirim ke pemilik gerai.",
                    "Berhasil",
                    JOptionPane.INFORMATION_MESSAGE
                );
                muatRiwayat();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengajukan batal: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
