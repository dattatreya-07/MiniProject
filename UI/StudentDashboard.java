package attendance.ui;

import attendance.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final int userId;
    private int studentId;

    private String fullName = "", roll = "", dept = "", section = "",
            mobile = "", email = "", address = "", dob = "";

    public StudentDashboard(int userId) {
        this.userId = userId;
        setTitle("Student Dashboard - MEPCO ERP");
        setSize(1200, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {

        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(28, 32, 38));
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(35, 40, 48));
        header.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel lblTitle = new JLabel("Student Dashboard");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(lblTitle, BorderLayout.WEST);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.setBackground(new Color(35, 40, 48));

        JButton btnProfile = UIUtils.createButton("My Profile");
        JButton btnLogout = UIUtils.createButton("Logout");

        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        rightButtons.add(btnProfile);
        rightButtons.add(btnLogout);
        header.add(rightButtons, BorderLayout.EAST);

        fetchProfile();


        JPanel profilePanel = new JPanel(new GridLayout(1, 4, 10, 10));
        profilePanel.setBackground(new Color(42, 48, 56));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        profilePanel.add(profileItem(fullName + "<br><span style='color:#aaa;'>" + roll + "</span>"));
        profilePanel.add(profileItem("Dept: " + dept));
        profilePanel.add(profileItem("Section: " + section));
        profilePanel.add(profileItem("ID: " + studentId));

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setBackground(new Color(30, 34, 40));
        topWrapper.add(header, BorderLayout.NORTH);
        topWrapper.add(profilePanel, BorderLayout.SOUTH);

        add(topWrapper, BorderLayout.NORTH);

        // Profile Button
        btnProfile.addActionListener(e -> showMyProfile());

        // ================= SPLIT PANE =================
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(480);

        add(splitPane, BorderLayout.CENTER);

        // ================= TOP AREA =================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(28, 32, 38));

        JPanel control = new JPanel(new FlowLayout(FlowLayout.LEFT));
        control.setBackground(new Color(28, 32, 38));

        JLabel lblFrom = label("From:");
        JTextField tfFrom = textField(LocalDate.now().minusWeeks(4).toString());

        JLabel lblTo = label("To:");
        JTextField tfTo = textField(LocalDate.now().toString());

        JButton btnLoad = UIUtils.createButton("Load Attendance");

        control.add(lblFrom);
        control.add(tfFrom);
        control.add(lblTo);
        control.add(tfTo);
        control.add(btnLoad);

        topPanel.add(control, BorderLayout.NORTH);

        // ============ ATTENDANCE TABLE ============
        DefaultTableModel model = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        model.addColumn("Date");
        for (int i = 1; i <= 8; i++) model.addColumn("P" + i);

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setBackground(new Color(55, 60, 70));
        table.setForeground(Color.WHITE);
        table.setDefaultRenderer(Object.class, new AttendanceTableRenderer());

        topPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        splitPane.setTopComponent(topPanel);

        // ============ SUMMARY BAR ============
        JPanel summary = new JPanel(new BorderLayout());
        summary.setBackground(new Color(28, 32, 38));

        JLabel sumLabel = label("Attendance Summary");
        summary.add(sumLabel, BorderLayout.NORTH);

        JPanel chartHolder = new JPanel(new BorderLayout());
        chartHolder.setBackground(new Color(40, 45, 55));

        summary.add(chartHolder, BorderLayout.CENTER);
        splitPane.setBottomComponent(summary);

        // ======= Load Button Action =======
        btnLoad.addActionListener(ev -> loadAttendance(tfFrom, tfTo, model, chartHolder));

        SwingUtilities.invokeLater(btnLoad::doClick);
    }

    // ================= PROFILE PANEL HELPER =================
    private JPanel profileItem(String htmlText) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(42, 48, 56));

        JLabel l = new JLabel("<html><b style='color:white;'>" + htmlText + "</b></html>",
                SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        p.add(l);
        return p;
    }

    // ================= SIMPLE LABEL & TEXTFIELD =================
    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private JTextField textField(String val) {
        JTextField tf = new JTextField(val, 10);
        tf.setBackground(new Color(60, 66, 74));
        tf.setForeground(Color.WHITE);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return tf;
    }

    // ================= LOAD ATTENDANCE =================
    private void loadAttendance(JTextField tfFrom, JTextField tfTo,
                                DefaultTableModel model, JPanel chartHolder) {

        model.setRowCount(0);

        LocalDate from, to;
        try {
            from = LocalDate.parse(tfFrom.getText().trim());
            to = LocalDate.parse(tfTo.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid dates");
            return;
        }

        if (to.isBefore(from)) {
            JOptionPane.showMessageDialog(this, "To date must be after From date.");
            return;
        }

        // Build date list (exclude Sunday)
        List<LocalDate> dates = new ArrayList<>();
        LocalDate d = from;
        while (!d.isAfter(to)) {
            if (d.getDayOfWeek() != java.time.DayOfWeek.SUNDAY)
                dates.add(d);
            d = d.plusDays(1);
        }

        Map<LocalDate, String[]> map = new LinkedHashMap<>();
        for (LocalDate dt : dates)
            map.put(dt, new String[]{"", "", "", "", "", "", "", ""});

        Map<String, Integer> sum = new LinkedHashMap<>();
        sum.put("P", 0);
        sum.put("A", 0);
        sum.put("ML", 0);
        sum.put("OD", 0);

        // Load DB
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT TRUNC(att_date), period_no, status " +
                             "FROM attendance " +
                             "WHERE student_id = ? AND TRUNC(att_date) BETWEEN TRUNC(?) AND TRUNC(?)")) {

            ps.setInt(1, studentId);
            ps.setDate(2, java.sql.Date.valueOf(from));
            ps.setDate(3, java.sql.Date.valueOf(to));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate dt = rs.getDate(1).toLocalDate();
                int p = rs.getInt(2);
                String st = rs.getString(3);

                if (map.containsKey(dt) && p >= 1 && p <= 8) {
                    map.get(dt)[p - 1] = st;

                    if (sum.containsKey(st))
                        sum.put(st, sum.get(st) + 1);
                }
            }

            c.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Add rows
        int total = 0, present = 0;

        for (LocalDate dt : map.keySet()) {
            Object[] row = new Object[9];
            row[0] = dt.toString();
            String[] arr = map.get(dt);

            for (int i = 0; i < 8; i++) {
                row[i + 1] = arr[i];

                if (!arr[i].isEmpty()) {
                    total++;
                    if ("P".equals(arr[i])) present++;
                }
            }
            model.addRow(row);
        }

        chartHolder.removeAll();
        chartHolder.add(new BarChartPanel(sum), BorderLayout.CENTER);
        chartHolder.revalidate();
        chartHolder.repaint();

        if (total > 0) {
            double percent = present * 100.0 / total;
            JOptionPane.showMessageDialog(this,
                    String.format("Attendance: %d/%d (%.2f%%)", present, total, percent));
        }
    }

    // ================= FETCH PROFILE =================
    private void fetchProfile() {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT s.student_id, s.full_name, s.roll_no, s.dept, s.section, " +
                             "s.dob, s.mobile, s.email, s.address " +
                             "FROM students s JOIN users u ON s.user_id = u.user_id " +
                             "WHERE u.user_id = ?")) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                studentId = rs.getInt("student_id");
                fullName = rs.getString("full_name");
                roll = rs.getString("roll_no");
                dept = rs.getString("dept");
                section = rs.getString("section");

                dob = Optional.ofNullable(rs.getDate("dob"))
                        .map(Date::toString).orElse("");

                mobile = rs.getString("mobile");
                email = rs.getString("email");
                address = rs.getString("address");
            }

            c.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // ================= SHOW PROFILE POPUP =================
    private void showMyProfile() {
        String info = String.format(
                "Name: %s%nRoll No: %s%nDepartment: %s%nSection: %s%nDOB: %s%n" +
                        "Mobile: %s%nEmail: %s%nAddress: %s",
                fullName, roll, dept, section, dob, mobile, email, address
        );

        JOptionPane.showMessageDialog(this, info, "My Profile", JOptionPane.INFORMATION_MESSAGE);
    }
}
