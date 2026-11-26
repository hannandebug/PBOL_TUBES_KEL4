package id.kelompok4.laundry.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

// OOP: Class - PembayaranTransaksi merepresentasikan entitas domain.
// OOP: Encapsulation - data objek disimpan pada atribut private.
public class PembayaranTransaksi {

    private Long idPembayaran;
    private Long idTransaksi;
    private String nomorStruk;
    private BigDecimal jumlahBayar;
    private String metodePembayaran;
    private Timestamp tanggalPembayaran;
    private Long diterimaOleh;
    private String catatan;
    private Timestamp dibuatPada;

    private String nomorTransaksi;
    private String namaPetugasPenerima;

    public PembayaranTransaksi() {}

    public Long getIdPembayaran() { return idPembayaran; }
    public void setIdPembayaran(Long idPembayaran) { this.idPembayaran = idPembayaran; }

    public Long getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(Long idTransaksi) { this.idTransaksi = idTransaksi; }

    public String getNomorStruk() { return nomorStruk; }
    public void setNomorStruk(String nomorStruk) { this.nomorStruk = nomorStruk; }

    public BigDecimal getJumlahBayar() { return jumlahBayar; }
    public void setJumlahBayar(BigDecimal jumlahBayar) { this.jumlahBayar = jumlahBayar; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    public Timestamp getTanggalPembayaran() { return tanggalPembayaran; }
    public void setTanggalPembayaran(Timestamp tanggalPembayaran) { this.tanggalPembayaran = tanggalPembayaran; }

    public Long getDiterimaOleh() { return diterimaOleh; }
    public void setDiterimaOleh(Long diterimaOleh) { this.diterimaOleh = diterimaOleh; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public Timestamp getDibuatPada() { return dibuatPada; }
    public void setDibuatPada(Timestamp dibuatPada) { this.dibuatPada = dibuatPada; }

    public String getNomorTransaksi() { return nomorTransaksi; }
    public void setNomorTransaksi(String nomorTransaksi) { this.nomorTransaksi = nomorTransaksi; }

    public String getNamaPetugasPenerima() { return namaPetugasPenerima; }
    public void setNamaPetugasPenerima(String namaPetugasPenerima) { this.namaPetugasPenerima = namaPetugasPenerima; }
}
