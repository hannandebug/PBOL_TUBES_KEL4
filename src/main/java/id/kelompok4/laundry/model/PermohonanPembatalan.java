package id.kelompok4.laundry.model;

import java.sql.Timestamp;

// OOP: Class - PermohonanPembatalan merepresentasikan entitas domain.
// OOP: Encapsulation - data objek disimpan pada atribut private.
public class PermohonanPembatalan {

    private Long idPermohonanPembatalan;
    private String nomorPermohonan;
    private Long idTransaksi;
    private Long idPelanggan;
    private Long idPenggunaPemohon;
    private String alasanPembatalan;
    private String statusPermohonan;
    private Timestamp diajukanPada;
    private Long diputuskanOlehAdmin;
    private Timestamp waktuKeputusan;
    private String catatanAdmin;

    private String nomorTransaksi;
    private String namaPelanggan;

    public PermohonanPembatalan() {
        this.statusPermohonan = "DIAJUKAN";
    }

    public Long getIdPermohonanPembatalan() { return idPermohonanPembatalan; }
    public void setIdPermohonanPembatalan(Long idPermohonanPembatalan) { this.idPermohonanPembatalan = idPermohonanPembatalan; }

    public String getNomorPermohonan() { return nomorPermohonan; }
    public void setNomorPermohonan(String nomorPermohonan) { this.nomorPermohonan = nomorPermohonan; }

    public Long getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(Long idTransaksi) { this.idTransaksi = idTransaksi; }

    public Long getIdPelanggan() { return idPelanggan; }
    public void setIdPelanggan(Long idPelanggan) { this.idPelanggan = idPelanggan; }

    public Long getIdPenggunaPemohon() { return idPenggunaPemohon; }
    public void setIdPenggunaPemohon(Long idPenggunaPemohon) { this.idPenggunaPemohon = idPenggunaPemohon; }

    public String getAlasanPembatalan() { return alasanPembatalan; }
    public void setAlasanPembatalan(String alasanPembatalan) { this.alasanPembatalan = alasanPembatalan; }

    public String getStatusPermohonan() { return statusPermohonan; }
    public void setStatusPermohonan(String statusPermohonan) { this.statusPermohonan = statusPermohonan; }

    public Timestamp getDiajukanPada() { return diajukanPada; }
    public void setDiajukanPada(Timestamp diajukanPada) { this.diajukanPada = diajukanPada; }

    public Long getDiputuskanOlehAdmin() { return diputuskanOlehAdmin; }
    public void setDiputuskanOlehAdmin(Long diputuskanOlehAdmin) { this.diputuskanOlehAdmin = diputuskanOlehAdmin; }

    public Timestamp getWaktuKeputusan() { return waktuKeputusan; }
    public void setWaktuKeputusan(Timestamp waktuKeputusan) { this.waktuKeputusan = waktuKeputusan; }

    public String getCatatanAdmin() { return catatanAdmin; }
    public void setCatatanAdmin(String catatanAdmin) { this.catatanAdmin = catatanAdmin; }

    public String getNomorTransaksi() { return nomorTransaksi; }
    public void setNomorTransaksi(String nomorTransaksi) { this.nomorTransaksi = nomorTransaksi; }

    public String getNamaPelanggan() { return namaPelanggan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }
}
