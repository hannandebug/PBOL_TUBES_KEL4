package id.kelompok4.laundry.util;

import id.kelompok4.laundry.model.Pengguna;

// Fitur: Kelola Sesi Pengguna
// OOP: Polymorphism - sesi menyimpan objek melalui tipe induk Pengguna.
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

    // Fitur: Logout
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
