package id.kelompok4.laundry.util;

import id.kelompok4.laundry.model.PembayaranTransaksi;
import id.kelompok4.laundry.model.TransaksiLaundry;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;

// Fitur: Cetak Struk Pembayaran
public class CetakStrukUtil implements Printable {

    private final String isiStruk;

    public CetakStrukUtil(String isiStruk) {
        this.isiStruk = isiStruk;
    }

    // Fitur: Menghasilkan teks struk rapi dan berformat
    public static String susunTeksStruk(TransaksiLaundry trx, PembayaranTransaksi bayar) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("               RAPIWASH                 \n");
        sb.append("      Laundry Management System         \n");
        sb.append("     Clean Clothes, Better Days.        \n");
        sb.append("========================================\n");
        sb.append(String.format("No. Struk     : %s\n", bayar.getNomorStruk()));
        sb.append(String.format("No. Transaksi : %s\n", trx.getNomorTransaksi()));
        sb.append(String.format("Waktu Bayar   : %s\n", FormatUtil.formatTanggalWaktu(bayar.getTanggalPembayaran())));
        sb.append(String.format("Pelanggan     : %s (%s)\n", trx.getNamaPelanggan(), trx.getKodePelanggan() != null ? trx.getKodePelanggan() : "-"));
        sb.append(String.format("No. Telepon   : %s\n", trx.getNomorTeleponPelanggan() != null ? trx.getNomorTeleponPelanggan() : "-"));
        sb.append("----------------------------------------\n");
        sb.append(String.format("Layanan       : %s\n", trx.getNamaLayanan()));
        sb.append(String.format("Paket         : %s (%d Jam)\n", trx.getNamaPaket(), trx.getDurasiJamSaatTransaksi()));
        sb.append(String.format("Berat Aktual  : %s\n", FormatUtil.formatBerat(trx.getBeratAktual())));
        sb.append(String.format("Berat Tagihan : %s (Min. 2 Kg)\n", FormatUtil.formatBerat(trx.getBeratTagihan())));
        sb.append(String.format("Tarif / Kg    : %s\n", FormatUtil.formatRupiah(trx.getTarifPerKgSaatTransaksi())));
        sb.append("----------------------------------------\n");
        sb.append(String.format("TOTAL BIAYA   : %s\n", FormatUtil.formatRupiah(trx.getTotalBiaya())));
        sb.append(String.format("JUMLAH BAYAR  : %s\n", FormatUtil.formatRupiah(bayar.getJumlahBayar())));
        sb.append(String.format("Metode Bayar  : %s\n", bayar.getMetodePembayaran()));
        sb.append("STATUS        : LUNAS\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("Estimasi Jadi : %s\n", FormatUtil.formatTanggalWaktu(trx.getEstimasiSelesai())));
        sb.append(String.format("Kasir         : %s\n", bayar.getNamaPetugasPenerima() != null ? bayar.getNamaPetugasPenerima() : "Petugas"));
        sb.append("========================================\n");
        sb.append("   Terima kasih sudah mencuci di sini   \n");
        sb.append("========================================\n");
        return sb.toString();
    }

    // Fitur: Menjalankan dialog print standar Java
    public static void cetakKePrinter(Component parent, String teksStruk) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new CetakStrukUtil(teksStruk));
        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
                JOptionPane.showMessageDialog(parent, "Struk sedang dikirim ke printer.", "Cetak Struk", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(parent, "Gagal mencetak struk: " + ex.getMessage(), "Kesalahan Cetak", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));

        int y = 20;
        for (String baris : isiStruk.split("\n")) {
            g2d.drawString(baris, 15, y);
            y += 12;
        }

        return PAGE_EXISTS;
    }
}
