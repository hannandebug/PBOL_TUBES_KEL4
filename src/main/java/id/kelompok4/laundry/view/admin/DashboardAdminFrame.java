package id.kelompok4.laundry.view.admin;

import id.kelompok4.laundry.view.komponen.HeaderBar;
import id.kelompok4.laundry.view.komponen.LogoLaundry;
import id.kelompok4.laundry.view.komponen.SidebarNav;
import id.kelompok4.laundry.view.komponen.TemaWarna;
import id.kelompok4.laundry.view.login.FormLogin;

import javax.swing.*;
import java.awt.*;

// Fitur: Dashboard Admin
// OOP: Inheritance - DashboardAdminFrame mewarisi JFrame.
public class DashboardAdminFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pnlKonten = new JPanel(cardLayout);
    private HeaderBar headerBar;

    private final PanelRingkasanAdmin panelRingkasan = new PanelRingkasanAdmin();
    private final PanelKelolaLayananPaket panelLayananPaket = new PanelKelolaLayananPaket();
    private final PanelKelolaPetugas panelPetugas = new PanelKelolaPetugas();
    private final PanelKelolaPengaduanAdmin panelPengaduan = new PanelKelolaPengaduanAdmin();
    private final PanelKelolaPembatalanAdmin panelPembatalan = new PanelKelolaPembatalanAdmin();
    private final PanelLaporanAdmin panelLaporan = new PanelLaporanAdmin();

    public DashboardAdminFrame() {
        super("RapiWash — Pemilik");
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

        SidebarNav sidebar = new SidebarNav("RapiWash", "Panel Pemilik");
        tambahNavigasi(sidebar, "Ringkasan", "Ringkasan Usaha", "RINGKASAN", panelRingkasan::muatData);
        tambahNavigasi(sidebar, "Layanan & Tarif", "Daftar Layanan, Paket & Tarif", "LAYANAN_PAKET", panelLayananPaket::muatSemuaData);
        tambahNavigasi(sidebar, "Kelola Petugas", "Akun Petugas / Kasir", "PETUGAS", panelPetugas::muatDataPetugas);
        tambahNavigasi(sidebar, "Pengaduan", "Penanganan Komplain Pelanggan", "PENGADUAN", panelPengaduan::muatData);
        tambahNavigasi(sidebar, "Pembatalan & Refund", "Permohonan Batal & Refund", "PEMBATALAN", panelPembatalan::muatData);
        tambahNavigasi(sidebar, "Laporan & Keuangan", "Laporan Transaksi & Kas", "LAPORAN", panelLaporan::muatSemuaData);
        sidebar.tambahSpacerBawah();

        root.add(sidebar, BorderLayout.WEST);

        headerBar = new HeaderBar("Ringkasan Usaha", () -> {
            new FormLogin().setVisible(true);
            dispose();
        });
        root.add(headerBar, BorderLayout.NORTH);

        pnlKonten.setBackground(TemaWarna.LATAR_BELAKANG);
        pnlKonten.add(panelRingkasan, "RINGKASAN");
        pnlKonten.add(panelLayananPaket, "LAYANAN_PAKET");
        pnlKonten.add(panelPetugas, "PETUGAS");
        pnlKonten.add(panelPengaduan, "PENGADUAN");
        pnlKonten.add(panelPembatalan, "PEMBATALAN");
        pnlKonten.add(panelLaporan, "LAPORAN");

        root.add(pnlKonten, BorderLayout.CENTER);
        setContentPane(root);
    }

    // Fitur: Tambah Navigasi
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
