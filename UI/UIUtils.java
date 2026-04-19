package attendance.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

	 public class UIUtils {

	     public static final Color DARK2 = new Color(40, 40, 40);     // Header / panels


	     public static JLabel label1(String txt) {
	         JLabel l = new JLabel(txt);
	         l.setForeground(TEXT);
	         l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
	         return l;
	     }

	     public static JLabel label(String txt, boolean bold) {
	         JLabel l = new JLabel(txt);
	         l.setForeground(TEXT);
	         l.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 15));
	         return l;
	     }

	     public static JLabel title1(String txt) {
	         JLabel l = new JLabel(txt);
	         l.setForeground(TEXT);
	         l.setFont(new Font("Segoe UI", Font.BOLD, 28));
	         return l;
	     }

	     public static JButton sideButton(String text) {

	    	    class SlideButton extends JButton {
	    	        int barWidth = 0;
	    	        final int MAX_BAR = 8;

	    	        SlideButton(String t) {
	    	            super(t);
	    	            setFocusPainted(false);
	    	            setBorderPainted(false);
	    	            setBackground(DARK2);
	    	            setForeground(Color.WHITE);
	    	            setFont(new Font("Segoe UI", Font.BOLD, 14));
	    	            setHorizontalAlignment(SwingConstants.LEFT);
	    	            setMargin(new Insets(8, 20, 8, 8));
	    	            setPreferredSize(new Dimension(180, 40));

	    	            addHoverAnimation1(this);
	    	        }

	    	        @Override
	    	        protected void paintComponent(Graphics g) {
	    	            super.paintComponent(g);
	    	            g.setColor(ACCENT);
	    	            g.fillRect(0, 0, barWidth, getHeight());
	    	        }
	    	    }

	    	    // Create button
	    	    SlideButton btn = new SlideButton(text);
	    	    return btn;
	    	}

	    	/* ------------------ HOVER ANIMATION HELPER ------------------ */

	    	private static void addHoverAnimation1(JButton btn) {

	    	    final Timer[] expand = new Timer[1];
	    	    final Timer[] shrink = new Timer[1];

	    	    btn.addMouseListener(new java.awt.event.MouseAdapter() {

	    	        @Override
	    	        public void mouseEntered(java.awt.event.MouseEvent e) {

	    	            // Stop shrinking if running
	    	            if (shrink[0] != null && shrink[0].isRunning()) {
	    	                shrink[0].stop();
	    	            }

	    	            expand[0] = new Timer(6, t -> {
	    	                try {
	    	                    // Access fields using reflection-safe cast
	    	                    var field = btn.getClass().getDeclaredField("barWidth");
	    	                    var maxField = btn.getClass().getDeclaredField("MAX_BAR");
	    	                    field.setAccessible(true);
	    	                    maxField.setAccessible(true);

	    	                    int bw = field.getInt(btn);
	    	                    int max = maxField.getInt(btn);

	    	                    if (bw < max) {
	    	                        field.setInt(btn, bw + 1);
	    	                        btn.repaint();
	    	                    }

	    	                } catch (Exception ignored) {}
	    	            });

	    	            expand[0].start();
	    	        }

	    	        @Override
	    	        public void mouseExited(java.awt.event.MouseEvent e) {

	    	            // Stop expanding if running
	    	            if (expand[0] != null && expand[0].isRunning()) {
	    	                expand[0].stop();
	    	            }

	    	            shrink[0] = new Timer(6, t -> {
	    	                try {
	    	                    var field = btn.getClass().getDeclaredField("barWidth");
	    	                    field.setAccessible(true);

	    	                    int bw = field.getInt(btn);
	    	                    if (bw > 0) {
	    	                        field.setInt(btn, bw - 1);
	    	                        btn.repaint();
	    	                    }

	    	                } catch (Exception ignored) {}
	    	            });

	    	            shrink[0].start();
	    	        }
	    	    });
	    	}
	     // ===================== TEXT FIELDS =====================
	     public static JTextField textField1(int size) {
	         JTextField tf = new JTextField(size);
	         tf.setBackground(DARK2);
	         tf.setForeground(TEXT);
	         tf.setCaretColor(TEXT);
	         tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
	         tf.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
	         return tf;
	     }

	     public static JPasswordField passwordField1(int size) {
	         JPasswordField pf = new JPasswordField(size);
	         pf.setBackground(DARK2);
	         pf.setForeground(TEXT);
	         pf.setCaretColor(TEXT);
	         pf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
	         pf.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
	         return pf;
	     }


	     // ===================== BUTTONS =====================
	     public static JButton createButton(String text) {
	         JButton btn = new JButton(text);
	         btn.setFocusPainted(false);
	         btn.setBackground(DARK2);
	         btn.setForeground(TEXT);
	         btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
	         btn.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
	         btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
	         return btn;
	     }

	     public static JButton iconButton(String text, Icon icon) {
	         JButton btn = new JButton(text, icon);
	         btn.setFocusPainted(false);
	         btn.setBackground(ACCENT);
	         btn.setForeground(Color.WHITE);
	         btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
	         btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
	         btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
	         return btn;
	     }


	     // ===================== PANELS =====================
	     public static JPanel panelBox() {
	         JPanel p = new JPanel();
	         p.setBackground(BG);
	         p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	         return p;
	     }


	     // ===================== GRIDBAG HELPERS =====================
	     public static GridBagConstraints gb() {
	         GridBagConstraints g = new GridBagConstraints();
	         g.insets = new Insets(10, 10, 10, 10);
	         g.anchor = GridBagConstraints.WEST;
	         g.gridx = 0;
	         g.gridy = 0;
	         return g;
	     }

	     public static void addRow1(JPanel p, GridBagConstraints g, String label, JComponent field) {
	         g.gridx = 0;
	         p.add(label1(label), g);

	         g.gridx = 1;
	         p.add(field, g);

	         g.gridy++;
	     }

	     public static void addRow(JPanel panel, String label, JComponent field) {

	    	    if (!(panel.getLayout() instanceof GridBagLayout)) {
	    	        panel.setLayout(new GridBagLayout());
	    	    }

	    	    GridBagLayout layout = (GridBagLayout) panel.getLayout();

	    	    GridBagConstraints g = new GridBagConstraints();
	    	    g.insets = new Insets(8, 10, 8, 10);
	    	    g.anchor = GridBagConstraints.LINE_START;
	    	    g.fill = GridBagConstraints.HORIZONTAL;

	    	    // Determine next row (count existing rows)
	    	    int row = panel.getComponentCount() / 2; 
	    	    g.gridy = row;

	    	    // Label
	    	    JLabel lbl = new JLabel(label);
	    	    lbl.setForeground(TEXT);
	    	    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

	    	    g.gridx = 0;
	    	    panel.add(lbl, g);

	    	    // Field
	    	    g.gridx = 1;
	    	    field.setPreferredSize(new Dimension(180, 28));
	    	    panel.add(field, g);
	    	}
	    	/* ------------------ HOVER ANIMATION HELPER ------------------ */

	    	private static void addHoverAnimation(JButton btn) {

	    	    final Timer[] expand = new Timer[1];
	    	    final Timer[] shrink = new Timer[1];

	    	    btn.addMouseListener(new java.awt.event.MouseAdapter() {

	    	        @Override
	    	        public void mouseEntered(java.awt.event.MouseEvent e) {

	    	            // Stop shrinking if running
	    	            if (shrink[0] != null && shrink[0].isRunning()) {
	    	                shrink[0].stop();
	    	            }

	    	            expand[0] = new Timer(6, t -> {
	    	                try {
	    	                    // Access fields using reflection-safe cast
	    	                    var field = btn.getClass().getDeclaredField("barWidth");
	    	                    var maxField = btn.getClass().getDeclaredField("MAX_BAR");
	    	                    field.setAccessible(true);
	    	                    maxField.setAccessible(true);

	    	                    int bw = field.getInt(btn);
	    	                    int max = maxField.getInt(btn);

	    	                    if (bw < max) {
	    	                        field.setInt(btn, bw + 1);
	    	                        btn.repaint();
	    	                    }

	    	                } catch (Exception ignored) {}
	    	            });

	    	            expand[0].start();
	    	        }

	    	        @Override
	    	        public void mouseExited(java.awt.event.MouseEvent e) {

	    	            // Stop expanding if running
	    	            if (expand[0] != null && expand[0].isRunning()) {
	    	                expand[0].stop();
	    	            }

	    	            shrink[0] = new Timer(6, t -> {
	    	                try {
	    	                    var field = btn.getClass().getDeclaredField("barWidth");
	    	                    field.setAccessible(true);

	    	                    int bw = field.getInt(btn);
	    	                    if (bw > 0) {
	    	                        field.setInt(btn, bw - 1);
	    	                        btn.repaint();
	    	                    }

	    	                } catch (Exception ignored) {}
	    	            });

	    	            shrink[0].start();
	    	        }
	    	    });
	    	}
	     // Colors & fonts
	     public static final Color BG = new Color(0x12,0x12,0x12);
	     public static final Color PANEL = new Color(0x1E,0x1E,0x1E);
	     public static final Color SURFACE = new Color(0x22,0x22,0x22);
	     public static final Color MUTED = new Color(0x99,0x99,0x99);
	     public static final Color TEXT = new Color(0xEE,0xEE,0xEE);
	     public static final Color ACCENT = new Color(0x29,0x79,0xFF);
	     public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
	     public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
	     public static final Font FONT = new Font("Segoe UI", Font.PLAIN, 13);
	     public static final int CORNER = 10;

	     // ========== Buttons ==========
	   	     public static ImageIcon createIcon(String type, int w, int h, Color fg) {
	         BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
	         Graphics2D g = img.createGraphics();
	         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	         g.setColor(new Color(0,0,0,0));
	         g.fillRect(0,0,w,h);
	         g.setColor(fg==null? Color.WHITE: fg);

	         int pad = Math.max(2, w/8);
	         switch (type.toLowerCase()) {
	             case "login":
	                 // arrow into rectangle
	                 g.drawRect(pad,pad,w-2*pad-6,h-2*pad-6);
	                 g.fillPolygon(new int[]{w-pad-6,w-pad-18,w-pad-6}, new int[]{h/2-pad,h/2,h/2+pad}, 3);
	                 break;
	             case "logout":
	                 g.drawRect(pad,pad,w-2*pad-6,h-2*pad-6);
	                 g.drawLine(w-pad-6-pad,h/2,w-pad-6-pad-14,h/2-10);
	                 g.drawLine(w-pad-6-pad,h/2,w-pad-6-pad-14,h/2+10);
	                 break;
	             case "user":
	                 g.fillOval(w/4, pad, w/2, h/2 - pad);
	                 g.drawOval(w/4, pad, w/2, h/2 - pad);
	                 g.fillRect(w/4, h/2 - pad, w/2, h/2 - pad*2);
	                 break;
	             case "student":
	                 g.fillOval(w/2 - 8, pad+2, 16, 16);
	                 g.drawRect(pad, pad+22, w-2*pad, h- (pad+22) - pad);
	                 break;
	             case "faculty":
	                 g.fillOval(w/2 - 8, pad+2, 16, 16);
	                 g.drawRect(pad, pad+22, w-2*pad, h- (pad+22) - pad);
	                 g.drawLine(pad+6, h- pad - 10, w-pad-6, h- pad -10);
	                 break;
	             case "timetable":
	                 int cellW = (w-2*pad)/3, cellH = (h-2*pad)/3;
	                 for (int r=0;r<3;r++) for (int c=0;c<3;c++) g.drawRect(pad + c*cellW, pad + r*cellH, cellW-2, cellH-2);
	                 break;
	             default:
	                 g.fillOval(pad,pad,w-2*pad,h-2*pad);
	         }
	         g.dispose();
	         return new ImageIcon(img);
	     }

	     public static ImageIcon iconLogin1(int size) { return createIcon("login", size, size, Color.WHITE); }
	     public static ImageIcon iconLogout(int size) { return createIcon("logout", size, size, Color.WHITE); }
	     public static ImageIcon iconUser(int size) { return createIcon("user", size, size, Color.WHITE); }
	     public static ImageIcon iconStudent(int size) { return createIcon("student", size, size, Color.WHITE); }
	     public static ImageIcon iconFaculty(int size) { return createIcon("faculty", size, size, Color.WHITE); }
	     public static ImageIcon iconTimetable(int size) { return createIcon("timetable", size, size, Color.WHITE); }

	     // date helper
	     public static String today() { return new SimpleDateFormat("yyyy-MM-dd").format(new Date()); }

	

    // ========== Buttons ==========
    public static JButton flatButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(ACCENT);
        b.setForeground(Color.BLACK);
        b.setFont(FONT_BOLD);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton neutralButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(SURFACE);
        b.setForeground(TEXT);
        b.setFont(FONT);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton iconButton(String text, ImageIcon icon) {
        JButton b = neutralButton(text);
        b.setIcon(icon);
        b.setHorizontalTextPosition(SwingConstants.RIGHT);
        b.setIconTextGap(8);
        return b;
    }

    // ========== Labels / Fields ==========
    public static JLabel title(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TEXT);
        l.setFont(FONT_TITLE);
        return l;
    }

    public static JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TEXT);
        l.setFont(FONT);
        return l;
    }

    public static JTextField textField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setBackground(SURFACE);
        tf.setForeground(TEXT);
        tf.setCaretColor(TEXT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40,40,40)),
                new EmptyBorder(6,8,6,8)
        ));
        tf.setFont(FONT);
        return tf;
    }

    public static JPasswordField passwordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setBackground(SURFACE);
        pf.setForeground(TEXT);
        pf.setCaretColor(TEXT);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40,40,40)),
                new EmptyBorder(6,8,6,8)
        ));
        pf.setFont(FONT);
        return pf;
    }

    public static JComboBox<String> combo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(SURFACE);
        cb.setForeground(TEXT);
        cb.setFont(FONT);
        return cb;
    }

    public static JSpinner dateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner sp = new JSpinner(model);
        sp.setEditor(new JSpinner.DateEditor(sp, "yyyy-MM-dd"));
        sp.setFont(FONT);
        sp.setBackground(SURFACE);
        sp.setForeground(TEXT);
        return sp;
    }

    // ========== Panel helpers ==========
    public static JPanel darkPanel() {
        JPanel p = new JPanel();
        p.setBackground(PANEL);
        return p;
    }

    public static JPanel roundedPanel(int arc, int padding) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(PANEL);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),arc,arc));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(padding,padding,padding,padding));
        return p;
    }

    // ========== Table styling ==========
    public static void styleTable(JTable table) {
        table.setBackground(new Color(0x16,0x16,0x16));
        table.setForeground(TEXT);
        table.setRowHeight(28);
        table.setFont(FONT);
        table.setShowGrid(false);
        JTableHeader h = table.getTableHeader();
        h.setBackground(new Color(0x1B,0x1B,0x1B));
        h.setForeground(ACCENT);
        h.setFont(FONT_BOLD);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            final DefaultTableCellRenderer def = new DefaultTableCellRenderer();
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c) {
                Component comp = def.getTableCellRendererComponent(t,v,s,f,r,c);
                comp.setBackground(r%2==0? new Color(0x16,0x16,0x16): new Color(0x1A,0x1A,0x1A));
                comp.setForeground(TEXT);
                ((JComponent)comp).setBorder(new EmptyBorder(4,8,4,8));
                return comp;
            }
        });
    }

    // ========== Scrollbar ==========
    public static void installScroll(JScrollPane sp) {
        sp.getVerticalScrollBar().setUI(new ThinScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new ThinScrollBarUI());
        sp.setBorder(BorderFactory.createEmptyBorder());
    }

    public static class ThinScrollBarUI extends BasicScrollBarUI {
        @Override protected JButton createDecreaseButton(int orientation) { return zero(); }
        @Override protected JButton createIncreaseButton(int orientation) { return zero(); }
        private JButton zero(){ JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(ACCENT.darker());
            g2.fillRoundRect(thumbBounds.x+2, thumbBounds.y+2, thumbBounds.width-4, thumbBounds.height-4, 8, 8);
            g2.dispose();
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0,0,0,60));
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }

    // ========== Toast ==========
    public static void toast(Window owner, String message) {
        final JWindow w = new JWindow(owner);
        JPanel p = new JPanel();
        p.setBackground(new Color(20,20,20,230));
        p.setBorder(new EmptyBorder(8,12,8,12));
        JLabel l = new JLabel(message);
        l.setForeground(TEXT);
        l.setFont(FONT);
        p.add(l);
        w.add(p);
        w.pack();
        Rectangle b = (owner!=null? owner.getBounds() : GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        w.setLocation(b.x + b.width - w.getWidth() - 20, b.y + b.height - w.getHeight() - 40);
        w.setVisible(true);
        new Timer(1600, e -> { w.setVisible(false); w.dispose(); }).start();
    }
	 }
