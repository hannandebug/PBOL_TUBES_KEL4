package id.kelompok4.laundry.model;

// OOP: Abstraction - interface menetapkan kontrak untuk setiap metode pembayaran.
public interface MetodePembayaran {

    // OOP: Abstraction - kontrak kode metode pembayaran
    String getKodeMetode();

    // OOP: Abstraction - kontrak nama tampilan metode pembayaran
    String getNamaTampilan();

    // OOP: Abstraction - kontrak petunjuk verifikasi pembayaran
    String getPetunjukVerifikasiKasir();
}
