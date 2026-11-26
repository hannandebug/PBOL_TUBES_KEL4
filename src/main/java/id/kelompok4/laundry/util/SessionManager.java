package id.kelompok4.laundry.util;

import id.kelompok4.laundry.model.Pengguna;

/**
 * Utilitas SessionManager
 * Menyimpan data pengguna yang sedang login saat ini di memori aplikasi.
 * Menerapkan prinsip Polymorphism: atribut bertipe induk Pengguna,
 * dapat menampung objek Admin, Petugas, atau Pelanggan.
 */
public class SessionManager {

    private static Pengguna penggunaAktif = null;

    public static synchronized void setPenggunaAktif(Pengguna pengguna) {
        penggunaAktif = pengguna;
    }

    public static synchronized Pengguna getPenggunaAktif() {
        return penggunaAktif;
    }

    public static synchronized boolean adaSesiAktif() {
        return penggunaAktif != null;
    }

    public static synchronized void logout() {
        penggunaAktif = null;
    }

    public static synchronized String getPeranAktif() {
        return penggunaAktif != null ? penggunaAktif.getPeran() : null;
    }

    public static synchronized Long getIdPenggunaAktif() {
        return penggunaAktif != null ? penggunaAktif.getIdPengguna() : null;
    }
}
