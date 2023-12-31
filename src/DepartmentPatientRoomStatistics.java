import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;


public class DepartmentPatientRoomStatistics extends JFrame {

    private JTable tableStatistics;

    public DepartmentPatientRoomStatistics() {
        setTitle("Department Patient Room Statistics");
        setSize(700, 400);
        setLayout(new BorderLayout());

        tableStatistics = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableStatistics);
        add(scrollPane, BorderLayout.CENTER);

        calculateAndDisplayStatistics();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void calculateAndDisplayStatistics() {
        Map<String, Integer[]> departmentStats = new HashMap<>();

        try (Connection conn = DBConnection.getConnection()) {
            // Fetch total appointments and those needing a room for each department
            String sql = "SELECT d.departmentName, " +
                    "COUNT(*) AS totalAppointments, " +
                    "SUM(CASE WHEN a.roomId != doc.roomId THEN 1 ELSE 0 END) AS roomsNeeded " +
                    "FROM appointment a " +
                    "JOIN doctor doc ON a.doctorId = doc.doctorId " +
                    "JOIN department d ON doc.departmentId = d.departmentId " +
                    "GROUP BY d.departmentName";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String department = rs.getString("departmentName");
                int totalAppointments = rs.getInt("totalAppointments");
                int roomsNeeded = rs.getInt("roomsNeeded");

                departmentStats.put(department, new Integer[]{roomsNeeded, totalAppointments});
            }

            displayStatistics(departmentStats);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error calculating statistics: " + e.getMessage());
        }
    }

    private void displayStatistics(Map<String, Integer[]> statistics) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Department", "Rooms Needed", "Total Appointments", "Ratio (%)"}, 0);
        statistics.forEach((department, counts) -> {
            int roomsNeeded = counts[0];
            int totalAppointments = counts[1];
            double ratio = (totalAppointments > 0) ? ((double) roomsNeeded / totalAppointments) * 100 : 0;
            model.addRow(new Object[]{department, roomsNeeded, totalAppointments, String.format("%.2f", ratio)});
        });
        tableStatistics.setModel(model);
    }

    public static void main(String[] args) {
        new DepartmentPatientRoomStatistics();
    }
}

