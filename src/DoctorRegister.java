import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DoctorRegister extends JFrame {

    private JTextField txtDoctorName, txtDoctorExpertise, txtDepartmentId;
    private JPasswordField txtPassword;
    private JButton btnRegister;

    public DoctorRegister() {
        setTitle("Register Doctor");
        setSize(350, 200);
        setLayout(new GridLayout(0, 2));

        // Initialize components
        txtDoctorName = new JTextField();
        txtPassword = new JPasswordField();
        txtDoctorExpertise = new JTextField();
        txtDepartmentId = new JTextField();
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
        String expertise = txtDoctorExpertise.getText();
        int departmentId = Integer.parseInt(txtDepartmentId.getText());

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

            // Insert the new doctor
            String sql = "INSERT INTO doctor (doctorId, doctorName, doctorPassword, doctorExpertise, departmentId) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, newDoctorId);
            pstmt.setString(2, name);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, expertise);
            pstmt.setInt(5, departmentId);

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
