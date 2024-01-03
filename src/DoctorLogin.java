import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DoctorLogin extends JFrame {

    private JTextField txtDoctorName;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public DoctorLogin() {
        setTitle("Doctor Login");
        setSize(300, 150);
        setLayout(new GridLayout(0, 2));

        txtDoctorName = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton("Login");

        add(new JLabel("Name:"));
        add(txtDoctorName);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(btnLogin);

        btnLogin.addActionListener(e -> loginDoctor());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loginDoctor() {
        String name = txtDoctorName.getText();
        String password = new String(txtPassword.getPassword());
        String hashedPassword = Hashing.hashPassword(password);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM doctor WHERE doctorName = ? AND doctorPassword = ?")) {

            pstmt.setString(1, name);
            pstmt.setString(2, hashedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    int doctorId = rs.getInt("doctorId");
                    new DoctorPage(doctorId);
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
}
