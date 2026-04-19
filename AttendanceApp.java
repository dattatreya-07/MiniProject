package attendance;
import javax.swing.SwingUtilities;
import attendance.ui.LoginFrame;
import attendance.db.DBConnection;
import javax.swing.UIManager;

/**
 * Main launcher for the MEPCO Attendance ERP (dark theme).
 * Java 11 compatible.
 */
public class AttendanceApp {
    public static void main(String[] args) {
        // Try to keep default LAF dark-like where possible (no external LAF required)
        try {
            UIManager.put("control", new java.awt.Color(44,47,51));
            UIManager.put("info", new java.awt.Color(44,47,51));
            UIManager.put("nimbusBase", new java.awt.Color(18, 18, 18));
            System.out.println("UIUtils loaded");
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LoginFrame f = new LoginFrame();
            f.setVisible(true);
        });
    }
}

