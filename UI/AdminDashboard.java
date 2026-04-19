package attendance.ui;

import attendance.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    private final int adminUserId;

    
    private DefaultTableModel studentsModel;
    private DefaultTableModel facultiesModel;
    private DefaultTableModel timetableModel;

    private static final String LOGO_PATH = "C:\\Users\\Dattatreya\\OneDrive\\Pictures\\logo.jpg";

    public AdminDashboard(int adminUserId) {
        this.adminUserId = adminUserId;

        setTitle("Admin Dashboard - MEPCO ERP");
        setSize(1280, 830);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UIUtils.BG);

        initUI();

       
        loadStudents();
        loadFaculties();
        loadTimetable();
    }
    

    private void initUI() {

        setLayout(new BorderLayout());

   
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.DARK2);
        header.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        // LOGO
        try {
            ImageIcon ic = new ImageIcon(LOGO_PATH);
            Image scaled = ic.getImage().getScaledInstance(58, 58, Image.SCALE_SMOOTH);
            header.add(new JLabel(new ImageIcon(scaled)), BorderLayout.WEST);
        } catch (Exception e) {
            header.add(UIUtils.title("MEPCO ERP"), BorderLayout.WEST);
        }

        JLabel title = UIUtils.title("Administrator Dashboard");
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // LOGOUT BUTTON
        JButton btnLogout = UIUtils.createButton("Logout");
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        header.add(btnLogout, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ============================================================
        // SIDEBAR
        // ============================================================
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UIUtils.DARK2);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        sidebar.setPreferredSize(new Dimension(230, 700));

        JButton bHome = UIUtils.sideButton("Home");
        JButton bStudents = UIUtils.sideButton("Students");
        JButton bFac = UIUtils.sideButton("Faculty");
        JButton bAdd = UIUtils.sideButton("Add New");
        JButton bTT = UIUtils.sideButton("Timetable");

        sidebar.add(bHome);
        sidebar.add(bStudents);
        sidebar.add(bFac);
        sidebar.add(bAdd);
        sidebar.add(bTT);

        add(sidebar, BorderLayout.WEST);

        // ============================================================
        // MAIN CONTENT AREA (CARD LAYOUT)
        // ============================================================
        JPanel content = new JPanel(new CardLayout());
        content.setBackground(UIUtils.BG);

        // ---------- HOME ----------
        JPanel home = new JPanel(new BorderLayout());
        home.setBackground(UIUtils.BG);
        home.add(new JLabel("<html><h2 style='color:white;'>Welcome Administrator</h2></html>"), BorderLayout.NORTH);
        content.add(home, "HOME");

        // ---------- STUDENTS TABLE ----------
        studentsModel = new DefaultTableModel(new String[]{"ID", "Roll", "Name", "Dept", "Year", "Section"}, 0);
        JTable tbStudents = new JTable(studentsModel);
        UIUtils.styleTable(tbStudents);
        JScrollPane spStudents = new JScrollPane(tbStudents);

        JButton btnViewStu = UIUtils.createButton("View Student Profile");
        btnViewStu.addActionListener(e -> viewStudentProfile(tbStudents));

        JPanel panelStudents = new JPanel(new BorderLayout());
        panelStudents.setBackground(UIUtils.BG);
        panelStudents.add(spStudents, BorderLayout.CENTER);
        panelStudents.add(btnViewStu, BorderLayout.SOUTH);
        content.add(panelStudents, "STUDENTS");

        // ---------- FACULTIES TABLE ----------
        facultiesModel = new DefaultTableModel(new String[]{"ID", "Name", "Dept", "Year", "Section"}, 0);
        JTable tbFac = new JTable(facultiesModel);
        UIUtils.styleTable(tbFac);
        JScrollPane spFac = new JScrollPane(tbFac);

        JButton btnViewFac = UIUtils.createButton("View Faculty Profile");
        btnViewFac.addActionListener(e -> viewFacultyProfile(tbFac));

        JPanel panelFaculty = new JPanel(new BorderLayout());
        panelFaculty.setBackground(UIUtils.BG);
        panelFaculty.add(spFac, BorderLayout.CENTER);
        panelFaculty.add(btnViewFac, BorderLayout.SOUTH);
        content.add(panelFaculty, "FACULTY");

        // ---------- TIMETABLE TABLE ----------
        timetableModel = new DefaultTableModel(new String[]{
                "Day", "P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "Section"
        }, 0);
        JTable tbTT = new JTable(timetableModel);
        UIUtils.styleTable(tbTT);
        JScrollPane spTT = new JScrollPane(tbTT);

        JPanel panelTT = new JPanel(new BorderLayout());
        panelTT.setBackground(UIUtils.BG);
        panelTT.add(spTT, BorderLayout.CENTER);
        content.add(panelTT, "TIMETABLE");

        // ---------- ADD NEW DATA ----------
        JPanel addNew = makeAddPanel();
        content.add(addNew, "ADD");

        add(content, BorderLayout.CENTER);

        // ============================================================
        // SIDEBAR BUTTON LOGIC
        // ============================================================
        CardLayout cl = (CardLayout) content.getLayout();

        bHome.addActionListener(e -> cl.show(content, "HOME"));
        bStudents.addActionListener(e -> {
            loadStudents();
            cl.show(content, "STUDENTS");
        });
        bFac.addActionListener(e -> {
            loadFaculties();
            cl.show(content, "FACULTY");
        });
        bTT.addActionListener(e -> {
            loadTimetable();
            cl.show(content, "TIMETABLE");
        });
        bAdd.addActionListener(e -> cl.show(content, "ADD"));
    }

    // ============================================================
    //  ADD PANEL (STUDENT + FACULTY + TIMETABLE)
    // ============================================================
    private JPanel makeAddPanel() {

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UIUtils.DARK2);
        tabs.setForeground(Color.WHITE);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabs.add("Add Student", makeAddStudentPanel());
        tabs.add("Add Faculty", makeAddFacultyPanel());
        tabs.add("Add Timetable", makeAddTimetablePanel());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(UIUtils.BG);
        wrap.add(tabs);

        return wrap;
    }

    // ============================================================
    // 1) ADD STUDENT PANEL
    // ============================================================
    private JPanel makeAddStudentPanel() {

        JPanel p = UIUtils.panelBox();

        JTextField tfRoll = UIUtils.textField(15);
        JTextField tfName = UIUtils.textField(15);
        JTextField tfDept = UIUtils.textField(15);
        JTextField tfSection = UIUtils.textField(15);
        JTextField tfDOB = UIUtils.textField(15);
        JTextField tfPhone = UIUtils.textField(15);
        JTextField tfEmail = UIUtils.textField(15);
        JTextField tfAddress = UIUtils.textField(15);
        JTextField tfYear = UIUtils.textField(15);
        JTextField tfPassword = UIUtils.textField(15);

        UIUtils.addRow(p, "Roll No", tfRoll);
        UIUtils.addRow(p, "Full Name", tfName);
        UIUtils.addRow(p, "Department", tfDept);
        UIUtils.addRow(p, "Section", tfSection);
        UIUtils.addRow(p, "DOB (YYYY-MM-DD)", tfDOB);
        UIUtils.addRow(p, "Phone", tfPhone);
        UIUtils.addRow(p, "Email", tfEmail);
        UIUtils.addRow(p, "Address", tfAddress);
        UIUtils.addRow(p, "Year", tfYear);
        UIUtils.addRow(p, "Password", tfPassword);

        JButton btnAdd = UIUtils.createButton("Add Student");
        btnAdd.addActionListener(e -> addStudent(
                tfRoll.getText(), tfName.getText(), tfDept.getText(), tfSection.getText(),
                tfDOB.getText(), tfPhone.getText(), tfEmail.getText(), tfAddress.getText(),
                tfYear.getText(), tfPassword.getText()
        ));

        p.add(btnAdd);
        return p;
    }

    private void addStudent(String roll, String name, String dept, String section,
                            String dob, String phone, String email, String address,
                            String year, String password) {

        try (Connection c = DBConnection.getConnection()) {

            PreparedStatement psu = c.prepareStatement(
                    "INSERT INTO users (username, password, role) VALUES (?, ?, 'STUDENT')",
                    new String[]{"user_id"}
            );
            psu.setString(1, roll);
            psu.setString(2, password);
            psu.executeUpdate();

            ResultSet rs = psu.getGeneratedKeys();
            rs.next();
            int userId = rs.getInt(1);

            PreparedStatement pss = c.prepareStatement(
                    "INSERT INTO students ( roll_no, full_name, dept, section, dob, mobile, email, address, year) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
          //  pss.setInt(1, userId);
            pss.setString(1, roll);
            pss.setString(2, name);
            pss.setString(3, dept);
            pss.setString(4, section);
            pss.setDate(5, Date.valueOf(dob));
            pss.setString(6, phone);
            pss.setString(7, email);
            pss.setString(8, address);
            pss.setInt(9, Integer.parseInt(year));

            pss.executeUpdate();
            c.commit();

            JOptionPane.showMessageDialog(this, "Student Added Successfully!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================================================
    // 2) ADD FACULTY PANEL
    // ============================================================
    private JPanel makeAddFacultyPanel() {

        JPanel p = UIUtils.panelBox();

        JTextField tfName = UIUtils.textField(15);
        JTextField tfDept = UIUtils.textField(15);
        JTextField tfDOB = UIUtils.textField(15);
        JTextField tfPhone = UIUtils.textField(15);
        JTextField tfEmail = UIUtils.textField(15);
        JTextField tfAddress = UIUtils.textField(15);
        JTextField tfYear = UIUtils.textField(15);
        JTextField tfSection = UIUtils.textField(15);
        JTextField tfPassword = UIUtils.textField(15);

        UIUtils.addRow(p, "Name", tfName);
        UIUtils.addRow(p, "Department", tfDept);
        UIUtils.addRow(p, "DOB", tfDOB);
        UIUtils.addRow(p, "Phone", tfPhone);
        UIUtils.addRow(p, "Email", tfEmail);
        UIUtils.addRow(p, "Address", tfAddress);
        UIUtils.addRow(p, "Year Handling", tfYear);
        UIUtils.addRow(p, "Section", tfSection);
        UIUtils.addRow(p, "Password", tfPassword);

        JButton btnAdd = UIUtils.createButton("Add Faculty");
        btnAdd.addActionListener(e -> addFaculty(
                tfName.getText(), tfDept.getText(), tfDOB.getText(),
                tfPhone.getText(), tfEmail.getText(), tfAddress.getText(),
                tfYear.getText(), tfSection.getText(), tfPassword.getText()
        ));

        p.add(btnAdd);
        return p;
    }

    private void addFaculty(String name, String dept, String dob, String phone,
                            String email, String address, String year, String section, String password) {

        try (Connection c = DBConnection.getConnection()) {

            PreparedStatement psu = c.prepareStatement(
                    "INSERT INTO users (username, password, role) VALUES (?, ?, 'FACULTY')",
                    new String[]{"user_id"}
            );
            psu.setString(1, email);
            psu.setString(2, password);
            psu.executeUpdate();

            ResultSet rs = psu.getGeneratedKeys();
            rs.next();
            int userId = rs.getInt(1);

            PreparedStatement psf = c.prepareStatement(
                    "INSERT INTO faculties (user_id, faculty_name, dept, dob, mobile, email, address, year_handling, section) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            psf.setInt(1, userId);
            psf.setString(2, name);
            psf.setString(3, dept);
            psf.setDate(4, Date.valueOf(dob));
            psf.setString(5, phone);
            psf.setString(6, email);
            psf.setString(7, address);
            psf.setInt(8, Integer.parseInt(year));
            psf.setString(9, section);

            psf.executeUpdate();
            c.commit();

            JOptionPane.showMessageDialog(this, "Faculty Added Successfully!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================================================
    // 3) ADD TIMETABLE PANEL
    // ============================================================
    private JPanel makeAddTimetablePanel() {

        JPanel p = UIUtils.panelBox();

        JTextField tfDept = UIUtils.textField(15);
        JTextField tfSection = UIUtils.textField(15);
        JTextField tfDay = UIUtils.textField(15);
        JTextField tfPeriod = UIUtils.textField(15);
        JTextField tfSubject = UIUtils.textField(15);
        JTextField tfFaculty = UIUtils.textField(15);

        UIUtils.addRow(p, "Department", tfDept);
        UIUtils.addRow(p, "Section", tfSection);
        UIUtils.addRow(p, "Day", tfDay);
        UIUtils.addRow(p, "Period", tfPeriod);
        UIUtils.addRow(p, "Subject Code", tfSubject);
        UIUtils.addRow(p, "Faculty Email", tfFaculty);

        JButton btn = UIUtils.createButton("Add Timetable Entry");
        btn.addActionListener(e -> addTimetable(
                tfDept.getText(), tfSection.getText(), tfDay.getText(),
                tfPeriod.getText(), tfSubject.getText(), tfFaculty.getText()
        ));

        p.add(btn);
        return p;
    }

    private void addTimetable(String dept, String section, String day,
                              String period, String subject, String facultyEmail) {

        try (Connection c = DBConnection.getConnection()) {

            PreparedStatement psf = c.prepareStatement("SELECT faculty_id FROM faculties WHERE email=?");
            psf.setString(1, facultyEmail);
            ResultSet rs = psf.executeQuery();

            int fid = 0;
            if (rs.next()) fid = rs.getInt(1);
            if (fid == 0) {
                JOptionPane.showMessageDialog(this, "Faculty not found.");
                return;
            }

            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO timetable (dept, section, day_of_week, period_no, subject_code, faculty_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, dept);
            ps.setString(2, section);
            ps.setString(3, day);
            ps.setInt(4, Integer.parseInt(period));
            ps.setString(5, subject);
            ps.setInt(6, fid);

            ps.executeUpdate();
            c.commit();

            JOptionPane.showMessageDialog(this, "Timetable Entry Added!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================================================
    //   LOAD TABLE DATA
    // ============================================================
    private void loadStudents() {
        studentsModel.setRowCount(0);
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT student_id, roll_no, full_name, dept, year, section FROM students")) {

            while (rs.next()) {
                studentsModel.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getInt(5), rs.getString(6)
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadFaculties() {
        facultiesModel.setRowCount(0);
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT faculty_id, faculty_name, dept, year_handling, section FROM faculties")) {

            while (rs.next()) {
                facultiesModel.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getInt(4), rs.getString(5)
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadTimetable() {
        timetableModel.setRowCount(0);
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT day_of_week, period1, period2, period3, period4, period5, period6, period7, period8, section FROM timetable"
             )) {

            while (rs.next()) {
                Object[] row = new Object[10];
                for (int i = 0; i < row.length; i++) row[i] = rs.getObject(i + 1);
                timetableModel.addRow(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ============================================================
    //  VIEW PROFILE POPUPS
    // ============================================================
    private void viewStudentProfile(JTable t) {

        int r = t.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a student.");
            return;
        }

        int id = (Integer) studentsModel.getValueAt(r, 0);

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT roll_no, full_name, dept, section, dob, mobile, email, address FROM students WHERE student_id=?"
             )) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                JPanel p = new JPanel(new GridLayout(0, 1, 5, 5));
                p.setBackground(UIUtils.BG);

                p.add(UIUtils.label("Roll No: " + rs.getString(1)));
                p.add(UIUtils.label("Name: " + rs.getString(2)));
                p.add(UIUtils.label("Department: " + rs.getString(3)));
                p.add(UIUtils.label("Section: " + rs.getString(4)));
                p.add(UIUtils.label("DOB: " + rs.getString(5)));
                p.add(UIUtils.label("Mobile: " + rs.getString(6)));
                p.add(UIUtils.label("Email: " + rs.getString(7)));
                p.add(UIUtils.label("Address: " + rs.getString(8)));

                JOptionPane.showMessageDialog(this, p, "Student Profile", JOptionPane.PLAIN_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void viewFacultyProfile(JTable t) {

        int r = t.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a faculty.");
            return;
        }

        int id = (Integer) facultiesModel.getValueAt(r, 0);

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT faculty_name, dept, year_handling, section, dob, mobile, email, address FROM faculties WHERE faculty_id=?"
             )) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                JPanel p = new JPanel(new GridLayout(0, 1, 5, 5));
                p.setBackground(UIUtils.BG);

                p.add(UIUtils.label("Name: " + rs.getString(1)));
                p.add(UIUtils.label("Department: " + rs.getString(2)));
                p.add(UIUtils.label("Year Handling: " + rs.getInt(3)));
                p.add(UIUtils.label("Section: " + rs.getString(4)));
                p.add(UIUtils.label("DOB: " + rs.getString(5)));
                p.add(UIUtils.label("Mobile: " + rs.getString(6)));
                p.add(UIUtils.label("Email: " + rs.getString(7)));
                p.add(UIUtils.label("Address: " + rs.getString(8)));

                JOptionPane.showMessageDialog(this, p, "Faculty Profile", JOptionPane.PLAIN_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
