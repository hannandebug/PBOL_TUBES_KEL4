package id.kelompok4.laundry.model;

// OOP: Polymorphism - class ini memberi implementasi berbeda untuk MetodePembayaran.
public class PembayaranTransfer implements MetodePembayaran {

    @Override
    public String getKodeMetode() {
        return "TRANSFER";
    }

    @Override
    public String getNamaTampilan() {
        return "Transfer Bank";
    }

    @Override
    public String getPetunjukVerifikasiKasir() {
        return "Cek mutasi rekening terlebih dahulu sebelum konfirmasi.";
    }
}
