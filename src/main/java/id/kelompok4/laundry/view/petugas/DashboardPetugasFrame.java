package id.kelompok4.laundry.view.petugas;

import id.kelompok4.laundry.view.komponen.HeaderBar;
import id.kelompok4.laundry.view.komponen.LogoLaundry;
import id.kelompok4.laundry.view.komponen.SidebarNav;
import id.kelompok4.laundry.view.komponen.TemaWarna;
import id.kelompok4.laundry.view.login.FormLogin;

import javax.swing.*;
import java.awt.*;

/**
 * DashboardPetugasFrame
 * Jendela kerja utama Petugas / Kasir Laundry.
 */
public class DashboardPetugasFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pnlKonten = new JPanel(cardLayout);
    private HeaderBar headerBar;

    private final PanelTransaksiBaru panelTransaksiBaru = new PanelTransaksiBaru();
    private final PanelVerifikasiPembayaran panelVerifikasi = new PanelVerifikasiPembayaran();
    private final PanelPengerjaanLaundry panelPengerjaan = new PanelPengerjaanLaundry();
    private final PanelPengambilanCucian panelPengambilan = new PanelPengambilanCucian();
    private final PanelPenelusuranKasir panelPenelusuran = new PanelPenelusuranKasir();
    private final PanelRefundKasir panelRefund = new PanelRefundKasir();

    public DashboardPetugasFrame() {
        super("RapiWash — Kasir");
        setSize(1200, 740);
        setMinimumSize(new Dimension(1020, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(LogoLaundry.ambilGambar());

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(TemaWarna.LATAR_BELAKANG);

        SidebarNav sidebar = new SidebarNav("RapiWash", "Kasir & Operasional");
        tambahNavigasi(sidebar, "Buat Pesanan", "Buat Pesanan Baru", "TRX_BARU", panelTransaksiBaru::muatDataMaster);
        tambahNavigasi(sidebar, "Pembayaran", "Konfirmasi Pembayaran", "BAYAR", panelVerifikasi::muatAntrean);
        tambahNavigasi(sidebar, "Proses Cuci", "Status Pengerjaan Cucian", "PENGERJAAN", panelPengerjaan::muatData);
        tambahNavigasi(sidebar, "Pengambilan", "Penyerahan Cucian Selesai", "PENGAMBILAN", panelPengambilan::muatData);
        tambahNavigasi(sidebar, "Lacak Komplain", "Penelusuran Barang Bermasalah", "PENELUSURAN", panelPenelusuran::muatData);
        tambahNavigasi(sidebar, "Pencairan Refund", "Pengembalian Dana Pelanggan", "REFUND", panelRefund::muatData);
        sidebar.tambahSpacerBawah();

        root.add(sidebar, BorderLayout.WEST);

        headerBar = new HeaderBar("Buat Pesanan Baru", () -> {
            new FormLogin().setVisible(true);
            dispose();
        });
        root.add(headerBar, BorderLayout.NORTH);

        pnlKonten.setBackground(TemaWarna.LATAR_BELAKANG);
        pnlKonten.add(panelTransaksiBaru, "TRX_BARU");
        pnlKonten.add(panelVerifikasi, "BAYAR");
        pnlKonten.add(panelPengerjaan, "PENGERJAAN");
        pnlKonten.add(panelPengambilan, "PENGAMBILAN");
        pnlKonten.add(panelPenelusuran, "PENELUSURAN");
        pnlKonten.add(panelRefund, "REFUND");

        root.add(pnlKonten, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void tambahNavigasi(SidebarNav sidebar, String menuLabel, String judulHeader, String cardName, Runnable onSelect) {
        sidebar.tambahMenu(menuLabel, () -> {
            cardLayout.show(pnlKonten, cardName);
            headerBar.setJudulLayar(judulHeader);
            if (onSelect != null) {
                onSelect.run();
            }
        });
    }
}
