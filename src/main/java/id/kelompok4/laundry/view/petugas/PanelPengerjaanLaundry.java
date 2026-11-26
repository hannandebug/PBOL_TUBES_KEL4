package id.kelompok4.laundry.view.petugas;

import id.kelompok4.laundry.dao.TransaksiDAO;
import id.kelompok4.laundry.model.TransaksiLaundry;
import id.kelompok4.laundry.service.PengerjaanService;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.util.SessionManager;
import id.kelompok4.laundry.view.komponen.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * PanelPengerjaanLaundry
 * Pembaruan status pengerjaan cucian dari antrean masuk -> sedang dicuci -> siap diambil.
 */
public class PanelPengerjaanLaundry extends JPanel {

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final PengerjaanService pengerjaanService = new PengerjaanService();

    private final DefaultTableModel modelDiterima = new DefaultTableModel(
        new String[]{"ID", "No. Transaksi", "Pelanggan", "Layanan", "Paket", "Berat", "Estimasi Selesai", "No. Struk"}, 0
    );
    private final TabelKustom tabelDiterima = new TabelKustom(modelDiterima);

    private final DefaultTableModel modelDiproses = new DefaultTableModel(
        new String[]{"ID", "No. Transaksi", "Pelanggan", "Layanan", "Paket", "Berat", "Estimasi Selesai", "Catatan"}, 0
    );
    private final TabelKustom tabelDiproses = new TabelKustom(modelDiproses);

    public PanelPengerjaanLaundry() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        muatData();
    }

    private void initUI() {
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(TemaWarna.FONT_BOLD);

        // Tab 1: Antrean Masuk
        PanelKartu kartu1 = new PanelKartu(new BorderLayout(0, 14), 18);
        JPanel pnlAtas1 = new JPanel(new BorderLayout());
        pnlAtas1.setOpaque(false);
        pnlAtas1.add(TemaWarna.buatJudulBagian("Cucian Masuk Siap Dicuci"), BorderLayout.WEST);

        JPanel pnlToolbar1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlToolbar1.setOpaque(false);
        TombolKustom btnMulai = TombolKustom.buatAksiUtama("Mulai Cuci");
        btnMulai.addActionListener(e -> prosesMulaiCuci());
        TombolKustom btnRef1 = TombolKustom.buatSekunder("Muat Ulang");
        btnRef1.addActionListener(e -> muatData());
        pnlToolbar1.add(btnMulai);
        pnlToolbar1.add(btnRef1);
        pnlAtas1.add(pnlToolbar1, BorderLayout.EAST);

        kartu1.add(pnlAtas1, BorderLayout.NORTH);
        tabelDiterima.sembunyikanKolom(0);
        kartu1.add(TemaWarna.bungkusTabel(tabelDiterima), BorderLayout.CENTER);
        tab.addTab("Antrean Masuk", kartu1);

        // Tab 2: Sedang Dicuci
        PanelKartu kartu2 = new PanelKartu(new BorderLayout(0, 14), 18);
        JPanel pnlAtas2 = new JPanel(new BorderLayout());
        pnlAtas2.setOpaque(false);
        pnlAtas2.add(TemaWarna.buatJudulBagian("Cucian Sedang Dikerjakan"), BorderLayout.WEST);

        JPanel pnlToolbar2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlToolbar2.setOpaque(false);
        TombolKustom btnSelesaiCuci = TombolKustom.buatSukses("Tandai Siap Diambil");
        btnSelesaiCuci.addActionListener(e -> prosesSelesaiCuci());
        TombolKustom btnRef2 = TombolKustom.buatSekunder("Muat Ulang");
        btnRef2.addActionListener(e -> muatData());
        pnlToolbar2.add(btnSelesaiCuci);
        pnlToolbar2.add(btnRef2);
        pnlAtas2.add(pnlToolbar2, BorderLayout.EAST);

        kartu2.add(pnlAtas2, BorderLayout.NORTH);
        tabelDiproses.sembunyikanKolom(0);
        kartu2.add(TemaWarna.bungkusTabel(tabelDiproses), BorderLayout.CENTER);
        tab.addTab("Sedang Dicuci", kartu2);

        add(tab, BorderLayout.CENTER);
    }

    public void muatData() {
        modelDiterima.setRowCount(0);
        modelDiproses.setRowCount(0);

        try {
            for (TransaksiLaundry t : transaksiDAO.ambilTransaksiDiterima()) {
                modelDiterima.addRow(new Object[]{
                    t.getIdTransaksi(),
                    t.getNomorTransaksi(),
                    t.getNamaPelanggan(),
                    t.getNamaLayanan(),
                    t.getNamaPaket(),
                    FormatUtil.formatBerat(t.getBeratTagihan()),
                    FormatUtil.formatTanggalWaktu(t.getEstimasiSelesai()),
                    t.getNomorStruk() != null ? t.getNomorStruk() : "-"
                });
            }

            for (TransaksiLaundry t : transaksiDAO.ambilTransaksiDiproses()) {
                modelDiproses.addRow(new Object[]{
                    t.getIdTransaksi(),
                    t.getNomorTransaksi(),
                    t.getNamaPelanggan(),
                    t.getNamaLayanan(),
                    t.getNamaPaket(),
                    FormatUtil.formatBerat(t.getBeratTagihan()),
                    FormatUtil.formatTanggalWaktu(t.getEstimasiSelesai()),
                    t.getCatatan() != null ? t.getCatatan() : "-"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat status pengerjaan: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prosesMulaiCuci() {
        int baris = tabelDiterima.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih cucian pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idTrx = (Long) modelDiterima.getValueAt(baris, 0);
        String noTrx = (String) modelDiterima.getValueAt(baris, 1);

        int konf = JOptionPane.showConfirmDialog(this,
            "Mulai proses cuci untuk pesanan " + noTrx + "?",
            "Mulai Cuci",
            JOptionPane.YES_NO_OPTION
        );

        if (konf == JOptionPane.YES_OPTION) {
            try {
                boolean berhasil = pengerjaanService.mulaiPengerjaan(idTrx, SessionManager.getIdPenggunaAktif());
                if (berhasil) {
                    JOptionPane.showMessageDialog(this, "Status diperbarui menjadi Sedang Dicuci.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Status transaksi sudah berubah. Muat ulang antrean.", "Status Berubah", JOptionPane.INFORMATION_MESSAGE);
                }
                muatData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah status: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void prosesSelesaiCuci() {
        int baris = tabelDiproses.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih cucian pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idTrx = (Long) modelDiproses.getValueAt(baris, 0);
        String noTrx = (String) modelDiproses.getValueAt(baris, 1);

        int konf = JOptionPane.showConfirmDialog(this,
            "Cucian " + noTrx + " sudah selesai dan siap diambil pelanggan?",
            "Selesai Cuci",
            JOptionPane.YES_NO_OPTION
        );

        if (konf == JOptionPane.YES_OPTION) {
            try {
                boolean berhasil = pengerjaanService.selesaiPengerjaan(idTrx, SessionManager.getIdPenggunaAktif());
                if (berhasil) {
                    JOptionPane.showMessageDialog(this, "Status diperbarui menjadi Siap Diambil.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Status transaksi sudah berubah. Muat ulang antrean.", "Status Berubah", JOptionPane.INFORMATION_MESSAGE);
                }
                muatData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah status: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
