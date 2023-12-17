import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterAdmin extends JFrame {

    // Components
    private JTextField txtAdminName;
    private JPasswordField txtPassword;
    private JButton btnRegister;

    public RegisterAdmin() {
        setTitle("Register Admin");
        setSize(300, 200);
        setLayout(new GridLayout(0, 2));

        // Initialize components
        txtAdminName = new JTextField();
        txtPassword = new JPasswordField();
        btnRegister = new JButton("Register");

        // Add components to the frame
        add(new JLabel("Name:"));
        add(txtAdminName);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(btnRegister);

        // Register button action
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerAdmin();
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void registerAdmin() {
        String name = txtAdminName.getText();
        String password = new String(txtPassword.getPassword());
        String hashedPassword = Hashing.hashPassword(password);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();

            // Retrieve the maximum adminId
            String queryMaxId = "SELECT MAX(adminId) FROM admin";
            pstmt = conn.prepareStatement(queryMaxId);
            rs = pstmt.executeQuery();
            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            int newAdminId = maxId + 1;

            // Insert the new admin
            String sql = "INSERT INTO admin (adminId, adminName, adminPassword) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, newAdminId);
            pstmt.setString(2, name);
            pstmt.setString(3, hashedPassword);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Admin Registered. ID: " + newAdminId);
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
        new RegisterAdmin();
    }
}
