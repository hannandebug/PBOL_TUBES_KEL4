package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.model.JenisLayanan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Layanan
 * Mengelola tabel jenis_layanan (CRUD).
 */
public class LayananDAO {

    public List<JenisLayanan> ambilSemua() throws SQLException {
        List<JenisLayanan> list = new ArrayList<>();
        String sql = "SELECT * FROM jenis_layanan ORDER BY id_jenis_layanan ASC";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<JenisLayanan> ambilSemuaAktif() throws SQLException {
        List<JenisLayanan> list = new ArrayList<>();
        String sql = "SELECT * FROM jenis_layanan WHERE status_aktif = 1 ORDER BY id_jenis_layanan ASC";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean tambah(JenisLayanan layanan) throws SQLException {
        String sql = "INSERT INTO jenis_layanan (kode_layanan, nama_layanan, keterangan, status_aktif) VALUES (?, ?, ?, ?)";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, layanan.getKodeLayanan());
            ps.setString(2, layanan.getNamaLayanan());
            ps.setString(3, layanan.getKeterangan());
            ps.setInt(4, layanan.isStatusAktif() ? 1 : 0);
            int aff = ps.executeUpdate();
            if (aff > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) layanan.setIdJenisLayanan(rs.getLong(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean ubah(JenisLayanan layanan) throws SQLException {
        String sql = "UPDATE jenis_layanan SET nama_layanan = ?, keterangan = ?, status_aktif = ? WHERE id_jenis_layanan = ?";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, layanan.getNamaLayanan());
            ps.setString(2, layanan.getKeterangan());
            ps.setInt(3, layanan.isStatusAktif() ? 1 : 0);
            ps.setLong(4, layanan.getIdJenisLayanan());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean setAktif(Long id, boolean aktif) throws SQLException {
        String sql = "UPDATE jenis_layanan SET status_aktif = ? WHERE id_jenis_layanan = ?";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, aktif ? 1 : 0);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    private JenisLayanan mapRow(ResultSet rs) throws SQLException {
        JenisLayanan jl = new JenisLayanan();
        jl.setIdJenisLayanan(rs.getLong("id_jenis_layanan"));
        jl.setKodeLayanan(rs.getString("kode_layanan"));
        jl.setNamaLayanan(rs.getString("nama_layanan"));
        jl.setKeterangan(rs.getString("keterangan"));
        jl.setStatusAktif(rs.getInt("status_aktif") == 1);
        jl.setDibuatPada(rs.getTimestamp("dibuat_pada"));
        return jl;
    }
}
