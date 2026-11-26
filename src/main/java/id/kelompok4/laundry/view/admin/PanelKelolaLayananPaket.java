package id.kelompok4.laundry.view.admin;

import id.kelompok4.laundry.dao.LayananDAO;
import id.kelompok4.laundry.dao.PaketDAO;
import id.kelompok4.laundry.dao.TarifDAO;
import id.kelompok4.laundry.model.JenisLayanan;
import id.kelompok4.laundry.model.PaketLaundry;
import id.kelompok4.laundry.model.TarifLayanan;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.view.komponen.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * PanelKelolaLayananPaket
 * Pengelolaan daftar layanan, paket kecepatan, dan tarif per kg.
 */
public class PanelKelolaLayananPaket extends JPanel {

    private final LayananDAO layananDAO = new LayananDAO();
    private final PaketDAO paketDAO = new PaketDAO();
    private final TarifDAO tarifDAO = new TarifDAO();

    private final DefaultTableModel modelLayanan = new DefaultTableModel(
        new String[]{"ID", "Kode", "Nama Layanan", "Keterangan", "Status"}, 0
    );
    private final TabelKustom tabelLayanan = new TabelKustom(modelLayanan);

    private final DefaultTableModel modelPaket = new DefaultTableModel(
        new String[]{"ID", "Kode", "Nama Paket", "Durasi", "Keterangan", "Status"}, 0
    );
    private final TabelKustom tabelPaket = new TabelKustom(modelPaket);

    private final DefaultTableModel modelTarif = new DefaultTableModel(
        new String[]{"ID", "Jenis Layanan", "Paket", "Durasi", "Tarif / Kg", "Status"}, 0
    );
    private final TabelKustom tabelTarif = new TabelKustom(modelTarif);

