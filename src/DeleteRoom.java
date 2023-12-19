import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeleteRoom extends JFrame {

    private JTextField txtRoomId;
    private JButton btnDelete;

    public DeleteRoom() {
        setTitle("Delete Room");
        setSize(300, 100);
        setLayout(new GridLayout(0, 2));

        txtRoomId = new JTextField();
        btnDelete = new JButton("Delete");

        add(new JLabel("Room ID:"));
        add(txtRoomId);
        add(btnDelete);

        btnDelete.addActionListener(e -> deleteRoom());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void deleteRoom() {
        int roomId = Integer.parseInt(txtRoomId.getText());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM room WHERE roomId = ?")) {

            pstmt.setInt(1, roomId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Room Deleted Successfully");
            } else {
                JOptionPane.showMessageDialog(this, "No Room Found with ID: " + roomId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in deleting room: " + ex.getMessage());
        }
    }
}
