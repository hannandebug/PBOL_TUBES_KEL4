package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.model.PengambilanCucian;

import java.sql.*;

/**
 * DAO PengambilanCucian
 * Menangani penyerahan cucian yang berstatus SIAP_DIAMBIL.
 * Trigger database trg_pengambilan_setelah_tambah otomatis mengubah status transaksi menjadi SELESAI.
 */
public class PengambilanDAO {

    public boolean simpanPengambilan(PengambilanCucian p) throws SQLException {
        String sql = "INSERT INTO pengambilan_cucian (id_transaksi, id_petugas_penyerah, nama_penerima, catatan) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, p.getIdTransaksi());
            ps.setLong(2, p.getIdPetugasPenyerah());
            ps.setString(3, p.getNamaPenerima());
            ps.setString(4, p.getCatatan());

            int aff = ps.executeUpdate();
            if (aff > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setIdPengambilan(rs.getLong(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public PengambilanCucian cariBerdasarkanTransaksiId(Long idTransaksi) throws SQLException {
        String sql = "SELECT a.*, t.nomor_transaksi, u.nama_lengkap AS nama_petugas " +
                     "FROM pengambilan_cucian a " +
                     "INNER JOIN transaksi_laundry t ON t.id_transaksi = a.id_transaksi " +
                     "INNER JOIN pengguna u ON u.id_pengguna = a.id_petugas_penyerah " +
                     "WHERE a.id_transaksi = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTransaksi);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PengambilanCucian p = new PengambilanCucian();
                    p.setIdPengambilan(rs.getLong("id_pengambilan"));
                    p.setIdTransaksi(rs.getLong("id_transaksi"));
                    p.setIdPetugasPenyerah(rs.getLong("id_petugas_penyerah"));
                    p.setWaktuPengambilan(rs.getTimestamp("waktu_pengambilan"));
                    p.setNamaPenerima(rs.getString("nama_penerima"));
                    p.setCatatan(rs.getString("catatan"));
                    p.setNomorTransaksi(rs.getString("nomor_transaksi"));
                    p.setNamaPetugas(rs.getString("nama_petugas"));
                    return p;
                }
            }
        }
        return null;
    }
}
