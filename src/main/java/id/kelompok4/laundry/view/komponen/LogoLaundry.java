package id.kelompok4.laundry.view.komponen;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;

// Fitur: Identitas RapiWash
public final class LogoLaundry {

    private static final String RESOURCE_PATH = "/assets/logo.png";
    private static final ImageIcon LOGO = muatLogo();

    private LogoLaundry() {
    }

    public static ImageIcon buatIkon(int lebar, int tinggi) {
        if (lebar <= 0 || tinggi <= 0) {
            throw new IllegalArgumentException("Ukuran logo harus lebih besar dari 0.");
        }
        Image gambarSkala = LOGO.getImage().getScaledInstance(lebar, tinggi, Image.SCALE_SMOOTH);
        return new ImageIcon(gambarSkala);
    }

    public static Image ambilGambar() {
        return LOGO.getImage();
    }

    private static ImageIcon muatLogo() {
        URL sumber = LogoLaundry.class.getResource(RESOURCE_PATH);
        if (sumber == null) {
            throw new IllegalStateException("Logo laundry tidak ditemukan: " + RESOURCE_PATH);
        }

        ImageIcon logo = new ImageIcon(sumber);
        if (logo.getIconWidth() <= 0 || logo.getIconHeight() <= 0) {
            throw new IllegalStateException("Logo laundry tidak dapat dibaca: " + RESOURCE_PATH);
        }
        return logo;
    }
}
