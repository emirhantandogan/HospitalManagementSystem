import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Vector;

public class ShowAvailableRooms extends JFrame {

    private JTable table;

    public ShowAvailableRooms() {
        setTitle("Available Rooms");
        setSize(500, 300);
        setLayout(new BorderLayout());

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadAvailableRooms();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loadAvailableRooms() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT+3"));
        Timestamp currentTime = Timestamp.valueOf(now.toLocalDateTime());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT r.roomId, r.roomType FROM room r " +
                             "LEFT JOIN roomschedule rs ON r.roomId = rs.roomId " +
                             "AND rs.rentStart <= ? AND rs.rentEnd >= ? " +
                             "WHERE r.availability = 'Available' AND rs.roomId IS NULL")) {

            pstmt.setTimestamp(1, currentTime);
            pstmt.setTimestamp(2, currentTime);
            ResultSet rs = pstmt.executeQuery();

            table.setModel(buildTableModel(rs));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in fetching available rooms: " + ex.getMessage());
        }
    }

    private DefaultTableModel buildTableModel(ResultSet rs) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Column names
        Vector<String> columnNames = new Vector<>();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // Data of the table
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }

    public static void main(String[] args) {
        new ShowAvailableRooms();
    }
}
