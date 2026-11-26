package id.kelompok4.laundry.view.admin;

import id.kelompok4.laundry.model.PengembalianDana;
import id.kelompok4.laundry.model.PermohonanPembatalan;
import id.kelompok4.laundry.service.PembatalanRefundService;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.util.SessionManager;
import id.kelompok4.laundry.view.komponen.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Fitur: Kelola Pembatalan dan Refund
// OOP: Inheritance - PanelKelolaPembatalanAdmin mewarisi JPanel.
public class PanelKelolaPembatalanAdmin extends JPanel {

    private final PembatalanRefundService service = new PembatalanRefundService();

    private final DefaultTableModel modelBatal = new DefaultTableModel(
        new String[]{"ID", "No. Pengajuan", "No. Transaksi", "Pelanggan", "Alasan Batal", "Waktu Pengajuan", "Status"}, 0
    );
    private final TabelKustom tabelBatal = new TabelKustom(modelBatal);

    private final DefaultTableModel modelRefund = new DefaultTableModel(
        new String[]{"ID", "No. Refund", "No. Transaksi", "Pelanggan", "Nominal Refund", "Status", "Metode Cair"}, 0
    );
    private final TabelKustom tabelRefund = new TabelKustom(modelRefund);

    public PanelKelolaPembatalanAdmin() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        muatData();
    }

    private void initUI() {
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(TemaWarna.FONT_BOLD);

        // Fitur: Kelola Pembatalan
        PanelKartu kartu1 = new PanelKartu(new BorderLayout(0, 14), 18);
        JPanel pnlAtas1 = new JPanel(new BorderLayout(0, 8));
        pnlAtas1.setOpaque(false);
        pnlAtas1.add(TemaWarna.buatJudulBagian("Pengajuan Batal dari Pelanggan"), BorderLayout.NORTH);

        JPanel pnlToolbar1 = new JPanel(new GridLayout(1, 3, 8, 0));
        pnlToolbar1.setOpaque(false);

        TombolKustom btnSetuju = TombolKustom.buatSukses("Setujui & Buat Refund");
        btnSetuju.addActionListener(e -> prosesPersetujuan(true));

        TombolKustom btnTolak = TombolKustom.buatBahaya("Tolak");
        btnTolak.addActionListener(e -> prosesPersetujuan(false));

        TombolKustom btnRefresh1 = TombolKustom.buatSekunder("Muat Ulang");
        btnRefresh1.addActionListener(e -> muatData());

        pnlToolbar1.add(btnSetuju);
        pnlToolbar1.add(btnTolak);
        pnlToolbar1.add(btnRefresh1);
        pnlAtas1.add(pnlToolbar1, BorderLayout.CENTER);

        kartu1.add(pnlAtas1, BorderLayout.NORTH);
        tabelBatal.sembunyikanKolom(0);
        tabelBatal.getColumnModel().getColumn(6).setCellRenderer(new BadgeStatusRenderer());
        kartu1.add(TemaWarna.bungkusTabel(tabelBatal), BorderLayout.CENTER);
        tab.addTab("Pengajuan Pembatalan", kartu1);

        // Fitur: Pantau Refund
        PanelKartu kartu2 = new PanelKartu(new BorderLayout(0, 14), 18);
        JPanel pnlAtas2 = new JPanel(new BorderLayout());
        pnlAtas2.setOpaque(false);
        pnlAtas2.add(TemaWarna.buatJudulBagian("Daftar Pengembalian Dana"), BorderLayout.WEST);

        TombolKustom btnRefresh2 = TombolKustom.buatSekunder("Muat Ulang");
        btnRefresh2.addActionListener(e -> muatData());
        pnlAtas2.add(btnRefresh2, BorderLayout.EAST);

        kartu2.add(pnlAtas2, BorderLayout.NORTH);
        tabelRefund.sembunyikanKolom(0);
        tabelRefund.getColumnModel().getColumn(5).setCellRenderer(new BadgeStatusRenderer());
        kartu2.add(TemaWarna.bungkusTabel(tabelRefund), BorderLayout.CENTER);
        tab.addTab("Status Pencairan Refund", kartu2);

        add(tab, BorderLayout.CENTER);
    }

    public void muatData() {
        modelBatal.setRowCount(0);
        modelRefund.setRowCount(0);

        try {
            List<PermohonanPembatalan> listBatal = service.ambilSemuaPermohonan();
            for (PermohonanPembatalan b : listBatal) {
                modelBatal.addRow(new Object[]{
                    b.getIdPermohonanPembatalan(),
                    b.getNomorPermohonan(),
                    b.getNomorTransaksi(),
                    b.getNamaPelanggan(),
                    b.getAlasanPembatalan(),
                    FormatUtil.formatTanggalWaktu(b.getDiajukanPada()),
                    b.getStatusPermohonan()
                });
            }

            List<PengembalianDana> listRefund = service.ambilSemuaRefund();
            for (PengembalianDana r : listRefund) {
                modelRefund.addRow(new Object[]{
                    r.getIdPengembalian(),
                    r.getNomorPengembalian(),
                    r.getNomorTransaksi(),
                    r.getNamaPelanggan(),
                    FormatUtil.formatRupiah(r.getJumlahPengembalian()),
                    r.getStatusPengembalian(),
                    r.getMetodePengembalian() != null ? r.getMetodePengembalian() : "Belum Dicairkan"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data pembatalan: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Fitur: Proses Persetujuan
    private void prosesPersetujuan(boolean setuju) {
        int baris = tabelBatal.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pengajuan pembatalan pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idPermohonan = (Long) modelBatal.getValueAt(baris, 0);
        String noPermohonan = (String) modelBatal.getValueAt(baris, 1);
        String statusSaatIni = (String) modelBatal.getValueAt(baris, 6);

        if (!"DIAJUKAN".equalsIgnoreCase(statusSaatIni)) {
            JOptionPane.showMessageDialog(this, "Pengajuan ini sudah diproses sebelumnya.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String catatan = JOptionPane.showInputDialog(this,
            "Catatan keputusan untuk pengajuan " + noPermohonan + ":",
            setuju ? "Setujui Pembatalan" : "Tolak Pembatalan",
            JOptionPane.PLAIN_MESSAGE
        );

        if (catatan != null) {
            try {
                service.putuskanPembatalanAdmin(idPermohonan, setuju, SessionManager.getIdPenggunaAktif(), catatan);
                String pesan = setuju
                    ? "Pembatalan disetujui. Kasir kini bisa mencairkan uang refund ke pelanggan."
                    : "Pengajuan pembatalan ditolak.";
                JOptionPane.showMessageDialog(this, pesan, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                muatData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan keputusan: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
