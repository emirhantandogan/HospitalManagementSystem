import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class NurseAppointmentRatioStatistics extends JFrame {

    private JTable tableStatistics;

    public NurseAppointmentRatioStatistics() {
        setTitle("Nurse to Booked Room Ratios");
        setSize(800, 400);
        setLayout(new BorderLayout());

        tableStatistics = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableStatistics);
        add(scrollPane, BorderLayout.CENTER);

        calculateAndDisplayStatistics();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void calculateAndDisplayStatistics() {
        try (Connection conn = DBConnection.getConnection()) {
            // Query to fetch the number of distinct nurses and non-'doctor room' appointments per department
            String sql = "SELECT d.departmentName, " +
                    "COUNT(DISTINCT r.nurseId) AS distinctNurses, " +
                    "SUM(CASE WHEN r.roomType != 'doctor room' THEN 1 ELSE 0 END) AS nonDoctorRoomAppointments " +
                    "FROM appointment a " +
                    "JOIN doctor doc ON a.doctorId = doc.doctorId " +
                    "JOIN department d ON doc.departmentId = d.departmentId " +
                    "JOIN room r ON a.roomId = r.roomId " +
                    "GROUP BY d.departmentName";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            Map<String, Integer[]> departmentStats = new HashMap<>();
            while (rs.next()) {
                String departmentName = rs.getString("departmentName");
                int distinctNurses = rs.getInt("distinctNurses");
                int nonDoctorRoomAppointments = rs.getInt("nonDoctorRoomAppointments");
                departmentStats.put(departmentName, new Integer[]{distinctNurses, nonDoctorRoomAppointments});
            }

            displayStatistics(departmentStats);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error calculating statistics: " + ex.getMessage());
        }
    }

    private void displayStatistics(Map<String, Integer[]> departmentStats) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Department", "Distinct Nurses", "Booked Rooms", "Nurse/Appointment Ratio (%)"}, 0);
        departmentStats.forEach((department, stats) -> {
            int distinctNurses = stats[0];
            int nonDoctorRoomAppointments = stats[1];
            double ratio = nonDoctorRoomAppointments > 0 ? (double) distinctNurses / nonDoctorRoomAppointments * 100 : 0.0;
            model.addRow(new Object[]{department, distinctNurses, nonDoctorRoomAppointments, String.format("%.2f", ratio)});
        });
        tableStatistics.setModel(model);
    }

    public static void main(String[] args) {
        new NurseAppointmentRatioStatistics();
    }
}
