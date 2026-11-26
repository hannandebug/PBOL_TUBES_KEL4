package model;

import java.sql.Timestamp;

/**
 * Model Pengaduan
 * Merepresentasikan tabel pengaduan saat pelanggan melaporkan masalah cucian (Hilang, Rusak, Tertukar).
 */
public class Pengaduan {

    private Long idPengaduan;
    private String nomorPengaduan;
    private Long idTransaksi;
    private Long idPelanggan;
    private Long idPenggunaPelapor;
    private String jenisPengaduan; // 'HILANG', 'RUSAK', 'TERTUKAR'
    private String deskripsiPakaian;
    private int jumlahPakaian;
    private String uraianKejadian;
    private Timestamp tanggalLapor;
    private Timestamp batasWaktuPenelusuran;
    private String hasilPenelusuran;
    private String statusTanggungJawab; // 'BELUM_DITENTUKAN', 'TANGGUNG_JAWAB_PENUH', 'TANGGUNG_JAWAB_SEBAGIAN', 'BUKAN_TANGGUNG_JAWAB_LAUNDRY'
    private String statusPengaduan; // 'DILAPORKAN', 'DIVERIFIKASI', 'DALAM_PENELUSURAN', 'MENUNGGU_PERSETUJUAN', 'SOLUSI_DITOLAK', 'DALAM_PENYELESAIAN', 'SELESAI', 'DITOLAK'
    private Long idAdminPemeriksa;
    private Long idPetugasPembantu;
    private Timestamp waktuPemeriksaan;
    private Long idPenggunaTerakhirMemperbarui;

    // Helper tampilan
    private String nomorTransaksi;
    private String namaPelanggan;
    private String nomorTeleponPelanggan;

    public Pengaduan() {
        this.jumlahPakaian = 1;
        this.statusTanggungJawab = "BELUM_DITENTUKAN";
        this.statusPengaduan = "DILAPORKAN";
    }

    public Long getIdPengaduan() { return idPengaduan; }
    public void setIdPengaduan(Long idPengaduan) { this.idPengaduan = idPengaduan; }

    public String getNomorPengaduan() { return nomorPengaduan; }
    public void setNomorPengaduan(String nomorPengaduan) { this.nomorPengaduan = nomorPengaduan; }

    public Long getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(Long idTransaksi) { this.idTransaksi = idTransaksi; }

    public Long getIdPelanggan() { return idPelanggan; }
    public void setIdPelanggan(Long idPelanggan) { this.idPelanggan = idPelanggan; }

    public Long getIdPenggunaPelapor() { return idPenggunaPelapor; }
    public void setIdPenggunaPelapor(Long idPenggunaPelapor) { this.idPenggunaPelapor = idPenggunaPelapor; }

    public String getJenisPengaduan() { return jenisPengaduan; }
    public void setJenisPengaduan(String jenisPengaduan) { this.jenisPengaduan = jenisPengaduan; }

    public String getDeskripsiPakaian() { return deskripsiPakaian; }
    public void setDeskripsiPakaian(String deskripsiPakaian) { this.deskripsiPakaian = deskripsiPakaian; }

    public int getJumlahPakaian() { return jumlahPakaian; }
    public void setJumlahPakaian(int jumlahPakaian) { this.jumlahPakaian = jumlahPakaian; }

    public String getUraianKejadian() { return uraianKejadian; }
    public void setUraianKejadian(String uraianKejadian) { this.uraianKejadian = uraianKejadian; }

    public Timestamp getTanggalLapor() { return tanggalLapor; }
    public void setTanggalLapor(Timestamp tanggalLapor) { this.tanggalLapor = tanggalLapor; }

    public Timestamp getBatasWaktuPenelusuran() { return batasWaktuPenelusuran; }
    public void setBatasWaktuPenelusuran(Timestamp batasWaktuPenelusuran) { this.batasWaktuPenelusuran = batasWaktuPenelusuran; }

    public String getHasilPenelusuran() { return hasilPenelusuran; }
    public void setHasilPenelusuran(String hasilPenelusuran) { this.hasilPenelusuran = hasilPenelusuran; }

    public String getStatusTanggungJawab() { return statusTanggungJawab; }
    public void setStatusTanggungJawab(String statusTanggungJawab) { this.statusTanggungJawab = statusTanggungJawab; }

    public String getStatusPengaduan() { return statusPengaduan; }
    public void setStatusPengaduan(String statusPengaduan) { this.statusPengaduan = statusPengaduan; }

    public Long getIdAdminPemeriksa() { return idAdminPemeriksa; }
    public void setIdAdminPemeriksa(Long idAdminPemeriksa) { this.idAdminPemeriksa = idAdminPemeriksa; }

    public Long getIdPetugasPembantu() { return idPetugasPembantu; }
    public void setIdPetugasPembantu(Long idPetugasPembantu) { this.idPetugasPembantu = idPetugasPembantu; }

    public Timestamp getWaktuPemeriksaan() { return waktuPemeriksaan; }
    public void setWaktuPemeriksaan(Timestamp waktuPemeriksaan) { this.waktuPemeriksaan = waktuPemeriksaan; }

    public Long getIdPenggunaTerakhirMemperbarui() { return idPenggunaTerakhirMemperbarui; }
    public void setIdPenggunaTerakhirMemperbarui(Long idPenggunaTerakhirMemperbarui) { this.idPenggunaTerakhirMemperbarui = idPenggunaTerakhirMemperbarui; }

    public String getNomorTransaksi() { return nomorTransaksi; }
    public void setNomorTransaksi(String nomorTransaksi) { this.nomorTransaksi = nomorTransaksi; }

    public String getNamaPelanggan() { return namaPelanggan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }

    public String getNomorTeleponPelanggan() { return nomorTeleponPelanggan; }
    public void setNomorTeleponPelanggan(String nomorTeleponPelanggan) { this.nomorTeleponPelanggan = nomorTeleponPelanggan; }
}
