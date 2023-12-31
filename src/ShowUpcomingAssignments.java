import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class ShowUpcomingAssignments extends JFrame {

    private int nurseId;
    private JTable table;

    public ShowUpcomingAssignments(int nurseId) {
        this.nurseId = nurseId;
        setTitle("Upcoming Assignments - Nurse ID: " + nurseId);
        setSize(600, 400);
        setLayout(new BorderLayout());

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        showAssignments();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void showAssignments() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT a.appointmentStart, r.roomType FROM appointment a " +
                             "JOIN room r ON a.roomId = r.roomId " +
                             "WHERE r.nurseId = ?")) {

            pstmt.setInt(1, nurseId);
            ResultSet rs = pstmt.executeQuery();

            Object[][] data = resultSetToObjectArray(rs);
            String[] columnNames = {"Appointment Start", "Room Type"};
            setTableData(data, columnNames);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in fetching assignments: " + ex.getMessage());
        }
    }

    // Helper method to convert ResultSet to Object[][]
    private Object[][] resultSetToObjectArray(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Object[]> records = new ArrayList<>();
        while (rs.next()) {
            Object[] record = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                record[i] = rs.getObject(i + 1);
            }
            records.add(record);
        }
        return records.toArray(new Object[0][]);
    }

    private void setTableData(Object[][] data, String[] columnNames) {
        table.setModel(new DefaultTableModel(data, columnNames));
    }
}
