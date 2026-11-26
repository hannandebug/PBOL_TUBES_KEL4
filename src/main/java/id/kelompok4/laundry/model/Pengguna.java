package id.kelompok4.laundry.model;

import java.sql.Timestamp;

/**
 * Abstract Class Pengguna
 * Induk dari seluruh entitas akun dalam aplikasi (Admin, Petugas, Pelanggan).
 * Menerapkan prinsip Abstraction dan Encapsulation:
 * - Atribut private dengan getter dan setter
 * - Abstract method yang wajib dioverride oleh masing-masing subclass
 * 
 * Kelompok 4 - Pemrograman Berorientasi Objek Lanjutan (PBOL)
 */
public abstract class Pengguna {

    private Long idPengguna;
    private String namaLengkap;
    private String namaPengguna;
    private String kataSandiHash;
    private String peran;
    private String nomorTelepon;
    private boolean statusAktif;
    private Timestamp dibuatPada;
    private Timestamp diperbaruiPada;

    // Constructor default
    public Pengguna() {
        this.statusAktif = true;
    }

    // Constructor dengan parameter umum
    public Pengguna(Long idPengguna, String namaLengkap, String namaPengguna, 
                    String kataSandiHash, String peran, String nomorTelepon, boolean statusAktif) {
        this.idPengguna = idPengguna;
        this.namaLengkap = namaLengkap;
        this.namaPengguna = namaPengguna;
        this.kataSandiHash = kataSandiHash;
        this.peran = peran;
        this.nomorTelepon = nomorTelepon;
        this.statusAktif = statusAktif;
    }

    /**
     * Method abstrak untuk mendapatkan nama display peran.
     * Wajib diimplementasikan oleh subclass (Admin, Petugas, Pelanggan).
     */
    public abstract String getNamaPeran();

    /**
     * Method abstrak untuk mendapatkan deskripsi hak akses pengguna.
     */
    public abstract String getDeskripsiPeran();

    // Getter dan Setter (Encapsulation)
    public Long getIdPengguna() { return idPengguna; }
    public void setIdPengguna(Long idPengguna) { this.idPengguna = idPengguna; }

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }

    public String getNamaPengguna() { return namaPengguna; }
    public void setNamaPengguna(String namaPengguna) { this.namaPengguna = namaPengguna; }

    public String getKataSandiHash() { return kataSandiHash; }
    public void setKataSandiHash(String kataSandiHash) { this.kataSandiHash = kataSandiHash; }

    public String getPeran() { return peran; }
    public void setPeran(String peran) { this.peran = peran; }

    public String getNomorTelepon() { return nomorTelepon; }
    public void setNomorTelepon(String nomorTelepon) { this.nomorTelepon = nomorTelepon; }

    public boolean isStatusAktif() { return statusAktif; }
    public void setStatusAktif(boolean statusAktif) { this.statusAktif = statusAktif; }

    public Timestamp getDibuatPada() { return dibuatPada; }
    public void setDibuatPada(Timestamp dibuatPada) { this.dibuatPada = dibuatPada; }

    public Timestamp getDiperbaruiPada() { return diperbaruiPada; }
    public void setDiperbaruiPada(Timestamp diperbaruiPada) { this.diperbaruiPada = diperbaruiPada; }

    @Override
    public String toString() {
        return namaLengkap + " (" + getNamaPeran() + ")";
    }
}
