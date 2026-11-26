package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.model.PengembalianDana;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO PengembalianDana
 * Menangani pembuatan refund oleh Admin dan pencairan uang oleh Petugas.
 * Trigger database trg_pengembalian_setelah_ubah otomatis membatalkan transaksi ketika status refund menjadi DIBAYAR.
 */
public class PengembalianDanaDAO {

    public boolean buatPengembalianDana(PengembalianDana p) throws SQLException {
        String sql = "INSERT INTO pengembalian_dana " +
                     "(nomor_pengembalian, id_transaksi, id_permohonan_pembatalan, id_penyelesaian, " +
                     " jumlah_pengembalian, status_pengembalian, dibuat_oleh_admin, alasan, catatan) " +
                     "VALUES (?, ?, ?, ?, ?, 'MENUNGGU', ?, ?, ?)";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNomorPengembalian());
            ps.setLong(2, p.getIdTransaksi());
            if (p.getIdPermohonanPembatalan() != null) ps.setLong(3, p.getIdPermohonanPembatalan());
            else ps.setNull(3, Types.BIGINT);
            if (p.getIdPenyelesaian() != null) ps.setLong(4, p.getIdPenyelesaian());
            else ps.setNull(4, Types.BIGINT);
            ps.setBigDecimal(5, p.getJumlahPengembalian());
            ps.setLong(6, p.getDibuatOlehAdmin());
            ps.setString(7, p.getAlasan());
            ps.setString(8, p.getCatatan());

            int aff = ps.executeUpdate();
            if (aff > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) p.setIdPengembalian(rs.getLong(1));
                }
                return true;
            }
        }
        return false;
    }

    public List<PengembalianDana> ambilSemuaRefund() throws SQLException {
        List<PengembalianDana> list = new ArrayList<>();
        String sql = "SELECT r.*, t.nomor_transaksi, u.nama_lengkap AS nama_pelanggan " +
                     "FROM pengembalian_dana r " +
                     "INNER JOIN transaksi_laundry t ON t.id_transaksi = r.id_transaksi " +
                     "INNER JOIN pelanggan pl ON pl.id_pelanggan = t.id_pelanggan " +
                     "INNER JOIN pengguna u ON u.id_pengguna = pl.id_pengguna " +
                     "ORDER BY r.dibuat_pada DESC";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<PengembalianDana> ambilRefundMenunggu() throws SQLException {
        List<PengembalianDana> list = new ArrayList<>();
        String sql = "SELECT r.*, t.nomor_transaksi, u.nama_lengkap AS nama_pelanggan " +
                     "FROM pengembalian_dana r " +
                     "INNER JOIN transaksi_laundry t ON t.id_transaksi = r.id_transaksi " +
                     "INNER JOIN pelanggan pl ON pl.id_pelanggan = t.id_pelanggan " +
                     "INNER JOIN pengguna u ON u.id_pengguna = pl.id_pengguna " +
                     "WHERE r.status_pengembalian = 'MENUNGGU' " +
                     "ORDER BY r.dibuat_pada ASC";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean eksekusiBayarRefundKasir(Long idPengembalian, String metode, Long idPetugas) throws SQLException {
        String sql = "UPDATE pengembalian_dana SET status_pengembalian = 'DIBAYAR', metode_pengembalian = ?, " +
                     "waktu_pengembalian = CURRENT_TIMESTAMP, dibayarkan_oleh = ? " +
                     "WHERE id_pengembalian = ? AND status_pengembalian = 'MENUNGGU'";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, metode);
            ps.setLong(2, idPetugas);
            ps.setLong(3, idPengembalian);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean batalkanRefundMenunggu(String nomorPengembalian, String catatanPembatalan) throws SQLException {
        String sql = "UPDATE pengembalian_dana SET status_pengembalian = 'DITOLAK', " +
                     "catatan = CONCAT_WS(CHAR(10), NULLIF(catatan, ''), ?) " +
                     "WHERE nomor_pengembalian = ? AND status_pengembalian = 'MENUNGGU'";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, catatanPembatalan);
            ps.setString(2, nomorPengembalian);
            return ps.executeUpdate() == 1;
        }
    }

    public String generateNomorPengembalianBaru() throws SQLException {
        String tgl = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "RFD-" + tgl + "-";
        String sql = "SELECT nomor_pengembalian FROM pengembalian_dana WHERE nomor_pengembalian LIKE ? ORDER BY id_pengembalian DESC LIMIT 1";

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

    private PengembalianDana mapRow(ResultSet rs) throws SQLException {
        PengembalianDana p = new PengembalianDana();
        p.setIdPengembalian(rs.getLong("id_pengembalian"));
        p.setNomorPengembalian(rs.getString("nomor_pengembalian"));
        p.setIdTransaksi(rs.getLong("id_transaksi"));
        p.setIdPermohonanPembatalan(getNullableLong(rs, "id_permohonan_pembatalan"));
        p.setIdPenyelesaian(getNullableLong(rs, "id_penyelesaian"));
        p.setJumlahPengembalian(rs.getBigDecimal("jumlah_pengembalian"));
        p.setStatusPengembalian(rs.getString("status_pengembalian"));
        p.setMetodePengembalian(rs.getString("metode_pengembalian"));
        p.setDibuatOlehAdmin(rs.getLong("dibuat_oleh_admin"));
        p.setDibayarkanOleh(getNullableLong(rs, "dibayarkan_oleh"));
        p.setWaktuPengembalian(rs.getTimestamp("waktu_pengembalian"));
        p.setAlasan(rs.getString("alasan"));
        p.setCatatan(rs.getString("catatan"));

        try {
            p.setNomorTransaksi(rs.getString("nomor_transaksi"));
            p.setNamaPelanggan(rs.getString("nama_pelanggan"));
        } catch (SQLException ignored) {}

        return p;
    }

    private Long getNullableLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }
}
