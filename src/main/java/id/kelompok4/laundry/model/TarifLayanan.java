package id.kelompok4.laundry.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model TarifLayanan
 * Merepresentasikan tarif per kilogram untuk kombinasi JenisLayanan dan PaketLaundry.
 */
public class TarifLayanan {

    private Long idTarif;
    private Long idJenisLayanan;
    private Long idPaket;
    private BigDecimal hargaPerKg;
    private boolean statusAktif;
    private Timestamp dibuatPada;
    private Timestamp diperbaruiPada;

    // Properti join pendukung tampilan GUI
    private String namaLayanan;
    private String namaPaket;
    private int durasiJam;

    public TarifLayanan() {
        this.statusAktif = true;
    }

    public TarifLayanan(Long idTarif, Long idJenisLayanan, Long idPaket, BigDecimal hargaPerKg, boolean statusAktif) {
        this.idTarif = idTarif;
        this.idJenisLayanan = idJenisLayanan;
        this.idPaket = idPaket;
        this.hargaPerKg = hargaPerKg;
        this.statusAktif = statusAktif;
    }

    public Long getIdTarif() { return idTarif; }
    public void setIdTarif(Long idTarif) { this.idTarif = idTarif; }

    public Long getIdJenisLayanan() { return idJenisLayanan; }
    public void setIdJenisLayanan(Long idJenisLayanan) { this.idJenisLayanan = idJenisLayanan; }

    public Long getIdPaket() { return idPaket; }
    public void setIdPaket(Long idPaket) { this.idPaket = idPaket; }

    public BigDecimal getHargaPerKg() { return hargaPerKg; }
    public void setHargaPerKg(BigDecimal hargaPerKg) { this.hargaPerKg = hargaPerKg; }

    public boolean isStatusAktif() { return statusAktif; }
    public void setStatusAktif(boolean statusAktif) { this.statusAktif = statusAktif; }

    public Timestamp getDibuatPada() { return dibuatPada; }
    public void setDibuatPada(Timestamp dibuatPada) { this.dibuatPada = dibuatPada; }

    public Timestamp getDiperbaruiPada() { return diperbaruiPada; }
    public void setDiperbaruiPada(Timestamp diperbaruiPada) { this.diperbaruiPada = diperbaruiPada; }

    public String getNamaLayanan() { return namaLayanan; }
    public void setNamaLayanan(String namaLayanan) { this.namaLayanan = namaLayanan; }

    public String getNamaPaket() { return namaPaket; }
    public void setNamaPaket(String namaPaket) { this.namaPaket = namaPaket; }

    public int getDurasiJam() { return durasiJam; }
    public void setDurasiJam(int durasiJam) { this.durasiJam = durasiJam; }
}
