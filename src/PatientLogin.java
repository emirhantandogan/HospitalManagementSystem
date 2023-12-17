import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PatientLogin extends JFrame {

    // Components
    private JTextField txtPatientName;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public PatientLogin() {
        setTitle("Patient Login");
        setSize(300, 200);
        setLayout(new GridLayout(0, 2));

        // Initialize components
        txtPatientName = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton("Login");

        // Add components to the frame
        add(new JLabel("Name:"));
        add(txtPatientName);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(btnLogin);

        // Login button action
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginPatient();
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loginPatient() {
        String name = txtPatientName.getText();
        String password = new String(txtPassword.getPassword());
        String hashedPassword = Hashing.hashPassword(password);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();

            // Check if the patient exists with the given name and hashed password
            String sql = "SELECT * FROM patient WHERE patientName = ? AND patientPassword = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, hashedPassword);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                // Here, you can redirect the patient to another window or perform other actions upon successful login
                new PatientPage().setVisible(true);
                this.dispose();//closes the login window
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in login: " + ex.getMessage());
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
        new PatientLogin();
    }
}
