package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.model.TarifLayanan;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Fitur: Kelola Tarif
public class TarifDAO {

    // Fitur: Ambil Semua Tarif
    public List<TarifLayanan> ambilSemuaTarif() throws SQLException {
        List<TarifLayanan> list = new ArrayList<>();
        String sql = "SELECT t.*, jl.nama_layanan, pl.nama_paket, pl.durasi_jam " +
                     "FROM tarif_layanan t " +
                     "INNER JOIN jenis_layanan jl ON jl.id_jenis_layanan = t.id_jenis_layanan " +
                     "INNER JOIN paket_laundry pl ON pl.id_paket = t.id_paket " +
                     "ORDER BY jl.nama_layanan ASC, pl.durasi_jam DESC";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Fitur: Ambil Tarif Aktif
    public List<TarifLayanan> ambilTarifAktif() throws SQLException {
        List<TarifLayanan> list = new ArrayList<>();
        String sql = "SELECT t.*, jl.nama_layanan, pl.nama_paket, pl.durasi_jam " +
                     "FROM tarif_layanan t " +
                     "INNER JOIN jenis_layanan jl ON jl.id_jenis_layanan = t.id_jenis_layanan " +
                     "INNER JOIN paket_laundry pl ON pl.id_paket = t.id_paket " +
                     "WHERE t.status_aktif = 1 AND jl.status_aktif = 1 AND pl.status_aktif = 1 " +
                     "ORDER BY jl.nama_layanan ASC, pl.durasi_jam DESC";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Fitur: Cari Tarif
    public TarifLayanan cariTarif(Long idJenisLayanan, Long idPaket) throws SQLException {
        String sql = "SELECT t.*, jl.nama_layanan, pl.nama_paket, pl.durasi_jam " +
                     "FROM tarif_layanan t " +
                     "INNER JOIN jenis_layanan jl ON jl.id_jenis_layanan = t.id_jenis_layanan " +
                     "INNER JOIN paket_laundry pl ON pl.id_paket = t.id_paket " +
                     "WHERE t.id_jenis_layanan = ? AND t.id_paket = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idJenisLayanan);
            ps.setLong(2, idPaket);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // Fitur: Simpan Atau Update Tarif
    public boolean simpanAtauUpdateTarif(Long idJenisLayanan, Long idPaket, BigDecimal hargaPerKg, boolean aktif) throws SQLException {
        String sql = "INSERT INTO tarif_layanan (id_jenis_layanan, id_paket, harga_per_kg, status_aktif) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE harga_per_kg = VALUES(harga_per_kg), status_aktif = VALUES(status_aktif)";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idJenisLayanan);
            ps.setLong(2, idPaket);
            ps.setBigDecimal(3, hargaPerKg);
            ps.setInt(4, aktif ? 1 : 0);
            return ps.executeUpdate() > 0;
        }
    }

    private TarifLayanan mapRow(ResultSet rs) throws SQLException {
        TarifLayanan t = new TarifLayanan();
        t.setIdTarif(rs.getLong("id_tarif"));
        t.setIdJenisLayanan(rs.getLong("id_jenis_layanan"));
        t.setIdPaket(rs.getLong("id_paket"));
        t.setHargaPerKg(rs.getBigDecimal("harga_per_kg"));
        t.setStatusAktif(rs.getInt("status_aktif") == 1);
        t.setDibuatPada(rs.getTimestamp("dibuat_pada"));
        t.setDiperbaruiPada(rs.getTimestamp("diperbarui_pada"));

        try {
            t.setNamaLayanan(rs.getString("nama_layanan"));
            t.setNamaPaket(rs.getString("nama_paket"));
            t.setDurasiJam(rs.getInt("durasi_jam"));
        } catch (SQLException ignored) {}

        return t;
    }
}
