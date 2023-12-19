import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddRoom extends JFrame {

    private JTextField txtRoomType, txtAvailability, txtNurseId;
    private JButton btnAdd;

    public AddRoom() {
        setTitle("Add Room");
        setSize(400, 150);
        setLayout(new GridLayout(0, 2));

        txtRoomType = new JTextField();
        txtAvailability = new JTextField();
        txtNurseId = new JTextField();
        btnAdd = new JButton("Add");

        add(new JLabel("Room Type:"));
        add(txtRoomType);
        add(new JLabel("Availability:"));
        add(txtAvailability);
        add(new JLabel("Nurse ID:"));
        add(txtNurseId);
        add(btnAdd);

        btnAdd.addActionListener(e -> addRoom());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void addRoom() {
        String roomType = txtRoomType.getText();
        String availability = txtAvailability.getText();
        int nurseId = Integer.parseInt(txtNurseId.getText());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmtGetMax = conn.prepareStatement("SELECT MAX(roomId) FROM room");
             ResultSet rs = pstmtGetMax.executeQuery()) {

            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            int newRoomId = maxId + 1;

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO room (roomId, roomType, availability, nurseId) VALUES (?, ?, ?, ?)")) {
                pstmt.setInt(1, newRoomId);
                pstmt.setString(2, roomType);
                pstmt.setString(3, availability);
                pstmt.setInt(4, nurseId);
                pstmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Room Added Successfully with ID: " + newRoomId);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in adding room: " + ex.getMessage());
        }
    }
}
