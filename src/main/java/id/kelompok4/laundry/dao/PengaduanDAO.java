package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.model.Pengaduan;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Pengaduan
 * Mengelola pengaduan pelanggan terkait cucian (Hilang, Rusak, Tertukar).
 */
public class PengaduanDAO {

    public boolean tambahPengaduan(Pengaduan p) throws SQLException {
        String sql = "INSERT INTO pengaduan " +
                     "(nomor_pengaduan, id_transaksi, id_pelanggan, id_pengguna_pelapor, jenis_pengaduan, " +
                     " deskripsi_pakaian, jumlah_pakaian, uraian_kejadian, status_tanggung_jawab, status_pengaduan) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'BELUM_DITENTUKAN', 'DILAPORKAN')";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNomorPengaduan());
            ps.setLong(2, p.getIdTransaksi());
            ps.setLong(3, p.getIdPelanggan());
            ps.setLong(4, p.getIdPenggunaPelapor());
            ps.setString(5, p.getJenisPengaduan());
            ps.setString(6, p.getDeskripsiPakaian());
            ps.setInt(7, p.getJumlahPakaian());
            ps.setString(8, p.getUraianKejadian());

            int aff = ps.executeUpdate();
            if (aff > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setIdPengaduan(rs.getLong(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public List<Pengaduan> ambilSemuaPengaduan() throws SQLException {
        List<Pengaduan> list = new ArrayList<>();
        String sql = "SELECT p.*, t.nomor_transaksi, u.nama_lengkap AS nama_pelanggan, u.nomor_telepon " +
                     "FROM pengaduan p " +
                     "INNER JOIN transaksi_laundry t ON t.id_transaksi = p.id_transaksi " +
                     "INNER JOIN pelanggan pl ON pl.id_pelanggan = p.id_pelanggan " +
                     "INNER JOIN pengguna u ON u.id_pengguna = pl.id_pengguna " +
                     "ORDER BY p.tanggal_lapor DESC";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Pengaduan> ambilPengaduanPelanggan(Long idPelanggan) throws SQLException {
        List<Pengaduan> list = new ArrayList<>();
        String sql = "SELECT p.*, t.nomor_transaksi, u.nama_lengkap AS nama_pelanggan, u.nomor_telepon " +
                     "FROM pengaduan p " +
                     "INNER JOIN transaksi_laundry t ON t.id_transaksi = p.id_transaksi " +
                     "INNER JOIN pelanggan pl ON pl.id_pelanggan = p.id_pelanggan " +
                     "INNER JOIN pengguna u ON u.id_pengguna = pl.id_pengguna " +
                     "WHERE p.id_pelanggan = ? " +
                     "ORDER BY p.tanggal_lapor DESC";

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

    public List<Pengaduan> ambilTugasPenelusuranPetugas() throws SQLException {
        List<Pengaduan> list = new ArrayList<>();
        String sql = "SELECT p.*, t.nomor_transaksi, u.nama_lengkap AS nama_pelanggan, u.nomor_telepon " +
                     "FROM pengaduan p " +
                     "INNER JOIN transaksi_laundry t ON t.id_transaksi = p.id_transaksi " +
                     "INNER JOIN pelanggan pl ON pl.id_pelanggan = p.id_pelanggan " +
                     "INNER JOIN pengguna u ON u.id_pengguna = pl.id_pengguna " +
                     "WHERE p.status_pengaduan IN ('DIVERIFIKASI', 'DALAM_PENELUSURAN') " +
                     "ORDER BY p.tanggal_lapor DESC";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Pengaduan cariBerdasarkanId(Long idPengaduan) throws SQLException {
        String sql = "SELECT p.*, t.nomor_transaksi, u.nama_lengkap AS nama_pelanggan, u.nomor_telepon " +
                     "FROM pengaduan p " +
                     "INNER JOIN transaksi_laundry t ON t.id_transaksi = p.id_transaksi " +
                     "INNER JOIN pelanggan pl ON pl.id_pelanggan = p.id_pelanggan " +
                     "INNER JOIN pengguna u ON u.id_pengguna = pl.id_pengguna " +
                     "WHERE p.id_pengaduan = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPengaduan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean updateTugaskanPenelusuran(Long idPengaduan, Long idAdmin, Long idPetugas, Timestamp batasWaktu) throws SQLException {
        String sql = "UPDATE pengaduan SET status_pengaduan = 'DALAM_PENELUSURAN', id_admin_pemeriksa = ?, " +
                     "id_petugas_pembantu = ?, batas_waktu_penelusuran = ?, waktu_pemeriksaan = CURRENT_TIMESTAMP " +
                     "WHERE id_pengaduan = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAdmin);
            ps.setLong(2, idPetugas);
            ps.setTimestamp(3, batasWaktu);
            ps.setLong(4, idPengaduan);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateHasilPenelusuranPetugas(Long idPengaduan, String hasil, Long idPetugas) throws SQLException {
        String sql = "UPDATE pengaduan SET hasil_penelusuran = ?, id_pengguna_terakhir_memperbarui = ? WHERE id_pengaduan = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hasil);
            ps.setLong(2, idPetugas);
            ps.setLong(3, idPengaduan);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateKeputusanAdmin(Long idPengaduan, String statusTanggungJawab, String statusPengaduan, Long idAdmin) throws SQLException {
        String sql = "UPDATE pengaduan SET status_tanggung_jawab = ?, status_pengaduan = ?, id_admin_pemeriksa = ?, id_pengguna_terakhir_memperbarui = ? " +
                     "WHERE id_pengaduan = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusTanggungJawab);
            ps.setString(2, statusPengaduan);
            ps.setLong(3, idAdmin);
            ps.setLong(4, idAdmin);
            ps.setLong(5, idPengaduan);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatusPengaduan(Long idPengaduan, String statusPengaduan) throws SQLException {
        String sql = "UPDATE pengaduan SET status_pengaduan = ? WHERE id_pengaduan = ?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusPengaduan);
            ps.setLong(2, idPengaduan);
            return ps.executeUpdate() > 0;
        }
    }

    public String generateNomorPengaduanBaru() throws SQLException {
        String tgl = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PGD-" + tgl + "-";
        String sql = "SELECT nomor_pengaduan FROM pengaduan WHERE nomor_pengaduan LIKE ? ORDER BY id_pengaduan DESC LIMIT 1";

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

    private Pengaduan mapRow(ResultSet rs) throws SQLException {
        Pengaduan p = new Pengaduan();
        p.setIdPengaduan(rs.getLong("id_pengaduan"));
        p.setNomorPengaduan(rs.getString("nomor_pengaduan"));
        p.setIdTransaksi(rs.getLong("id_transaksi"));
        p.setIdPelanggan(rs.getLong("id_pelanggan"));
        p.setIdPenggunaPelapor(rs.getLong("id_pengguna_pelapor"));
        p.setJenisPengaduan(rs.getString("jenis_pengaduan"));
        p.setDeskripsiPakaian(rs.getString("deskripsi_pakaian"));
        p.setJumlahPakaian(rs.getInt("jumlah_pakaian"));
        p.setUraianKejadian(rs.getString("uraian_kejadian"));
        p.setTanggalLapor(rs.getTimestamp("tanggal_lapor"));
        p.setBatasWaktuPenelusuran(rs.getTimestamp("batas_waktu_penelusuran"));
        p.setHasilPenelusuran(rs.getString("hasil_penelusuran"));
        p.setStatusTanggungJawab(rs.getString("status_tanggung_jawab"));
        p.setStatusPengaduan(rs.getString("status_pengaduan"));
        p.setIdAdminPemeriksa(rs.getLong("id_admin_pemeriksa"));
        p.setIdPetugasPembantu(rs.getLong("id_petugas_pembantu"));
        p.setWaktuPemeriksaan(rs.getTimestamp("waktu_pemeriksaan"));
        p.setIdPenggunaTerakhirMemperbarui(rs.getLong("id_pengguna_terakhir_memperbarui"));

        try {
            p.setNomorTransaksi(rs.getString("nomor_transaksi"));
            p.setNamaPelanggan(rs.getString("nama_pelanggan"));
            p.setNomorTeleponPelanggan(rs.getString("nomor_telepon"));
        } catch (SQLException ignored) {}

        return p;
    }
}
