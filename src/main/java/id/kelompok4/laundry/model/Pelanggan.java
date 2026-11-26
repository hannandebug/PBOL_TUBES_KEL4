package id.kelompok4.laundry.model;

/**
 * Class Pelanggan
 * Subclass dari Pengguna yang merepresentasikan Pelanggan Laundry.
 * Memiliki relasi 1-to-1 dengan tabel pelanggan di database.
 * Menerapkan prinsip Inheritance, Encapsulation, dan Method Overriding.
 */
public class Pelanggan extends Pengguna {

    private Long idPelanggan;
    private String kodePelanggan;
    private String alamat;
    private String catatan;

    public Pelanggan() {
        super();
        setPeran("PELANGGAN");
    }

    public Pelanggan(Long idPengguna, String namaLengkap, String namaPengguna, 
                     String kataSandiHash, String nomorTelepon, boolean statusAktif,
                     Long idPelanggan, String kodePelanggan, String alamat, String catatan) {
        super(idPengguna, namaLengkap, namaPengguna, kataSandiHash, "PELANGGAN", nomorTelepon, statusAktif);
        this.idPelanggan = idPelanggan;
        this.kodePelanggan = kodePelanggan;
        this.alamat = alamat;
        this.catatan = catatan;
    }

    @Override
    public String getNamaPeran() {
        return "Pelanggan";
    }

    @Override
    public String getDeskripsiPeran() {
        return "Pengguna layanan laundry: pantau status cucian, lihat riwayat, cetak struk, dan ajukan pengaduan.";
    }

    // Getter dan Setter khusus profil Pelanggan
    public Long getIdPelanggan() { return idPelanggan; }
    public void setIdPelanggan(Long idPelanggan) { this.idPelanggan = idPelanggan; }

    public String getKodePelanggan() { return kodePelanggan; }
    public void setKodePelanggan(String kodePelanggan) { this.kodePelanggan = kodePelanggan; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
}
