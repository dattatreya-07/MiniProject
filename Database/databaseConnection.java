package attendance.db;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class DBConnection {

    public static Connection getConnection() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe",
                "System",
                "System123"
        );
    }

    // Convert ResultSet → JTable model
    public static DefaultTableModel buildTableModel(ResultSet rs) throws Exception {
        ResultSetMetaData meta = rs.getMetaData();
        Vector<String> col = new Vector<>();
        int columnCount = meta.getColumnCount();

        for (int i = 1; i <= columnCount; i++)
            col.add(meta.getColumnName(i));

        Vector<Vector<Object>> data = new Vector<>();

        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= columnCount; i++)
                row.add(rs.getObject(i));
            data.add(row);
        }

        return new DefaultTableModel(data, col);
    }
}
