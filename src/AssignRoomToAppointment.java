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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AssignRoomToAppointment extends JFrame {

    private int doctorId;
    private JTable table;
    private JTextField txtAppointmentId, txtNewRoomId, txtStartTime;
    private JButton btnAssign;

    public AssignRoomToAppointment(int doctorId) {
        this.doctorId = doctorId;
        setTitle("Assign Room to Appointment - Doctor ID: " + doctorId);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(580, 200));
        mainPanel.add(scrollPane);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        txtAppointmentId = new JTextField(10);
        txtNewRoomId = new JTextField(10);
        txtStartTime = new JTextField(15);
        btnAssign = new JButton("Assign Room");

        panel.add(new JLabel("Appointment ID:"));
        panel.add(txtAppointmentId);
        panel.add(new JLabel("New Room ID:"));
        panel.add(txtNewRoomId);
        panel.add(new JLabel("Start Time (YYYY-MM-DD HH:MM):"));
        panel.add(txtStartTime);
        panel.add(btnAssign);

        mainPanel.add(panel);

        add(mainPanel);

        pack();
        setLocationRelativeTo(null);

        btnAssign.addActionListener(e -> assignRoom());

        showAppointments();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    private void assignRoom() {
        Connection conn = null;
        try {
            int appointmentId = Integer.parseInt(txtAppointmentId.getText().trim());
            int newRoomId = Integer.parseInt(txtNewRoomId.getText().trim());
            LocalDateTime startTime = LocalDateTime.parse(txtStartTime.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime endTime = startTime.plusMinutes(15);

            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String checkOverlapSql = "SELECT COUNT(*) FROM roomschedule "
                    + "WHERE roomId = ? AND ((rentStart < ? AND rentEnd > ?) OR (rentStart < ? AND rentEnd > ?))";
            try (PreparedStatement pstmtCheckOverlap = conn.prepareStatement(checkOverlapSql)) {
                pstmtCheckOverlap.setInt(1, newRoomId);
                pstmtCheckOverlap.setTimestamp(2, Timestamp.valueOf(endTime));
                pstmtCheckOverlap.setTimestamp(3, Timestamp.valueOf(startTime));
                pstmtCheckOverlap.setTimestamp(4, Timestamp.valueOf(endTime));
                pstmtCheckOverlap.setTimestamp(5, Timestamp.valueOf(startTime));
                ResultSet rsOverlap = pstmtCheckOverlap.executeQuery();
                if (rsOverlap.next() && rsOverlap.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "This room is already booked for the selected time.");
                    conn.rollback();
                    return;
                }
            }

            String updateAppointmentSql = "UPDATE appointment SET roomId = ? WHERE appointmentId = ? AND doctorId = ?";
            try (PreparedStatement pstmtUpdateAppointment = conn.prepareStatement(updateAppointmentSql)) {
                pstmtUpdateAppointment.setInt(1, newRoomId);
                pstmtUpdateAppointment.setInt(2, appointmentId);
                pstmtUpdateAppointment.setInt(3, doctorId);
                pstmtUpdateAppointment.executeUpdate();
            }

            String insertRoomScheduleSql = "INSERT INTO roomschedule (roomId, rentStart, rentEnd, doctorId) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmtInsertRoomSchedule = conn.prepareStatement(insertRoomScheduleSql)) {
                pstmtInsertRoomSchedule.setInt(1, newRoomId);
                pstmtInsertRoomSchedule.setTimestamp(2, Timestamp.valueOf(startTime));
                pstmtInsertRoomSchedule.setTimestamp(3, Timestamp.valueOf(endTime));
                pstmtInsertRoomSchedule.setInt(4, doctorId);
                pstmtInsertRoomSchedule.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Room assigned successfully.");
            showAppointments(); // Refresh the appointments table

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Appointment ID and Room ID.");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please enter the start time in 'YYYY-MM-DD HH:MM' format.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex2) {
                    ex2.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }




    private void showAppointments() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT appointmentId, appointmentStart, appointmentFinish, patientId, roomId FROM appointment WHERE doctorId = ?")) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            Object[][] data = resultSetToObjectArray(rs);
            String[] columnNames = {"Appointment ID", "Start Time", "End Time", "Patient ID", "Room ID"};
            setTableData(data, columnNames);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in fetching appointments: " + ex.getMessage());
        }
    }

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
