-- Jalankan satu kali pada database lama RapiWash untuk menyelaraskan
-- constraint tanggung jawab dan status pengaduan dengan aplikasi.
ALTER TABLE pengaduan
    DROP CHECK ck_pengaduan_tanggung_jawab,
    ADD CONSTRAINT ck_pengaduan_tanggung_jawab CHECK (status_tanggung_jawab IN (
        'BELUM_DITENTUKAN', 'TANGGUNG_JAWAB_LAUNDRY', 'TANGGUNG_JAWAB_PENUH',
        'TANGGUNG_JAWAB_SEBAGIAN', 'BUKAN_TANGGUNG_JAWAB_LAUNDRY'
    )),
    DROP CHECK ck_pengaduan_status,
    ADD CONSTRAINT ck_pengaduan_status CHECK (status_pengaduan IN (
        'DILAPORKAN', 'DIVERIFIKASI', 'DALAM_PENELUSURAN',
        'MENUNGGU_PERSETUJUAN', 'SOLUSI_DITOLAK', 'DALAM_PENYELESAIAN', 'SELESAI', 'DITOLAK'
    ));
