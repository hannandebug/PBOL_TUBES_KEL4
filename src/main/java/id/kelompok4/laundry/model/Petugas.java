package id.kelompok4.laundry.model;

// OOP: Inheritance - subclass ini mewarisi data dan perilaku dari Pengguna.
// OOP: Polymorphism - perilaku peran disediakan melalui method overriding.
public class Petugas extends Pengguna {

    public Petugas() {
        super();
        setPeran("PETUGAS");
    }

    public Petugas(Long idPengguna, String namaLengkap, String namaPengguna, 
                   String kataSandiHash, String nomorTelepon, boolean statusAktif) {
        super(idPengguna, namaLengkap, namaPengguna, kataSandiHash, "PETUGAS", nomorTelepon, statusAktif);
    }

    @Override
    public String getNamaPeran() {
        return "Petugas / Kasir";
    }

    @Override
    public String getDeskripsiPeran() {
        return "Menjalankan operasional gerai: catat transaksi, periksa pembayaran, pengerjaan cucian, dan serah terima pakaian.";
    }
}
