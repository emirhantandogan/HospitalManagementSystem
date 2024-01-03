import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NurseLogin extends JFrame {

    private JTextField txtNurseName;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public NurseLogin() {
        setTitle("Nurse Login");
        setSize(300, 150);
        setLayout(new GridLayout(0, 2));

        txtNurseName = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton("Login");

        add(new JLabel("Name:"));
        add(txtNurseName);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(btnLogin);

        btnLogin.addActionListener(e -> loginNurse());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loginNurse() {
        String name = txtNurseName.getText();
        String password = new String(txtPassword.getPassword());
        String hashedPassword = Hashing.hashPassword(password);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM nurse WHERE nurseName = ? AND nursePassword = ?")) {

            pstmt.setString(1, name);
            pstmt.setString(2, hashedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    int nurseId = rs.getInt("nurseId"); // Assuming the column name is 'nurseId'
                    new NursePage(nurseId); // Open NursePage with the nurse's ID
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
