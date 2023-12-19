import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MakeAppointment extends JFrame {

    private int patientId;
    private JTextField txtDoctorName, txtAppointmentStart, txtAppointmentFinish, txtRoomId;
    private JButton btnSubmit;

    public MakeAppointment(int patientId) {
        this.patientId = patientId;

        setTitle("Make Appointment");
        setSize(650, 300);
        setLayout(new GridLayout(0, 2));

        txtDoctorName = new JTextField();
        txtAppointmentStart = new JTextField();
        txtAppointmentFinish = new JTextField();
        txtRoomId = new JTextField();
        btnSubmit = new JButton("Submit");

        add(new JLabel("Doctor Name:"));
        add(txtDoctorName);
        add(new JLabel("Appointment Start (YYYY-MM-DD HH:MM):"));
        add(txtAppointmentStart);
        add(new JLabel("Appointment Finish (YYYY-MM-DD HH:MM):"));
        add(txtAppointmentFinish);
        add(new JLabel("Room ID:"));
        add(txtRoomId);
        add(btnSubmit);

        btnSubmit.addActionListener(e -> submitAppointment());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void submitAppointment() {
        String doctorName = txtDoctorName.getText();
        String appointmentStart = txtAppointmentStart.getText();
        String appointmentFinish = txtAppointmentFinish.getText();
        int roomId = Integer.parseInt(txtRoomId.getText());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT doctorId FROM doctor WHERE doctorName = ?")) {

            pstmt.setString(1, doctorName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int doctorId = rs.getInt("doctorId");
                insertAppointment(doctorId, appointmentStart, appointmentFinish, roomId);
            } else {
                JOptionPane.showMessageDialog(this, "Doctor not found");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
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
