package id.kelompok4.laundry.view.petugas;

import id.kelompok4.laundry.model.*;
import id.kelompok4.laundry.service.PembayaranService;
import id.kelompok4.laundry.service.TransaksiService;
import id.kelompok4.laundry.util.CetakStrukUtil;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.util.SessionManager;
import id.kelompok4.laundry.view.komponen.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

// Fitur: Approve atau Tolak Pembayaran
// OOP: Inheritance - PanelVerifikasiPembayaran mewarisi JPanel.
public class PanelVerifikasiPembayaran extends JPanel {

    private final TransaksiService transaksiService = new TransaksiService();
    private final PembayaranService pembayaranService = new PembayaranService();

    private final DefaultTableModel modelAntrean = new DefaultTableModel(
        new String[]{"ID", "No. Transaksi", "Pelanggan", "Layanan", "Paket", "Berat", "Total Tagihan", "Waktu"}, 0
    );
    private final TabelKustom tabelAntrean = new TabelKustom(modelAntrean);

    private final JComboBox<String> cmbMetode = new JComboBox<>(new String[]{"TUNAI", "TRANSFER", "QRIS"});
    private final JTextField txtJumlahBayar = new JTextField(12);
    private final JTextArea txtCatatan = new JTextArea(2, 15);
    private final JLabel lblPetunjukVerifikasi = new JLabel();

    private final MetodePembayaran metodeTunai = new PembayaranTunai();
    private final MetodePembayaran metodeTransfer = new PembayaranTransfer();
    private final MetodePembayaran metodeQRIS = new PembayaranQRIS();

    public PanelVerifikasiPembayaran() {
        setLayout(new BorderLayout(0, 16));
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        muatAntrean();
    }

