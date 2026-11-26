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
 * PanelPengambilanCucian
 * Penyerahan pakaian yang sudah selesai kepada pelanggan di gerai.
 */
public class PanelPengambilanCucian extends JPanel {

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final PengerjaanService pengerjaanService = new PengerjaanService();

    private final DefaultTableModel modelSiapAmbil = new DefaultTableModel(
        new String[]{"ID", "No. Transaksi", "Pelanggan", "No. HP", "Layanan", "Paket", "Total", "Selesai Pada"}, 0
    );
    private final TabelKustom tabelSiapAmbil = new TabelKustom(modelSiapAmbil);

    public PanelPengambilanCucian() {
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
        pnlAtas.add(TemaWarna.buatJudulBagian("Cucian Siap Diambil Pelanggan"), BorderLayout.WEST);

        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlToolbar.setOpaque(false);

        TombolKustom btnSerahkan = TombolKustom.buatAksiUtama("Serahkan Cucian");
        btnSerahkan.addActionListener(e -> prosesPenyerahan());

        TombolKustom btnRefresh = TombolKustom.buatSekunder("Muat Ulang");
        btnRefresh.addActionListener(e -> muatData());

        pnlToolbar.add(btnSerahkan);
        pnlToolbar.add(btnRefresh);
        pnlAtas.add(pnlToolbar, BorderLayout.EAST);

        kartu.add(pnlAtas, BorderLayout.NORTH);
        tabelSiapAmbil.sembunyikanKolom(0);
        kartu.add(TemaWarna.bungkusTabel(tabelSiapAmbil), BorderLayout.CENTER);

        add(kartu, BorderLayout.CENTER);
    }

    public void muatData() {
        modelSiapAmbil.setRowCount(0);
        try {
            List<TransaksiLaundry> list = transaksiDAO.ambilTransaksiSiapDiambil();
            for (TransaksiLaundry t : list) {
                modelSiapAmbil.addRow(new Object[]{
                    t.getIdTransaksi(),
                    t.getNomorTransaksi(),
                    t.getNamaPelanggan(),
                    t.getNomorTeleponPelanggan() != null ? t.getNomorTeleponPelanggan() : "-",
                    t.getNamaLayanan(),
                    t.getNamaPaket(),
                    FormatUtil.formatRupiah(t.getTotalBiaya()),
                    FormatUtil.formatTanggalWaktu(t.getTanggalSiapDiambil())
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat cucian siap diambil: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prosesPenyerahan() {
        int baris = tabelSiapAmbil.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih cucian pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idTrx = (Long) modelSiapAmbil.getValueAt(baris, 0);
        String noTrx = (String) modelSiapAmbil.getValueAt(baris, 1);
        String namaPlg = (String) modelSiapAmbil.getValueAt(baris, 2);

        JTextField txtPenerima = new JTextField(namaPlg, 18);
        JTextField txtCatatan = new JTextField(18);
        TemaWarna.aturPlaceholder(txtCatatan, "Opsional");

        JPanel pnl = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0;
        pnl.add(new JLabel("Nama Pengambil *"), gbc);
        gbc.gridx = 1;
        txtPenerima.setPreferredSize(new Dimension(210, 34));
        pnl.add(txtPenerima, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnl.add(new JLabel("Catatan"), gbc);
        gbc.gridx = 1;
        txtCatatan.setPreferredSize(new Dimension(210, 34));
        pnl.add(txtCatatan, gbc);

        int ok = JOptionPane.showConfirmDialog(this, pnl, "Serahkan Cucian (" + noTrx + ")", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            String penerima = txtPenerima.getText().trim();
            if (penerima.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama pengambil wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                boolean sukses = pengerjaanService.catatPengambilan(
                    idTrx, SessionManager.getIdPenggunaAktif(), penerima, txtCatatan.getText().trim()
                );

                if (sukses) {
                    JOptionPane.showMessageDialog(this,
                        "Cucian diserahkan kepada " + penerima + ".\nPesanan selesai.",
                        "Penyerahan Selesai",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    muatData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Penyerahan tidak tersimpan. Perbarui daftar dan periksa status transaksi.",
                        "Penyerahan Gagal",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mencatat penyerahan: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
