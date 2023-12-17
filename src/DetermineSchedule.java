import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DetermineSchedule extends JFrame {

    private int doctorId;
    private JTextField txtWorkStart, txtWorkEnd;
    private JButton btnAddSchedule;

    public DetermineSchedule(int doctorId) {
        this.doctorId = doctorId;

        setTitle("Determine Doctor Schedule");
        setSize(400, 200);
        setLayout(new GridLayout(0, 2));

        txtWorkStart = new JTextField();
        txtWorkEnd = new JTextField();
        btnAddSchedule = new JButton("Add Schedule");

        add(new JLabel("Work Start (YYYY-MM-DD HH:MM):"));
        add(txtWorkStart);
        add(new JLabel("Work End (YYYY-MM-DD HH:MM):"));
        add(txtWorkEnd);
        add(btnAddSchedule);

        btnAddSchedule.addActionListener(e -> addSchedule());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void addSchedule() {
        String workStart = txtWorkStart.getText();
        String workEnd = txtWorkEnd.getText();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO doctorschedule (doctorId, workStart, workEnd) VALUES (?, ?, ?)")) {

            pstmt.setInt(1, doctorId);
            pstmt.setString(2, workStart);
            pstmt.setString(3, workEnd);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Schedule Added Successfully");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in adding schedule: " + ex.getMessage());
        }
    }
}
