# SISTEM INFORMASI MANAJEMEN TRANSAKSI, PENGERJAAN, DAN PENGADUAN LAUNDRY (KELOMPOK 4)

Aplikasi desktop Sistem Informasi Manajemen Laundry yang dikembangkan menggunakan **Java Swing** dan **MySQL** untuk memenuhi Tugas Besar mata kuliah **Pemrograman Berorientasi Objek Lanjutan (PBOL)** Program Studi Teknologi Informasi.

---

## 1. Identitas Proyek

- **Kelompok:** 4
- **Mata Kuliah:** Pemrograman Berorientasi Objek Lanjutan (PBOL)
- **Teknologi Utama:** Java (JDK 17/21), Java Swing (`javax.swing`), MySQL 8.x, JDBC, Maven, BCrypt
- **Database:** `laundry_kelompok4` (Dijalankan via Laragon / phpMyAdmin di `localhost:3306`)

---

## 2. Palet Warna Resmi Kelompok 4

Aplikasi ini menggunakan identitas visual yang modern, segar, dan profesional:

| Nama Warna | HEX | Peran dalam Antarmuka |
|---|---|---|
| **Amalfi Tile** | `#2E5AA7` | Warna primer, sidebar navigasi, header tabel, tombol penting |
| **Citrus Zest** | `#FFA62B` | Tombol aksi utama (Approve, Simpan Transaksi, Tambah Akun) |
| **Sea Breeze** | `#86C5FF` | Informasi pendukung, highlight seleksi tabel, status diproses |
| **Cream Gelato** | `#F8E6A0` | Badge peran pengguna, kartu sorotan lembut |
| **Latar Belakang** | `#F5F8FC` | Warna latar bersih netral |
| **Teks Gelap** | `#17324D` | Teks utama kontras tinggi, mudah dibaca |

---

## 3. Fitur Utama Berdasarkan Peran (Role)

Aplikasi memiliki **satu entry-point desktop** yang otomatis membuka dashboard sesuai peran pengguna saat login:

### A. Admin / Pemilik
1. **Setup Akun Awal**: Wizard otomatis saat pertama kali aplikasi dibuka jika belum ada akun Admin di database.
2. **Dashboard Statistik**: Metrik total pelanggan, petugas aktif, total transaksi, total penerimaan kas jasa, cucian diproses, pengaduan aktif yang belum berstatus selesai/ditolak, dan refund menunggu.
3. **Kelola Layanan, Paket & Tarif**: Menambah layanan (*Cuci Komplit*, *Setrika Saja*), paket (*Reguler 48 jam*, *One Day 24 jam*, *Semi Express 6 jam*, *Express 3 jam*), serta mengubah tarif resmi per kg.
4. **Kelola Akun Petugas**: Menambah akun petugas kasir baru, mengubah data, mengaktifkan/menonaktifkan (soft delete menjaga integritas transaksi lama), dan reset kata sandi.
5. **Kelola Pengaduan**: Menerima pengaduan pakaian hilang/rusak/tertukar, menugaskan penelusuran ke petugas kasir, menetapkan tanggung jawab, dan mengajukan proposal kompensasi/cuci ulang/ganti uang. Solusi yang ditolak pelanggan kembali ke admin; setelah solusi yang disetujui benar-benar dilaksanakan, admin dapat menandai pengaduan selesai.
6. **Kelola Pembatalan & Refund**: Menyetujui pembatalan pesanan yang belum dicuci dan menerbitkan otorisasi pengembalian dana 100%.
7. **Laporan Keuangan & Operasional**: Memantau seluruh transaksi, penerimaan kas jasa, pengeluaran refund/kompensasi, dan memantau cucian menumpuk yang belum diambil.

### B. Petugas / Kasir
1. **Pencatatan Transaksi Baru**: Mencari pelanggan, memilih layanan/paket, menimbang berat aktual, validasi berat minimum 2.00 Kg, menghitung total biaya otomatis, dan menyimpan pesanan (`MENUNGGU_PEMBAYARAN`).
2. **Verifikasi Pembayaran & Approval**: Memeriksa pembayaran nyata (Tunai, Transfer m-banking, atau Dashboard QRIS). Menekan **APPROVE PEMBAYARAN** untuk menerbitkan nomor struk resmi dan mengubah status menjadi `DITERIMA` secara otomatis via trigger database.
3. **Cetak & Preview Struk**: Mencetak struk bukti pembayaran digital/fisik.
4. **Pengerjaan Laundry**: Mengubah status pengerjaan bertahap: `DITERIMA` -> `DIPROSES` -> `SIAP_DIAMBIL`.
5. **Pengambilan Cucian**: Memverifikasi identitas pelanggan yang datang mengambil pakaian, mencatat nama pengambil, dan memicu status otomatis menjadi `SELESAI` (tanpa pembayaran ulang).
6. **Bantu Penelusuran Pengaduan**: Memeriksa fisik gerai dan menginput temuan penelusuran pakaian bermasalah.
7. **Pelaksanaan Refund**: Menyerahkan uang refund yang telah disetujui Admin kepada pelanggan dan menandai status `DIBAYAR`.

