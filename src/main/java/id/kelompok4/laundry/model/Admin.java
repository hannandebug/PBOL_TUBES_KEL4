package id.kelompok4.laundry.model;

/**
 * Class Admin
 * Subclass dari Pengguna yang merepresentasikan peran Admin / Pemilik Laundry.
 * Menerapkan prinsip Inheritance dan Method Overriding.
 */
public class Admin extends Pengguna {

    public Admin() {
        super();
        setPeran("ADMIN");
    }

    public Admin(Long idPengguna, String namaLengkap, String namaPengguna, 
                 String kataSandiHash, String nomorTelepon, boolean statusAktif) {
        super(idPengguna, namaLengkap, namaPengguna, kataSandiHash, "ADMIN", nomorTelepon, statusAktif);
    }

    @Override
    public String getNamaPeran() {
        return "Admin / Pemilik";
    }

    @Override
    public String getDeskripsiPeran() {
        return "Memiliki wewenang manajerial: kelola layanan, tarif, akun petugas, persetujuan pembatalan/refund, dan laporan.";
    }
}
