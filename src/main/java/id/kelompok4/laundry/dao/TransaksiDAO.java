package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;
import id.kelompok4.laundry.model.TransaksiLaundry;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Fitur: Kelola Transaksi Laundry
public class TransaksiDAO {

    // Fitur: Menyimpan transaksi baru yang dicatat oleh Petugas
    public boolean tambahTransaksi(TransaksiLaundry trx) throws SQLException {
        String sql = "INSERT INTO transaksi_laundry " +
                     "(nomor_transaksi, id_pelanggan, id_petugas_pencatat, id_jenis_layanan, id_paket, " +
                     " berat_aktual, pelanggan_setuju_minimum, tarif_per_kg_saat_transaksi, " +
                     " durasi_jam_saat_transaksi, status_transaksi, catatan) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'MENUNGGU_PEMBAYARAN', ?)";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, trx.getNomorTransaksi());
            ps.setLong(2, trx.getIdPelanggan());
            ps.setLong(3, trx.getIdPetugasPencatat());
            ps.setLong(4, trx.getIdJenisLayanan());
            ps.setLong(5, trx.getIdPaket());
            ps.setBigDecimal(6, trx.getBeratAktual());
            ps.setInt(7, trx.isPelangganSetujuMinimum() ? 1 : 0);
            ps.setBigDecimal(8, trx.getTarifPerKgSaatTransaksi());
            ps.setInt(9, trx.getDurasiJamSaatTransaksi());
            ps.setString(10, trx.getCatatan());

