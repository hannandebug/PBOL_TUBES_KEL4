package model;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class CatatanKeuangan {

    private Long idCatatanKeuangan;
    private Long idPenyelesaian;
    private String jenisCatatan; // 'KOMPENSASI_TUNAI', 'PENGGANTIAN_BARANG'
    private BigDecimal nominal;
    private String statusCatatan; // 'MENUNGGU', 'DIBAYAR', 'DIBATALKAN'
    private String metodePembayaran;
    private Long dicatatOleh;
    private Long dilaksanakanOleh;
    private Timestamp tanggalDicatat;
    private Timestamp tanggalDibayar;
    private String catatan;

    public CatatanKeuangan() {
        this.statusCatatan = "MENUNGGU";
    }

    public Long getIdCatatanKeuangan() { return idCatatanKeuangan; }
    public void setIdCatatanKeuangan(Long idCatatanKeuangan) { this.idCatatanKeuangan = idCatatanKeuangan; }

    public Long getIdPenyelesaian() { return idPenyelesaian; }
    public void setIdPenyelesaian(Long idPenyelesaian) { this.idPenyelesaian = idPenyelesaian; }

    public String getJenisCatatan() { return jenisCatatan; }
    public void setJenisCatatan(String jenisCatatan) { this.jenisCatatan = jenisCatatan; }

    public BigDecimal getNominal() { return nominal; }
    public void setNominal(BigDecimal nominal) { this.nominal = nominal; }

    public String getStatusCatatan() { return statusCatatan; }
    public void setStatusCatatan(String statusCatatan) { this.statusCatatan = statusCatatan; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    public Long getDicatatOleh() { return dicatatOleh; }
    public void setDicatatOleh(Long dicatatOleh) { this.dicatatOleh = dicatatOleh; }

    public Long getDilaksanakanOleh() { return dilaksanakanOleh; }
    public void setDilaksanakanOleh(Long dilaksanakanOleh) { this.dilaksanakanOleh = dilaksanakanOleh; }

    public Timestamp getTanggalDicatat() { return tanggalDicatat; }
    public void setTanggalDicatat(Timestamp tanggalDicatat) { this.tanggalDicatat = tanggalDicatat; }

    public Timestamp getTanggalDibayar() { return tanggalDibayar; }
    public void setTanggalDibayar(Timestamp tanggalDibayar) { this.tanggalDibayar = tanggalDibayar; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
}
