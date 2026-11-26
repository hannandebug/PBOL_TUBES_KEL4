package id.kelompok4.laundry.service;

import id.kelompok4.laundry.dao.PengambilanDAO;
import id.kelompok4.laundry.dao.TransaksiDAO;
import id.kelompok4.laundry.model.PengambilanCucian;
import id.kelompok4.laundry.model.TransaksiLaundry;

import java.sql.SQLException;

// Fitur: Pengerjaan dan Pengambilan Cucian
public class PengerjaanService {

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final PengambilanDAO pengambilanDAO = new PengambilanDAO();

    // Fitur: Petugas memulai mencuci pakaian (status DITERIMA -> DIPROSES)
    public boolean mulaiPengerjaan(Long idTransaksi, Long idPetugas) throws Exception {
        validasiIdentitas(idTransaksi, idPetugas);
        TransaksiLaundry trx = transaksiDAO.cariBerdasarkanId(idTransaksi);
        if (trx == null) throw new IllegalArgumentException("Transaksi tidak ditemukan.");

        if (!"DITERIMA".equalsIgnoreCase(trx.getStatusTransaksi())) {
            throw new IllegalArgumentException("Pengerjaan hanya dapat dimulai dari transaksi berstatus DITERIMA.");
        }

        return transaksiDAO.updateStatusPengerjaan(idTransaksi, "DITERIMA", "DIPROSES", idPetugas);
    }

    // Fitur: Petugas menyelesaikan pengerjaan (status DIPROSES -> SIAP_DIAMBIL)
    public boolean selesaiPengerjaan(Long idTransaksi, Long idPetugas) throws Exception {
        validasiIdentitas(idTransaksi, idPetugas);
        TransaksiLaundry trx = transaksiDAO.cariBerdasarkanId(idTransaksi);
        if (trx == null) throw new IllegalArgumentException("Transaksi tidak ditemukan.");

        if (!"DIPROSES".equalsIgnoreCase(trx.getStatusTransaksi())) {
            throw new IllegalArgumentException("Hanya cucian berstatus DIPROSES yang dapat diubah menjadi SIAP_DIAMBIL.");
        }

        return transaksiDAO.updateStatusPengerjaan(idTransaksi, "DIPROSES", "SIAP_DIAMBIL", idPetugas);
    }

    // Fitur: Pelanggan mengambil pakaian fisik di gerai (status SIAP_DIAMBIL -> SELESAI)
    public boolean catatPengambilan(Long idTransaksi, Long idPetugas, String namaPenerima, String catatan) throws Exception {
        validasiIdentitas(idTransaksi, idPetugas);
        TransaksiLaundry trx = transaksiDAO.cariBerdasarkanId(idTransaksi);
        if (trx == null) throw new IllegalArgumentException("Transaksi tidak ditemukan.");

        if (!"SIAP_DIAMBIL".equalsIgnoreCase(trx.getStatusTransaksi())) {
            throw new IllegalArgumentException("Pengambilan hanya dapat dicatat jika cucian sudah berstatus SIAP_DIAMBIL.");
        }

        if (namaPenerima == null || namaPenerima.trim().isEmpty()) {
            namaPenerima = trx.getNamaPelanggan();
        }

        PengambilanCucian pengambilan = new PengambilanCucian();
        pengambilan.setIdTransaksi(idTransaksi);
        pengambilan.setIdPetugasPenyerah(idPetugas);
        pengambilan.setNamaPenerima(namaPenerima.trim());
        pengambilan.setCatatan(catatan);

        return pengambilanDAO.simpanPengambilan(pengambilan);
    }

    // Fitur: Validasi Identitas
    private void validasiIdentitas(Long idTransaksi, Long idPetugas) {
        if (idTransaksi == null) throw new IllegalArgumentException("ID transaksi tidak valid.");
        if (idPetugas == null) throw new IllegalArgumentException("Identitas petugas tidak valid.");
    }
}
