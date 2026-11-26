package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.model.PermohonanPembatalan;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO PermohonanPembatalan
 * Menangani pengajuan pembatalan transaksi oleh Pelanggan dan persetujuan oleh Admin.
 */
public class PembatalanDAO {

    public boolean ajukanPembatalan(PermohonanPembatalan p) throws SQLException {
        String sql = "INSERT INTO permohonan_pembatalan " +
                     "(nomor_permohonan, id_transaksi, id_pelanggan, id_pengguna_pemohon, alasan_pembatalan, status_permohonan) " +
                     "VALUES (?, ?, ?, ?, ?, 'DIAJUKAN')";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNomorPermohonan());
            ps.setLong(2, p.getIdTransaksi());
            ps.setLong(3, p.getIdPelanggan());
            ps.setLong(4, p.getIdPenggunaPemohon());
            ps.setString(5, p.getAlasanPembatalan());

            int aff = ps.executeUpdate();
            if (aff > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) p.setIdPermohonanPembatalan(rs.getLong(1));
                }
                return true;
            }
        }
        return false;
    }

    public List<PermohonanPembatalan> ambilSemuaPermohonan() throws SQLException {
        List<PermohonanPembatalan> list = new ArrayList<>();
        String sql = "SELECT pb.*, t.nomor_transaksi, u.nama_lengkap AS nama_pelanggan " +
                     "FROM permohonan_pembatalan pb " +
                     "INNER JOIN transaksi_laundry t ON t.id_transaksi = pb.id_transaksi " +
                     "INNER JOIN pelanggan pl ON pl.id_pelanggan = pb.id_pelanggan " +
                     "INNER JOIN pengguna u ON u.id_pengguna = pl.id_pengguna " +
                     "ORDER BY pb.diajukan_pada DESC";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<PermohonanPembatalan> ambilPermohonanPelanggan(Long idPelanggan) throws SQLException {
        List<PermohonanPembatalan> list = new ArrayList<>();
        String sql = "SELECT pb.*, t.nomor_transaksi, u.nama_lengkap AS nama_pelanggan " +
                     "FROM permohonan_pembatalan pb " +
                     "INNER JOIN transaksi_laundry t ON t.id_transaksi = pb.id_transaksi " +
                     "INNER JOIN pelanggan pl ON pl.id_pelanggan = pb.id_pelanggan " +
                     "INNER JOIN pengguna u ON u.id_pengguna = pl.id_pengguna " +
                     "WHERE pb.id_pelanggan = ? ORDER BY pb.diajukan_pada DESC";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPelanggan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public PermohonanPembatalan cariBerdasarkanTransaksiId(Long idTransaksi) throws SQLException {
        String sql = "SELECT pb.*, t.nomor_transaksi, u.nama_lengkap AS nama_pelanggan " +
                     "FROM permohonan_pembatalan pb " +
                     "INNER JOIN transaksi_laundry t ON t.id_transaksi = pb.id_transaksi " +
                     "INNER JOIN pelanggan pl ON pl.id_pelanggan = pb.id_pelanggan " +
                     "INNER JOIN pengguna u ON u.id_pengguna = pl.id_pengguna " +
                     "WHERE pb.id_transaksi = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTransaksi);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean putuskanAdmin(Long idPermohonan, String statusBaru, Long idAdmin, String catatan) throws SQLException {
        String sql = "UPDATE permohonan_pembatalan SET status_permohonan = ?, diputuskan_oleh_admin = ?, " +
                     "waktu_keputusan = CURRENT_TIMESTAMP, catatan_admin = ? WHERE id_permohonan_pembatalan = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusBaru);
            ps.setLong(2, idAdmin);
            ps.setString(3, catatan);
            ps.setLong(4, idPermohonan);
            return ps.executeUpdate() > 0;
        }
    }

    public String generateNomorPermohonanBaru() throws SQLException {
        String tgl = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "BTL-" + tgl + "-";
        String sql = "SELECT nomor_permohonan FROM permohonan_pembatalan WHERE nomor_permohonan LIKE ? ORDER BY id_permohonan_pembatalan DESC LIMIT 1";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                int urut = 1;
                if (rs.next()) {
                    String last = rs.getString(1);
                    try {
                        String sub = last.substring(prefix.length());
                        urut = Integer.parseInt(sub) + 1;
                    } catch (Exception ignored) {}
                }
                return String.format("%s%04d", prefix, urut);
            }
        }
    }

    private PermohonanPembatalan mapRow(ResultSet rs) throws SQLException {
        PermohonanPembatalan p = new PermohonanPembatalan();
        p.setIdPermohonanPembatalan(rs.getLong("id_permohonan_pembatalan"));
        p.setNomorPermohonan(rs.getString("nomor_permohonan"));
        p.setIdTransaksi(rs.getLong("id_transaksi"));
        p.setIdPelanggan(rs.getLong("id_pelanggan"));
        p.setIdPenggunaPemohon(rs.getLong("id_pengguna_pemohon"));
        p.setAlasanPembatalan(rs.getString("alasan_pembatalan"));
        p.setStatusPermohonan(rs.getString("status_permohonan"));
        p.setDiajukanPada(rs.getTimestamp("diajukan_pada"));
        p.setDiputuskanOlehAdmin(rs.getLong("diputuskan_oleh_admin"));
        p.setWaktuKeputusan(rs.getTimestamp("waktu_keputusan"));
        p.setCatatanAdmin(rs.getString("catatan_admin"));

        try {
            p.setNomorTransaksi(rs.getString("nomor_transaksi"));
            p.setNamaPelanggan(rs.getString("nama_pelanggan"));
        } catch (SQLException ignored) {}

        return p;
    }
}
