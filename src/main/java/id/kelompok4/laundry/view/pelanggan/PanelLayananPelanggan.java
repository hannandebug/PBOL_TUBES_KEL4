package id.kelompok4.laundry.view.pelanggan;

import id.kelompok4.laundry.dao.TarifDAO;
import id.kelompok4.laundry.model.TarifLayanan;
import id.kelompok4.laundry.util.FormatUtil;
import id.kelompok4.laundry.view.komponen.PanelKartu;
import id.kelompok4.laundry.view.komponen.TabelKustom;
import id.kelompok4.laundry.view.komponen.TemaWarna;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * PanelLayananPelanggan
 * Daftar layanan, paket, durasi, dan tarif resmi yang berlaku di gerai.
 */
public class PanelLayananPelanggan extends JPanel {

    private final TarifDAO tarifDAO = new TarifDAO();

    private final DefaultTableModel modelKatalog = new DefaultTableModel(
        new String[]{"Layanan", "Paket", "Estimasi Selesai", "Tarif / Kg", "Ketentuan"}, 0
    );
    private final TabelKustom tabelKatalog = new TabelKustom(modelKatalog);

    public PanelLayananPelanggan() {
        setLayout(new BorderLayout());
        setBackground(TemaWarna.LATAR_BELAKANG);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        muatDataKatalog();
    }

    private void initUI() {
        PanelKartu kartu = new PanelKartu(new BorderLayout(0, 14), 20);

        JPanel pnlHeader = new JPanel(new BorderLayout(0, 4));
        pnlHeader.setOpaque(false);

        JLabel lblJudul = TemaWarna.buatJudulBagian("Daftar Layanan & Tarif");
        JLabel lblSub = new JLabel("Harga resmi yang berlaku saat ini. Datang langsung ke gerai untuk menimbang cucian Anda.");
        lblSub.setFont(TemaWarna.FONT_KECIL);
        lblSub.setForeground(TemaWarna.ABU_TEKS);

        pnlHeader.add(lblJudul, BorderLayout.NORTH);
        pnlHeader.add(lblSub, BorderLayout.SOUTH);
        kartu.add(pnlHeader, BorderLayout.NORTH);

        kartu.add(TemaWarna.bungkusTabel(tabelKatalog), BorderLayout.CENTER);
        add(kartu, BorderLayout.CENTER);
    }

    public void muatDataKatalog() {
        modelKatalog.setRowCount(0);
        try {
            List<TarifLayanan> list = tarifDAO.ambilTarifAktif();
            for (TarifLayanan t : list) {
                modelKatalog.addRow(new Object[]{
                    t.getNamaLayanan(),
                    t.getNamaPaket(),
                    t.getDurasiJam() + " Jam",
                    FormatUtil.formatRupiah(t.getHargaPerKg()),
                    "Minimal berat 2 kg"
                });
            }
        } catch (Exception e) {
            System.err.println("[PanelLayananPelanggan] Gagal muat katalog: " + e.getMessage());
        }
    }
}
