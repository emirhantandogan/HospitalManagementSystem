import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SetUnavailableTime extends JFrame {

    private int doctorId;
    private JTextField txtDayoffStart, txtDayoffEnd;
    private JButton btnSubmit;

    public SetUnavailableTime(int doctorId) {
        this.doctorId = doctorId;

        setTitle("Set Unavailable Time for Doctor ID: " + doctorId);
        setSize(400, 200);
        setLayout(new GridLayout(0, 2));

        txtDayoffStart = new JTextField();
        txtDayoffEnd = new JTextField();
        btnSubmit = new JButton("Submit");

        add(new JLabel("Dayoff Start (YYYY-MM-DD HH:MM):"));
        add(txtDayoffStart);
        add(new JLabel("Dayoff End (YYYY-MM-DD HH:MM):"));
        add(txtDayoffEnd);
        add(btnSubmit);

        btnSubmit.addActionListener(e -> checkAndSubmitUnavailableTime());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    private void checkAndSubmitUnavailableTime() {
        String dayoffStart = txtDayoffStart.getText();
        String dayoffEnd = txtDayoffEnd.getText();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmtCheck = conn.prepareStatement("SELECT * FROM appointment WHERE doctorId = ? AND ((appointmentStart >= ? AND appointmentStart < ?) OR (appointmentFinish > ? AND appointmentFinish <= ?))")) {

            pstmtCheck.setInt(1, doctorId);
            pstmtCheck.setString(2, dayoffStart);
            pstmtCheck.setString(3, dayoffEnd);
            pstmtCheck.setString(4, dayoffStart);
            pstmtCheck.setString(5, dayoffEnd);

            try (ResultSet rs = pstmtCheck.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Unavailable time conflicts with existing appointments.");
                } else {
                    insertUnavailableTime(doctorId, dayoffStart, dayoffEnd);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void insertUnavailableTime(int doctorId, String dayoffStart, String dayoffEnd) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmtInsert = conn.prepareStatement("INSERT INTO doctorschedule (doctorId, dayoffStart, dayoffEnd) VALUES (?, ?, ?)")) {

            pstmtInsert.setInt(1, doctorId);
            pstmtInsert.setString(2, dayoffStart);
            pstmtInsert.setString(3, dayoffEnd);
            pstmtInsert.executeUpdate();

            JOptionPane.showMessageDialog(this, "Unavailable time added successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in adding unavailable time: " + ex.getMessage());
        }
    }
}
