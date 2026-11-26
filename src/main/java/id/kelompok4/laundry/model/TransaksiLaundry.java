package id.kelompok4.laundry.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model TransaksiLaundry
 * Merepresentasikan tabel transaksi_laundry.
 * Kolom berat_tagihan dan total_biaya adalah generated column dari MySQL.
 */
public class TransaksiLaundry {

    private Long idTransaksi;
    private String nomorTransaksi;
    private Long idPelanggan;
    private Long idPetugasPencatat;
    private Long idJenisLayanan;
    private Long idPaket;
    private BigDecimal beratAktual;
    private boolean pelangganSetujuMinimum;
    private BigDecimal beratTagihan;
    private BigDecimal tarifPerKgSaatTransaksi;
    private BigDecimal totalBiaya;
    private int durasiJamSaatTransaksi;
    private Timestamp tanggalTransaksi;
    private Timestamp estimasiSelesai;
    private Timestamp tanggalSiapDiambil;
    private String statusTransaksi;
    private String catatan;
    private Long idPenggunaTerakhirMemperbarui;

    // Helper tampilan join GUI
    private String namaPelanggan;
    private String kodePelanggan;
    private String nomorTeleponPelanggan;
    private String namaLayanan;
    private String namaPaket;
    private String namaPetugasPencatat;
    private String statusPembayaran;
    private String nomorStruk;
    private String metodePembayaran;

    public TransaksiLaundry() {
        this.statusTransaksi = "MENUNGGU_PEMBAYARAN";
        this.pelangganSetujuMinimum = false;
    }

    // Getters and Setters
    public Long getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(Long idTransaksi) { this.idTransaksi = idTransaksi; }

    public String getNomorTransaksi() { return nomorTransaksi; }
    public void setNomorTransaksi(String nomorTransaksi) { this.nomorTransaksi = nomorTransaksi; }

    public Long getIdPelanggan() { return idPelanggan; }
    public void setIdPelanggan(Long idPelanggan) { this.idPelanggan = idPelanggan; }

    public Long getIdPetugasPencatat() { return idPetugasPencatat; }
    public void setIdPetugasPencatat(Long idPetugasPencatat) { this.idPetugasPencatat = idPetugasPencatat; }

    public Long getIdJenisLayanan() { return idJenisLayanan; }
    public void setIdJenisLayanan(Long idJenisLayanan) { this.idJenisLayanan = idJenisLayanan; }

    public Long getIdPaket() { return idPaket; }
    public void setIdPaket(Long idPaket) { this.idPaket = idPaket; }

    public BigDecimal getBeratAktual() { return beratAktual; }
    public void setBeratAktual(BigDecimal beratAktual) { this.beratAktual = beratAktual; }

    public boolean isPelangganSetujuMinimum() { return pelangganSetujuMinimum; }
    public void setPelangganSetujuMinimum(boolean pelangganSetujuMinimum) { this.pelangganSetujuMinimum = pelangganSetujuMinimum; }

    public BigDecimal getBeratTagihan() { return beratTagihan; }
    public void setBeratTagihan(BigDecimal beratTagihan) { this.beratTagihan = beratTagihan; }

    public BigDecimal getTarifPerKgSaatTransaksi() { return tarifPerKgSaatTransaksi; }
    public void setTarifPerKgSaatTransaksi(BigDecimal tarifPerKgSaatTransaksi) { this.tarifPerKgSaatTransaksi = tarifPerKgSaatTransaksi; }

    public BigDecimal getTotalBiaya() { return totalBiaya; }
    public void setTotalBiaya(BigDecimal totalBiaya) { this.totalBiaya = totalBiaya; }

    public int getDurasiJamSaatTransaksi() { return durasiJamSaatTransaksi; }
    public void setDurasiJamSaatTransaksi(int durasiJamSaatTransaksi) { this.durasiJamSaatTransaksi = durasiJamSaatTransaksi; }

    public Timestamp getTanggalTransaksi() { return tanggalTransaksi; }
    public void setTanggalTransaksi(Timestamp tanggalTransaksi) { this.tanggalTransaksi = tanggalTransaksi; }

    public Timestamp getEstimasiSelesai() { return estimasiSelesai; }
    public void setEstimasiSelesai(Timestamp estimasiSelesai) { this.estimasiSelesai = estimasiSelesai; }

    public Timestamp getTanggalSiapDiambil() { return tanggalSiapDiambil; }
    public void setTanggalSiapDiambil(Timestamp tanggalSiapDiambil) { this.tanggalSiapDiambil = tanggalSiapDiambil; }

    public String getStatusTransaksi() { return statusTransaksi; }
    public void setStatusTransaksi(String statusTransaksi) { this.statusTransaksi = statusTransaksi; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public Long getIdPenggunaTerakhirMemperbarui() { return idPenggunaTerakhirMemperbarui; }
    public void setIdPenggunaTerakhirMemperbarui(Long idPenggunaTerakhirMemperbarui) { this.idPenggunaTerakhirMemperbarui = idPenggunaTerakhirMemperbarui; }

    public String getNamaPelanggan() { return namaPelanggan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }

    public String getKodePelanggan() { return kodePelanggan; }
    public void setKodePelanggan(String kodePelanggan) { this.kodePelanggan = kodePelanggan; }

    public String getNomorTeleponPelanggan() { return nomorTeleponPelanggan; }
    public void setNomorTeleponPelanggan(String nomorTeleponPelanggan) { this.nomorTeleponPelanggan = nomorTeleponPelanggan; }

    public String getNamaLayanan() { return namaLayanan; }
    public void setNamaLayanan(String namaLayanan) { this.namaLayanan = namaLayanan; }

    public String getNamaPaket() { return namaPaket; }
    public void setNamaPaket(String namaPaket) { this.namaPaket = namaPaket; }

    public String getNamaPetugasPencatat() { return namaPetugasPencatat; }
    public void setNamaPetugasPencatat(String namaPetugasPencatat) { this.namaPetugasPencatat = namaPetugasPencatat; }

    public String getStatusPembayaran() { return statusPembayaran; }
    public void setStatusPembayaran(String statusPembayaran) { this.statusPembayaran = statusPembayaran; }

    public String getNomorStruk() { return nomorStruk; }
    public void setNomorStruk(String nomorStruk) { this.nomorStruk = nomorStruk; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }
}
