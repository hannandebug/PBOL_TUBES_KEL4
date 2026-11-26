package model;

/**
 * Interface MetodePembayaran
 * Menerapkan konsep Interface dalam Pemrograman Berorientasi Objek.
 * Menstandarkan kode, nama tampilan, dan petunjuk verifikasi metode pembayaran.
 */
public interface MetodePembayaran {

    /**
     * Mengembalikan kode metode yang cocok dengan CHECK constraint database
     * ('TUNAI', 'TRANSFER', 'QRIS', 'LAINNYA').
     */
    String getKodeMetode();

    /**
     * Mengembalikan nama tampilan user-friendly untuk GUI.
     */
    String getNamaTampilan();

    /**
     * Mengembalikan petunjuk langkah verifikasi bagi Petugas/Kasir.
     */
    String getPetunjukVerifikasiKasir();
}
