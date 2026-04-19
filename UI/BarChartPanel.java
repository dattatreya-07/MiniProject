package attendance.ui;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
public class BarChartPanel extends JPanel {
    private Map<String,Integer> data;
    public BarChartPanel(Map<String,Integer> data) {
        this.data = data;
        setBackground(new Color(42,48,56));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int left = 40, bottom = h - 40;
        int max = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        int n = data.size();
        int gap = 18;
        int barW = (w - left - 40 - (n-1)*gap) / Math.max(1,n);
        int i = 0;
        Color[] palette = {new Color(76,209,55), new Color(234,32,39), new Color(241,196,15), new Color(18,137,167), new Color(131,149,167)};
        for (Map.Entry<String,Integer> e : data.entrySet()) {
            int val = e.getValue();
            int bh = (int)((h - 120) * (val / (double)Math.max(1, max)));
            int bx = left + i*(barW+gap);
            int by = bottom - bh;
            g2.setColor(palette[i % palette.length]);
            g2.fillRoundRect(bx, by, barW, bh, 8, 8);
            g2.setColor(Color.WHITE);
            g2.drawString(e.getKey() + " (" + val + ")", bx, bottom + 15);
            i++;
        }
    }
}
