import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MostBookedRoomStatistics extends JFrame {

    private JTable tableStatistics;

    public MostBookedRoomStatistics() {
        setTitle("Most Booked Room Statistics");
        setSize(600, 300);
        setLayout(new BorderLayout());

        tableStatistics = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableStatistics);
        add(scrollPane, BorderLayout.CENTER);

        displayMostBookedRooms();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void displayMostBookedRooms() {
        String sql = "SELECT d.departmentName, r.roomId, COUNT(*) AS bookingCount " +
                "FROM roomschedule rs " +
                "JOIN doctor doc ON rs.doctorId = doc.doctorId " +
                "JOIN department d ON doc.departmentId = d.departmentId " +
                "JOIN room r ON rs.roomId = r.roomId " +
                "GROUP BY d.departmentName, r.roomId " +
                "ORDER BY d.departmentName, bookingCount DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(new String[]{"Department", "Room ID", "Bookings"}, 0);
            String lastDept = "";
            while (rs.next()) {
                String deptName = rs.getString("departmentName");
                // Only add the top result for each department
                if (!lastDept.equals(deptName)) {
                    int roomId = rs.getInt("roomId");
                    int bookings = rs.getInt("bookingCount");
                    model.addRow(new Object[]{deptName, roomId, bookings});
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
        new MostBookedRoomStatistics();
    }
}
