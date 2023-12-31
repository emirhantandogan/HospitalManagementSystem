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
             PreparedStatement pstmtCheckNurse = conn.prepareStatement("SELECT COUNT(*) FROM room WHERE nurseId = ?")) {

            // Check if the nurse is already assigned to a room
            pstmtCheckNurse.setInt(1, nurseId);
            ResultSet rsCheckNurse = pstmtCheckNurse.executeQuery();
            if (rsCheckNurse.next() && rsCheckNurse.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "This nurse is already assigned to a room.");
                return; // Stop further execution
            }

            // If nurse is not assigned, proceed with adding the room
            PreparedStatement pstmtGetMax = conn.prepareStatement("SELECT MAX(roomId) FROM room");
            ResultSet rsGetMax = pstmtGetMax.executeQuery();

            int maxId = 0;
            if (rsGetMax.next()) {
                maxId = rsGetMax.getInt(1);
            }
            int newRoomId = maxId + 1;

            try (PreparedStatement pstmtAddRoom = conn.prepareStatement("INSERT INTO room (roomId, roomType, availability, nurseId) VALUES (?, ?, ?, ?)")) {
                pstmtAddRoom.setInt(1, newRoomId);
                pstmtAddRoom.setString(2, roomType);
                pstmtAddRoom.setString(3, availability);
                pstmtAddRoom.setInt(4, nurseId);
                pstmtAddRoom.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Room Added Successfully with ID: " + newRoomId);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in adding room: " + ex.getMessage());
        }
    }

}
