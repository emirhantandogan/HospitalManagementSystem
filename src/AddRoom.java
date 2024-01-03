import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

public class AddRoom extends JFrame {

    private JComboBox<String> cmbRoomType;
    private JTextField txtNurseId;
    private JButton btnAdd;

    private final List<String> rooms = Arrays.asList(
            "doctor room", "Pathology Lab", "Vaccination Room", "Recovery Room",
            "Ultrasound Room", "X-Ray Room", "Burn Unit Room", "Isolation Room",
            "Intensive Care Unit Room", "Physical Therapy Room", "Endoscopy Room",
            "Dialysis Room", "CT Scan Room", "MRI Room", "Radiology Room","Surgery Room",
            "Delivery Room", "Sleep Study Room"
    );

    public AddRoom() {
        setTitle("Add Room");
        setSize(400, 150);
        setLayout(new GridLayout(0, 2));

        cmbRoomType = new JComboBox<>(rooms.toArray(new String[0]));
        txtNurseId = new JTextField();
        btnAdd = new JButton("Add");

        add(new JLabel("Room Type:"));
        add(cmbRoomType);
        add(new JLabel("Nurse ID:"));
        add(txtNurseId);
        add(btnAdd);

        btnAdd.addActionListener(e -> addRoom());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void addRoom() {
        String roomType = (String) cmbRoomType.getSelectedItem();
        int nurseId = Integer.parseInt(txtNurseId.getText());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmtCheckNurse = conn.prepareStatement("SELECT COUNT(*) FROM room WHERE nurseId = ?")) {

            pstmtCheckNurse.setInt(1, nurseId);
            ResultSet rsCheckNurse = pstmtCheckNurse.executeQuery();
            if (rsCheckNurse.next() && rsCheckNurse.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "This nurse is already assigned to a room.");
                return;
            }

            PreparedStatement pstmtGetMax = conn.prepareStatement("SELECT MAX(roomId) FROM room");
            ResultSet rsGetMax = pstmtGetMax.executeQuery();

            int maxId = 0;
            if (rsGetMax.next()) {
                maxId = rsGetMax.getInt(1);
            }
            int newRoomId = maxId + 1;

            try (PreparedStatement pstmtAddRoom = conn.prepareStatement("INSERT INTO room (roomId, roomType, availability, nurseId) VALUES (?, ?, 'Available', ?)")) {
                pstmtAddRoom.setInt(1, newRoomId);
                pstmtAddRoom.setString(2, roomType);
                pstmtAddRoom.setInt(3, nurseId);
                pstmtAddRoom.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Room Added Successfully with ID: " + newRoomId);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in adding room: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new AddRoom();
    }
}