            int aff = ps.executeUpdate();
            if (aff > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        trx.setIdTransaksi(rs.getLong(1));
                    }
                }

                TransaksiLaundry dataSegar = cariBerdasarkanId(trx.getIdTransaksi());
                if (dataSegar != null) {
                    trx.setBeratTagihan(dataSegar.getBeratTagihan());
                    trx.setTotalBiaya(dataSegar.getTotalBiaya());
                    trx.setTanggalTransaksi(dataSegar.getTanggalTransaksi());
                    trx.setStatusTransaksi(dataSegar.getStatusTransaksi());
                }
                return true;
            }
        }
        return false;
    }

    // Fitur: Mengambil transaksi berdasarkan ID primer
    public TransaksiLaundry cariBerdasarkanId(Long idTransaksi) throws SQLException {
        String sql = getQueryLengkap() + " WHERE t.id_transaksi = ?";
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

    // Fitur: Mengambil transaksi berdasarkan Nomor Transaksi (TRX-...)
    public TransaksiLaundry cariBerdasarkanNomor(String nomorTransaksi) throws SQLException {
        String sql = getQueryLengkap() + " WHERE t.nomor_transaksi = ?";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomorTransaksi);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // Fitur: Mengambil transaksi milik pelanggan tertentu (digunakan oleh role Pelanggan)
    public List<TransaksiLaundry> ambilTransaksiPelanggan(Long idPelanggan) throws SQLException {
        List<TransaksiLaundry> list = new ArrayList<>();
        String sql = getQueryLengkap() + " WHERE t.id_pelanggan = ? ORDER BY t.tanggal_transaksi DESC";

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

    // Fitur: Mengambil semua transaksi dengan filter status dan pencarian keyword (untuk Admin / Kasir)
    public List<TransaksiLaundry> ambilSemuaTransaksi(String filterStatus, String keyword) throws SQLException {
        List<TransaksiLaundry> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder(getQueryLengkap());
        sb.append(" WHERE 1=1 ");

        if (filterStatus != null && !filterStatus.trim().isEmpty() && !"SEMUA".equalsIgnoreCase(filterStatus)) {
            sb.append(" AND t.status_transaksi = ? ");
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sb.append(" AND (t.nomor_transaksi LIKE ? OR u.nama_lengkap LIKE ? OR pl.kode_pelanggan LIKE ?) ");
        }

        sb.append(" ORDER BY t.tanggal_transaksi DESC");

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            int idx = 1;
            if (filterStatus != null && !filterStatus.trim().isEmpty() && !"SEMUA".equalsIgnoreCase(filterStatus)) {
                ps.setString(idx++, filterStatus);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(idx++, kw);
                ps.setString(idx++, kw);
                ps.setString(idx++, kw);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    // Fitur: Mengambil antrean transaksi yang masih menunggu pembayaran kasir
    public List<TransaksiLaundry> ambilTransaksiMenungguBayar() throws SQLException {
        return ambilSemuaTransaksi("MENUNGGU_PEMBAYARAN", null);
    }

    // Fitur: Mengambil transaksi yang siap diproses pengerjaannya (status DITERIMA)
    public List<TransaksiLaundry> ambilTransaksiDiterima() throws SQLException {
        return ambilSemuaTransaksi("DITERIMA", null);
    }

    // Fitur: Mengambil transaksi yang sedang diproses (status DIPROSES)
    public List<TransaksiLaundry> ambilTransaksiDiproses() throws SQLException {
        return ambilSemuaTransaksi("DIPROSES", null);
    }

    // Fitur: Mengambil cucian yang siap diambil oleh pelanggan (status SIAP_DIAMBIL)
    public List<TransaksiLaundry> ambilTransaksiSiapDiambil() throws SQLException {
        return ambilSemuaTransaksi("SIAP_DIAMBIL", null);
    }

    // Fitur: Mengambil cucian yang belum diambil dari view database v_cucian_belum_diambil
    public List<TransaksiLaundry> ambilCucianBelumDiambil() throws SQLException {
        List<TransaksiLaundry> list = new ArrayList<>();
        String sql = "SELECT t.*, u.nama_lengkap AS nama_pelanggan, u.nomor_telepon AS nomor_telepon, " +
                     "jl.nama_layanan, pk.nama_paket, pet.nama_lengkap AS nama_petugas, " +
                     "p.nomor_struk, p.metode_pembayaran, pl.kode_pelanggan " +
                     "FROM transaksi_laundry t " +
                     "INNER JOIN pelanggan pl ON pl.id_pelanggan = t.id_pelanggan " +
                     "INNER JOIN pengguna u ON u.id_pengguna = pl.id_pengguna " +
                     "INNER JOIN jenis_layanan jl ON jl.id_jenis_layanan = t.id_jenis_layanan " +
                     "INNER JOIN paket_laundry pk ON pk.id_paket = t.id_paket " +
                     "INNER JOIN pengguna pet ON pet.id_pengguna = t.id_petugas_pencatat " +
                     joinPembayaranDisetujuiTerakhir() +
                     "WHERE t.status_transaksi = 'SIAP_DIAMBIL' " +
                     "ORDER BY t.tanggal_siap_diambil ASC";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Fitur: Mengubah status pengerjaan cucian (DITERIMA -> DIPROSES -> SIAP_DIAMBIL)
    public boolean updateStatusPengerjaan(Long idTransaksi, String statusSebelumnya,
                                           String statusBaru, Long idPetugasPengubah) throws SQLException {
        String sql = "UPDATE transaksi_laundry SET status_transaksi = ?, id_pengguna_terakhir_memperbarui = ?, " +
                     "tanggal_siap_diambil = CASE WHEN ? = 'SIAP_DIAMBIL' THEN CURRENT_TIMESTAMP ELSE tanggal_siap_diambil END " +
                     "WHERE id_transaksi = ? AND status_transaksi = ?";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusBaru);
            ps.setLong(2, idPetugasPengubah);
            ps.setString(3, statusBaru);
            ps.setLong(4, idTransaksi);
            ps.setString(5, statusSebelumnya);
            return ps.executeUpdate() == 1;
        }
    }

    // Fitur: Menghasilkan Nomor Transaksi unik baru: TRX-YYYYMMDD-XXXX
    public String generateNomorTransaksiBaru() throws SQLException {
        String tgl = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "TRX-" + tgl + "-";
        String sql = "SELECT nomor_transaksi FROM transaksi_laundry WHERE nomor_transaksi LIKE ? ORDER BY id_transaksi DESC LIMIT 1";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                int urut = 1;
                if (rs.next()) {
                    String lastNomor = rs.getString(1);
                    try {
                        String sub = lastNomor.substring(prefix.length());
                        urut = Integer.parseInt(sub) + 1;
                    } catch (Exception ignored) {}
                }
                return String.format("%s%04d", prefix, urut);
            }
        }
    }

    private String getQueryLengkap() {
        return "SELECT t.*, " +
               "u.nama_lengkap AS nama_pelanggan, u.nomor_telepon AS nomor_telepon, pl.kode_pelanggan, " +
               "jl.nama_layanan, pk.nama_paket, pet.nama_lengkap AS nama_petugas, " +
               "p.nomor_struk, p.metode_pembayaran, " +
               "CASE WHEN p.id_pembayaran IS NOT NULL THEN 'LUNAS' ELSE 'BELUM_DIBAYAR' END AS status_bayar " +
               "FROM transaksi_laundry t " +
               "INNER JOIN pelanggan pl ON pl.id_pelanggan = t.id_pelanggan " +
               "INNER JOIN pengguna u ON u.id_pengguna = pl.id_pengguna " +
               "INNER JOIN jenis_layanan jl ON jl.id_jenis_layanan = t.id_jenis_layanan " +
               "INNER JOIN paket_laundry pk ON pk.id_paket = t.id_paket " +
               "INNER JOIN pengguna pet ON pet.id_pengguna = t.id_petugas_pencatat " +
               joinPembayaranDisetujuiTerakhir();
    }

    private String joinPembayaranDisetujuiTerakhir() {
        return "LEFT JOIN (" +
               "SELECT id_transaksi, MAX(id_pembayaran) AS id_pembayaran " +
               "FROM pembayaran_transaksi WHERE nomor_struk IS NOT NULL GROUP BY id_transaksi" +
               ") pembayaran_terakhir ON pembayaran_terakhir.id_transaksi = t.id_transaksi " +
               "LEFT JOIN pembayaran_transaksi p ON p.id_pembayaran = pembayaran_terakhir.id_pembayaran ";
    }

    private TransaksiLaundry mapRow(ResultSet rs) throws SQLException {
        TransaksiLaundry t = new TransaksiLaundry();
        t.setIdTransaksi(rs.getLong("id_transaksi"));
        t.setNomorTransaksi(rs.getString("nomor_transaksi"));
        t.setIdPelanggan(rs.getLong("id_pelanggan"));
        t.setIdPetugasPencatat(rs.getLong("id_petugas_pencatat"));
        t.setIdJenisLayanan(rs.getLong("id_jenis_layanan"));
        t.setIdPaket(rs.getLong("id_paket"));
        t.setBeratAktual(rs.getBigDecimal("berat_aktual"));
        t.setPelangganSetujuMinimum(rs.getInt("pelanggan_setuju_minimum") == 1);
        t.setBeratTagihan(rs.getBigDecimal("berat_tagihan"));
        t.setTarifPerKgSaatTransaksi(rs.getBigDecimal("tarif_per_kg_saat_transaksi"));
        t.setTotalBiaya(rs.getBigDecimal("total_biaya"));
        t.setDurasiJamSaatTransaksi(rs.getInt("durasi_jam_saat_transaksi"));
        t.setTanggalTransaksi(rs.getTimestamp("tanggal_transaksi"));
        t.setEstimasiSelesai(rs.getTimestamp("estimasi_selesai"));
        t.setTanggalSiapDiambil(rs.getTimestamp("tanggal_siap_diambil"));
        t.setStatusTransaksi(rs.getString("status_transaksi"));
        t.setCatatan(rs.getString("catatan"));
        t.setIdPenggunaTerakhirMemperbarui(rs.getLong("id_pengguna_terakhir_memperbarui"));

        try {
            t.setNamaPelanggan(rs.getString("nama_pelanggan"));
            t.setNomorTeleponPelanggan(rs.getString("nomor_telepon"));
            t.setKodePelanggan(rs.getString("kode_pelanggan"));
            t.setNamaLayanan(rs.getString("nama_layanan"));
            t.setNamaPaket(rs.getString("nama_paket"));
            t.setNamaPetugasPencatat(rs.getString("nama_petugas"));
            t.setNomorStruk(rs.getString("nomor_struk"));
            t.setMetodePembayaran(rs.getString("metode_pembayaran"));
            t.setStatusPembayaran(rs.getString("status_bayar"));
        } catch (SQLException ignored) {}

        return t;
    }
}
