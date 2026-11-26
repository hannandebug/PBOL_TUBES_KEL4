package model;

// OOP: Polymorphism - class ini memberi implementasi berbeda untuk MetodePembayaran.
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
