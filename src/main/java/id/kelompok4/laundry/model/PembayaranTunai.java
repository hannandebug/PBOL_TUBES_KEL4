package id.kelompok4.laundry.model;

/**
 * Implementasi MetodePembayaran untuk TUNAI / CASH.
 */
public class PembayaranTunai implements MetodePembayaran {

    @Override
    public String getKodeMetode() {
        return "TUNAI";
    }

    @Override
    public String getNamaTampilan() {
        return "Tunai / Cash";
    }

    @Override
    public String getPetunjukVerifikasiKasir() {
        return "Pastikan uang tunai sudah diterima sesuai nominal.";
    }
}
