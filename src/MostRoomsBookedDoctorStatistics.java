import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MostRoomsBookedDoctorStatistics extends JFrame {

    private JTable tableStatistics;

    public MostRoomsBookedDoctorStatistics() {
        setTitle("Most Rooms Booked by Doctors Statistics");
        setSize(700, 350);
        setLayout(new BorderLayout());

        tableStatistics = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableStatistics);
        add(scrollPane, BorderLayout.CENTER);

        displayMostRoomsBookedDoctors();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void displayMostRoomsBookedDoctors() {
        String sql = "SELECT d.departmentName, doc.doctorName, COUNT(rs.roomId) AS roomBookingCount " +
                "FROM roomschedule rs " +
                "JOIN doctor doc ON rs.doctorId = doc.doctorId " +
                "JOIN department d ON doc.departmentId = d.departmentId " +
                "GROUP BY d.departmentName, doc.doctorName " +
                "ORDER BY d.departmentName, roomBookingCount DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(new String[]{"Department", "Doctor", "Room Bookings"}, 0);
            String lastDept = "";
            while (rs.next()) {
                String deptName = rs.getString("departmentName");
                // Only add the top result for each department
                if (!lastDept.equals(deptName)) {
                    String doctorName = rs.getString("doctorName");
                    int roomBookings = rs.getInt("roomBookingCount");
                    model.addRow(new Object[]{deptName, doctorName, roomBookings});
                    lastDept = deptName;
                }
            }
            tableStatistics.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new MostRoomsBookedDoctorStatistics();
    }
}
