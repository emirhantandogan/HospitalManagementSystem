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

        txtPatientName = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton("Login");

        add(new JLabel("Name:"));
        add(txtPatientName);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(btnLogin);

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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM patient WHERE patientName = ? AND patientPassword = ?")) {

            pstmt.setString(1, name);
            pstmt.setString(2, hashedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int patientId = rs.getInt("patientId"); // Assuming the column name is 'patientId'
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    new PatientPage(patientId).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials!");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in login: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new PatientLogin();
    }
}
