CREATE DATABASE IF NOT EXISTS laundry_kelompok4
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE laundry_kelompok4;
SET NAMES utf8mb4;
SET time_zone = '+07:00';

-- Fitur: Kelola Pengguna
CREATE TABLE IF NOT EXISTS pengguna (
    id_pengguna BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nama_lengkap VARCHAR(100) NOT NULL,
    nama_pengguna VARCHAR(50) NOT NULL,
    kata_sandi_hash VARCHAR(255) NOT NULL,
    peran VARCHAR(20) NOT NULL,
    nomor_telepon VARCHAR(25) NULL,
    status_aktif TINYINT(1) NOT NULL DEFAULT 1,
    dibuat_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_pengguna),
    CONSTRAINT uq_pengguna_nama_pengguna UNIQUE (nama_pengguna),
    CONSTRAINT ck_pengguna_peran CHECK (peran IN ('ADMIN', 'PETUGAS', 'PELANGGAN')),
    CONSTRAINT ck_pengguna_status_aktif CHECK (status_aktif IN (0, 1)),
    INDEX idx_pengguna_peran_aktif (peran, status_aktif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Kelola Pelanggan
CREATE TABLE IF NOT EXISTS pelanggan (
    id_pelanggan BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_pengguna BIGINT UNSIGNED NOT NULL,
    kode_pelanggan VARCHAR(30) NOT NULL,
    alamat TEXT NULL,
    catatan TEXT NULL,
    dibuat_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_pelanggan),
    CONSTRAINT uq_pelanggan_pengguna UNIQUE (id_pengguna),
    CONSTRAINT uq_pelanggan_kode UNIQUE (kode_pelanggan),
    CONSTRAINT uq_pelanggan_id_pengguna_pair UNIQUE (id_pelanggan, id_pengguna),
    CONSTRAINT fk_pelanggan_pengguna FOREIGN KEY (id_pengguna)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    INDEX idx_pelanggan_kode (kode_pelanggan)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Kelola Jenis Layanan
CREATE TABLE IF NOT EXISTS jenis_layanan (
    id_jenis_layanan BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    kode_layanan VARCHAR(30) NOT NULL,
    nama_layanan VARCHAR(80) NOT NULL,
    keterangan TEXT NULL,
    status_aktif TINYINT(1) NOT NULL DEFAULT 1,
    dibuat_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_jenis_layanan),
    CONSTRAINT uq_jenis_layanan_kode UNIQUE (kode_layanan),
    CONSTRAINT ck_jenis_layanan_aktif CHECK (status_aktif IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Kelola Paket Laundry
CREATE TABLE IF NOT EXISTS paket_laundry (
    id_paket BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    kode_paket VARCHAR(30) NOT NULL,
    nama_paket VARCHAR(80) NOT NULL,
    durasi_jam INT UNSIGNED NOT NULL,
    keterangan TEXT NULL,
    status_aktif TINYINT(1) NOT NULL DEFAULT 1,
    dibuat_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_paket),
    CONSTRAINT uq_paket_kode UNIQUE (kode_paket),
    CONSTRAINT ck_paket_durasi CHECK (durasi_jam > 0),
    CONSTRAINT ck_paket_aktif CHECK (status_aktif IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Kelola Tarif Layanan
CREATE TABLE IF NOT EXISTS tarif_layanan (
    id_tarif BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_jenis_layanan BIGINT UNSIGNED NOT NULL,
    id_paket BIGINT UNSIGNED NOT NULL,
    harga_per_kg DECIMAL(12,2) NOT NULL,
    status_aktif TINYINT(1) NOT NULL DEFAULT 1,
    dibuat_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_tarif),
    CONSTRAINT uq_tarif_layanan_paket UNIQUE (id_jenis_layanan, id_paket),
    CONSTRAINT fk_tarif_jenis_layanan FOREIGN KEY (id_jenis_layanan)
        REFERENCES jenis_layanan (id_jenis_layanan)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_tarif_paket FOREIGN KEY (id_paket)
        REFERENCES paket_laundry (id_paket)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT ck_tarif_harga CHECK (harga_per_kg > 0),
    CONSTRAINT ck_tarif_aktif CHECK (status_aktif IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Kelola Transaksi Laundry
CREATE TABLE IF NOT EXISTS transaksi_laundry (
    id_transaksi BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nomor_transaksi VARCHAR(40) NOT NULL,
    id_pelanggan BIGINT UNSIGNED NOT NULL,
    id_petugas_pencatat BIGINT UNSIGNED NOT NULL,
    id_jenis_layanan BIGINT UNSIGNED NOT NULL,
    id_paket BIGINT UNSIGNED NOT NULL,
    berat_aktual DECIMAL(8,2) NOT NULL,
    pelanggan_setuju_minimum TINYINT(1) NOT NULL DEFAULT 0,
    berat_tagihan DECIMAL(8,2)
        GENERATED ALWAYS AS (GREATEST(berat_aktual, 2.00)) STORED,
    tarif_per_kg_saat_transaksi DECIMAL(12,2) NOT NULL,
    total_biaya DECIMAL(14,2)
        GENERATED ALWAYS AS (ROUND(GREATEST(berat_aktual, 2.00) * tarif_per_kg_saat_transaksi, 2)) STORED,
    durasi_jam_saat_transaksi INT UNSIGNED NOT NULL,
    tanggal_transaksi DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estimasi_selesai DATETIME NULL,
    tanggal_siap_diambil DATETIME NULL,
    status_transaksi VARCHAR(30) NOT NULL DEFAULT 'MENUNGGU_PEMBAYARAN',
    catatan TEXT NULL,
    id_pengguna_terakhir_memperbarui BIGINT UNSIGNED NULL,
    dibuat_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_transaksi),
    CONSTRAINT uq_transaksi_nomor UNIQUE (nomor_transaksi),
    CONSTRAINT uq_transaksi_id_pelanggan_pair UNIQUE (id_transaksi, id_pelanggan),
    CONSTRAINT fk_transaksi_pelanggan FOREIGN KEY (id_pelanggan)
        REFERENCES pelanggan (id_pelanggan)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_transaksi_petugas FOREIGN KEY (id_petugas_pencatat)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_transaksi_pengubah FOREIGN KEY (id_pengguna_terakhir_memperbarui)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_transaksi_tarif FOREIGN KEY (id_jenis_layanan, id_paket)
        REFERENCES tarif_layanan (id_jenis_layanan, id_paket)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT ck_transaksi_berat CHECK (berat_aktual > 0),
    CONSTRAINT ck_transaksi_tarif CHECK (tarif_per_kg_saat_transaksi > 0),
    CONSTRAINT ck_transaksi_durasi CHECK (durasi_jam_saat_transaksi > 0),
    CONSTRAINT ck_transaksi_setuju_minimum CHECK (pelanggan_setuju_minimum IN (0, 1)),
    CONSTRAINT ck_transaksi_minimum_disetujui CHECK (berat_aktual >= 2.00 OR pelanggan_setuju_minimum = 1),
    CONSTRAINT ck_transaksi_status CHECK (status_transaksi IN (
        'MENUNGGU_PEMBAYARAN', 'DITERIMA', 'DIPROSES', 'SIAP_DIAMBIL', 'SELESAI', 'DIBATALKAN'
    )),
    CONSTRAINT ck_transaksi_estimasi CHECK (estimasi_selesai IS NULL OR estimasi_selesai >= tanggal_transaksi),
    INDEX idx_transaksi_pelanggan_tanggal (id_pelanggan, tanggal_transaksi),
    INDEX idx_transaksi_status (status_transaksi),
    INDEX idx_transaksi_tanggal (tanggal_transaksi),
    INDEX idx_transaksi_estimasi (estimasi_selesai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Kelola Pembayaran Transaksi
CREATE TABLE IF NOT EXISTS pembayaran_transaksi (
    id_pembayaran BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_transaksi BIGINT UNSIGNED NOT NULL,
    nomor_struk VARCHAR(45) NULL,
    jumlah_bayar DECIMAL(14,2) NOT NULL,
    metode_pembayaran VARCHAR(20) NOT NULL,
    status_verifikasi VARCHAR(25) NOT NULL DEFAULT 'MENUNGGU_VERIFIKASI',
    waktu_pencatatan DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    waktu_verifikasi DATETIME NULL,
    id_petugas_pencatat BIGINT UNSIGNED NOT NULL,
    id_petugas_verifikasi BIGINT UNSIGNED NULL,
    nomor_referensi VARCHAR(100) NULL,
    lokasi_bukti_pembayaran VARCHAR(255) NULL,
    catatan_verifikasi TEXT NULL,
    id_transaksi_menunggu BIGINT UNSIGNED
        GENERATED ALWAYS AS (CASE WHEN status_verifikasi = 'MENUNGGU_VERIFIKASI' THEN id_transaksi ELSE NULL END) STORED,
    id_transaksi_disetujui BIGINT UNSIGNED
        GENERATED ALWAYS AS (CASE WHEN status_verifikasi = 'DISETUJUI' THEN id_transaksi ELSE NULL END) STORED,
    dibuat_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_pembayaran),
    CONSTRAINT uq_pembayaran_struk UNIQUE (nomor_struk),
    CONSTRAINT uq_pembayaran_satu_menunggu UNIQUE (id_transaksi_menunggu),
    CONSTRAINT uq_pembayaran_satu_disetujui UNIQUE (id_transaksi_disetujui),
    CONSTRAINT fk_pembayaran_transaksi FOREIGN KEY (id_transaksi)
        REFERENCES transaksi_laundry (id_transaksi)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pembayaran_pencatat FOREIGN KEY (id_petugas_pencatat)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pembayaran_verifikator FOREIGN KEY (id_petugas_verifikasi)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT ck_pembayaran_data_verifikasi CHECK (
        (status_verifikasi = 'MENUNGGU_VERIFIKASI'
            AND id_petugas_verifikasi IS NULL AND waktu_verifikasi IS NULL AND nomor_struk IS NULL)
        OR (status_verifikasi = 'DISETUJUI'
            AND id_petugas_verifikasi IS NOT NULL AND waktu_verifikasi IS NOT NULL AND nomor_struk IS NOT NULL)
        OR (status_verifikasi IN ('DITOLAK', 'DIBATALKAN')
            AND id_petugas_verifikasi IS NOT NULL AND waktu_verifikasi IS NOT NULL AND nomor_struk IS NULL)
    ),
    CONSTRAINT ck_pembayaran_jumlah CHECK (jumlah_bayar > 0),
    CONSTRAINT ck_pembayaran_metode CHECK (metode_pembayaran IN ('TUNAI', 'TRANSFER', 'QRIS')),
    CONSTRAINT ck_pembayaran_status CHECK (status_verifikasi IN (
        'MENUNGGU_VERIFIKASI', 'DISETUJUI', 'DITOLAK', 'DIBATALKAN'
    )),
    INDEX idx_pembayaran_transaksi_status (id_transaksi, status_verifikasi),
    INDEX idx_pembayaran_status_waktu (status_verifikasi, waktu_pencatatan),
    INDEX idx_pembayaran_verifikasi (id_petugas_verifikasi, waktu_verifikasi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Lihat Riwayat Status Transaksi
CREATE TABLE IF NOT EXISTS riwayat_status_transaksi (
    id_riwayat BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_transaksi BIGINT UNSIGNED NOT NULL,
    status_sebelumnya VARCHAR(30) NULL,
    status_baru VARCHAR(30) NOT NULL,
    id_pengguna_pengubah BIGINT UNSIGNED NOT NULL,
    waktu_perubahan DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    catatan TEXT NULL,
    PRIMARY KEY (id_riwayat),
    CONSTRAINT fk_riwayat_transaksi FOREIGN KEY (id_transaksi)
        REFERENCES transaksi_laundry (id_transaksi)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_riwayat_transaksi_pengguna FOREIGN KEY (id_pengguna_pengubah)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    INDEX idx_riwayat_transaksi_waktu (id_transaksi, waktu_perubahan)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Catat Pengambilan Cucian
CREATE TABLE IF NOT EXISTS pengambilan_cucian (
    id_pengambilan BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_transaksi BIGINT UNSIGNED NOT NULL,
    id_petugas_penyerah BIGINT UNSIGNED NOT NULL,
    waktu_pengambilan DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nama_penerima VARCHAR(100) NULL,
    catatan TEXT NULL,
    PRIMARY KEY (id_pengambilan),
    CONSTRAINT uq_pengambilan_transaksi UNIQUE (id_transaksi),
    CONSTRAINT fk_pengambilan_transaksi FOREIGN KEY (id_transaksi)
        REFERENCES transaksi_laundry (id_transaksi)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pengambilan_petugas FOREIGN KEY (id_petugas_penyerah)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Lihat Riwayat Pengingat Pengambilan
CREATE TABLE IF NOT EXISTS riwayat_pengingat_pengambilan (
    id_pengingat BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_transaksi BIGINT UNSIGNED NOT NULL,
    id_pengguna_pengirim BIGINT UNSIGNED NOT NULL,
    media_pengingat VARCHAR(20) NOT NULL,
    waktu_pengingat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    hasil_pengingat VARCHAR(30) NULL,
    catatan TEXT NULL,
    PRIMARY KEY (id_pengingat),
    CONSTRAINT fk_pengingat_transaksi FOREIGN KEY (id_transaksi)
        REFERENCES transaksi_laundry (id_transaksi)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pengingat_pengguna FOREIGN KEY (id_pengguna_pengirim)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT ck_pengingat_media CHECK (media_pengingat IN ('TELEPON', 'WHATSAPP', 'SMS', 'LANGSUNG', 'LAINNYA')),
    INDEX idx_pengingat_transaksi_waktu (id_transaksi, waktu_pengingat)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Kelola Permohonan Pembatalan
CREATE TABLE IF NOT EXISTS permohonan_pembatalan (
    id_permohonan_pembatalan BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nomor_permohonan VARCHAR(45) NOT NULL,
    id_transaksi BIGINT UNSIGNED NOT NULL,
    id_pelanggan BIGINT UNSIGNED NOT NULL,
    id_pengguna_pemohon BIGINT UNSIGNED NOT NULL,
    alasan_pembatalan TEXT NOT NULL,
    status_permohonan VARCHAR(20) NOT NULL DEFAULT 'DIAJUKAN',
    diajukan_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diputuskan_oleh_admin BIGINT UNSIGNED NULL,
    waktu_keputusan DATETIME NULL,
    catatan_admin TEXT NULL,
    dibuat_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_permohonan_pembatalan),
    CONSTRAINT uq_permohonan_nomor UNIQUE (nomor_permohonan),
    CONSTRAINT uq_permohonan_transaksi UNIQUE (id_transaksi),
    CONSTRAINT uq_permohonan_transaksi_pair UNIQUE (id_permohonan_pembatalan, id_transaksi),
    CONSTRAINT fk_permohonan_transaksi_pelanggan FOREIGN KEY (id_transaksi, id_pelanggan)
        REFERENCES transaksi_laundry (id_transaksi, id_pelanggan)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_permohonan_pemohon_pelanggan FOREIGN KEY (id_pelanggan, id_pengguna_pemohon)
        REFERENCES pelanggan (id_pelanggan, id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_permohonan_admin FOREIGN KEY (diputuskan_oleh_admin)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT ck_permohonan_status CHECK (status_permohonan IN ('DIAJUKAN', 'DISETUJUI', 'DITOLAK', 'SELESAI')),
    INDEX idx_permohonan_status (status_permohonan, diajukan_pada)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Kelola Pengaduan
CREATE TABLE IF NOT EXISTS pengaduan (
    id_pengaduan BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nomor_pengaduan VARCHAR(45) NOT NULL,
    id_transaksi BIGINT UNSIGNED NOT NULL,
    id_pelanggan BIGINT UNSIGNED NOT NULL,
    id_pengguna_pelapor BIGINT UNSIGNED NOT NULL,
    jenis_pengaduan VARCHAR(20) NOT NULL,
    deskripsi_pakaian TEXT NOT NULL,
    jumlah_pakaian INT UNSIGNED NOT NULL DEFAULT 1,
    uraian_kejadian TEXT NOT NULL,
    tanggal_lapor DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    batas_waktu_penelusuran DATETIME NULL,
    hasil_penelusuran TEXT NULL,
    status_tanggung_jawab VARCHAR(35) NOT NULL DEFAULT 'BELUM_DITENTUKAN',
    status_pengaduan VARCHAR(30) NOT NULL DEFAULT 'DILAPORKAN',
    id_admin_pemeriksa BIGINT UNSIGNED NULL,
    id_petugas_pembantu BIGINT UNSIGNED NULL,
    waktu_pemeriksaan DATETIME NULL,
    id_pengguna_terakhir_memperbarui BIGINT UNSIGNED NULL,
    dibuat_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_pengaduan),
    CONSTRAINT uq_pengaduan_nomor UNIQUE (nomor_pengaduan),
    CONSTRAINT uq_pengaduan_id_pelanggan_pair UNIQUE (id_pengaduan, id_pelanggan),
    CONSTRAINT fk_pengaduan_transaksi_pelanggan FOREIGN KEY (id_transaksi, id_pelanggan)
        REFERENCES transaksi_laundry (id_transaksi, id_pelanggan)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pengaduan_pelapor_pelanggan FOREIGN KEY (id_pelanggan, id_pengguna_pelapor)
        REFERENCES pelanggan (id_pelanggan, id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pengaduan_admin FOREIGN KEY (id_admin_pemeriksa)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pengaduan_petugas FOREIGN KEY (id_petugas_pembantu)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pengaduan_pengubah FOREIGN KEY (id_pengguna_terakhir_memperbarui)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT ck_pengaduan_jenis CHECK (jenis_pengaduan IN ('HILANG', 'RUSAK', 'TERTUKAR')),
    CONSTRAINT ck_pengaduan_jumlah CHECK (jumlah_pakaian > 0),
    CONSTRAINT ck_pengaduan_tanggung_jawab CHECK (status_tanggung_jawab IN (
        'BELUM_DITENTUKAN', 'TANGGUNG_JAWAB_LAUNDRY', 'TANGGUNG_JAWAB_PENUH',
        'TANGGUNG_JAWAB_SEBAGIAN', 'BUKAN_TANGGUNG_JAWAB_LAUNDRY'
    )),
    CONSTRAINT ck_pengaduan_status CHECK (status_pengaduan IN (
        'DILAPORKAN', 'DIVERIFIKASI', 'DALAM_PENELUSURAN',
        'MENUNGGU_PERSETUJUAN', 'SOLUSI_DITOLAK', 'DALAM_PENYELESAIAN', 'SELESAI', 'DITOLAK'
    )),
    CONSTRAINT ck_pengaduan_batas_waktu CHECK (batas_waktu_penelusuran IS NULL OR batas_waktu_penelusuran >= tanggal_lapor),
    INDEX idx_pengaduan_pelanggan_tanggal (id_pelanggan, tanggal_lapor),
    INDEX idx_pengaduan_status (status_pengaduan),
    INDEX idx_pengaduan_jenis (jenis_pengaduan)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Lihat Riwayat Status Pengaduan
CREATE TABLE IF NOT EXISTS riwayat_status_pengaduan (
    id_riwayat BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_pengaduan BIGINT UNSIGNED NOT NULL,
    status_sebelumnya VARCHAR(30) NULL,
    status_baru VARCHAR(30) NOT NULL,
    id_pengguna_pengubah BIGINT UNSIGNED NOT NULL,
    waktu_perubahan DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    catatan TEXT NULL,
    PRIMARY KEY (id_riwayat),
    CONSTRAINT fk_riwayat_pengaduan FOREIGN KEY (id_pengaduan)
        REFERENCES pengaduan (id_pengaduan)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_riwayat_pengaduan_pengguna FOREIGN KEY (id_pengguna_pengubah)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    INDEX idx_riwayat_pengaduan_waktu (id_pengaduan, waktu_perubahan)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Kelola Penyelesaian Pengaduan
CREATE TABLE IF NOT EXISTS penyelesaian_pengaduan (
    id_penyelesaian BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_pengaduan BIGINT UNSIGNED NOT NULL,
    id_pelanggan BIGINT UNSIGNED NOT NULL,
    jenis_penyelesaian VARCHAR(35) NOT NULL,
    alasan_penyelesaian TEXT NOT NULL,
    nominal_disepakati DECIMAL(14,2) NOT NULL DEFAULT 0,
    status_persetujuan VARCHAR(25) NOT NULL DEFAULT 'MENUNGGU',
    waktu_pelanggan_merespons DATETIME NULL,
    id_admin_penetap BIGINT UNSIGNED NOT NULL,
    status_pelaksanaan VARCHAR(25) NOT NULL DEFAULT 'BELUM_DILAKSANAKAN',
    dilaksanakan_oleh BIGINT UNSIGNED NULL,
    waktu_pelaksanaan DATETIME NULL,
    catatan TEXT NULL,
    dibuat_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_penyelesaian),
    CONSTRAINT uq_penyelesaian_pengaduan_pair UNIQUE (id_penyelesaian, id_pengaduan),
    CONSTRAINT fk_penyelesaian_pengaduan_pelanggan FOREIGN KEY (id_pengaduan, id_pelanggan)
        REFERENCES pengaduan (id_pengaduan, id_pelanggan)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_penyelesaian_admin FOREIGN KEY (id_admin_penetap)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_penyelesaian_pelaksana FOREIGN KEY (dilaksanakan_oleh)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT ck_penyelesaian_jenis CHECK (jenis_penyelesaian IN (
        'PAKAIAN_DITEMUKAN', 'CUCI_ULANG_GRATIS', 'SETRIKA_ULANG_GRATIS',
        'PENGEMBALIAN_UANG', 'PENGGANTIAN_BARANG',
        'KOMPENSASI_TUNAI', 'LAINNYA'
    )),
    CONSTRAINT ck_penyelesaian_nominal CHECK (nominal_disepakati >= 0),
    CONSTRAINT ck_penyelesaian_status_persetujuan CHECK (status_persetujuan IN (
        'MENUNGGU', 'DISETUJUI_PELANGGAN', 'DITOLAK_PELANGGAN'
    )),
    CONSTRAINT ck_penyelesaian_status_pelaksanaan CHECK (status_pelaksanaan IN (
        'BELUM_DILAKSANAKAN', 'DALAM_PROSES', 'SELESAI', 'DIBATALKAN'
    )),
    CONSTRAINT ck_penyelesaian_respons_waktu CHECK (
        (status_persetujuan = 'MENUNGGU' AND waktu_pelanggan_merespons IS NULL)
        OR (status_persetujuan IN ('DISETUJUI_PELANGGAN', 'DITOLAK_PELANGGAN') AND waktu_pelanggan_merespons IS NOT NULL)
    ),
    CONSTRAINT ck_penyelesaian_waktu_pelaksanaan CHECK (
        (status_pelaksanaan = 'SELESAI' AND waktu_pelaksanaan IS NOT NULL)
        OR (status_pelaksanaan <> 'SELESAI' AND waktu_pelaksanaan IS NULL)
    ),
    INDEX idx_penyelesaian_pengaduan (id_pengaduan, dibuat_pada)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Kelola Pengembalian Dana
CREATE TABLE IF NOT EXISTS pengembalian_dana (
    id_pengembalian BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nomor_pengembalian VARCHAR(45) NOT NULL,
    id_transaksi BIGINT UNSIGNED NOT NULL,
    id_permohonan_pembatalan BIGINT UNSIGNED NULL,
    id_penyelesaian BIGINT UNSIGNED NULL,
    jumlah_pengembalian DECIMAL(14,2) NOT NULL,
    status_pengembalian VARCHAR(20) NOT NULL DEFAULT 'MENUNGGU',
    metode_pengembalian VARCHAR(20) NULL,
    dibuat_oleh_admin BIGINT UNSIGNED NOT NULL,
    dibayarkan_oleh BIGINT UNSIGNED NULL,
    waktu_pengembalian DATETIME NULL,
    alasan TEXT NOT NULL,
    catatan TEXT NULL,
    dibuat_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diperbarui_pada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_pengembalian),
    CONSTRAINT uq_pengembalian_nomor UNIQUE (nomor_pengembalian),
    CONSTRAINT uq_pengembalian_permohonan UNIQUE (id_permohonan_pembatalan),
    CONSTRAINT uq_pengembalian_penyelesaian UNIQUE (id_penyelesaian),
    CONSTRAINT fk_pengembalian_transaksi FOREIGN KEY (id_transaksi)
        REFERENCES transaksi_laundry (id_transaksi)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pengembalian_permohonan FOREIGN KEY (id_permohonan_pembatalan)
        REFERENCES permohonan_pembatalan (id_permohonan_pembatalan)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pengembalian_penyelesaian FOREIGN KEY (id_penyelesaian)
        REFERENCES penyelesaian_pengaduan (id_penyelesaian)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pengembalian_admin FOREIGN KEY (dibuat_oleh_admin)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pengembalian_petugas FOREIGN KEY (dibayarkan_oleh)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT ck_pengembalian_sumber CHECK (
        (id_permohonan_pembatalan IS NOT NULL AND id_penyelesaian IS NULL)
        OR (id_permohonan_pembatalan IS NULL AND id_penyelesaian IS NOT NULL)
    ),
    CONSTRAINT ck_pengembalian_jumlah CHECK (jumlah_pengembalian > 0),
    CONSTRAINT ck_pengembalian_status CHECK (status_pengembalian IN ('MENUNGGU', 'DIBAYAR', 'DITOLAK')),
    CONSTRAINT ck_pengembalian_metode CHECK (
        (status_pengembalian = 'DIBAYAR' AND metode_pengembalian IS NOT NULL AND waktu_pengembalian IS NOT NULL AND dibayarkan_oleh IS NOT NULL)
        OR (status_pengembalian <> 'DIBAYAR' AND metode_pengembalian IS NULL AND waktu_pengembalian IS NULL)
    ),
    CONSTRAINT ck_pengembalian_metode_valid CHECK (
        metode_pengembalian IS NULL OR metode_pengembalian IN ('TUNAI', 'TRANSFER', 'QRIS', 'LAINNYA')
    ),
    INDEX idx_pengembalian_transaksi (id_transaksi),
    INDEX idx_pengembalian_status (status_pengembalian, dibuat_pada)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Catat Keuangan Penyelesaian
CREATE TABLE IF NOT EXISTS catatan_keuangan_penyelesaian (
    id_catatan_keuangan BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_penyelesaian BIGINT UNSIGNED NOT NULL,
    jenis_catatan VARCHAR(30) NOT NULL,
    nominal DECIMAL(14,2) NOT NULL,
    status_catatan VARCHAR(20) NOT NULL DEFAULT 'MENUNGGU',
    metode_pembayaran VARCHAR(20) NULL,
    dicatat_oleh BIGINT UNSIGNED NOT NULL,
    dilaksanakan_oleh BIGINT UNSIGNED NULL,
    tanggal_dicatat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tanggal_dibayar DATETIME NULL,
    catatan TEXT NULL,
    PRIMARY KEY (id_catatan_keuangan),
    CONSTRAINT fk_catatan_keuangan_penyelesaian FOREIGN KEY (id_penyelesaian)
        REFERENCES penyelesaian_pengaduan (id_penyelesaian)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_catatan_keuangan_pencatat FOREIGN KEY (dicatat_oleh)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_catatan_keuangan_pelaksana FOREIGN KEY (dilaksanakan_oleh)
        REFERENCES pengguna (id_pengguna)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT ck_catatan_keuangan_jenis CHECK (jenis_catatan IN (
        'KOMPENSASI_TUNAI', 'PENGGANTIAN_BARANG'
    )),
    CONSTRAINT ck_catatan_keuangan_nominal CHECK (nominal > 0),
    CONSTRAINT ck_catatan_keuangan_status CHECK (status_catatan IN ('MENUNGGU', 'DIBAYAR', 'DIBATALKAN')),
    CONSTRAINT ck_catatan_keuangan_pembayaran CHECK (
        (status_catatan = 'DIBAYAR'
            AND metode_pembayaran IS NOT NULL AND tanggal_dibayar IS NOT NULL AND dilaksanakan_oleh IS NOT NULL)
        OR
        (status_catatan <> 'DIBAYAR' AND tanggal_dibayar IS NULL)
    ),
    CONSTRAINT ck_catatan_keuangan_metode CHECK (
        metode_pembayaran IS NULL OR metode_pembayaran IN ('TUNAI', 'TRANSFER', 'QRIS', 'LAINNYA')
    ),
    INDEX idx_catatan_keuangan_penyelesaian (id_penyelesaian),
    INDEX idx_catatan_keuangan_tanggal (tanggal_dibayar)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fitur: Validasi dan Perubahan Status
DELIMITER $$

CREATE TRIGGER trg_pelanggan_sebelum_tambah
BEFORE INSERT ON pelanggan
FOR EACH ROW
BEGIN
    DECLARE v_peran VARCHAR(20);
    SELECT peran INTO v_peran FROM pengguna WHERE id_pengguna = NEW.id_pengguna;
    IF v_peran <> 'PELANGGAN' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Profil pelanggan hanya boleh terhubung ke akun dengan role PELANGGAN.';
    END IF;
END$$

CREATE TRIGGER trg_transaksi_sebelum_tambah
BEFORE INSERT ON transaksi_laundry
FOR EACH ROW
BEGIN
    DECLARE v_peran_pencatat VARCHAR(20);
    SELECT peran INTO v_peran_pencatat FROM pengguna WHERE id_pengguna = NEW.id_petugas_pencatat;

    IF v_peran_pencatat <> 'PETUGAS' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Transaksi baru harus dicatat oleh akun Petugas/Kasir.';
    END IF;
    IF NEW.berat_aktual < 2.00 AND NEW.pelanggan_setuju_minimum <> 1 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Berat di bawah 2 kg harus disetujui pelanggan terlebih dahulu.';
    END IF;
    IF NEW.status_transaksi <> 'MENUNGGU_PEMBAYARAN' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Transaksi baru harus berstatus MENUNGGU_PEMBAYARAN.';
    END IF;
END$$

CREATE TRIGGER trg_transaksi_sebelum_ubah
BEFORE UPDATE ON transaksi_laundry
FOR EACH ROW
BEGIN
    IF NOT (NEW.status_transaksi <=> OLD.status_transaksi) THEN
        IF OLD.status_transaksi = 'MENUNGGU_PEMBAYARAN'
           AND NEW.status_transaksi NOT IN ('DITERIMA', 'DIBATALKAN') THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Transaksi menunggu pembayaran hanya dapat menjadi DITERIMA atau DIBATALKAN.';
        ELSEIF OLD.status_transaksi = 'DITERIMA'
           AND NEW.status_transaksi NOT IN ('DIPROSES', 'DIBATALKAN') THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Transaksi DITERIMA hanya dapat diproses atau dibatalkan sesuai prosedur.';
        ELSEIF OLD.status_transaksi = 'DIPROSES'
           AND NEW.status_transaksi <> 'SIAP_DIAMBIL' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Transaksi DIPROSES hanya dapat berubah menjadi SIAP_DIAMBIL.';
        ELSEIF OLD.status_transaksi = 'SIAP_DIAMBIL'
           AND NEW.status_transaksi <> 'SELESAI' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Transaksi SIAP_DIAMBIL hanya dapat menjadi SELESAI setelah diserahkan.';
        ELSEIF OLD.status_transaksi IN ('SELESAI', 'DIBATALKAN') THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Status transaksi akhir tidak dapat diubah kembali.';
        END IF;

        IF NEW.status_transaksi IN ('DITERIMA', 'DIPROSES', 'SIAP_DIAMBIL', 'SELESAI')
           AND NOT EXISTS (
               SELECT 1 FROM pembayaran_transaksi p
               WHERE p.id_transaksi = NEW.id_transaksi
                 AND p.status_verifikasi = 'DISETUJUI'
           ) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Transaksi hanya dapat diterima atau dikerjakan setelah pembayaran di-approve Petugas.';
        END IF;

        IF NEW.status_transaksi = 'DIBATALKAN'
           AND EXISTS (
               SELECT 1 FROM pembayaran_transaksi p
               WHERE p.id_transaksi = NEW.id_transaksi
                 AND p.status_verifikasi = 'MENUNGGU_VERIFIKASI'
           ) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Selesaikan atau batalkan percobaan verifikasi pembayaran sebelum membatalkan transaksi.';
        END IF;

        IF NEW.status_transaksi = 'DIBATALKAN'
           AND EXISTS (
               SELECT 1 FROM pembayaran_transaksi p
               WHERE p.id_transaksi = NEW.id_transaksi
                 AND p.status_verifikasi = 'DISETUJUI'
           )
           AND NOT EXISTS (
               SELECT 1 FROM pengembalian_dana r
               WHERE r.id_transaksi = NEW.id_transaksi
                 AND r.status_pengembalian = 'DIBAYAR'
                 AND r.id_permohonan_pembatalan IS NOT NULL
           ) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Transaksi yang pembayarannya sudah di-approve hanya dapat dibatalkan setelah refund penuh dibayarkan.';
        END IF;

        IF NEW.status_transaksi = 'SELESAI'
           AND NOT EXISTS (
               SELECT 1 FROM pengambilan_cucian a
               WHERE a.id_transaksi = NEW.id_transaksi
           ) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Transaksi hanya dapat SELESAI setelah pengambilan cucian dicatat.';
        END IF;

        IF NEW.status_transaksi = 'SIAP_DIAMBIL' AND NEW.tanggal_siap_diambil IS NULL THEN
            SET NEW.tanggal_siap_diambil = CURRENT_TIMESTAMP;
        END IF;
    END IF;
END$$

CREATE TRIGGER trg_transaksi_setelah_tambah
AFTER INSERT ON transaksi_laundry
FOR EACH ROW
BEGIN
    INSERT INTO riwayat_status_transaksi
        (id_transaksi, status_sebelumnya, status_baru, id_pengguna_pengubah, catatan)
    VALUES
        (NEW.id_transaksi, NULL, NEW.status_transaksi, NEW.id_petugas_pencatat, 'Transaksi dibuat dan menunggu pembayaran.');
END$$

CREATE TRIGGER trg_transaksi_setelah_ubah
AFTER UPDATE ON transaksi_laundry
FOR EACH ROW
BEGIN
    IF NOT (NEW.status_transaksi <=> OLD.status_transaksi) THEN
        INSERT INTO riwayat_status_transaksi
            (id_transaksi, status_sebelumnya, status_baru, id_pengguna_pengubah, catatan)
        VALUES
            (NEW.id_transaksi, OLD.status_transaksi, NEW.status_transaksi,
             COALESCE(NEW.id_pengguna_terakhir_memperbarui, NEW.id_petugas_pencatat),
             'Perubahan status transaksi.');
    END IF;
END$$

CREATE TRIGGER trg_pembayaran_sebelum_tambah
BEFORE INSERT ON pembayaran_transaksi
FOR EACH ROW
BEGIN
    DECLARE v_total DECIMAL(14,2);
    DECLARE v_status VARCHAR(30);
    DECLARE v_peran_pencatat VARCHAR(20);

    SELECT total_biaya, status_transaksi
      INTO v_total, v_status
    FROM transaksi_laundry
    WHERE id_transaksi = NEW.id_transaksi;

    IF v_status <> 'MENUNGGU_PEMBAYARAN' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Percobaan pembayaran hanya dapat dicatat untuk transaksi yang menunggu pembayaran.';
    END IF;

    SELECT peran INTO v_peran_pencatat
    FROM pengguna WHERE id_pengguna = NEW.id_petugas_pencatat;

    IF v_peran_pencatat <> 'PETUGAS' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Percobaan pembayaran harus dicatat oleh Petugas/Kasir.';
    END IF;
    IF NEW.jumlah_bayar <> v_total THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Pembayaran harus lunas sekaligus dan sama dengan total biaya transaksi.';
    END IF;
    IF NEW.status_verifikasi <> 'MENUNGGU_VERIFIKASI'
       OR NEW.nomor_struk IS NOT NULL
       OR NEW.id_petugas_verifikasi IS NOT NULL
       OR NEW.waktu_verifikasi IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Percobaan pembayaran baru harus menunggu verifikasi dan belum boleh memiliki nomor struk.';
    END IF;
END$$

CREATE TRIGGER trg_pembayaran_sebelum_ubah
BEFORE UPDATE ON pembayaran_transaksi
FOR EACH ROW
BEGIN
    DECLARE v_peran_verifikator VARCHAR(20);
    DECLARE v_total DECIMAL(14,2);
    DECLARE v_status_transaksi VARCHAR(30);

    IF OLD.status_verifikasi <> 'MENUNGGU_VERIFIKASI' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Percobaan pembayaran yang sudah diputuskan tidak dapat diubah lagi.';
    END IF;
    IF NEW.status_verifikasi NOT IN ('DISETUJUI', 'DITOLAK', 'DIBATALKAN') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Status verifikasi hanya dapat diubah menjadi DISETUJUI, DITOLAK, atau DIBATALKAN.';
    END IF;
    IF NEW.id_transaksi <> OLD.id_transaksi
       OR NEW.jumlah_bayar <> OLD.jumlah_bayar
       OR NEW.metode_pembayaran <> OLD.metode_pembayaran
       OR NEW.id_petugas_pencatat <> OLD.id_petugas_pencatat
       OR NEW.waktu_pencatatan <> OLD.waktu_pencatatan THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Data pokok percobaan pembayaran tidak boleh diubah saat verifikasi.';
    END IF;
    IF NEW.id_petugas_verifikasi IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Petugas yang melakukan verifikasi wajib dicatat.';
    END IF;

    SELECT peran INTO v_peran_verifikator
    FROM pengguna WHERE id_pengguna = NEW.id_petugas_verifikasi;
    IF v_peran_verifikator <> 'PETUGAS' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Verifikasi pembayaran harus dilakukan oleh Petugas/Kasir.';
    END IF;

    SET NEW.waktu_verifikasi = COALESCE(NEW.waktu_verifikasi, CURRENT_TIMESTAMP);
    IF NEW.status_verifikasi = 'DISETUJUI' THEN
        IF NEW.nomor_struk IS NULL OR TRIM(NEW.nomor_struk) = '' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Nomor struk wajib dibuat ketika pembayaran di-approve.';
        END IF;
        SELECT total_biaya, status_transaksi
          INTO v_total, v_status_transaksi
        FROM transaksi_laundry
        WHERE id_transaksi = NEW.id_transaksi;
        IF v_status_transaksi <> 'MENUNGGU_PEMBAYARAN' OR NEW.jumlah_bayar <> v_total THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Pembayaran tidak dapat di-approve: status transaksi atau jumlah pembayaran tidak sesuai.';
        END IF;
    ELSE
        SET NEW.nomor_struk = NULL;
        IF NEW.status_verifikasi = 'DITOLAK'
           AND (NEW.catatan_verifikasi IS NULL OR TRIM(NEW.catatan_verifikasi) = '') THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Alasan penolakan pembayaran wajib dicatat.';
        END IF;
    END IF;
END$$

CREATE TRIGGER trg_pembayaran_setelah_ubah
AFTER UPDATE ON pembayaran_transaksi
FOR EACH ROW
BEGIN
    DECLARE v_durasi_jam INT UNSIGNED;

    IF OLD.status_verifikasi = 'MENUNGGU_VERIFIKASI'
       AND NEW.status_verifikasi = 'DISETUJUI' THEN
        SELECT durasi_jam_saat_transaksi INTO v_durasi_jam
        FROM transaksi_laundry WHERE id_transaksi = NEW.id_transaksi;
        UPDATE transaksi_laundry
           SET status_transaksi = 'DITERIMA',
               estimasi_selesai = DATE_ADD(NEW.waktu_verifikasi, INTERVAL v_durasi_jam HOUR),
               id_pengguna_terakhir_memperbarui = NEW.id_petugas_verifikasi
         WHERE id_transaksi = NEW.id_transaksi;
    END IF;
END$$

CREATE TRIGGER trg_pengambilan_sebelum_tambah
BEFORE INSERT ON pengambilan_cucian
FOR EACH ROW
BEGIN
    DECLARE v_status VARCHAR(30);
    DECLARE v_peran_petugas VARCHAR(20);
    SELECT status_transaksi INTO v_status
    FROM transaksi_laundry
    WHERE id_transaksi = NEW.id_transaksi;
    IF v_status <> 'SIAP_DIAMBIL' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Pengambilan hanya dapat dicatat ketika cucian berstatus SIAP_DIAMBIL.';
    END IF;
    SELECT peran INTO v_peran_petugas
    FROM pengguna WHERE id_pengguna = NEW.id_petugas_penyerah;
    IF v_peran_petugas <> 'PETUGAS' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Pengambilan cucian harus dicatat oleh Petugas/Kasir.';
    END IF;
END$$

CREATE TRIGGER trg_pengambilan_setelah_tambah
AFTER INSERT ON pengambilan_cucian
FOR EACH ROW
BEGIN
    UPDATE transaksi_laundry
       SET status_transaksi = 'SELESAI',
           id_pengguna_terakhir_memperbarui = NEW.id_petugas_penyerah
     WHERE id_transaksi = NEW.id_transaksi;
END$$

CREATE TRIGGER trg_pembatalan_sebelum_tambah
BEFORE INSERT ON permohonan_pembatalan
FOR EACH ROW
BEGIN
    DECLARE v_status VARCHAR(30);
    SELECT status_transaksi INTO v_status
    FROM transaksi_laundry
    WHERE id_transaksi = NEW.id_transaksi;
    IF v_status <> 'DITERIMA' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Permohonan pembatalan biasa hanya dapat diajukan sebelum pengerjaan dimulai.';
    END IF;
END$$

CREATE TRIGGER trg_pengembalian_sebelum_tambah
BEFORE INSERT ON pengembalian_dana
FOR EACH ROW
BEGIN
    DECLARE v_total DECIMAL(14,2);
    DECLARE v_status_permohonan VARCHAR(20);
    DECLARE v_status_penyelesaian VARCHAR(25);
    DECLARE v_jenis_penyelesaian VARCHAR(35);
    DECLARE v_nominal_disepakati DECIMAL(14,2);
    DECLARE v_id_transaksi_sumber BIGINT UNSIGNED;
    DECLARE v_peran_admin VARCHAR(20);

    SELECT peran INTO v_peran_admin FROM pengguna WHERE id_pengguna = NEW.dibuat_oleh_admin;
    IF v_peran_admin <> 'ADMIN' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Catatan pengembalian dana harus dibuat oleh Admin/Pemilik.';
    END IF;

    SELECT total_biaya INTO v_total
    FROM transaksi_laundry
    WHERE id_transaksi = NEW.id_transaksi;

    IF NEW.id_permohonan_pembatalan IS NOT NULL THEN
        SELECT status_permohonan, id_transaksi
          INTO v_status_permohonan, v_id_transaksi_sumber
        FROM permohonan_pembatalan
        WHERE id_permohonan_pembatalan = NEW.id_permohonan_pembatalan;
        IF v_status_permohonan <> 'DISETUJUI' OR v_id_transaksi_sumber <> NEW.id_transaksi THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Refund pembatalan harus memiliki persetujuan Admin dan transaksi yang sesuai.';
        END IF;
        IF NEW.jumlah_pengembalian <> v_total THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Refund pembatalan biasa harus mengembalikan seluruh pembayaran.';
        END IF;
    ELSE
        SELECT s.status_persetujuan, s.jenis_penyelesaian, s.nominal_disepakati, pg.id_transaksi
          INTO v_status_penyelesaian, v_jenis_penyelesaian, v_nominal_disepakati, v_id_transaksi_sumber
        FROM penyelesaian_pengaduan s
        INNER JOIN pengaduan pg ON pg.id_pengaduan = s.id_pengaduan
        WHERE s.id_penyelesaian = NEW.id_penyelesaian;
        IF v_status_penyelesaian <> 'DISETUJUI_PELANGGAN'
           OR v_jenis_penyelesaian <> 'PENGEMBALIAN_UANG'
           OR v_id_transaksi_sumber <> NEW.id_transaksi THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Refund pengaduan harus berasal dari penyelesaian pengembalian uang yang disetujui pelanggan.';
        END IF;
        IF NEW.jumlah_pengembalian > v_total THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Nominal refund tidak boleh melebihi pembayaran transaksi.';
        END IF;
        IF NEW.jumlah_pengembalian <> v_nominal_disepakati THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Nominal refund harus sama dengan nominal yang disepakati pada penyelesaian pengaduan.';
        END IF;
    END IF;
END$$

CREATE TRIGGER trg_pengembalian_sebelum_ubah
BEFORE UPDATE ON pengembalian_dana
FOR EACH ROW
BEGIN
    IF OLD.status_pengembalian = 'DIBAYAR' AND NEW.status_pengembalian <> 'DIBAYAR' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Catatan pengembalian dana yang sudah dibayar tidak boleh dibatalkan atau diubah statusnya.';
    END IF;
    IF NEW.status_pengembalian = 'DIBAYAR'
       AND (NEW.metode_pengembalian IS NULL OR NEW.waktu_pengembalian IS NULL OR NEW.dibayarkan_oleh IS NULL) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Lengkapi metode, waktu, dan petugas pengembali sebelum menandai refund dibayar.';
    END IF;
END$$

CREATE TRIGGER trg_pengembalian_setelah_ubah
AFTER UPDATE ON pengembalian_dana
FOR EACH ROW
BEGIN
    IF OLD.status_pengembalian <> 'DIBAYAR' AND NEW.status_pengembalian = 'DIBAYAR' THEN
        IF NEW.id_permohonan_pembatalan IS NOT NULL THEN
            UPDATE permohonan_pembatalan
               SET status_permohonan = 'SELESAI',
                   waktu_keputusan = COALESCE(waktu_keputusan, NEW.waktu_pengembalian)
             WHERE id_permohonan_pembatalan = NEW.id_permohonan_pembatalan;
            UPDATE transaksi_laundry
               SET status_transaksi = 'DIBATALKAN',
                   id_pengguna_terakhir_memperbarui = NEW.dibayarkan_oleh
             WHERE id_transaksi = NEW.id_transaksi;
        ELSE
            UPDATE penyelesaian_pengaduan
               SET status_pelaksanaan = 'SELESAI',
                   dilaksanakan_oleh = NEW.dibayarkan_oleh,
                   waktu_pelaksanaan = NEW.waktu_pengembalian
             WHERE id_penyelesaian = NEW.id_penyelesaian;
            UPDATE pengaduan p
            INNER JOIN penyelesaian_pengaduan s ON s.id_pengaduan = p.id_pengaduan
               SET p.status_pengaduan = 'SELESAI',
                   p.id_pengguna_terakhir_memperbarui = NEW.dibayarkan_oleh
             WHERE s.id_penyelesaian = NEW.id_penyelesaian;
        END IF;
    END IF;
END$$

CREATE TRIGGER trg_pengaduan_sebelum_tambah
BEFORE INSERT ON pengaduan
FOR EACH ROW
BEGIN
    DECLARE v_status VARCHAR(30);
    SELECT status_transaksi INTO v_status
    FROM transaksi_laundry
    WHERE id_transaksi = NEW.id_transaksi;
    IF v_status IN ('MENUNGGU_PEMBAYARAN', 'DIBATALKAN') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Pengaduan hanya dapat dikaitkan dengan transaksi yang sudah diterima dan tidak dibatalkan.';
    END IF;
END$$

CREATE TRIGGER trg_pengaduan_setelah_tambah
AFTER INSERT ON pengaduan
FOR EACH ROW
BEGIN
    INSERT INTO riwayat_status_pengaduan
        (id_pengaduan, status_sebelumnya, status_baru, id_pengguna_pengubah, catatan)
    VALUES
        (NEW.id_pengaduan, NULL, NEW.status_pengaduan, NEW.id_pengguna_pelapor, 'Pengaduan dikirim oleh pelanggan.');
END$$

CREATE TRIGGER trg_pengaduan_setelah_ubah
AFTER UPDATE ON pengaduan
FOR EACH ROW
BEGIN
    IF NOT (NEW.status_pengaduan <=> OLD.status_pengaduan) THEN
        INSERT INTO riwayat_status_pengaduan
            (id_pengaduan, status_sebelumnya, status_baru, id_pengguna_pengubah, catatan)
        VALUES
            (NEW.id_pengaduan, OLD.status_pengaduan, NEW.status_pengaduan,
             COALESCE(NEW.id_pengguna_terakhir_memperbarui, NEW.id_admin_pemeriksa, NEW.id_pengguna_pelapor),
             'Perubahan status pengaduan.');
    END IF;
END$$

CREATE TRIGGER trg_penyelesaian_sebelum_tambah
BEFORE INSERT ON penyelesaian_pengaduan
FOR EACH ROW
BEGIN
    DECLARE v_peran_admin VARCHAR(20);
    SELECT peran INTO v_peran_admin FROM pengguna WHERE id_pengguna = NEW.id_admin_penetap;
    IF v_peran_admin <> 'ADMIN' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Penyelesaian pengaduan harus ditetapkan oleh Admin/Pemilik.';
    END IF;
END$$

CREATE TRIGGER trg_catatan_keuangan_sebelum_tambah
BEFORE INSERT ON catatan_keuangan_penyelesaian
FOR EACH ROW
BEGIN
    DECLARE v_status_persetujuan VARCHAR(25);
    DECLARE v_jenis_penyelesaian VARCHAR(35);
    DECLARE v_peran_pencatat VARCHAR(20);

    SELECT status_persetujuan, jenis_penyelesaian
      INTO v_status_persetujuan, v_jenis_penyelesaian
    FROM penyelesaian_pengaduan
    WHERE id_penyelesaian = NEW.id_penyelesaian;
    SELECT peran INTO v_peran_pencatat
    FROM pengguna WHERE id_pengguna = NEW.dicatat_oleh;

    IF v_status_persetujuan <> 'DISETUJUI_PELANGGAN' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Catatan keuangan hanya boleh dibuat setelah pelanggan menyetujui penyelesaian.';
    END IF;
    IF v_peran_pencatat <> 'ADMIN' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Catatan keuangan penyelesaian harus dicatat oleh Admin/Pemilik.';
    END IF;
    IF (NEW.jenis_catatan = 'KOMPENSASI_TUNAI' AND v_jenis_penyelesaian <> 'KOMPENSASI_TUNAI')
       OR (NEW.jenis_catatan = 'PENGGANTIAN_BARANG' AND v_jenis_penyelesaian <> 'PENGGANTIAN_BARANG') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Jenis catatan keuangan harus sesuai dengan jenis penyelesaian yang disetujui.';
    END IF;
END$$

DELIMITER ;
