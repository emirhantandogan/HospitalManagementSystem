import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ShowPastAppointmentsPatient extends JFrame {

    private int patientId;
    private JTextField txtDays;
    private JButton btnShow;
    private JTable tableAppointments;

    public ShowPastAppointmentsPatient(int patientId) {
        this.patientId = patientId;
        setTitle("Show Past Appointments");
        setSize(600, 300);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        txtDays = new JTextField(5);
        btnShow = new JButton("Show Appointments");

        inputPanel.add(new JLabel("Show my past appointments from the last (days):"));
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
        ZonedDateTime startDateTimeZoned = ZonedDateTime.now(ZoneId.of("GMT+3")).minusDays(days).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startDateTime = startDateTimeZoned.toLocalDateTime();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT appointmentId, appointmentStart, appointmentFinish, doctorId FROM appointment " +
                             "WHERE patientId = ? AND appointmentStart >= ? AND appointmentStart < ? " +
                             "ORDER BY appointmentStart")) {

            pstmt.setInt(1, patientId);
            pstmt.setTimestamp(2, Timestamp.valueOf(startDateTime));
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("GMT+3"))));
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
