package id.kelompok4.laundry.view.petugas;

import id.kelompok4.laundry.model.PengembalianDana;
import id.kelompok4.laundry.service.PembatalanRefundService;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.util.SessionManager;
import id.kelompok4.laundry.view.komponen.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * PanelRefundKasir
 * Pencairan uang pengembalian (refund) kepada pelanggan oleh kasir.
 */
public class PanelRefundKasir extends JPanel {

    private final PembatalanRefundService service = new PembatalanRefundService();

    private final DefaultTableModel modelRefund = new DefaultTableModel(
        new String[]{"ID", "No. Refund", "No. Transaksi", "Pelanggan", "Jumlah Refund", "Alasan", "Status"}, 0
    );
    private final TabelKustom tabelRefund = new TabelKustom(modelRefund);

    public PanelRefundKasir() {
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
        pnlAtas.add(TemaWarna.buatJudulBagian("Antrean Pengembalian Dana"), BorderLayout.WEST);

        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlToolbar.setOpaque(false);

        TombolKustom btnCairkan = TombolKustom.buatAksiUtama("Bayar Refund");
        btnCairkan.addActionListener(e -> prosesPenyerahanRefund());

        TombolKustom btnRefresh = TombolKustom.buatSekunder("Muat Ulang");
        btnRefresh.addActionListener(e -> muatData());

        pnlToolbar.add(btnCairkan);
        pnlToolbar.add(btnRefresh);
        pnlAtas.add(pnlToolbar, BorderLayout.EAST);

        kartu.add(pnlAtas, BorderLayout.NORTH);
        tabelRefund.sembunyikanKolom(0);
        tabelRefund.getColumnModel().getColumn(6).setCellRenderer(new BadgeStatusRenderer());
        kartu.add(TemaWarna.bungkusTabel(tabelRefund), BorderLayout.CENTER);

        add(kartu, BorderLayout.CENTER);
    }

    public void muatData() {
        modelRefund.setRowCount(0);
        try {
            List<PengembalianDana> list = service.ambilRefundMenunggu();
            for (PengembalianDana r : list) {
                modelRefund.addRow(new Object[]{
                    r.getIdPengembalian(),
                    r.getNomorPengembalian(),
                    r.getNomorTransaksi(),
                    r.getNamaPelanggan(),
                    FormatUtil.formatRupiah(r.getJumlahPengembalian()),
                    r.getAlasan(),
                    r.getStatusPengembalian()
                });
            }
        } catch (Exception e) {
            System.err.println("[PanelRefundKasir] Gagal muat refund: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Gagal memuat antrean refund: " + e.getMessage(),
                "Kesalahan",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void prosesPenyerahanRefund() {
        int baris = tabelRefund.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris refund yang akan dicairkan terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idRefund = (Long) modelRefund.getValueAt(baris, 0);
        String noRefund = (String) modelRefund.getValueAt(baris, 1);
        String nominal = (String) modelRefund.getValueAt(baris, 4);

        JComboBox<String> cmbMetode = new JComboBox<>(new String[]{"TUNAI", "TRANSFER", "QRIS"});

        JPanel pnl = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0;
        pnl.add(new JLabel("Metode Penyerahan"), gbc);
        gbc.gridx = 1;
        cmbMetode.setPreferredSize(new Dimension(200, 34));
        pnl.add(cmbMetode, gbc);

        int ok = JOptionPane.showConfirmDialog(this, pnl, "Bayar Refund (" + noRefund + " • " + nominal + ")", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            String metode = (String) cmbMetode.getSelectedItem();

            try {
                boolean berhasil = service.eksekusiBayarRefundKasir(idRefund, metode, SessionManager.getIdPenggunaAktif());
                if (!berhasil) {
                    JOptionPane.showMessageDialog(this,
                        "Refund tidak dapat dibayarkan karena sudah diproses atau tidak lagi berstatus MENUNGGU.",
                        "Refund Tidak Tersedia",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    muatData();
                    return;
                }
                JOptionPane.showMessageDialog(this,
                    "Uang refund sebesar " + nominal + " berhasil dicairkan.",
                    "Refund Selesai",
                    JOptionPane.INFORMATION_MESSAGE
                );
                muatData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal memproses refund: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
