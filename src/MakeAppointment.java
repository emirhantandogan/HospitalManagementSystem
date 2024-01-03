import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MakeAppointment extends JFrame {

    private int patientId;
    private JTextField txtDoctorName, txtAppointmentStart;
    private JButton btnCheckAvailability, btnSubmit;
    private JTable appointmentsTable;

    public MakeAppointment(int patientId) {
        this.patientId = patientId;

        setTitle("Make Appointment");
        setSize(650, 400);
        getContentPane().setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        txtDoctorName = new JTextField(20);
        btnCheckAvailability = new JButton("Check Doctor's Unavailable Hours");

        topPanel.add(new JLabel("Doctor Name:"));
        topPanel.add(txtDoctorName);
        topPanel.add(btnCheckAvailability);

        getContentPane().add(topPanel, BorderLayout.NORTH);

        appointmentsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        txtAppointmentStart = new JTextField(20);
        txtAppointmentStart.setEnabled(false);
        btnSubmit = new JButton("Submit");
        btnSubmit.setEnabled(false);

        bottomPanel.add(new JLabel("Appointment Start (YYYY-MM-DD HH:MM):"));
        bottomPanel.add(txtAppointmentStart);
        bottomPanel.add(btnSubmit);

        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        btnCheckAvailability.addActionListener(e -> showDoctorAppointments());
        btnSubmit.addActionListener(e -> submitAppointment());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void showDoctorAppointments() {
        String doctorName = txtDoctorName.getText();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT doctorId FROM doctor WHERE doctorName = ?")) {

            pstmt.setString(1, doctorName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int doctorId = rs.getInt("doctorId");
                DefaultTableModel model = new DefaultTableModel(new String[]{"Type", "Start", "End"}, 0);
                fetchAppointmentsAndDayOffs(conn, doctorId, model);
                appointmentsTable.setModel(model);
                txtAppointmentStart.setEnabled(true);
                btnSubmit.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Doctor not found");
                txtAppointmentStart.setEnabled(false);
                btnSubmit.setEnabled(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }


    private void fetchAppointmentsAndDayOffs(Connection conn, int doctorId, DefaultTableModel model) {
        // Fetch appointments
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT 'Appointment' as Type, appointmentStart, appointmentFinish FROM appointment WHERE doctorId = ?")) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{"Appointment", rs.getTimestamp("appointmentStart"), rs.getTimestamp("appointmentFinish")});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }


        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT 'Day Off' as Type, dayoffStart, dayoffEnd FROM doctorschedule WHERE doctorId = ?")) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{"Day Off", rs.getTimestamp("dayoffStart"), rs.getTimestamp("dayoffEnd")});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private boolean isOverlappingAppointment(Connection conn, int doctorId, LocalDateTime start, LocalDateTime finish) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment WHERE doctorId = ? AND NOT (appointmentFinish <= ? OR appointmentStart >= ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            pstmt.setString(3, finish.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private boolean isDoctorUnavailable(Connection conn, int doctorId, LocalDateTime start, LocalDateTime finish) throws SQLException {
        String sql = "SELECT COUNT(*) FROM doctorschedule WHERE doctorId = ? AND ((dayoffStart < ? AND dayoffEnd > ?) OR (dayoffStart < ? AND dayoffEnd > ?))";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            pstmt.setString(3, start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            pstmt.setString(4, finish.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            pstmt.setString(5, finish.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    private boolean hasExistingAppointmentOnSameDay(Connection conn, int doctorId, LocalDateTime start) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment WHERE doctorId = ? AND patientId = ? AND DATE(appointmentStart) = DATE(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setInt(2, this.patientId);
            pstmt.setString(3, start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    private void submitAppointment() {
        String doctorName = txtDoctorName.getText();
        String appointmentStartStr = txtAppointmentStart.getText();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime start = LocalDateTime.parse(appointmentStartStr, formatter);
            LocalDateTime finish = start.plusMinutes(15);

            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT+3"));
            LocalDateTime oneYearLater = now.plusMonths(12).toLocalDateTime();

            if (start.atZone(ZoneId.of("GMT+3")).isBefore(now)) {
                JOptionPane.showMessageDialog(this, "You cannot make appointments in the past.");
                return;
            }

            if (start.isAfter(oneYearLater)) {
                JOptionPane.showMessageDialog(this, "You cannot make appointments more than one year in future.");
                return;
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT d.doctorId, d.roomId, dep.departmentName " +
                                 "FROM doctor d " +
                                 "JOIN department dep ON d.departmentId = dep.departmentId " +
                                 "WHERE d.doctorName = ?")) {

                pstmt.setString(1, doctorName);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int doctorId = rs.getInt("doctorId");
                    int roomId = rs.getInt("roomId");
                    String departmentName = rs.getString("departmentName");

                    if (!"Emergency".equals(departmentName) && (start.getHour() < 8 || start.getHour() >= 19)) {
                        JOptionPane.showMessageDialog(this, "Appointments can only be made between 08:00 and 19:00.");
                        return;
                    }

                    if (hasExistingAppointmentOnSameDay(conn, doctorId, start)) {
                        JOptionPane.showMessageDialog(this, "You already have an appointment with this doctor on the same day.");
                        return;
                    }

                    if (isOverlappingAppointment(conn, doctorId, start, finish) || isDoctorUnavailable(conn, doctorId, start, finish)) {
                        JOptionPane.showMessageDialog(this, "The doctor is not available at that time.");
                        return;
                    }

                    insertAppointment(doctorId, appointmentStartStr, formatter.format(finish), roomId);
                } else {
                    JOptionPane.showMessageDialog(this, "Doctor not found");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please enter date and time in 'YYYY-MM-DD HH:MM' format.");
        }
    }



    private void insertAppointment(int doctorId, String start, String finish, int roomId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO appointment (appointmentStart, appointmentFinish, doctorId, patientId, roomId) VALUES (?, ?, ?, ?, ?)")) {

            pstmt.setString(1, start);
            pstmt.setString(2, finish);
            pstmt.setInt(3, doctorId);
            pstmt.setInt(4, this.patientId);
            pstmt.setInt(5, roomId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Appointment created successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in creating appointment: " + ex.getMessage());
        }
    }
}
