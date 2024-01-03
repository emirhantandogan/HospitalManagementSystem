import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ShowAppointmentsPatient extends JFrame {

    private int patientId;
    private JTextField txtDays;
    private JButton btnShow;
    private JTable tableAppointments;

    public ShowAppointmentsPatient(int patientId) {
        this.patientId = patientId;
        setTitle("Show Upcoming Appointments");
        setSize(600, 300);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        txtDays = new JTextField(5);
        btnShow = new JButton("Show Appointments");

        inputPanel.add(new JLabel("Show my upcoming appointments in next (days):"));
        inputPanel.add(txtDays);
        inputPanel.add(btnShow);

        tableAppointments = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableAppointments);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnShow.addActionListener(this::showAppointmentsAction);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void showAppointmentsAction(ActionEvent event) {
        String daysStr = txtDays.getText();
        try {
            int days = Integer.parseInt(daysStr);
            fetchAppointments(days);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of days.");
        }
    }

    private void fetchAppointments(int days) {
        ZonedDateTime nowZoned = ZonedDateTime.now(ZoneId.of("GMT+3"));
        LocalDateTime now = nowZoned.toLocalDateTime();
        ZonedDateTime endDateTimeZoned = nowZoned.plusDays(days).withHour(23).withMinute(59).withSecond(59);
        LocalDateTime endDateTime = endDateTimeZoned.toLocalDateTime();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT appointmentId, appointmentStart, appointmentFinish, doctorId FROM appointment " +
                             "WHERE patientId = ? AND appointmentStart > ? AND appointmentStart <= ? " +
                             "ORDER BY appointmentStart")) {

            pstmt.setInt(1, patientId);
            pstmt.setTimestamp(2, Timestamp.valueOf(now));
            pstmt.setTimestamp(3, Timestamp.valueOf(endDateTime));
            ResultSet rs = pstmt.executeQuery();

            String[] columnNames = {"Appointment ID", "Start Time", "End Time", "Doctor ID"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
                int appointmentId = rs.getInt("appointmentId");
                Timestamp start = rs.getTimestamp("appointmentStart");
                Timestamp finish = rs.getTimestamp("appointmentFinish");
                int doctorId = rs.getInt("doctorId");

                model.addRow(new Object[]{appointmentId, start, finish, doctorId});
            }
            tableAppointments.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching appointments: " + e.getMessage());
        }
    }
}