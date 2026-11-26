package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

// OOP: Class - PenyelesaianPengaduan merepresentasikan entitas domain.
// OOP: Encapsulation - data objek disimpan pada atribut private.
public class PenyelesaianPengaduan {

    private Long idPenyelesaian;
    private Long idPengaduan;
    private Long idPelanggan;
    private String jenisPenyelesaian;
    private String alasanPenyelesaian;
    private BigDecimal nominalDisepakati;
    private String statusPersetujuan;
    private Timestamp waktuPelangganMerespons;
    private Long idAdminPenetap;
    private String statusPelaksanaan;
    private Long dilaksanakanOleh;
    private Timestamp waktuPelaksanaan;
    private String catatan;

    private String nomorPengaduan;
    private String namaAdminPenetap;

    public PenyelesaianPengaduan() {
        this.nominalDisepakati = BigDecimal.ZERO;
        this.statusPersetujuan = "MENUNGGU";
        this.statusPelaksanaan = "BELUM_DILAKSANAKAN";
    }

    public Long getIdPenyelesaian() { return idPenyelesaian; }
    public void setIdPenyelesaian(Long idPenyelesaian) { this.idPenyelesaian = idPenyelesaian; }

    public Long getIdPengaduan() { return idPengaduan; }
    public void setIdPengaduan(Long idPengaduan) { this.idPengaduan = idPengaduan; }

    public Long getIdPelanggan() { return idPelanggan; }
    public void setIdPelanggan(Long idPelanggan) { this.idPelanggan = idPelanggan; }

    public String getJenisPenyelesaian() { return jenisPenyelesaian; }
    public void setJenisPenyelesaian(String jenisPenyelesaian) { this.jenisPenyelesaian = jenisPenyelesaian; }

    public String getAlasanPenyelesaian() { return alasanPenyelesaian; }
    public void setAlasanPenyelesaian(String alasanPenyelesaian) { this.alasanPenyelesaian = alasanPenyelesaian; }

    public BigDecimal getNomorDisepakati() { return nominalDisepakati; }
    public void setNominalDisepakati(BigDecimal nominalDisepakati) { this.nominalDisepakati = nominalDisepakati; }

    public String getStatusPersetujuan() { return statusPersetujuan; }
    public void setStatusPersetujuan(String statusPersetujuan) { this.statusPersetujuan = statusPersetujuan; }

    public Timestamp getWaktuPelangganMerespons() { return waktuPelangganMerespons; }
    public void setWaktuPelangganMerespons(Timestamp waktuPelangganMerespons) { this.waktuPelangganMerespons = waktuPelangganMerespons; }

    public Long getIdAdminPenetap() { return idAdminPenetap; }
    public void setIdAdminPenetap(Long idAdminPenetap) { this.idAdminPenetap = idAdminPenetap; }

    public String getStatusPelaksanaan() { return statusPelaksanaan; }
    public void setStatusPelaksanaan(String statusPelaksanaan) { this.statusPelaksanaan = statusPelaksanaan; }

    public Long getDilaksanakanOleh() { return dilaksanakanOleh; }
    public void setDilaksanakanOleh(Long dilaksanakanOleh) { this.dilaksanakanOleh = dilaksanakanOleh; }

    public Timestamp getWaktuPelaksanaan() { return waktuPelaksanaan; }
    public void setWaktuPelaksanaan(Timestamp waktuPelaksanaan) { this.waktuPelaksanaan = waktuPelaksanaan; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public String getNomorPengaduan() { return nomorPengaduan; }
    public void setNomorPengaduan(String nomorPengaduan) { this.nomorPengaduan = nomorPengaduan; }

    public String getNamaAdminPenetap() { return namaAdminPenetap; }
    public void setNamaAdminPenetap(String namaAdminPenetap) { this.namaAdminPenetap = namaAdminPenetap; }
}
