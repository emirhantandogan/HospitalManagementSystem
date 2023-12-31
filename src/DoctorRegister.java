import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

public class DoctorRegister extends JFrame {

    private JTextField txtDoctorName, txtDoctorExpertise, txtDepartmentId, txtRoomId;
    private JPasswordField txtPassword;
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
        setSize(350, 250); // Adjusted size for the new field
        setLayout(new GridLayout(0, 2));

        // Initialize components
        txtDoctorName = new JTextField();
        txtPassword = new JPasswordField();
        txtDoctorExpertise = new JTextField();
        txtDepartmentId = new JTextField();
        txtRoomId = new JTextField(); // New text field for Room ID
        btnRegister = new JButton("Register");

        // Add components to the frame
        add(new JLabel("Name:"));
        add(txtDoctorName);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(new JLabel("Expertise:"));
        add(txtDoctorExpertise);
        add(new JLabel("Department ID:"));
        add(txtDepartmentId);
        add(new JLabel("Room ID:"));
        add(txtRoomId);
        add(btnRegister);

        // Register button action
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerDoctor();
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void registerDoctor() {
        String name = txtDoctorName.getText();
        String password = new String(txtPassword.getPassword());
        String hashedPassword = Hashing.hashPassword(password);
        String expertise = txtDoctorExpertise.getText().toLowerCase(); // Convert input expertise to lowercase
        int departmentId = Integer.parseInt(txtDepartmentId.getText());
        int roomId = Integer.parseInt(txtRoomId.getText());

        // Check if the doctor's expertise is valid (case-insensitive check)
        boolean isValidExpertise = validExpertises.stream()
                .map(String::toLowerCase) // Convert each expertise in the list to lowercase
                .anyMatch(e -> e.equals(expertise)); // Check for a match

        if (!isValidExpertise) {
            JOptionPane.showMessageDialog(this, "Such doctor expertise is not accepted.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();

            // Retrieve the maximum doctorId
            String queryMaxId = "SELECT MAX(doctorId) FROM doctor";
            pstmt = conn.prepareStatement(queryMaxId);
            rs = pstmt.executeQuery();
            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            int newDoctorId = maxId + 1;

            // Insert the new doctor with Room ID
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
