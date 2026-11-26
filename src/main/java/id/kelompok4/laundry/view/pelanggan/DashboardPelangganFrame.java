package id.kelompok4.laundry.view.pelanggan;

import id.kelompok4.laundry.view.komponen.HeaderBar;
import id.kelompok4.laundry.view.komponen.LogoLaundry;
import id.kelompok4.laundry.view.komponen.SidebarNav;
import id.kelompok4.laundry.view.komponen.TemaWarna;
import id.kelompok4.laundry.view.login.FormLogin;

import javax.swing.*;
import java.awt.*;

// Fitur: Dashboard Pelanggan
// OOP: Inheritance - DashboardPelangganFrame mewarisi JFrame.
public class DashboardPelangganFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pnlKonten = new JPanel(cardLayout);
    private HeaderBar headerBar;

    private final PanelRingkasanPelanggan panelRingkasan = new PanelRingkasanPelanggan();
    private final PanelRiwayatPelanggan panelRiwayat = new PanelRiwayatPelanggan();
    private final PanelLayananPelanggan panelLayanan = new PanelLayananPelanggan();
    private final PanelPengaduanPelanggan panelPengaduan = new PanelPengaduanPelanggan();

    public DashboardPelangganFrame() {
        super("RapiWash — Pelanggan");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(LogoLaundry.ambilGambar());

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(TemaWarna.LATAR_BELAKANG);

        SidebarNav sidebar = new SidebarNav("RapiWash", "Area Pelanggan");
        tambahNavigasi(sidebar, "Beranda", "Beranda Saya", "BERANDA", panelRingkasan::muatData);
        tambahNavigasi(sidebar, "Cucian & Struk", "Riwayat Cucian & Struk", "RIWAYAT", panelRiwayat::muatRiwayat);
        tambahNavigasi(sidebar, "Daftar Harga", "Layanan & Tarif Resmi", "KATALOG", panelLayanan::muatDataKatalog);
        tambahNavigasi(sidebar, "Pengaduan", "Pengaduan & Solusi", "PENGADUAN", panelPengaduan::muatPengaduan);
        sidebar.tambahSpacerBawah();

        root.add(sidebar, BorderLayout.WEST);

        headerBar = new HeaderBar("Beranda Saya", () -> {
            new FormLogin().setVisible(true);
            dispose();
        });
        root.add(headerBar, BorderLayout.NORTH);

        pnlKonten.setBackground(TemaWarna.LATAR_BELAKANG);
        pnlKonten.add(panelRingkasan, "BERANDA");
        pnlKonten.add(panelRiwayat, "RIWAYAT");
        pnlKonten.add(panelLayanan, "KATALOG");
        pnlKonten.add(panelPengaduan, "PENGADUAN");

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
