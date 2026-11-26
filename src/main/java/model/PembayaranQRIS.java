package model;

/**
 * Implementasi MetodePembayaran untuk QRIS.
 */
public class PembayaranQRIS implements MetodePembayaran {

    @Override
    public String getKodeMetode() {
        return "QRIS";
    }

    @Override
    public String getNamaTampilan() {
        return "QRIS";
    }

    @Override
    public String getPetunjukVerifikasiKasir() {
        return "Cek notifikasi berhasil pada aplikasi merchant QRIS.";
    }
}
