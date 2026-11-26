package id.kelompok4.laundry.model;

import java.sql.Timestamp;

// OOP: Class - PaketLaundry merepresentasikan entitas domain.
// OOP: Encapsulation - data objek disimpan pada atribut private.
public class PaketLaundry {

    private Long idPaket;
    private String kodePaket;
    private String namaPaket;
    private int durasiJam;
    private String keterangan;
    private boolean statusAktif;
    private Timestamp dibuatPada;

    public PaketLaundry() {
        this.statusAktif = true;
    }

    public PaketLaundry(Long idPaket, String kodePaket, String namaPaket, int durasiJam, String keterangan, boolean statusAktif) {
        this.idPaket = idPaket;
        this.kodePaket = kodePaket;
        this.namaPaket = namaPaket;
        this.durasiJam = durasiJam;
        this.keterangan = keterangan;
        this.statusAktif = statusAktif;
    }

    public Long getIdPaket() { return idPaket; }
    public void setIdPaket(Long idPaket) { this.idPaket = idPaket; }

    public String getKodePaket() { return kodePaket; }
    public void setKodePaket(String kodePaket) { this.kodePaket = kodePaket; }

    public String getNamaPaket() { return namaPaket; }
    public void setNamaPaket(String namaPaket) { this.namaPaket = namaPaket; }

    public int getDurasiJam() { return durasiJam; }
    public void setDurasiJam(int durasiJam) { this.durasiJam = durasiJam; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public boolean isStatusAktif() { return statusAktif; }
    public void setStatusAktif(boolean statusAktif) { this.statusAktif = statusAktif; }

    public Timestamp getDibuatPada() { return dibuatPada; }
    public void setDibuatPada(Timestamp dibuatPada) { this.dibuatPada = dibuatPada; }

    @Override
    public String toString() {
        return namaPaket + " (" + durasiJam + " Jam)";
    }
}