    private void initUI() {
        // Fitur: Lihat Antrean Pembayaran
        PanelKartu kartuAtas = new PanelKartu(new BorderLayout(0, 12), 18);

        JPanel pnlHeaderTabel = new JPanel(new BorderLayout());
        pnlHeaderTabel.setOpaque(false);
        pnlHeaderTabel.add(TemaWarna.buatJudulBagian("Antrean Menunggu Pembayaran"), BorderLayout.WEST);

        TombolKustom btnSegarkan = TombolKustom.buatSekunder("Muat Ulang");
        btnSegarkan.addActionListener(e -> muatAntrean());
        pnlHeaderTabel.add(btnSegarkan, BorderLayout.EAST);

        kartuAtas.add(pnlHeaderTabel, BorderLayout.NORTH);

        tabelAntrean.sembunyikanKolom(0);
        tabelAntrean.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) pilihTransaksiTerpilih();
        });
        kartuAtas.add(TemaWarna.bungkusTabel(tabelAntrean), BorderLayout.CENTER);
        add(kartuAtas, BorderLayout.CENTER);

        // Fitur: Verifikasi Pembayaran
        PanelKartu kartuBawah = new PanelKartu(new BorderLayout(0, 14), 20);
        kartuBawah.add(TemaWarna.buatJudulBagian("Konfirmasi Penerimaan Pembayaran"), BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridBagLayout());
        pnlFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 8);

        // Fitur: Pilih Metode Pembayaran
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        JLabel lMet = new JLabel("Metode Bayar");
        lMet.setFont(TemaWarna.FONT_BOLD);
        pnlFields.add(lMet, gbc);

        gbc.gridx = 1; gbc.weightx = 0.30;
        cmbMetode.setPreferredSize(new Dimension(160, 36));
        cmbMetode.addActionListener(e -> updatePetunjukMetode());
        pnlFields.add(cmbMetode, gbc);

        gbc.gridx = 2; gbc.gridwidth = 2; gbc.weightx = 0.55;
        lblPetunjukVerifikasi.setFont(TemaWarna.FONT_KECIL);
        lblPetunjukVerifikasi.setForeground(TemaWarna.AMALFI_TILE);
        pnlFields.add(lblPetunjukVerifikasi, gbc);
        gbc.gridwidth = 1;

        // Fitur: Masukkan Nominal Pembayaran
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15;
        JLabel lNom = new JLabel("Nominal Bayar *");
        lNom.setFont(TemaWarna.FONT_BOLD);
        pnlFields.add(lNom, gbc);

        gbc.gridx = 1; gbc.weightx = 0.85; gbc.gridwidth = 3;
        txtJumlahBayar.setFont(TemaWarna.FONT_BOLD);
        txtJumlahBayar.setPreferredSize(new Dimension(160, 36));
        pnlFields.add(txtJumlahBayar, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.15; gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lCatatan = new JLabel("Catatan");
        lCatatan.setFont(TemaWarna.FONT_BOLD);
        pnlFields.add(lCatatan, gbc);

        gbc.gridx = 1; gbc.weightx = 0.85; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.CENTER;
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        JScrollPane scrollCatatan = new JScrollPane(txtCatatan);
        scrollCatatan.setPreferredSize(new Dimension(320, 54));
        pnlFields.add(scrollCatatan, gbc);
        gbc.gridwidth = 1;

        kartuBawah.add(pnlFields, BorderLayout.CENTER);

        // Fitur: Approve atau Tolak Pembayaran
        JPanel pnlTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlTombol.setOpaque(false);

        TombolKustom btnTolak = TombolKustom.buatBahaya("Tolak Pembayaran");
        btnTolak.addActionListener(e -> prosesTolak());

        TombolKustom btnApprove = TombolKustom.buatAksiUtama("Konfirmasi Lunas");
        btnApprove.setPreferredSize(new Dimension(180, 40));
        btnApprove.addActionListener(e -> prosesApprove());

        pnlTombol.add(btnTolak);
        pnlTombol.add(btnApprove);
        kartuBawah.add(pnlTombol, BorderLayout.SOUTH);

        add(kartuBawah, BorderLayout.SOUTH);
        updatePetunjukMetode();
    }

    private void updatePetunjukMetode() {
        String metode = (String) cmbMetode.getSelectedItem();
        if ("TUNAI".equalsIgnoreCase(metode)) {
            lblPetunjukVerifikasi.setText("• " + metodeTunai.getPetunjukVerifikasiKasir());
        } else if ("TRANSFER".equalsIgnoreCase(metode)) {
            lblPetunjukVerifikasi.setText("• " + metodeTransfer.getPetunjukVerifikasiKasir());
        } else {
            lblPetunjukVerifikasi.setText("• " + metodeQRIS.getPetunjukVerifikasiKasir());
        }
    }

    public void muatAntrean() {
        modelAntrean.setRowCount(0);
        try {
            List<TransaksiLaundry> list = transaksiService.ambilAntreanBayar();
            for (TransaksiLaundry t : list) {
                modelAntrean.addRow(new Object[]{
                    t.getIdTransaksi(),
                    t.getNomorTransaksi(),
                    t.getNamaPelanggan(),
                    t.getNamaLayanan(),
                    t.getNamaPaket(),
                    FormatUtil.formatBerat(t.getBeratTagihan()),
                    FormatUtil.formatRupiah(t.getTotalBiaya()),
                    FormatUtil.formatTanggalWaktu(t.getTanggalTransaksi())
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat antrean: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pilihTransaksiTerpilih() {
        int baris = tabelAntrean.getSelectedRow();
        if (baris >= 0) {
            Long idTrx = (Long) modelAntrean.getValueAt(baris, 0);
            try {
                TransaksiLaundry trx = transaksiService.cariBerdasarkanId(idTrx);
                if (trx != null) {
                    txtJumlahBayar.setText(trx.getTotalBiaya().toPlainString());
                } else {
                    txtJumlahBayar.setText("");
                    JOptionPane.showMessageDialog(this, "Transaksi tidak ditemukan. Muat ulang antrean.", "Transaksi Tidak Tersedia", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                txtJumlahBayar.setText("");
                JOptionPane.showMessageDialog(this, "Gagal memuat tagihan transaksi: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Fitur: Approve Pembayaran
    private void prosesApprove() {
        int baris = tabelAntrean.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan pada tabel di atas terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idTrx = (Long) modelAntrean.getValueAt(baris, 0);
        String noTrx = (String) modelAntrean.getValueAt(baris, 1);
        String metode = (String) cmbMetode.getSelectedItem();

        BigDecimal jumlah;
        try {
            jumlah = new BigDecimal(txtJumlahBayar.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Nominal pembayaran tidak valid.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konf = JOptionPane.showConfirmDialog(this,
            String.format("Pesanan: %s\nMetode: %s\nJumlah: %s\n\nPastikan uang tunai atau transfer sudah benar-benar masuk. Lanjutkan?",
                noTrx, metode, FormatUtil.formatRupiah(jumlah)),
            "Konfirmasi Pembayaran",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (konf == JOptionPane.YES_OPTION) {
            PembayaranTransaksi bayar;
            try {
                bayar = pembayaranService.prosesApprovePembayaran(
                    idTrx, SessionManager.getIdPenggunaAktif(), metode, jumlah, txtCatatan.getText().trim()
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal memproses pembayaran: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }

            muatAntrean();
            txtJumlahBayar.setText("");
            txtCatatan.setText("");

            TransaksiLaundry trxSegar;
            try {
                trxSegar = transaksiService.cariBerdasarkanId(idTrx);
                if (trxSegar == null) {
                    JOptionPane.showMessageDialog(this,
                        "Pembayaran berhasil dicatat dengan struk " + bayar.getNomorStruk() +
                        ", tetapi rincian transaksi tidak dapat dimuat.",
                        "Pembayaran Tercatat",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Pembayaran berhasil dicatat dengan struk " + bayar.getNomorStruk() +
                    ", tetapi rincian transaksi gagal dimuat: " + ex.getMessage(),
                    "Pembayaran Tercatat",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                JOptionPane.showMessageDialog(this,
                    "Pembayaran berhasil dicatat!\n" +
                    "No. Struk : " + bayar.getNomorStruk() + "\n" +
                    "Estimasi Selesai : " + FormatUtil.formatTanggalWaktu(trxSegar.getEstimasiSelesai()),
                    "Pembayaran Lunas",
                    JOptionPane.INFORMATION_MESSAGE
                );

                String teksStruk = CetakStrukUtil.susunTeksStruk(trxSegar, bayar);
                JTextArea ta = new JTextArea(teksStruk, 18, 38);
                ta.setFont(TemaWarna.FONT_MONO);
                ta.setEditable(false);

                int cetak = JOptionPane.showOptionDialog(
                    this,
                    new JScrollPane(ta),
                    "Struk Pembayaran",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new String[]{"Cetak Struk", "Tutup"},
                    "Cetak Struk"
                );

                if (cetak == 0) {
                    CetakStrukUtil.cetakKePrinter(this, teksStruk);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Pembayaran berhasil dicatat dengan struk " + bayar.getNomorStruk() +
                    ", tetapi struk gagal ditampilkan: " + ex.getMessage(),
                    "Pembayaran Tercatat",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    // Fitur: Tolak Pembayaran
    private void prosesTolak() {
        int baris = tabelAntrean.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan pada tabel di atas terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idTrx = (Long) modelAntrean.getValueAt(baris, 0);
        String noTrx = (String) modelAntrean.getValueAt(baris, 1);
        String alasan = JOptionPane.showInputDialog(this,
            "Alasan penolakan pembayaran untuk " + noTrx + ":",
            "Tolak Pembayaran",
            JOptionPane.PLAIN_MESSAGE
        );

        if (alasan != null && !alasan.trim().isEmpty()) {
            try {
                boolean berhasil = pembayaranService.tolakPembayaran(
                    idTrx, SessionManager.getIdPenggunaAktif(),
                    (String) cmbMetode.getSelectedItem(), alasan
                );
                if (berhasil) {
                    JOptionPane.showMessageDialog(this,
                        "Penolakan pembayaran dan alasannya berhasil dicatat.\nPelanggan dapat mencoba pembayaran kembali.",
                        "Pembayaran Ditolak",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Penolakan tidak tersimpan. Perbarui antrean dan coba lagi.",
                        "Penolakan Gagal",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
                muatAntrean();
                txtJumlahBayar.setText("");
                txtCatatan.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mencatat penolakan: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        } else if (alasan != null) {
            JOptionPane.showMessageDialog(this, "Alasan penolakan wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
        }
    }
}
