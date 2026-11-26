package id.kelompok4.laundry.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utilitas FormatUtil
 * Memformat angka ke format Rupiah Indonesia (Rp), format desimal berat (Kg),
 * serta format tanggal/waktu yang ramah pengguna.
 */
public class FormatUtil {

    private static final Locale LOCALE_ID = Locale.forLanguageTag("id-ID");
    private static final NumberFormat FORMAT_RUPIAH = NumberFormat.getCurrencyInstance(LOCALE_ID);
    private static final DateTimeFormatter FORMAT_TANGGAL_WAKTU = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    static {
        FORMAT_RUPIAH.setMaximumFractionDigits(0);
    }

    public static String formatRupiah(BigDecimal nominal) {
        if (nominal == null) return "Rp0";
        return FORMAT_RUPIAH.format(nominal).replace(",00", "");
    }

    public static String formatRupiah(double nominal) {
        return formatRupiah(BigDecimal.valueOf(nominal));
    }

    public static String formatBerat(BigDecimal beratKg) {
        if (beratKg == null) return "0,00 Kg";
        return String.format(LOCALE_ID, "%.2f Kg", beratKg);
    }

    public static String formatTanggalWaktu(Timestamp ts) {
        if (ts == null) return "-";
        return ts.toLocalDateTime().format(FORMAT_TANGGAL_WAKTU);
    }

    public static String formatTanggal(Timestamp ts) {
        if (ts == null) return "-";
        return ts.toLocalDateTime().format(FORMAT_TANGGAL);
    }
}