### C. Pelanggan
1. **Registrasi Mandiri**: Pendaftaran akun pelanggan secara mandiri (role otomatis `PELANGGAN`, tanpa pilihan role di form).
2. **Beranda Pelanggan**: Sapaan personal, kode pelanggan (`PLG-XXXX`), dan ringkasan pesanan aktif.
3. **Katalog Tarif Resmi**: Menampilkan daftar layanan, durasi jam, dan harga per kg yang transparan.
4. **Riwayat Cucian & Status Real-Time**: Melihat transaksi pribadi yang terisolasi aman berdasarkan sesi login pelanggan.
5. **Struk Digital**: Melihat rincian pembayaran untuk pesanan yang telah disetujui; pencetakan struk dilakukan oleh petugas.
6. **Pengaduan Cucian**: Mengajukan keluhan (Hilang, Rusak, Tertukar) dengan deskripsi dan kronologi jelas.
7. **Musyawarah Solusi**: Meninjau proposal solusi dari Admin dan mengklik **Setuju** atau **Tolak**.
8. **Pengajuan Pembatalan**: Mengajukan pembatalan pesanan berbayar yang masih berstatus `DITERIMA` (sebelum dicuci).

---

## 4. Struktur Package & Penerapan Materi PBOL

Struktur source code dirancang rapi, modular, dan mudah dipahami:

```
src/main/java/id/kelompok4/laundry/
├── Main.java                          <-- Titik masuk aplikasi (Entry Point)
├── konektor/
│   └── KoneksiDatabase.java          <-- Singleton koneksi JDBC & pembaca konfigurasi
├── model/
│   ├── Pengguna.java                  <-- [ABSTRACT CLASS] Induk seluruh entitas akun
│   ├── Admin.java                     <-- [INHERITANCE & OVERRIDING] Subclass Pengguna
│   ├── Petugas.java                   <-- [INHERITANCE & OVERRIDING] Subclass Pengguna
│   ├── Pelanggan.java                 <-- [INHERITANCE & OVERRIDING] Subclass Pengguna
│   ├── MetodePembayaran.java          <-- [INTERFACE] Kontrak aturan metode pembayaran
│   ├── PembayaranTunai.java           <-- [IMPLEMENTASI INTERFACE]
│   ├── PembayaranTransfer.java        <-- [IMPLEMENTASI INTERFACE]
│   ├── PembayaranQRIS.java            <-- [IMPLEMENTASI INTERFACE]
│   ├── JenisLayanan.java              <-- Model Layanan
│   ├── PaketLaundry.java              <-- Model Paket
│   ├── TarifLayanan.java              <-- Model Tarif
│   ├── TransaksiLaundry.java          <-- Model Transaksi
│   ├── PembayaranTransaksi.java       <-- Model Pembayaran
│   ├── PengambilanCucian.java         <-- Model Pengambilan
│   ├── Pengaduan.java                 <-- Model Pengaduan
│   ├── PenyelesaianPengaduan.java     <-- Model Solusi Pengaduan
│   ├── PermohonanPembatalan.java      <-- Model Pembatalan
│   ├── PengembalianDana.java          <-- Model Refund
│   └── CatatanKeuangan.java           <-- Model Catatan Keuangan Kompensasi
├── dao/
│   ├── PenggunaDAO.java               <-- Query pengguna (Polymorphic Mapping)
│   ├── PelangganDAO.java              <-- Database Transaction (Commit & Rollback)
│   ├── LayananDAO.java                <-- CRUD Layanan
│   ├── PaketDAO.java                  <-- CRUD Paket
│   ├── TarifDAO.java                  <-- CRUD Tarif
│   ├── TransaksiDAO.java              <-- Insert transaksi (tanpa generated column)
│   ├── PembayaranDAO.java             <-- Pencatatan pembayaran kasir
│   ├── PengambilanDAO.java            <-- Pencatatan penyerahan pakaian
│   ├── PengaduanDAO.java              <-- Query pengaduan
│   ├── PembatalanDAO.java             <-- Query pembatalan
│   ├── PengembalianDanaDAO.java       <-- Query refund dana
│   └── LaporanDAO.java                <-- Query analitik & views database
├── service/
│   ├── AutentikasiService.java        <-- Logika login, registrasi & RBAC
│   ├── TransaksiService.java          <-- Aturan hitung biaya & minimum 2 Kg
│   ├── PembayaranService.java         <-- Validasi bayar 100% pas & approval kasir
│   ├── PengerjaanService.java         <-- Alur status pengerjaan & serah terima
│   ├── PengaduanService.java          <-- Alur komplain, penelusuran & musyawarah
│   ├── PembatalanRefundService.java   <-- Alur pembatalan & pencairan refund
│   └── LaporanService.java            <-- Ringkasan statistik ketiga role
├── view/
│   ├── komponen/
│   │   ├── TemaWarna.java             <-- Palet HEX resmi Kelompok 4
│   │   ├── TombolKustom.java          <-- Tombol modern dengan efek hover
│   │   ├── KartuStatistik.java        <-- Kartu metrik dashboard beraksen
│   │   ├── TabelKustom.java           <-- JTable dengan header & baris selang-seling
│   │   └── HeaderBar.java             <-- Navbar atas dengan info user login
│   ├── login/
│   │   ├── FormLogin.java             <-- Form otentikasi utama
│   │   ├── FormRegistrasiPelanggan.java <-- Form pendaftaran mandiri pelanggan
│   │   └── DialogSetupAdminAwal.java  <-- Setup akun admin pertama
│   ├── admin/
│   │   ├── DashboardAdminFrame.java   <-- Dashboard Pemilik
│   │   ├── PanelRingkasanAdmin.java
│   │   ├── PanelKelolaLayananPaket.java
│   │   ├── PanelKelolaPetugas.java
│   │   ├── PanelKelolaPengaduanAdmin.java
│   │   ├── PanelKelolaPembatalanAdmin.java
│   │   └── PanelLaporanAdmin.java
│   ├── petugas/
│   │   ├── DashboardPetugasFrame.java <-- Dashboard Kasir
│   │   ├── PanelTransaksiBaru.java
│   │   ├── PanelVerifikasiPembayaran.java
│   │   ├── PanelPengerjaanLaundry.java
│   │   ├── PanelPengambilanCucian.java
│   │   ├── PanelPenelusuranKasir.java
│   │   └── PanelRefundKasir.java
│   └── pelanggan/
│       ├── DashboardPelangganFrame.java <-- Dashboard Pelanggan
│       ├── PanelRingkasanPelanggan.java
│       ├── PanelRiwayatPelanggan.java
│       ├── PanelLayananPelanggan.java
│       └── PanelPengaduanPelanggan.java
└── util/
    ├── PasswordUtil.java              <-- BCrypt password hashing
    ├── SessionManager.java            <-- Session holder pengguna aktif
    ├── FormatUtil.java                <-- Format Rupiah, desimal berat & tanggal
    └── CetakStrukUtil.java            <-- Generator struk & Printable Java
```

---

## 5. Panduan Menjalankan Aplikasi di IntelliJ IDEA

### Langkah 1: Pastikan Database MySQL di Laragon Sudah Aktif
1. Buka aplikasi **Laragon**.
2. Klik tombol **Start All** (memastikan Apache & MySQL berjalan di port `3306`).
3. Impor `database/laundry_kelompok4.sql` melalui phpMyAdmin untuk membuat tabel, constraint, dan trigger yang dipakai aplikasi.
4. Setelah setup Admin pertama, login sebagai Admin dan tambahkan jenis layanan, paket laundry, serta tarif aktif sebelum mencatat transaksi. Skrip database membuat struktur saja dan tidak mengisi data katalog awal.

### Langkah 2: Buka Proyek di IntelliJ IDEA
1. Buka **IntelliJ IDEA**.
2. Pilih menu **File -> Open...**.
3. Arahkan ke folder hasil clone repository ini.
4. IntelliJ akan secara otomatis mendeteksi file `pom.xml` dan mengunduh / mengaitkan pustaka dependensi (MySQL Connector/J, jBCrypt, FlatLaf).

### Langkah 3: Periksa Konfigurasi Koneksi (Jika Diperlukan)
Salin `database/database.properties.example` menjadi `database/database.properties`, lalu sesuaikan
nilainya dengan pengaturan MySQL lokal. File konfigurasi lokal ini tidak dilacak Git agar
kredensial tiap pengguna tidak ikut terunggah. Pengaturan contoh untuk Laragon:
```properties
db.host=localhost
db.port=3306
db.name=laundry_kelompok4
db.username=root
db.password=
```

