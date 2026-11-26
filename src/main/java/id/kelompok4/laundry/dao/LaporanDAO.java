package id.kelompok4.laundry.dao;

import id.kelompok4.laundry.konektor.KoneksiDatabase;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Fitur: Laporan Operasional dan Keuangan
public class LaporanDAO {

    // Fitur: Mengambil ringkasan metrik untuk Dashboard Admin/Pemilik
    public Map<String, Object> ambilRingkasanAdmin() throws SQLException {
        Map<String, Object> map = new HashMap<>();

        try (Connection conn = KoneksiDatabase.getKoneksi()) {
            String filterPembayaran = PembayaranDAO.menggunakanAlurVerifikasi(conn)
                ? " WHERE status_verifikasi = 'DISETUJUI'"
                : "";
            String sql = "SELECT " +
                         "(SELECT COUNT(*) FROM pelanggan) AS total_pelanggan, " +
                         "(SELECT COUNT(*) FROM pengguna WHERE peran = 'PETUGAS' AND status_aktif = 1) AS total_petugas, " +
                         "(SELECT COUNT(*) FROM transaksi_laundry) AS total_transaksi, " +
                         "(SELECT COUNT(*) FROM transaksi_laundry WHERE status_transaksi = 'MENUNGGU_PEMBAYARAN') AS trx_menunggu_bayar, " +
                         "(SELECT COUNT(*) FROM transaksi_laundry WHERE status_transaksi = 'DITERIMA') AS trx_diterima, " +
                         "(SELECT COUNT(*) FROM transaksi_laundry WHERE status_transaksi = 'DIPROSES') AS trx_diproses, " +
                         "(SELECT COUNT(*) FROM transaksi_laundry WHERE status_transaksi = 'SIAP_DIAMBIL') AS trx_siap_diambil, " +
                         "(SELECT COUNT(*) FROM transaksi_laundry WHERE status_transaksi = 'SELESAI') AS trx_selesai, " +
                         "(SELECT COUNT(*) FROM pengaduan WHERE status_pengaduan NOT IN ('SELESAI', 'DITOLAK')) AS pengaduan_aktif, " +
                         "(SELECT COUNT(*) FROM permohonan_pembatalan WHERE status_permohonan = 'DIAJUKAN') AS pembatalan_menunggu, " +
                         "(SELECT COUNT(*) FROM pengembalian_dana WHERE status_pengembalian = 'MENUNGGU') AS refund_menunggu, " +
                         "(SELECT COALESCE(SUM(jumlah_bayar), 0) FROM pembayaran_transaksi" + filterPembayaran + ") AS total_penerimaan_jasa, " +
                         "(SELECT COALESCE(SUM(jumlah_pengembalian), 0) FROM pengembalian_dana WHERE status_pengembalian = 'DIBAYAR') AS total_refund_dibayar";

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    map.put("total_pelanggan", rs.getInt("total_pelanggan"));
                    map.put("total_petugas", rs.getInt("total_petugas"));
                    map.put("total_transaksi", rs.getInt("total_transaksi"));
                    map.put("trx_menunggu_bayar", rs.getInt("trx_menunggu_bayar"));
                    map.put("trx_diterima", rs.getInt("trx_diterima"));
                    map.put("trx_diproses", rs.getInt("trx_diproses"));
                    map.put("trx_siap_diambil", rs.getInt("trx_siap_diambil"));
                    map.put("trx_selesai", rs.getInt("trx_selesai"));
                    map.put("pengaduan_aktif", rs.getInt("pengaduan_aktif"));
                    map.put("pembatalan_menunggu", rs.getInt("pembatalan_menunggu"));
                    map.put("refund_menunggu", rs.getInt("refund_menunggu"));
                    map.put("total_penerimaan_jasa", rs.getBigDecimal("total_penerimaan_jasa"));
                    map.put("total_refund_dibayar", rs.getBigDecimal("total_refund_dibayar"));
                }
            }
        }
        return map;
    }

    // Fitur: Mengambil ringkasan operasional untuk Dashboard Petugas/Kasir
    public Map<String, Object> ambilRingkasanPetugas() throws SQLException {
        Map<String, Object> map = new HashMap<>();

        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM transaksi_laundry WHERE status_transaksi = 'MENUNGGU_PEMBAYARAN') AS antrean_bayar, " +
                     "(SELECT COUNT(*) FROM transaksi_laundry WHERE status_transaksi = 'DITERIMA') AS antrean_cuci, " +
                     "(SELECT COUNT(*) FROM transaksi_laundry WHERE status_transaksi = 'DIPROSES') AS sedang_diproses, " +
                     "(SELECT COUNT(*) FROM transaksi_laundry WHERE status_transaksi = 'SIAP_DIAMBIL') AS siap_diambil, " +
                     "(SELECT COUNT(*) FROM transaksi_laundry WHERE status_transaksi = 'SELESAI') AS selesai, " +
                     "(SELECT COUNT(*) FROM pengaduan WHERE status_pengaduan IN ('DIVERIFIKASI', 'DALAM_PENELUSURAN')) AS tugas_penelusuran, " +
                     "(SELECT COUNT(*) FROM pengembalian_dana WHERE status_pengembalian = 'MENUNGGU') AS refund_harus_diserahkan";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                map.put("antrean_bayar", rs.getInt("antrean_bayar"));
                map.put("antrean_cuci", rs.getInt("antrean_cuci"));
                map.put("sedang_diproses", rs.getInt("sedang_diproses"));
                map.put("siap_diambil", rs.getInt("siap_diambil"));
                map.put("selesai", rs.getInt("selesai"));
                map.put("tugas_penelusuran", rs.getInt("tugas_penelusuran"));
                map.put("refund_harus_diserahkan", rs.getInt("refund_harus_diserahkan"));
            }
        }
        return map;
    }

    // Fitur: Mengambil ringkasan personal untuk Dashboard Pelanggan
    public Map<String, Object> ambilRingkasanPelanggan(Long idPelanggan) throws SQLException {
        Map<String, Object> map = new HashMap<>();

        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM transaksi_laundry WHERE id_pelanggan = " + idPelanggan + " AND status_transaksi IN ('DITERIMA', 'DIPROSES')) AS sedang_dikerjakan, " +
                     "(SELECT COUNT(*) FROM transaksi_laundry WHERE id_pelanggan = " + idPelanggan + " AND status_transaksi = 'SIAP_DIAMBIL') AS siap_diambil, " +
                     "(SELECT COUNT(*) FROM transaksi_laundry WHERE id_pelanggan = " + idPelanggan + ") AS total_pesanan, " +
                     "(SELECT COUNT(*) FROM pengaduan WHERE id_pelanggan = " + idPelanggan + " AND status_pengaduan <> 'SELESAI') AS pengaduan_berjalan";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                map.put("sedang_dikerjakan", rs.getInt("sedang_dikerjakan"));
                map.put("siap_diambil", rs.getInt("siap_diambil"));
                map.put("total_pesanan", rs.getInt("total_pesanan"));
                map.put("pengaduan_berjalan", rs.getInt("pengaduan_berjalan"));
            }
        }
        return map;
    }

    // Fitur: Mengambil riwayat arus kas keuangan (Pemasukan jasa, Pengeluaran refund, dan Kompensasi)
    public List<Map<String, Object>> ambilLaporanArusKas() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = KoneksiDatabase.getKoneksi()) {
            boolean alurVerifikasi = PembayaranDAO.menggunakanAlurVerifikasi(conn);
            String tanggalPembayaran = alurVerifikasi ? "pt.waktu_verifikasi" : "pt.tanggal_pembayaran";
            String petugasPembayaran = alurVerifikasi ? "pt.id_petugas_verifikasi" : "pt.diterima_oleh";
            String filterPembayaran = alurVerifikasi ? "WHERE pt.status_verifikasi = 'DISETUJUI' " : "";
            String sql =
                "SELECT 'PEMASUKAN' AS tipe, " + tanggalPembayaran + " AS tanggal, " +
                "CONCAT('Pembayaran Trx #', t.nomor_transaksi, ' (Struk: ', pt.nomor_struk, ')') AS deskripsi, " +
                "pt.metode_pembayaran AS metode, pt.jumlah_bayar AS nominal, " +
                "COALESCE(u.nama_lengkap, 'Petugas') AS operator " +
                "FROM pembayaran_transaksi pt " +
                "JOIN transaksi_laundry t ON t.id_transaksi = pt.id_transaksi " +
                "LEFT JOIN pengguna u ON u.id_pengguna = " + petugasPembayaran + " " +
                filterPembayaran +
                "UNION ALL " +
                "SELECT 'PENGELUARAN' AS tipe, rd.waktu_pengembalian AS tanggal, " +
                "CONCAT('Refund Batal Trx #', t.nomor_transaksi, ' (No: ', rd.nomor_pengembalian, ')') AS deskripsi, " +
                "COALESCE(rd.metode_pengembalian, 'TUNAI') AS metode, rd.jumlah_pengembalian AS nominal, " +
                "COALESCE(u.nama_lengkap, 'Kasir') AS operator " +
                "FROM pengembalian_dana rd " +
                "JOIN transaksi_laundry t ON t.id_transaksi = rd.id_transaksi " +
                "LEFT JOIN pengguna u ON u.id_pengguna = rd.dibayarkan_oleh " +
                "WHERE rd.status_pengembalian = 'DIBAYAR' " +
                "UNION ALL " +
                "SELECT 'PENGELUARAN' AS tipe, ck.tanggal_dibayar AS tanggal, " +
                "CONCAT('Ganti Rugi Komplain: ', ck.jenis_catatan) AS deskripsi, " +
                "COALESCE(ck.metode_pembayaran, 'TUNAI') AS metode, ck.nominal AS nominal, " +
                "COALESCE(u.nama_lengkap, 'Kasir') AS operator " +
                "FROM catatan_keuangan_penyelesaian ck " +
                "LEFT JOIN pengguna u ON u.id_pengguna = ck.dilaksanakan_oleh " +
                "WHERE ck.status_catatan = 'DIBAYAR' " +
                "ORDER BY tanggal DESC";

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("tipe", rs.getString("tipe"));
                    m.put("tanggal", rs.getTimestamp("tanggal"));
                    m.put("deskripsi", rs.getString("deskripsi"));
                    m.put("metode", rs.getString("metode"));
                    m.put("nominal", rs.getBigDecimal("nominal"));
                    m.put("operator", rs.getString("operator"));
                    list.add(m);
                }
            }
        }
        return list;
    }
}
