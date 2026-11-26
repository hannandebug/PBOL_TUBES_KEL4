package id.kelompok4.laundry.model;

/**
 * Implementasi MetodePembayaran untuk TRANSFER BANK.
 */
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
