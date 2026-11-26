package id.kelompok4.laundry.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

// OOP: Class - PengembalianDana merepresentasikan entitas domain.
// OOP: Encapsulation - data objek disimpan pada atribut private.
public class PengembalianDana {

    private Long idPengembalian;
    private String nomorPengembalian;
    private Long idTransaksi;
    private Long idPermohonanPembatalan;
    private Long idPenyelesaian;
    private BigDecimal jumlahPengembalian;
    private String statusPengembalian;
    private String metodePengembalian;
    private Long dibuatOlehAdmin;
    private Long dibayarkanOleh;
    private Timestamp waktuPengembalian;
    private String alasan;
    private String catatan;

    private String nomorTransaksi;
    private String namaPelanggan;

    public PengembalianDana() {
        this.statusPengembalian = "MENUNGGU";
    }

    public Long getIdPengembalian() { return idPengembalian; }
    public void setIdPengembalian(Long idPengembalian) { this.idPengembalian = idPengembalian; }

    public String getNomorPengembalian() { return nomorPengembalian; }
    public void setNomorPengembalian(String nomorPengembalian) { this.nomorPengembalian = nomorPengembalian; }

    public Long getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(Long idTransaksi) { this.idTransaksi = idTransaksi; }

    public Long getIdPermohonanPembatalan() { return idPermohonanPembatalan; }
    public void setIdPermohonanPembatalan(Long idPermohonanPembatalan) { this.idPermohonanPembatalan = idPermohonanPembatalan; }

    public Long getIdPenyelesaian() { return idPenyelesaian; }
    public void setIdPenyelesaian(Long idPenyelesaian) { this.idPenyelesaian = idPenyelesaian; }

    public BigDecimal getJumlahPengembalian() { return jumlahPengembalian; }
    public void setJumlahPengembalian(BigDecimal jumlahPengembalian) { this.jumlahPengembalian = jumlahPengembalian; }

    public String getStatusPengembalian() { return statusPengembalian; }
    public void setStatusPengembalian(String statusPengembalian) { this.statusPengembalian = statusPengembalian; }

    public String getMetodePengembalian() { return metodePengembalian; }
    public void setMetodePengembalian(String metodePengembalian) { this.metodePengembalian = metodePengembalian; }

    public Long getDibuatOlehAdmin() { return dibuatOlehAdmin; }
    public void setDibuatOlehAdmin(Long dibuatOlehAdmin) { this.dibuatOlehAdmin = dibuatOlehAdmin; }

    public Long getDibayarkanOleh() { return dibayarkanOleh; }
    public void setDibayarkanOleh(Long dibayarkanOleh) { this.dibayarkanOleh = dibayarkanOleh; }

    public Timestamp getWaktuPengembalian() { return waktuPengembalian; }
    public void setWaktuPengembalian(Timestamp waktuPengembalian) { this.waktuPengembalian = waktuPengembalian; }

    public String getAlasan() { return alasan; }
    public void setAlasan(String alasan) { this.alasan = alasan; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public String getNomorTransaksi() { return nomorTransaksi; }
    public void setNomorTransaksi(String nomorTransaksi) { this.nomorTransaksi = nomorTransaksi; }

    public String getNamaPelanggan() { return namaPelanggan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }
}
