package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.model.PembayaranTransaksi;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Fitur: Pembayaran dan Verifikasi
public class PembayaranDAO {

    // Fitur: Simpan Pembayaran
    public boolean simpanPembayaran(PembayaranTransaksi p) throws SQLException {
        try (Connection conn = KoneksiDatabase.getKoneksi()) {
            if (menggunakanAlurVerifikasi(conn)) {
                return simpanPembayaranDenganVerifikasi(conn, p);
            }

            return simpanPembayaranLangsung(conn, p);
        }
    }

    // Fitur: Tolak Pembayaran
    public boolean tolakPembayaran(PembayaranTransaksi p) throws SQLException {
        try (Connection conn = KoneksiDatabase.getKoneksi()) {
            if (!menggunakanAlurVerifikasi(conn)) {
                throw new SQLException("Skema database belum mendukung pencatatan penolakan pembayaran.");
            }

            conn.setAutoCommit(false);
            try {
                Long idPembayaran = cariPercobaanMenunggu(conn, p.getIdTransaksi());
                if (idPembayaran == null) {
                    String sqlTambah = "INSERT INTO pembayaran_transaksi " +
                                       "(id_transaksi, jumlah_bayar, metode_pembayaran, id_petugas_pencatat, catatan_verifikasi) " +
                                       "VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlTambah, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setLong(1, p.getIdTransaksi());
                        ps.setBigDecimal(2, p.getJumlahBayar());
                        ps.setString(3, p.getMetodePembayaran());
                        ps.setLong(4, p.getDiterimaOleh());
                        ps.setString(5, p.getCatatan());
                        if (ps.executeUpdate() != 1) {
                            throw new SQLException("Gagal mencatat percobaan pembayaran yang ditolak.");
                        }
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (!rs.next()) {
                                throw new SQLException("ID percobaan pembayaran tidak berhasil dibuat.");
                            }
                            idPembayaran = rs.getLong(1);
                        }
                    }
                }

                String sqlTolak = "UPDATE pembayaran_transaksi SET status_verifikasi = 'DITOLAK', " +
                                  "id_petugas_verifikasi = ?, waktu_verifikasi = CURRENT_TIMESTAMP, " +
                                  "catatan_verifikasi = ? " +
                                  "WHERE id_pembayaran = ? AND status_verifikasi = 'MENUNGGU_VERIFIKASI'";
                try (PreparedStatement ps = conn.prepareStatement(sqlTolak)) {
                    ps.setLong(1, p.getDiterimaOleh());
                    ps.setString(2, p.getCatatan());
                    ps.setLong(3, idPembayaran);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Percobaan pembayaran sudah diproses oleh petugas lain.");
                    }
                }