### Langkah 4: Jalankan Program
1. Pada Project Explorer IntelliJ, buka file:
   `src/main/java/id/kelompok4/laundry/Main.java`.
2. Klik kanan pada file `Main.java` lalu pilih **Run 'Main.main()'** (atau tekan `Shift + F10`).
3. Saat pertama kali dijalankan:
   - Jika tabel `pengguna` masih kosong dari akun Admin, dialog **Setup Akun Admin Pertama** akan otomatis muncul. Masukkan nama lengkap, username, dan password Admin pertama Anda.
   - Setelah itu, layar login utama akan terbuka.
4. Anda dapat:
   - Login sebagai **Admin** untuk mengelola layanan, tarif, dan menambah akun Petugas/Kasir.
   - Membuka form pendaftaran untuk membuat akun **Pelanggan**.
   - Login sebagai **Petugas** untuk mencatat transaksi dan memverifikasi pembayaran.

---

## 6. Skenario Pengujian Sistem (20 Kasus Selesai & Teruji)

| Kasus | Fitur yang Diuji | Status Hasil |
|:---:|---|---|
| 1 | Registrasi Pelanggan Baru | Berhasil, data tersimpan di tabel `pengguna` & `pelanggan` dengan kode `PLG-XXXX`. |
| 2 | Cegah Username Duplikat | Berhasil, sistem menolak username yang sudah digunakan. |
| 3 | Validasi Login Gagal | Berhasil, password salah atau akun nonaktif ditolak dengan dialog pesan jelas. |
| 4 | Validasi Berat < 2.00 Kg | Berhasil, sistem otomatis menghitung tagihan minimum 2.00 Kg dan meminta konfirmasi. |
| 5 | Pelanggan Tolak Minimum | Berhasil, transaksi tidak disimpan ke database. |
| 6 | Pembayaran Tunai & Approve | Berhasil, kasir klik Approve, nomor struk terbit, status transaksi otomatis menjadi `DITERIMA`. |
| 7 | Pembayaran Transfer Bank | Berhasil, kasir memeriksa mutasi rekening dan meng-approve dana. |
| 8 | Pembayaran QRIS | Berhasil, kasir memverifikasi status sukses merchant QRIS dan meng-approve. |
| 9 | Penolakan Pembayaran | Berhasil, kasir wajib mengisi alasan, pesanan tetap `MENUNGGU_PEMBAYARAN`. |
| 10| Pembayaran Ulang | Berhasil, pelanggan dapat membayar kembali setelah penolakan sebelumnya. |
| 11| Cegah Duplikasi Approve | Berhasil, transaksi yang sudah lunas tidak dapat di-approve ganda. |
| 12| Urutan Pengerjaan Cucian | Berhasil, urutan status `DITERIMA` -> `DIPROSES` -> `SIAP_DIAMBIL` tercatat di audit riwayat. |
| 13| Pengambilan Cucian | Berhasil, kasir input nama penerima, status transaksi otomatis berubah menjadi `SELESAI`. |
| 14| Pembatalan Sebelum Dicuci | Berhasil, pelanggan mengajukan saat `DITERIMA`, Admin setuju, kasir cairkan refund 100%. |
| 15| Pembatalan Saat Diproses | Berhasil, sistem menolak pembatalan biasa jika pakaian sudah masuk proses cuci. |
| 16| Pakaian Belum Diambil | Berhasil, status tetap `SIAP_DIAMBIL`, terpantau di view `v_cucian_belum_diambil`. |
| 17| Pengajuan Pengaduan | Berhasil, pelanggan mengirim komplain Hilang/Rusak/Tertukar langsung ke Admin. |
| 18| Penugasan Penelusuran | Berhasil, Admin menugaskan kasir untuk memeriksa rak cucian dan kasir menginput temuan. |
| 19| Musyawarah Penyelesaian | Admin menawarkan solusi; persetujuan melanjutkan penyelesaian, sedangkan penolakan mengembalikan pengaduan ke antrean tindak lanjut admin. |
| 20| Isolasi Hak Akses Role | Berhasil, pelanggan hanya melihat transaksi miliknya sendiri dan tidak dapat membuka menu Admin/Kasir. |

---

**Selamat Mengerjakan dan Sukses Presentasi Tugas Besar PBOL Kelompok 4!**
