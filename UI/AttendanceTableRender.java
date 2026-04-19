package attendance.ui;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class AttendanceTableRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // date column -> dark card color
        if (column == 0) {
            c.setBackground(new Color(42,48,56));
            c.setForeground(new Color(230,233,237));
            setHorizontalAlignment(LEFT);
            return c;
        }
        String v = value == null ? "" : value.toString().trim().toUpperCase();
        switch (v) {
            case "P":
                c.setBackground(new Color(76,209,55)); c.setForeground(Color.BLACK); break;
            case "A":
                c.setBackground(new Color(234,32,39)); c.setForeground(Color.WHITE); break;
            case "ML":
                c.setBackground(new Color(241,196,15)); c.setForeground(Color.BLACK); break;
            case "OD":
                c.setBackground(new Color(18,137,167)); c.setForeground(Color.WHITE); break;
            case "C":
                c.setBackground(new Color(131,149,167)); c.setForeground(Color.WHITE); break;
            default:
                c.setBackground(new Color(60,66,74)); c.setForeground(new Color(190,190,190)); break;
        }
        setHorizontalAlignment(CENTER);
        return c;
    }
}