                conn.commit();
                p.setIdPembayaran(idPembayaran);
                return true;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
    }

    // Fitur: Simpan Pembayaran Langsung
    private boolean simpanPembayaranLangsung(Connection conn, PembayaranTransaksi p) throws SQLException {
        String sql = "INSERT INTO pembayaran_transaksi " +
                     "(id_transaksi, nomor_struk, jumlah_bayar, metode_pembayaran, diterima_oleh, catatan) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, p.getIdTransaksi());
            ps.setString(2, p.getNomorStruk());
            ps.setBigDecimal(3, p.getJumlahBayar());
            ps.setString(4, p.getMetodePembayaran());
            ps.setLong(5, p.getDiterimaOleh());
            ps.setString(6, p.getCatatan());

            int aff = ps.executeUpdate();
            if (aff > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setIdPembayaran(rs.getLong(1));
                    }
                }
                p.setTanggalPembayaran(new Timestamp(System.currentTimeMillis()));
                return true;
            }
        }
        return false;
    }

    // Fitur: Simpan Pembayaran Dengan Verifikasi
    private boolean simpanPembayaranDenganVerifikasi(Connection conn, PembayaranTransaksi p) throws SQLException {
        conn.setAutoCommit(false);
        try {
            String sqlTambah = "INSERT INTO pembayaran_transaksi " +
                               "(id_transaksi, jumlah_bayar, metode_pembayaran, id_petugas_pencatat, catatan_verifikasi) " +
                               "VALUES (?, ?, ?, ?, ?)";
            long idPembayaran;

            try (PreparedStatement ps = conn.prepareStatement(sqlTambah, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, p.getIdTransaksi());
                ps.setBigDecimal(2, p.getJumlahBayar());
                ps.setString(3, p.getMetodePembayaran());
                ps.setLong(4, p.getDiterimaOleh());
                ps.setString(5, p.getCatatan());

                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Gagal mencatat percobaan pembayaran.");
                }
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("ID pembayaran tidak berhasil dibuat.");
                    }
                    idPembayaran = rs.getLong(1);
                }
            }

            String sqlSetujui = "UPDATE pembayaran_transaksi " +
                                "SET status_verifikasi = 'DISETUJUI', nomor_struk = ?, " +
                                "id_petugas_verifikasi = ?, waktu_verifikasi = CURRENT_TIMESTAMP " +
                                "WHERE id_pembayaran = ? AND status_verifikasi = 'MENUNGGU_VERIFIKASI'";
            try (PreparedStatement ps = conn.prepareStatement(sqlSetujui)) {
                ps.setString(1, p.getNomorStruk());
                ps.setLong(2, p.getDiterimaOleh());
                ps.setLong(3, idPembayaran);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("Pembayaran tidak dapat disetujui.");
                }
            }

            Timestamp waktuPembayaran;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT waktu_verifikasi FROM pembayaran_transaksi WHERE id_pembayaran = ?")) {
                ps.setLong(1, idPembayaran);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Data pembayaran yang disetujui tidak ditemukan.");
                    }
                    waktuPembayaran = rs.getTimestamp("waktu_verifikasi");
                }
            }

            conn.commit();
            p.setIdPembayaran(idPembayaran);
            p.setTanggalPembayaran(waktuPembayaran);
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    // Fitur: Cari Berdasarkan Transaksi ID
    public PembayaranTransaksi cariBerdasarkanTransaksiId(Long idTransaksi) throws SQLException {
        try (Connection conn = KoneksiDatabase.getKoneksi()) {
            boolean alurVerifikasi = menggunakanAlurVerifikasi(conn);
            String sql = queryPencarianPembayaran(conn) + " WHERE p.id_transaksi = ? " +
                         (alurVerifikasi ? "AND p.status_verifikasi = 'DISETUJUI' " : "") +
                         "ORDER BY p.id_pembayaran DESC LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idTransaksi);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapRow(rs);
                    }
                }
            }
        }
        return null;
    }

    public boolean adaPercobaanMenungguVerifikasi(Long idTransaksi) throws SQLException {
        try (Connection conn = KoneksiDatabase.getKoneksi()) {
            if (!menggunakanAlurVerifikasi(conn)) return false;
            String sql = "SELECT 1 FROM pembayaran_transaksi " +
                         "WHERE id_transaksi = ? AND status_verifikasi = 'MENUNGGU_VERIFIKASI' LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idTransaksi);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    // Fitur: Cari Percobaan Menunggu
    private Long cariPercobaanMenunggu(Connection conn, Long idTransaksi) throws SQLException {
        String sql = "SELECT id_pembayaran FROM pembayaran_transaksi " +
                     "WHERE id_transaksi = ? AND status_verifikasi = 'MENUNGGU_VERIFIKASI' " +
                     "ORDER BY id_pembayaran DESC LIMIT 1 FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idTransaksi);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("id_pembayaran") : null;
            }
        }
    }

    // Fitur: Cari Berdasarkan Nomor Struk
    public PembayaranTransaksi cariBerdasarkanNomorStruk(String nomorStruk) throws SQLException {
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(queryPencarianPembayaran(conn) + " WHERE p.nomor_struk = ?")) {
            ps.setString(1, nomorStruk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    static boolean menggunakanAlurVerifikasi(Connection conn) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns(
                conn.getCatalog(), null, "pembayaran_transaksi", null)) {
            while (rs.next()) {
                if ("status_verifikasi".equalsIgnoreCase(rs.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private String queryPencarianPembayaran(Connection conn) throws SQLException {
        boolean alurVerifikasi = menggunakanAlurVerifikasi(conn);
        String tanggal = alurVerifikasi ? "p.waktu_verifikasi" : "p.tanggal_pembayaran";
        String petugas = alurVerifikasi ? "p.id_petugas_verifikasi" : "p.diterima_oleh";
        String catatan = alurVerifikasi ? "p.catatan_verifikasi" : "p.catatan";
        String jenisJoin = alurVerifikasi ? "LEFT JOIN" : "INNER JOIN";

        return "SELECT p.id_pembayaran, p.id_transaksi, p.nomor_struk, p.jumlah_bayar, " +
               "p.metode_pembayaran, " + tanggal + " AS tanggal_pembayaran, " +
               petugas + " AS diterima_oleh, " + catatan + " AS catatan, " +
               "p.dibuat_pada, t.nomor_transaksi, u.nama_lengkap AS nama_petugas " +
               "FROM pembayaran_transaksi p " +
               "INNER JOIN transaksi_laundry t ON t.id_transaksi = p.id_transaksi " +
               jenisJoin + " pengguna u ON u.id_pengguna = " + petugas;
    }

    // Fitur: Generate Nomor Struk Baru
    public String generateNomorStrukBaru() throws SQLException {
        String tgl = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "STRUK-" + tgl + "-";
        String sql = "SELECT nomor_struk FROM pembayaran_transaksi WHERE nomor_struk LIKE ? ORDER BY id_pembayaran DESC LIMIT 1";

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

    private PembayaranTransaksi mapRow(ResultSet rs) throws SQLException {
        PembayaranTransaksi p = new PembayaranTransaksi();
        p.setIdPembayaran(rs.getLong("id_pembayaran"));
        p.setIdTransaksi(rs.getLong("id_transaksi"));
        p.setNomorStruk(rs.getString("nomor_struk"));
        p.setJumlahBayar(rs.getBigDecimal("jumlah_bayar"));
        p.setMetodePembayaran(rs.getString("metode_pembayaran"));
        p.setTanggalPembayaran(rs.getTimestamp("tanggal_pembayaran"));
        long diterimaOleh = rs.getLong("diterima_oleh");
        p.setDiterimaOleh(rs.wasNull() ? null : diterimaOleh);
        p.setCatatan(rs.getString("catatan"));
        p.setDibuatPada(rs.getTimestamp("dibuat_pada"));

        try {
            p.setNomorTransaksi(rs.getString("nomor_transaksi"));
            p.setNamaPetugasPenerima(rs.getString("nama_petugas"));
        } catch (SQLException ignored) {}

        return p;
    }
}
