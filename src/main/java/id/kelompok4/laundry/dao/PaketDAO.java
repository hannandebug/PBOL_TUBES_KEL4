package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.model.PaketLaundry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Paket
 * Mengelola tabel paket_laundry (CRUD).
 */
public class PaketDAO {

    public List<PaketLaundry> ambilSemua() throws SQLException {
        List<PaketLaundry> list = new ArrayList<>();
        String sql = "SELECT * FROM paket_laundry ORDER BY durasi_jam ASC";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<PaketLaundry> ambilSemuaAktif() throws SQLException {
        List<PaketLaundry> list = new ArrayList<>();
        String sql = "SELECT * FROM paket_laundry WHERE status_aktif = 1 ORDER BY durasi_jam ASC";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean tambah(PaketLaundry paket) throws SQLException {
        String sql = "INSERT INTO paket_laundry (kode_paket, nama_paket, durasi_jam, keterangan, status_aktif) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, paket.getKodePaket());
            ps.setString(2, paket.getNamaPaket());
            ps.setInt(3, paket.getDurasiJam());
            ps.setString(4, paket.getKeterangan());
            ps.setInt(5, paket.isStatusAktif() ? 1 : 0);
            int aff = ps.executeUpdate();
            if (aff > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) paket.setIdPaket(rs.getLong(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean ubah(PaketLaundry paket) throws SQLException {
        String sql = "UPDATE paket_laundry SET nama_paket = ?, durasi_jam = ?, keterangan = ?, status_aktif = ? WHERE id_paket = ?";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paket.getNamaPaket());
            ps.setInt(2, paket.getDurasiJam());
            ps.setString(3, paket.getKeterangan());
            ps.setInt(4, paket.isStatusAktif() ? 1 : 0);
            ps.setLong(5, paket.getIdPaket());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean setAktif(Long id, boolean aktif) throws SQLException {
        String sql = "UPDATE paket_laundry SET status_aktif = ? WHERE id_paket = ?";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, aktif ? 1 : 0);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    private PaketLaundry mapRow(ResultSet rs) throws SQLException {
        PaketLaundry p = new PaketLaundry();
        p.setIdPaket(rs.getLong("id_paket"));
        p.setKodePaket(rs.getString("kode_paket"));
        p.setNamaPaket(rs.getString("nama_paket"));
        p.setDurasiJam(rs.getInt("durasi_jam"));
        p.setKeterangan(rs.getString("keterangan"));
        p.setStatusAktif(rs.getInt("status_aktif") == 1);
        p.setDibuatPada(rs.getTimestamp("dibuat_pada"));
        return p;
    }
}
