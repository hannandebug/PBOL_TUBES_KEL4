<p align="center">
  <img src="src/main/resources/assets/logo.png" alt="Logo RapiWash" width="160">
</p>

<h1 align="center">RapiWash</h1>

<p align="center">
  Sistem Informasi Manajemen Laundry berbasis Java Swing dan MySQL
  <br>
  Proyek Tugas Besar Pemrograman Berorientasi Objek Lanjutan (PBOL) · Kelompok 4
</p>

<p align="center">
  Java 17+ &nbsp;·&nbsp; Swing &nbsp;·&nbsp; MySQL 8 &nbsp;·&nbsp; Maven
</p>

---

## Tentang Proyek

RapiWash membantu operasional laundry dalam satu aplikasi desktop: mulai dari
pencatatan pesanan dan pembayaran, pemantauan pengerjaan, hingga penanganan
pengaduan dan laporan. Antarmuka menyediakan alur kerja yang berbeda untuk
Admin, Petugas/Kasir, dan Pelanggan.

## Fitur

| Peran | Fitur |
|---|---|
| **Admin** | Ringkasan operasional, pengelolaan layanan/paket/tarif dan akun petugas, penanganan pengaduan, persetujuan pembatalan/refund, serta laporan. |
| **Petugas/Kasir** | Pencatatan transaksi, verifikasi pembayaran, cetak struk, pembaruan status pengerjaan, pencatatan pengambilan, penelusuran pengaduan, dan pencairan refund. |
| **Pelanggan** | Registrasi, katalog tarif, riwayat pesanan, pengajuan pengaduan dan pembatalan, serta tanggapan atas solusi yang diajukan Admin. |

Pembayaran Tunai, Transfer, dan QRIS dicatat dan diverifikasi oleh Petugas.
Aplikasi ini tidak terhubung ke payment gateway.

## Teknologi

- **Java 17+** dan **Java Swing**
- **FlatLaf** untuk tampilan aplikasi
- **MySQL 8.0.16+** melalui JDBC
- **Maven** untuk mengelola dependency dan proses build
- **BCrypt** untuk hashing kata sandi

## Arsitektur Singkat

```text
id.kelompok4.laundry
├── Main.java       Entry point dan inisialisasi aplikasi
├── konektor/       Koneksi JDBC dan konfigurasi database
├── model/          Entitas serta aturan objek domain
├── dao/            Akses dan operasi data MySQL
├── service/        Aturan bisnis dan alur proses
├── util/           Utilitas format, sesi, password, dan struk
└── view/           Form, dashboard, dan komponen Swing
```

Struktur ini memisahkan antarmuka, aturan bisnis, objek domain, dan akses data
agar tanggung jawab tiap bagian lebih mudah dipahami dan dikembangkan.

## Persiapan

Pastikan perangkat sudah memiliki:

1. JDK 17 atau lebih baru.
2. MySQL 8.0.16 atau lebih baru.
3. Maven atau IDE yang mendukung proyek Maven.

### 1. Siapkan database

Jalankan MySQL, lalu impor [`database/laundry_kelompok4.sql`](database/laundry_kelompok4.sql)
menggunakan MySQL Workbench, phpMyAdmin, atau klien MySQL lainnya. Skrip ini
membuat database, tabel, constraint, dan trigger.

`database/migration_pengaduan_tindak_lanjut.sql` hanya diperlukan untuk
menyelaraskan constraint pada database lama. Jangan jalankan migrasi tersebut
pada database baru yang sudah dibuat dari skrip utama.

### 2. Atur koneksi lokal

Salin file contoh konfigurasi ke file lokal yang diabaikan Git:

```powershell
Copy-Item database\database.properties.example database\database.properties
```

Sesuaikan nilainya dengan konfigurasi MySQL setempat:

```properties
db.host=localhost
db.port=3306
db.name=laundry_kelompok4
db.username=root
db.password=
db.useSSL=false
db.serverTimezone=Asia/Jakarta
db.allowPublicKeyRetrieval=true
```

Nilai di atas ditujukan untuk pengembangan lokal. Jangan gunakan akun database
tanpa kata sandi di lingkungan produksi.

### 3. Build dan jalankan

Dari direktori proyek:

```powershell
mvn clean package
mvn exec:java
```

Atau buka proyek melalui `pom.xml` di IntelliJ IDEA, tunggu Maven menyelesaikan
sinkronisasi dependency, lalu jalankan `id.kelompok4.laundry.Main`.

Pada instalasi baru, aplikasi akan meminta pembuatan akun Admin awal jika belum
ada Admin. Setelah login, Admin perlu menambahkan layanan, paket, dan tarif
sebelum Petugas dapat membuat transaksi.

## Identitas Visual

| Warna | Kode | Penggunaan |
|---|---|---|
| Amalfi Tile | `#2E5AA7` | Warna primer dan navigasi |
| Citrus Zest | `#FFA62B` | Aksi utama |
| Sea Breeze | `#86C5FF` | Sorotan dan informasi |
| Cream Gelato | `#F8E6A0` | Aksen lembut |
| Latar belakang | `#F5F8FC` | Latar antarmuka |
| Teks gelap | `#17324D` | Teks utama |

## Skenario Uji Manual

Gunakan database lokal untuk memeriksa alur utama berikut:

- Setup Admin, login, dan registrasi Pelanggan.
- Pembuatan transaksi serta penerapan minimum tagihan 2 kg.
- Verifikasi atau penolakan pembayaran dan pencetakan struk.
- Perubahan status pengerjaan hingga cucian diambil.
- Pengajuan pengaduan, tanggapan solusi, pembatalan, dan refund.
- Pembatasan data dan menu sesuai peran pengguna.

Repository ini belum menyertakan automated test suite. Lakukan pengujian
fungsional secara manual setelah database dan konfigurasi lokal siap.

---

<p align="center">
  Dikembangkan oleh Kelompok 4 untuk mata kuliah PBOL.
</p>
