package attendance.ui;

import attendance.db.DBConnection;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class FacultyDashboard extends JFrame {

    private final int userId;

    private int facultyId = -1;
    private String facultyName = "";
    private String facultyDept = "";
    private int facultyYear = 0;
    private String facultySection = "";

    private JComboBox<String> cbDate;
    private JComboBox<String> cbSubject;
    private JComboBox<Integer> cbPeriod;

    private JTable studentsTable;
    private DefaultTableModel studentsModel;

    private final String[] STATUS_CYCLE = {"", "P", "A", "ML", "OD"};

    public FacultyDashboard(int userId) {
        this.userId = userId;

        setTitle("Faculty Dashboard - MEPCO ERP");
        setSize(1200, 720);
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

        JLabel lblTitle = new JLabel("Faculty Dashboard");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(lblTitle, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(new Color(35, 40, 48));

        JButton btnProfile = UIUtils.createButton("My Profile");
        JButton btnLogout = UIUtils.createButton("Logout");

        right.add(btnProfile);
        right.add(btnLogout);

        header.add(right, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // logout action
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        // -------------- FETCH FACULTY INFO ---------------
        fetchFacultyInfo();

        btnProfile.addActionListener(e -> showProfile());

        // ---------------- FILTER BAR ----------------
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        filter.setBackground(new Color(28, 32, 38));

        // DATE
        cbDate = new JComboBox<>();
        for (int i = 0; i < 14; i++)
            cbDate.addItem(LocalDate.now().minusDays(i).toString());

        // SUBJECT
        cbSubject = new JComboBox<>();
        loadSubjects();

        // PERIOD
        cbPeriod = new JComboBox<>();
        for (int p = 1; p <= 8; p++)
            cbPeriod.addItem(p);

        filter.add(label("Date:"));
        filter.add(cbDate);

        filter.add(label("Subject:"));
        filter.add(cbSubject);

        filter.add(label("Period:"));
        filter.add(cbPeriod);

        JButton btnLoad = UIUtils.createButton("Load Students");
        filter.add(btnLoad);

        add(filter, BorderLayout.WEST);

        // ---------------- STUDENTS TABLE ----------------
        studentsModel = new DefaultTableModel(
                new Object[]{"SID", "Roll No", "Name", "Status"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        studentsTable = new JTable(studentsModel);
        studentsTable.setRowHeight(30);

        studentsTable.setBackground(new Color(55, 60, 70));
        studentsTable.setForeground(Color.WHITE);
        studentsTable.getColumnModel().getColumn(0).setMaxWidth(60);
        studentsTable.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());

        studentsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = studentsTable.rowAtPoint(e.getPoint());
                int col = studentsTable.columnAtPoint(e.getPoint());

                if (col == 3) {
                    int modelRow = studentsTable.convertRowIndexToModel(row);
                    String cur = String.valueOf(studentsModel.getValueAt(modelRow, 3));
                    studentsModel.setValueAt(nextStatus(cur), modelRow, 3);
                }
            }
        });

        add(new JScrollPane(studentsTable), BorderLayout.CENTER);

        // ---------------- SAVE BUTTON ----------------
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(new Color(28, 32, 38));

        JButton btnSave = UIUtils.createButton("Save Attendance");
        bottom.add(btnSave);

        add(bottom, BorderLayout.SOUTH);

        btnLoad.addActionListener(e -> loadStudents());
        btnSave.addActionListener(e -> saveAttendance());
    }

    private JLabel label(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(Color.WHITE);
        return l;
    }

    // ---------------- FETCH FACULTY PROFILE ----------------
    private void fetchFacultyInfo() {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT faculty_id, faculty_name, dept, year_handling, section " +
                             "FROM faculties WHERE user_id = ?"
             )) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                facultyId = rs.getInt("faculty_id");
                facultyName = rs.getString("faculty_name");
                facultyDept = rs.getString("dept");
                facultyYear = rs.getInt("year_handling");
                facultySection = rs.getString("section");
            }

            c.commit();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void showProfile() {
        JOptionPane.showMessageDialog(this,
                "FACULTY PROFILE\n\n" +
                        "Faculty ID: " + facultyId + "\n" +
                        "Name: " + facultyName + "\n" +
                        "Department: " + facultyDept + "\n" +
                        "Year Handling: " + facultyYear + "\n" +
                        "Section: " + facultySection,
                "My Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------------- LOAD SUBJECTS ----------------
    private void loadSubjects() {
        cbSubject.removeAllItems();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT subject_code FROM subjects")) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) cbSubject.addItem(rs.getString(1));

        } catch (Exception ex) {
            cbSubject.addItem("CS301");
        }
    }

    // ---------------- LOAD STUDENTS ----------------
    private void loadStudents() {
        studentsModel.setRowCount(0);

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT student_id, roll_no, full_name FROM students " +
                             "WHERE dept = ? AND year = ? AND section = ? ORDER BY roll_no"
             )) {

            ps.setString(1, facultyDept);
            ps.setInt(2, facultyYear);
            ps.setString(3, facultySection);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studentsModel.addRow(new Object[]{
                        rs.getInt("student_id"),
                        rs.getString("roll_no"),
                        rs.getString("full_name"),
                        ""
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------------- SAVE ATTENDANCE ----------------
    private void saveAttendance() {

        LocalDate date = LocalDate.parse((String) cbDate.getSelectedItem());
        String subject = (String) cbSubject.getSelectedItem();
        int period = (int) cbPeriod.getSelectedItem();

        try (Connection c = DBConnection.getConnection()) {

            PreparedStatement psCheck = c.prepareStatement(
                    "SELECT COUNT(*) FROM attendance WHERE student_id = ? " +
                            "AND TRUNC(att_date)=TRUNC(?) AND subject_code=? AND period_no=?"
            );

            PreparedStatement psInsert = c.prepareStatement(
                    "INSERT INTO attendance(student_id, att_date, subject_code, period_no, status) " +
                            "VALUES(?,?,?,?,?)"
            );

            PreparedStatement psUpdate = c.prepareStatement(
                    "UPDATE attendance SET status=? WHERE student_id=? " +
                            "AND TRUNC(att_date)=TRUNC(?) AND subject_code=? AND period_no=?"
            );

            for (int i = 0; i < studentsModel.getRowCount(); i++) {
                int sid = (int) studentsModel.getValueAt(i, 0);
                String st = (String) studentsModel.getValueAt(i, 3);

                if (st == null || st.trim().isEmpty()) continue;

                psCheck.setInt(1, sid);
                psCheck.setDate(2, java.sql.Date.valueOf(date));
                psCheck.setString(3, subject);
                psCheck.setInt(4, period);

                ResultSet rs = psCheck.executeQuery();
                rs.next();

                if (rs.getInt(1) > 0) {
                    psUpdate.setString(1, st);
                    psUpdate.setInt(2, sid);
                    psUpdate.setDate(3, java.sql.Date.valueOf(date));
                    psUpdate.setString(4, subject);
                    psUpdate.setInt(5, period);
                    psUpdate.executeUpdate();
                } else {
                    psInsert.setInt(1, sid);
                    psInsert.setDate(2, java.sql.Date.valueOf(date));
                    psInsert.setString(3, subject);
                    psInsert.setInt(4, period);
                    psInsert.setString(5, st);
                    psInsert.executeUpdate();
                }
            }

            c.commit();
            JOptionPane.showMessageDialog(this, "Attendance Saved.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------------- STATUS CYCLER ----------------
    private String nextStatus(String cur) {
        String val = (cur == null ? "" : cur.trim());

        for (int i = 0; i < STATUS_CYCLE.length; i++) {
            if (STATUS_CYCLE[i].equalsIgnoreCase(val)) {
                return STATUS_CYCLE[(i + 1) % STATUS_CYCLE.length];
            }
        }
        return "P";
    }

    // ---------------- STATUS RENDERER ----------------
    static class StatusRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            String v = value == null ? "" : value.toString().trim().toUpperCase();

            switch (v) {
                case "P": c.setBackground(new Color(0, 150, 0)); break;
                case "A": c.setBackground(new Color(190, 30, 30)); break;
                case "ML": c.setBackground(Color.ORANGE); break;
                case "OD": c.setBackground(new Color(0, 110, 220)); break;
                default: c.setBackground(new Color(70, 75, 85)); break;
            }

            c.setForeground(Color.WHITE);
            setHorizontalAlignment(CENTER);
            return c;
        }
    }
}
