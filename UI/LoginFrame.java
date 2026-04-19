package attendance.ui;

import attendance.db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {
    private final JTextField tfUser;
    private final JPasswordField pf;
    private static final String LOGO_PATH = "C:\\Users\\Dattatreya\\OneDrive\\Pictures\\logo.jpg";

    public LoginFrame() {
        setTitle("MEPCO - Login");
        setSize(520,370);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtils.BG);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JLabel logo = UIUtils.label(" ");
        try {
            ImageIcon ic = new ImageIcon(LOGO_PATH);
            Image img = ic.getImage().getScaledInstance(64,64, Image.SCALE_SMOOTH);
            logo = new JLabel(new ImageIcon(img));
        } catch (Exception ignored) { logo = UIUtils.title("MEPCO"); }
        top.add(logo, BorderLayout.WEST);
        JLabel title = UIUtils.title("   MEPCO Attendance ERP");
        title.setForeground(UIUtils.TEXT);
        top.add(title, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // center: login form
        JPanel center = new JPanel();
        center.setBackground(UIUtils.BG);
        center.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        center.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8,8,8,8);
        g.gridx=0; g.gridy=0; g.anchor = GridBagConstraints.EAST;
        center.add(UIUtils.label("Username:"), g);
        g.gridx=1; g.anchor = GridBagConstraints.WEST;
        tfUser = UIUtils.textField(18);
        center.add(tfUser, g);
        g.gridx=0; g.gridy++;
        g.anchor = GridBagConstraints.EAST;
        center.add(UIUtils.label("Password:"), g);
        g.gridx=1; g.anchor = GridBagConstraints.WEST;
        pf = UIUtils.passwordField(18);
        center.add(pf, g);
        g.gridx=0; g.gridy++; g.gridwidth=2; g.anchor = GridBagConstraints.CENTER;
        JButton btn = UIUtils.iconButton("Login", UIUtils.iconLogin1(18));
        btn.setBackground(UIUtils.ACCENT);
        center.add(btn, g);

        add(center, BorderLayout.CENTER);

        // about/footer
        JPanel foot = new JPanel(new BorderLayout());
        foot.setOpaque(false);
        JLabel about = new JLabel("<html><span style='color:#bbb'>About: Student Attendance Management System</span></html>");
        foot.add(about, BorderLayout.WEST);
        add(foot, BorderLayout.SOUTH);

        btn.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String username = tfUser.getText().trim();
        String password = new String(pf.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password.");
            return;
        }
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT user_id, role FROM users WHERE username = ? AND password = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String role = rs.getString("role");
                c.commit();
                openDashboard(userId, role);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void openDashboard(int userId, String role) {
        SwingUtilities.invokeLater(() -> {
            if (role == null) { JOptionPane.showMessageDialog(this, "Unknown role."); return; }
            switch (role.toUpperCase()) {
                case "ADMIN":
                    new AdminDashboard(userId).setVisible(true);
                    break;
                case "FACULTY":
                    new FacultyDashboard(userId).setVisible(true);
                    break;
                case "STUDENT":
                    new StudentDashboard(userId).setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unknown role: " + role);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

