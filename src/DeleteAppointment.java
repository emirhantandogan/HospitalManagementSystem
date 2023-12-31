import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DeleteAppointment extends JFrame {

    private int patientId;
    private JTextField txtAppointmentId;
    private JButton btnDelete;

    public DeleteAppointment(int patientId) {
        this.patientId = patientId;

        setTitle("Delete Appointment");
        setSize(300, 100);
        setLayout(new GridLayout(0, 2));

        txtAppointmentId = new JTextField();
        btnDelete = new JButton("Cancel");

        add(new JLabel("Appointment ID:"));
        add(txtAppointmentId);
        add(btnDelete);

        btnDelete.addActionListener(e -> deleteAppointment());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void deleteAppointment() {
        int appointmentId = Integer.parseInt(txtAppointmentId.getText());
        LocalDateTime now = LocalDateTime.now(ZoneId.of("GMT+3"));

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmtCheck = conn.prepareStatement(
                     "SELECT appointmentStart FROM appointment WHERE appointmentId = ? AND patientId = ?")) {

            pstmtCheck.setInt(1, appointmentId);
            pstmtCheck.setInt(2, this.patientId);
            ResultSet rs = pstmtCheck.executeQuery();

            if (rs.next()) {
                Timestamp appointmentStart = rs.getTimestamp("appointmentStart");
                LocalDateTime startDateTime = appointmentStart.toLocalDateTime();

                if (startDateTime.isAfter(now.plusHours(24))) {
                    // More than 24 hours until the appointment, proceed with deletion
                    try (PreparedStatement pstmtDelete = conn.prepareStatement("DELETE FROM appointment WHERE appointmentId = ?")) {
                        pstmtDelete.setInt(1, appointmentId);
                        pstmtDelete.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Appointment Deleted Successfully");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot cancel appointment. There is less than 24 hours left to the appointment.");
                }
            } else {
                // Appointment does not belong to this patient or does not exist
                JOptionPane.showMessageDialog(this, "You do not have an appointment with the provided ID.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in deleting appointment: " + ex.getMessage());
        }
    }
}
