import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
        btnDelete = new JButton("Delete");

        add(new JLabel("Appointment ID:"));
        add(txtAppointmentId);
        add(btnDelete);

        btnDelete.addActionListener(e -> deleteAppointment());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void deleteAppointment() {
        int appointmentId = Integer.parseInt(txtAppointmentId.getText());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmtCheck = conn.prepareStatement("SELECT * FROM appointment WHERE appointmentId = ? AND patientId = ?")) {

            pstmtCheck.setInt(1, appointmentId);
            pstmtCheck.setInt(2, this.patientId);
            ResultSet rs = pstmtCheck.executeQuery();

            if (rs.next()) {
                // Appointment belongs to this patient, proceed with deletion
                try (PreparedStatement pstmtDelete = conn.prepareStatement("DELETE FROM appointment WHERE appointmentId = ?")) {
                    pstmtDelete.setInt(1, appointmentId);
                    pstmtDelete.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Appointment Deleted Successfully");
                }
            } else {
                // Appointment does not belong to this patient
                JOptionPane.showMessageDialog(this, "You do not have an appointment with the provided ID.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in deleting appointment: " + ex.getMessage());
        }
    }
}