    public PanelKelolaLayananPaket() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        muatSemuaData();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TemaWarna.FONT_BOLD);

        // ==========================================
        // TAB 1: JENIS LAYANAN
        // ==========================================
        PanelKartu kartuLayanan = new PanelKartu(new BorderLayout(0, 14), 18);
        JPanel headerLayanan = new JPanel(new BorderLayout(0, 8));
        headerLayanan.setOpaque(false);
        headerLayanan.add(TemaWarna.buatJudulBagian("Jenis Layanan Laundry"), BorderLayout.NORTH);

        JPanel toolbarLayanan = new JPanel(new GridLayout(1, 4, 8, 0));
        toolbarLayanan.setOpaque(false);

        TombolKustom btnTambahLayanan = TombolKustom.buatAksiUtama("+ Tambah Layanan");
        btnTambahLayanan.addActionListener(e -> dialogTambahLayanan());

        TombolKustom btnUbahLayanan = TombolKustom.buatPrimer("Edit");
        btnUbahLayanan.addActionListener(e -> dialogUbahLayanan());

        TombolKustom btnToggleLayanan = TombolKustom.buatSekunder("Aktif / Nonaktif");
        btnToggleLayanan.addActionListener(e -> toggleStatusLayanan());

        TombolKustom btnRefreshLayanan = TombolKustom.buatSekunder("Muat Ulang");
        btnRefreshLayanan.addActionListener(e -> muatDataLayanan());

        toolbarLayanan.add(btnTambahLayanan);
        toolbarLayanan.add(btnUbahLayanan);
        toolbarLayanan.add(btnToggleLayanan);
        toolbarLayanan.add(btnRefreshLayanan);
        headerLayanan.add(toolbarLayanan, BorderLayout.CENTER);

        kartuLayanan.add(headerLayanan, BorderLayout.NORTH);
        tabelLayanan.sembunyikanKolom(0);
        tabelLayanan.getColumnModel().getColumn(4).setCellRenderer(new BadgeStatusRenderer());
        kartuLayanan.add(TemaWarna.bungkusTabel(tabelLayanan), BorderLayout.CENTER);
        tabbedPane.addTab("Jenis Layanan", kartuLayanan);

        // ==========================================
        // TAB 2: PAKET KECEPATAN
        // ==========================================
        PanelKartu kartuPaket = new PanelKartu(new BorderLayout(0, 14), 18);
        JPanel headerPaket = new JPanel(new BorderLayout(0, 8));
        headerPaket.setOpaque(false);
        headerPaket.add(TemaWarna.buatJudulBagian("Paket Kecepatan Cucian"), BorderLayout.NORTH);

        JPanel toolbarPaket = new JPanel(new GridLayout(1, 4, 8, 0));
        toolbarPaket.setOpaque(false);

        TombolKustom btnTambahPaket = TombolKustom.buatAksiUtama("+ Tambah Paket");
        btnTambahPaket.addActionListener(e -> dialogTambahPaket());

        TombolKustom btnUbahPaket = TombolKustom.buatPrimer("Edit");
        btnUbahPaket.addActionListener(e -> dialogUbahPaket());

        TombolKustom btnTogglePaket = TombolKustom.buatSekunder("Aktif / Nonaktif");
        btnTogglePaket.addActionListener(e -> toggleStatusPaket());

        TombolKustom btnRefreshPaket = TombolKustom.buatSekunder("Muat Ulang");
        btnRefreshPaket.addActionListener(e -> muatDataPaket());

        toolbarPaket.add(btnTambahPaket);
        toolbarPaket.add(btnUbahPaket);
        toolbarPaket.add(btnTogglePaket);
        toolbarPaket.add(btnRefreshPaket);
        headerPaket.add(toolbarPaket, BorderLayout.CENTER);

        kartuPaket.add(headerPaket, BorderLayout.NORTH);
        tabelPaket.sembunyikanKolom(0);
        tabelPaket.getColumnModel().getColumn(5).setCellRenderer(new BadgeStatusRenderer());
        kartuPaket.add(TemaWarna.bungkusTabel(tabelPaket), BorderLayout.CENTER);
        tabbedPane.addTab("Paket Cucian", kartuPaket);

        // ==========================================
        // TAB 3: TARIF PER KG
        // ==========================================
        PanelKartu kartuTarif = new PanelKartu(new BorderLayout(0, 14), 18);
        JPanel headerTarif = new JPanel(new BorderLayout(0, 8));
        headerTarif.setOpaque(false);
        headerTarif.add(TemaWarna.buatJudulBagian("Daftar Tarif per Kilogram"), BorderLayout.NORTH);

        JPanel toolbarTarif = new JPanel(new GridLayout(1, 4, 8, 0));
        toolbarTarif.setOpaque(false);

        TombolKustom btnAturTarifBaru = TombolKustom.buatAksiUtama("+ Atur Tarif Baru");
        btnAturTarifBaru.addActionListener(e -> dialogAturTarifBaru());

        TombolKustom btnUbahTarif = TombolKustom.buatPrimer("Ubah Harga");
        btnUbahTarif.addActionListener(e -> dialogUbahTarif());

        TombolKustom btnToggleTarif = TombolKustom.buatSekunder("Aktif / Nonaktif");
        btnToggleTarif.addActionListener(e -> toggleStatusTarif());

        TombolKustom btnRefreshTarif = TombolKustom.buatSekunder("Muat Ulang");
        btnRefreshTarif.addActionListener(e -> muatDataTarif());

        toolbarTarif.add(btnAturTarifBaru);
        toolbarTarif.add(btnUbahTarif);
        toolbarTarif.add(btnToggleTarif);
        toolbarTarif.add(btnRefreshTarif);
        headerTarif.add(toolbarTarif, BorderLayout.CENTER);

        kartuTarif.add(headerTarif, BorderLayout.NORTH);
        tabelTarif.sembunyikanKolom(0);
        tabelTarif.getColumnModel().getColumn(5).setCellRenderer(new BadgeStatusRenderer());
        kartuTarif.add(TemaWarna.bungkusTabel(tabelTarif), BorderLayout.CENTER);
        tabbedPane.addTab("Daftar Tarif", kartuTarif);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void muatSemuaData() {
        muatDataLayanan();
        muatDataPaket();
        muatDataTarif();
    }

    public void muatDataLayanan() {
        modelLayanan.setRowCount(0);
        try {
            List<JenisLayanan> list = layananDAO.ambilSemua();
            for (JenisLayanan l : list) {
                modelLayanan.addRow(new Object[]{
                    l.getIdJenisLayanan(),
                    l.getKodeLayanan(),
                    l.getNamaLayanan(),
                    l.getKeterangan() != null ? l.getKeterangan() : "-",
                    l.isStatusAktif() ? "AKTIF" : "NONAKTIF"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat layanan: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dialogTambahLayanan() {
        JTextField txtKode = new JTextField(18);
        JTextField txtNama = new JTextField(18);
        JTextField txtKet = new JTextField(18);
        TemaWarna.aturPlaceholder(txtKode, "Contoh: DRY_CLEAN");
        TemaWarna.aturPlaceholder(txtNama, "Contoh: Cuci Kering");
        TemaWarna.aturPlaceholder(txtKet, "Opsional");

        JPanel pnl = buatFormPanel(
            new String[]{"Kode Layanan", "Nama Layanan", "Keterangan"},
            new JComponent[]{txtKode, txtNama, txtKet}
        );

        int ok = JOptionPane.showConfirmDialog(this, pnl, "Tambah Layanan Baru", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            String kode = txtKode.getText().trim().toUpperCase();
            String nama = txtNama.getText().trim();
            String ket = txtKet.getText().trim();

            if (kode.isEmpty() || nama.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kode dan nama layanan wajib diisi.", "Data Belum Lengkap", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                JenisLayanan jl = new JenisLayanan();
                jl.setKodeLayanan(kode);
                jl.setNamaLayanan(nama);
                jl.setKeterangan(ket);
                jl.setStatusAktif(true);

                if (layananDAO.tambah(jl)) {
                    muatDataLayanan();
                    int tanya = JOptionPane.showConfirmDialog(this,
                        String.format("Layanan '%s' sudah tersimpan.\nIngin langsung pasang tarif untuk semua paket yang tersedia?", nama),
                        "Atur Tarif Sekarang?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );

                    if (tanya == JOptionPane.YES_OPTION) {
                        dialogAturTarifUntukLayananBaru(jl);
                    }
                    muatSemuaData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menambah layanan: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dialogAturTarifUntukLayananBaru(JenisLayanan jl) {
        try {
            List<PaketLaundry> paketList = paketDAO.ambilSemuaAktif();
            if (paketList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Belum ada paket aktif.", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String inputHarga = JOptionPane.showInputDialog(this,
                String.format("Masukkan harga dasar per kg untuk '%s' (contoh: 10000):", jl.getNamaLayanan()),
                "Atur Tarif",
                JOptionPane.PLAIN_MESSAGE
            );

            if (inputHarga != null && !inputHarga.trim().isEmpty()) {
                BigDecimal hargaDasar = new BigDecimal(inputHarga.trim());
                if (hargaDasar.compareTo(BigDecimal.ZERO) > 0) {
                    for (PaketLaundry pl : paketList) {
                        tarifDAO.simpanAtauUpdateTarif(jl.getIdJenisLayanan(), pl.getIdPaket(), hargaDasar, true);
                    }
                    JOptionPane.showMessageDialog(this, "Tarif berhasil dipasang ke semua paket.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    muatDataTarif();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengatur tarif: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dialogUbahLayanan() {
        int baris = tabelLayanan.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih layanan pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idLayanan = (Long) modelLayanan.getValueAt(baris, 0);
        String namaLama = (String) modelLayanan.getValueAt(baris, 2);
        String ketLama = (String) modelLayanan.getValueAt(baris, 3);

        JTextField txtNama = new JTextField(namaLama, 18);
        JTextField txtKet = new JTextField("-".equals(ketLama) ? "" : ketLama, 18);

        JPanel pnl = buatFormPanel(
            new String[]{"Nama Layanan", "Keterangan"},
            new JComponent[]{txtNama, txtKet}
        );

        int ok = JOptionPane.showConfirmDialog(this, pnl, "Edit Layanan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                JenisLayanan jl = new JenisLayanan();
                jl.setIdJenisLayanan(idLayanan);
                jl.setNamaLayanan(txtNama.getText().trim());
                jl.setKeterangan(txtKet.getText().trim());
                jl.setStatusAktif(true);

                layananDAO.ubah(jl);
                JOptionPane.showMessageDialog(this, "Data layanan berhasil diperbarui.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                muatSemuaData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah layanan: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleStatusLayanan() {
        int baris = tabelLayanan.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih layanan pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idLayanan = (Long) modelLayanan.getValueAt(baris, 0);
        String nama = (String) modelLayanan.getValueAt(baris, 2);
        String statusSaatIni = (String) modelLayanan.getValueAt(baris, 4);

        boolean aktifBaru = !"AKTIF".equalsIgnoreCase(statusSaatIni);
        int konf = JOptionPane.showConfirmDialog(this,
            String.format("%s layanan '%s'?", aktifBaru ? "Aktifkan" : "Nonaktifkan", nama),
            "Konfirmasi Status",
            JOptionPane.YES_NO_OPTION
        );

        if (konf == JOptionPane.YES_OPTION) {
            try {
                layananDAO.setAktif(idLayanan, aktifBaru);
                muatSemuaData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah status: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void muatDataPaket() {
        modelPaket.setRowCount(0);
        try {
            List<PaketLaundry> list = paketDAO.ambilSemua();
            for (PaketLaundry p : list) {
                modelPaket.addRow(new Object[]{
                    p.getIdPaket(),
                    p.getKodePaket(),
                    p.getNamaPaket(),
                    p.getDurasiJam() + " Jam",
                    p.getKeterangan() != null ? p.getKeterangan() : "-",
                    p.isStatusAktif() ? "AKTIF" : "NONAKTIF"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat paket: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dialogTambahPaket() {
        JTextField txtKode = new JTextField(18);
        JTextField txtNama = new JTextField(18);
        JTextField txtDurasi = new JTextField(18);
        JTextField txtKet = new JTextField(18);

        TemaWarna.aturPlaceholder(txtKode, "Contoh: KILAT");
        TemaWarna.aturPlaceholder(txtNama, "Contoh: Super Kilat");
        TemaWarna.aturPlaceholder(txtDurasi, "Angka dalam jam, misal: 3");
        TemaWarna.aturPlaceholder(txtKet, "Opsional");

        JPanel pnl = buatFormPanel(
            new String[]{"Kode Paket", "Nama Paket", "Durasi (Jam)", "Keterangan"},
            new JComponent[]{txtKode, txtNama, txtDurasi, txtKet}
        );

        int ok = JOptionPane.showConfirmDialog(this, pnl, "Tambah Paket Baru", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                int durasi = Integer.parseInt(txtDurasi.getText().trim());
                PaketLaundry pl = new PaketLaundry();
                pl.setKodePaket(txtKode.getText().trim().toUpperCase());
                pl.setNamaPaket(txtNama.getText().trim());
                pl.setDurasiJam(durasi);
                pl.setKeterangan(txtKet.getText().trim());
                pl.setStatusAktif(true);

                paketDAO.tambah(pl);
                JOptionPane.showMessageDialog(this, "Paket baru berhasil ditambahkan.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                muatSemuaData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menambah paket: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dialogUbahPaket() {
        int baris = tabelPaket.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih paket pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idPaket = (Long) modelPaket.getValueAt(baris, 0);
        String namaLama = (String) modelPaket.getValueAt(baris, 2);
        String durasiLama = modelPaket.getValueAt(baris, 3).toString().replace(" Jam", "");
        String ketLama = (String) modelPaket.getValueAt(baris, 4);

        JTextField txtNama = new JTextField(namaLama, 18);
        JTextField txtDurasi = new JTextField(durasiLama, 18);
        JTextField txtKet = new JTextField("-".equals(ketLama) ? "" : ketLama, 18);

        JPanel pnl = buatFormPanel(
            new String[]{"Nama Paket", "Durasi (Jam)", "Keterangan"},
            new JComponent[]{txtNama, txtDurasi, txtKet}
        );

        int ok = JOptionPane.showConfirmDialog(this, pnl, "Edit Paket", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                int durasi = Integer.parseInt(txtDurasi.getText().trim());
                PaketLaundry pl = new PaketLaundry();
                pl.setIdPaket(idPaket);
                pl.setNamaPaket(txtNama.getText().trim());
                pl.setDurasiJam(durasi);
                pl.setKeterangan(txtKet.getText().trim());
                pl.setStatusAktif(true);

                paketDAO.ubah(pl);
                JOptionPane.showMessageDialog(this, "Data paket berhasil diperbarui.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                muatSemuaData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah paket: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleStatusPaket() {
        int baris = tabelPaket.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih paket pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idPaket = (Long) modelPaket.getValueAt(baris, 0);
        String nama = (String) modelPaket.getValueAt(baris, 2);
        String statusSaatIni = (String) modelPaket.getValueAt(baris, 5);

        boolean aktifBaru = !"AKTIF".equalsIgnoreCase(statusSaatIni);
        int konf = JOptionPane.showConfirmDialog(this,
            String.format("%s paket '%s'?", aktifBaru ? "Aktifkan" : "Nonaktifkan", nama),
            "Konfirmasi Status",
            JOptionPane.YES_NO_OPTION
        );

        if (konf == JOptionPane.YES_OPTION) {
            try {
                paketDAO.setAktif(idPaket, aktifBaru);
                muatSemuaData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah status: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void muatDataTarif() {
        modelTarif.setRowCount(0);
        try {
            List<TarifLayanan> list = tarifDAO.ambilSemuaTarif();
            for (TarifLayanan t : list) {
                modelTarif.addRow(new Object[]{
                    t.getIdTarif(),
                    t.getNamaLayanan(),
                    t.getNamaPaket(),
                    t.getDurasiJam() + " Jam",
                    FormatUtil.formatRupiah(t.getHargaPerKg()),
                    t.isStatusAktif() ? "AKTIF" : "NONAKTIF"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat tarif: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dialogAturTarifBaru() {
        try {
            List<JenisLayanan> layananList = layananDAO.ambilSemua();
            List<PaketLaundry> paketList = paketDAO.ambilSemua();

            if (layananList.isEmpty() || paketList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pastikan layanan dan paket sudah tersedia.", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<JenisLayanan> cmbLayanan = new JComboBox<>(layananList.toArray(new JenisLayanan[0]));
            JComboBox<PaketLaundry> cmbPaket = new JComboBox<>(paketList.toArray(new PaketLaundry[0]));
            JTextField txtHarga = new JTextField("10000", 15);

            JPanel pnl = buatFormPanel(
                new String[]{"Layanan", "Paket", "Tarif / Kg (Rp)"},
                new JComponent[]{cmbLayanan, cmbPaket, txtHarga}
            );

            int ok = JOptionPane.showConfirmDialog(this, pnl, "Atur Tarif Layanan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok == JOptionPane.OK_OPTION) {
                JenisLayanan jl = (JenisLayanan) cmbLayanan.getSelectedItem();
                PaketLaundry pl = (PaketLaundry) cmbPaket.getSelectedItem();
                BigDecimal harga = new BigDecimal(txtHarga.getText().trim());

                if (harga.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "Harga per kg harus lebih dari 0.", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                tarifDAO.simpanAtauUpdateTarif(jl.getIdJenisLayanan(), pl.getIdPaket(), harga, true);
                JOptionPane.showMessageDialog(this, "Tarif berhasil disimpan.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                muatDataTarif();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengatur tarif: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dialogUbahTarif() {
        int baris = tabelTarif.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih tarif pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idTarif = (Long) modelTarif.getValueAt(baris, 0);
        String layanan = (String) modelTarif.getValueAt(baris, 1);
        String paket = (String) modelTarif.getValueAt(baris, 2);

        String input = JOptionPane.showInputDialog(this,
            String.format("Tarif baru per kg untuk %s (%s):", layanan, paket),
            "Ubah Harga",
            JOptionPane.PLAIN_MESSAGE
        );

        if (input != null && !input.trim().isEmpty()) {
            try {
                BigDecimal hargaBaru = new BigDecimal(input.trim());
                if (hargaBaru.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "Harga harus lebih dari 0.", "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                for (TarifLayanan t : tarifDAO.ambilSemuaTarif()) {
                    if (t.getIdTarif().equals(idTarif)) {
                        tarifDAO.simpanAtauUpdateTarif(t.getIdJenisLayanan(), t.getIdPaket(), hargaBaru, true);
                        JOptionPane.showMessageDialog(this, "Harga berhasil diperbarui.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                        muatDataTarif();
                        break;
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Angka tidak valid: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleStatusTarif() {
        int baris = tabelTarif.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih tarif pada tabel terlebih dahulu.", "Pilih Baris", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long idTarif = (Long) modelTarif.getValueAt(baris, 0);
        String layanan = (String) modelTarif.getValueAt(baris, 1);
        String paket = (String) modelTarif.getValueAt(baris, 2);
        String statusSaatIni = (String) modelTarif.getValueAt(baris, 5);

        boolean aktifBaru = !"AKTIF".equalsIgnoreCase(statusSaatIni);
        int konf = JOptionPane.showConfirmDialog(this,
            String.format("%s tarif %s (%s)?", aktifBaru ? "Aktifkan" : "Nonaktifkan", layanan, paket),
            "Konfirmasi Status",
            JOptionPane.YES_NO_OPTION
        );

        if (konf == JOptionPane.YES_OPTION) {
            try {
                for (TarifLayanan t : tarifDAO.ambilSemuaTarif()) {
                    if (t.getIdTarif().equals(idTarif)) {
                        tarifDAO.simpanAtauUpdateTarif(t.getIdJenisLayanan(), t.getIdPaket(), t.getHargaPerKg(), aktifBaru);
                        muatDataTarif();
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah status tarif: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel buatFormPanel(String[] labels, JComponent[] fields) {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBorder(new EmptyBorder(6, 4, 6, 4));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.35;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(TemaWarna.FONT_BOLD);
            lbl.setForeground(TemaWarna.TEKS_GELAP);
            pnl.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.65;
            fields[i].setPreferredSize(new Dimension(220, 34));
            pnl.add(fields[i], gbc);
        }
        return pnl;
    }
}
