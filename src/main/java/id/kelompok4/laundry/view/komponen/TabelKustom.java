package id.kelompok4.laundry.view.komponen;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;

// Fitur: Tampilkan Data pada Tabel
// OOP: Inheritance - TabelKustom mewarisi JTable.
public class TabelKustom extends JTable {

    public TabelKustom(DefaultTableModel model) {
        super(model);
        aturTampilan();
    }

    public TabelKustom() {
        super();
        aturTampilan();
    }

    private void aturTampilan() {
        setRowHeight(40);
        setFont(TemaWarna.FONT_REGULER);
        setForeground(TemaWarna.TEKS_GELAP);
        setBackground(TemaWarna.PUTIH);
        setGridColor(new Color(0xF1, 0xF5, 0xF9));
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setIntercellSpacing(new Dimension(0, 1));
        setSelectionBackground(new Color(0xE8, 0xF1, 0xFC));
        setSelectionForeground(TemaWarna.TEKS_GELAP);
        setFillsViewportHeight(true);

        JTableHeader header = getTableHeader();
        header.setFont(TemaWarna.FONT_KECIL_BOLD);
        header.setBackground(new Color(0xF1, 0xF5, 0xF9));
        header.setForeground(TemaWarna.ABU_TEKS);
        header.setPreferredSize(new Dimension(100, 40));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TemaWarna.ABU_BORDER));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(new Color(0xF1, 0xF5, 0xF9));
                lbl.setForeground(new Color(0x47, 0x55, 0x69));
                lbl.setFont(TemaWarna.FONT_KECIL_BOLD);
                TableColumn tableColumn = table.getColumnModel().getColumn(column);
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                if (tableColumn.getCellRenderer() instanceof BadgeStatusRenderer) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                }
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, TemaWarna.ABU_BORDER),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)
                ));
                return lbl;
            }
        };
        header.setDefaultRenderer(headerRenderer);

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? TemaWarna.PUTIH : TemaWarna.PERMUKAAN_SOFT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
    }

    // Fitur: Menyembunyikan kolom ID internal database dari tampilan pengguna
    public void sembunyikanKolom(int... indeksKolom) {
        for (int idx : indeksKolom) {
            if (idx >= 0 && idx < getColumnModel().getColumnCount()) {
                TableColumn col = getColumnModel().getColumn(idx);
                col.setMinWidth(0);
                col.setMaxWidth(0);
                col.setPreferredWidth(0);
                col.setWidth(0);
            }
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
