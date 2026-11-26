package id.kelompok4.laundry.model;

import java.sql.Timestamp;

// OOP: Class - PengambilanCucian merepresentasikan entitas domain.
// OOP: Encapsulation - data objek disimpan pada atribut private.
public class PengambilanCucian {

    private Long idPengambilan;
    private Long idTransaksi;
    private Long idPetugasPenyerah;
    private Timestamp waktuPengambilan;
    private String namaPenerima;
    private String catatan;

    private String nomorTransaksi;
    private String namaPetugas;

    public PengambilanCucian() {}

    public Long getIdPengambilan() { return idPengambilan; }
    public void setIdPengambilan(Long idPengambilan) { this.idPengambilan = idPengambilan; }

    public Long getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(Long idTransaksi) { this.idTransaksi = idTransaksi; }

    public Long getIdPetugasPenyerah() { return idPetugasPenyerah; }
    public void setIdPetugasPenyerah(Long idPetugasPenyerah) { this.idPetugasPenyerah = idPetugasPenyerah; }

    public Timestamp getWaktuPengambilan() { return waktuPengambilan; }
    public void setWaktuPengambilan(Timestamp waktuPengambilan) { this.waktuPengambilan = waktuPengambilan; }

    public String getNamaPenerima() { return namaPenerima; }
    public void setNamaPenerima(String namaPenerima) { this.namaPenerima = namaPenerima; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public String getNomorTransaksi() { return nomorTransaksi; }
    public void setNomorTransaksi(String nomorTransaksi) { this.nomorTransaksi = nomorTransaksi; }

    public String getNamaPetugas() { return namaPetugas; }
    public void setNamaPetugas(String namaPetugas) { this.namaPetugas = namaPetugas; }
}
