package id.kelompok4.laundry.view.petugas;

import id.kelompok4.laundry.model.Pengaduan;
import id.kelompok4.laundry.service.PengaduanService;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.util.SessionManager;
import id.kelompok4.laundry.view.komponen.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * PanelPenelusuranKasir
 * Daftar tugas pengecekan barang bermasalah dari pemilik gerai.
 */
public class PanelPenelusuranKasir extends JPanel {

    private final PengaduanService pengaduanService = new PengaduanService();

    private final DefaultTableModel modelTugas = new DefaultTableModel(
        new String[]{"ID", "No. Komplain", "No. Transaksi", "Pelanggan", "Masalah", "Barang", "Batas Waktu", "Temuan"}, 0
    );
    private final TabelKustom tabelTugas = new TabelKustom(modelTugas);

    public PanelPenelusuranKasir() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        muatData();
    }

    private void initUI() {
        PanelKartu kartu = new PanelKartu(new BorderLayout(0, 16), 20);

        JPanel pnlAtas = new JPanel(new BorderLayout());
        pnlAtas.setOpaque(false);
        pnlAtas.add(TemaWarna.buatJudulBagian("Tugas Pengecekan Komplain"), BorderLayout.WEST);

        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlToolbar.setOpaque(false);

        TombolKustom btnIsiHasil = TombolKustom.buatAksiUtama("Catat Hasil Cek");
        btnIsiHasil.addActionListener(e -> prosesInputHasil());

        TombolKustom btnRefresh = TombolKustom.buatSekunder("Muat Ulang");
        btnRefresh.addActionListener(e -> muatData());

        pnlToolbar.add(btnIsiHasil);
        pnlToolbar.add(btnRefresh);
        pnlAtas.add(pnlToolbar, BorderLayout.EAST);

        kartu.add(pnlAtas, BorderLayout.NORTH);
        tabelTugas.sembunyikanKolom(0);
        kartu.add(TemaWarna.bungkusTabel(tabelTugas), BorderLayout.CENTER);

        add(kartu, BorderLayout.CENTER);
    }

    public void muatData() {
        modelTugas.setRowCount(0);
        try {
            List<Pengaduan> list = pengaduanService.ambilTugasPenelusuranPetugas();
            for (Pengaduan p : list) {
                modelTugas.addRow(new Object[]{
                    p.getIdPengaduan(),
                    p.getNomorPengaduan(),
                    p.getNomorTransaksi(),
                    p.getNamaPelanggan(),
                    p.getJenisPengaduan(),
                    p.getDeskripsiPakaian(),
                    FormatUtil.formatTanggalWaktu(p.getBatasWaktuPenelusuran()),
                    p.getHasilPenelusuran() != null ? p.getHasilPenelusuran() : "Belum dicek"
                });
            }
        } catch (Exception e) {
            System.err.println("[PanelPenelusuranKasir] Gagal muat tugas: " + e.getMessage());
        }
    }

    private void prosesInputHasil() {
        int baris = tabelTugas.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih tugas pengecekan pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idPengaduan = (Long) modelTugas.getValueAt(baris, 0);
        String noPengaduan = (String) modelTugas.getValueAt(baris, 1);

        String hasil = JOptionPane.showInputDialog(this,
            "Catat hasil pengecekan di gerai untuk komplain " + noPengaduan + ":",
            "Hasil Pengecekan",
            JOptionPane.PLAIN_MESSAGE
        );

        if (hasil != null && !hasil.trim().isEmpty()) {
            try {
                pengaduanService.isiHasilPenelusuran(idPengaduan, hasil.trim(), SessionManager.getIdPenggunaAktif());
                JOptionPane.showMessageDialog(this, "Hasil pengecekan berhasil disimpan.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                muatData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
