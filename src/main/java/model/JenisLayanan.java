package model;

import java.sql.Timestamp;

/**
 * Model JenisLayanan
 * Merepresentasikan tabel jenis_layanan (misal: Cuci Komplit, Setrika Saja).
 */
public class JenisLayanan {

    private Long idJenisLayanan;
    private String kodeLayanan;
    private String namaLayanan;
    private String keterangan;
    private boolean statusAktif;
    private Timestamp dibuatPada;

    public JenisLayanan() {
        this.statusAktif = true;
    }

    public JenisLayanan(Long idJenisLayanan, String kodeLayanan, String namaLayanan, String keterangan, boolean statusAktif) {
        this.idJenisLayanan = idJenisLayanan;
        this.kodeLayanan = kodeLayanan;
        this.namaLayanan = namaLayanan;
        this.keterangan = keterangan;
        this.statusAktif = statusAktif;
    }

    public Long getIdJenisLayanan() { return idJenisLayanan; }
    public void setIdJenisLayanan(Long idJenisLayanan) { this.idJenisLayanan = idJenisLayanan; }

    public String getKodeLayanan() { return kodeLayanan; }
    public void setKodeLayanan(String kodeLayanan) { this.kodeLayanan = kodeLayanan; }

    public String getNamaLayanan() { return namaLayanan; }
    public void setNamaLayanan(String namaLayanan) { this.namaLayanan = namaLayanan; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public boolean isStatusAktif() { return statusAktif; }
    public void setStatusAktif(boolean statusAktif) { this.statusAktif = statusAktif; }

    public Timestamp getDibuatPada() { return dibuatPada; }
    public void setDibuatPada(Timestamp dibuatPada) { this.dibuatPada = dibuatPada; }

    @Override
    public String toString() {
        return namaLayanan;
    }
}
