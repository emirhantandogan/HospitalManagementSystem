import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;

public class RegisterPatient extends JFrame {

    // Components
    private JTextField txtPatientName, txtContactInfo, txtAddress;
    private JPasswordField txtPassword;
    private JComboBox<String> comboGender;
    private JFormattedTextField txtDob;
    private JButton btnRegister;

    public RegisterPatient() {
        setTitle("Register Patient");
        setSize(300, 400);
        setLayout(new GridLayout(0, 2));

        // Initialize components
        txtPatientName = new JTextField();
        txtPassword = new JPasswordField();
        txtContactInfo = new JTextField();
        txtAddress = new JTextField();
        comboGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        txtDob = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        btnRegister = new JButton("Register");

        // Add components to the frame
        add(new JLabel("Name:"));
        add(txtPatientName);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(new JLabel("Contact Info:"));
        add(txtContactInfo);
        add(new JLabel("Address:"));
        add(txtAddress);
        add(new JLabel("Gender:"));
        add(comboGender);
        add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        add(txtDob);
        add(btnRegister);

        // Register button action
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerPatient();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void registerPatient() {
        String name = txtPatientName.getText();
        String password = new String(txtPassword.getPassword());
        String hashedPassword = Hashing.hashPassword(password);
        String contact = txtContactInfo.getText();
        String address = txtAddress.getText();
        String gender = comboGender.getSelectedItem().toString();
        String dob = txtDob.getText();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();

            // Retrieve the maximum patientId
            String queryMaxId = "SELECT MAX(patientId) FROM patient";
            pstmt = conn.prepareStatement(queryMaxId);
            rs = pstmt.executeQuery();
            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            int newPatientId = maxId + 1;

            // Insert the new patient
            String sql = "INSERT INTO patient (patientId, patientPassword, patientName, contactInfo, dob, gender, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, newPatientId);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, name);
            pstmt.setString(4, contact);
            pstmt.setDate(5, Date.valueOf(dob));
            pstmt.setString(6, gender);
            pstmt.setString(7, address);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Patient Registered. ID: " + newPatientId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in registration: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        new RegisterPatient();
    }
}
