import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

public class DoctorRegister extends JFrame {

    private JTextField txtDoctorName, txtDepartmentId, txtRoomId;
    private JPasswordField txtPassword;
    private JComboBox<String> comboDoctorExpertise;
    private JButton btnRegister;

    private final List<String> validExpertises = Arrays.asList(
            "Emergency Doctor", "Forensic Medicine Specialist", "Child Psychiatrist", "Pediatrician", "Dermatologist",
            "Infectious Disease Specialist", "Pulmonologist", "Internist", "Cardiologist", "Neurologist", "Radiologist",
            "Psychiatrist", "Anesthesiologist", "Neurosurgeon", "General Surgeon", "Pediatric Surgeon", "Cardiovascular Surgeon",
            "Thoracic Surgeon", "Ophthalmologist", "Otolaryngologist", "Orthopedic Surgeon", "Pathologist", "Urologist",
            "Plastic Surgeon", "Sports Medicine Physician", "Neonatologist", "Geriatrician", "Physiatrist", "Allergist/Immunologist",
            "Pathologist", "Plastic Surgeon"
    );

    public DoctorRegister() {
        setTitle("Register Doctor");
        setSize(350, 250);
        setLayout(new GridLayout(0, 2));

        txtDoctorName = new JTextField();
        txtPassword = new JPasswordField();
        txtDepartmentId = new JTextField();
        txtRoomId = new JTextField();
        btnRegister = new JButton("Register");

        String[] expertiseArray = validExpertises.toArray(new String[0]);
        comboDoctorExpertise = new JComboBox<>(expertiseArray);

        add(new JLabel("Name:"));
        add(txtDoctorName);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(new JLabel("Expertise:"));
        add(comboDoctorExpertise);
        add(new JLabel("Department ID:"));
        add(txtDepartmentId);
        add(new JLabel("Room ID:"));
        add(txtRoomId);
        add(btnRegister);

        btnRegister.addActionListener(e -> registerDoctor());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void registerDoctor() {
        String name = txtDoctorName.getText();
        String password = new String(txtPassword.getPassword());
        String hashedPassword = Hashing.hashPassword(password);
        String expertise = (String) comboDoctorExpertise.getSelectedItem();
        int departmentId = Integer.parseInt(txtDepartmentId.getText());
        int roomId = Integer.parseInt(txtRoomId.getText());

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();

            String queryRoom = "SELECT doctorId FROM doctor WHERE roomId = ?";
            pstmt = conn.prepareStatement(queryRoom);
            pstmt.setInt(1, roomId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "This room is already assigned to another doctor.");
                return;
            }


            String queryMaxId = "SELECT MAX(doctorId) FROM doctor";
            pstmt = conn.prepareStatement(queryMaxId);
            rs = pstmt.executeQuery();
            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            int newDoctorId = maxId + 1;


            String sql = "INSERT INTO doctor (doctorId, doctorName, doctorPassword, doctorExpertise, departmentId, roomId) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, newDoctorId);
            pstmt.setString(2, name);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, expertise);
            pstmt.setInt(5, departmentId);
            pstmt.setInt(6, roomId); // Set Room ID

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {

                String updateRoomSql = "UPDATE room SET availability = 'Occupied' WHERE roomId = ?";
                try (PreparedStatement pstmtUpdateRoom = conn.prepareStatement(updateRoomSql)) {
                    pstmtUpdateRoom.setInt(1, roomId);
                    pstmtUpdateRoom.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Doctor Registered. ID: " + newDoctorId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in registration: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        new DoctorRegister();
    }
}
