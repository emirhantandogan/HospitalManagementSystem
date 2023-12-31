import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.swing.table.DefaultTableModel;

public class ShowAppointmentsDoctor extends JFrame {

    private int doctorId;
    private JTextField txtDays;
    private JButton btnShow;
    private JTable tableAppointments;

    public ShowAppointmentsDoctor(int doctorId) {
        this.doctorId = doctorId;
        setTitle("Show Upcoming Appointments");
        setSize(600, 300);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        txtDays = new JTextField(5);
        btnShow = new JButton("Show Appointments");

        inputPanel.add(new JLabel("Show my upcoming appointments for the next (days):"));
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
        // Use the current date and time in the GMT+3 timezone
        ZonedDateTime nowZoned = ZonedDateTime.now(ZoneId.of("GMT+3"));
        LocalDateTime now = nowZoned.toLocalDateTime(); // Convert to a LocalDateTime object
        ZonedDateTime endDateTimeZoned = nowZoned.plusDays(days).withHour(23).withMinute(59).withSecond(59);
        LocalDateTime endDateTime = endDateTimeZoned.toLocalDateTime(); // Convert to a LocalDateTime object

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT appointmentId, appointmentStart, appointmentFinish, patientId FROM appointment " +
                             "WHERE doctorId = ? AND appointmentStart > ? AND appointmentStart <= ? " +
                             "ORDER BY appointmentStart")) {

            pstmt.setInt(1, doctorId);
            pstmt.setTimestamp(2, Timestamp.valueOf(now)); // Current time
            pstmt.setTimestamp(3, Timestamp.valueOf(endDateTime)); // End of the period
            ResultSet rs = pstmt.executeQuery();

            String[] columnNames = {"Appointment ID", "Start Time", "End Time", "Patient ID"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
                int appointmentId = rs.getInt("appointmentId");
                Timestamp start = rs.getTimestamp("appointmentStart");
                Timestamp finish = rs.getTimestamp("appointmentFinish");
                int patientId = rs.getInt("patientId");

                model.addRow(new Object[]{appointmentId, start, finish, patientId});
            }
            tableAppointments.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching appointments: " + e.getMessage());
        }
    }



}
