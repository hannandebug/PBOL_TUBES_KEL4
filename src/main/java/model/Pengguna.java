package model;

import java.sql.Timestamp;

// OOP: Abstraction - class abstrak menetapkan perilaku umum seluruh pengguna.
// OOP: Encapsulation - atribut private diakses melalui getter dan setter.
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

    public Pengguna() {
        this.statusAktif = true;
    }

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

    // OOP: Abstraction - subclass wajib mengimplementasikan method ini.
    public abstract String getNamaPeran();

    // OOP: Abstraction - subclass wajib mengimplementasikan method ini.
    public abstract String getDeskripsiPeran();

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
