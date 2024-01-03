import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NurseRegister extends JFrame {

    private JTextField txtNurseName;
    private JPasswordField txtPassword;
    private JButton btnRegister;

    public NurseRegister() {
        setTitle("Register Nurse");
        setSize(300, 150);
        setLayout(new GridLayout(0, 2));

        txtNurseName = new JTextField();
        txtPassword = new JPasswordField();
        btnRegister = new JButton("Register");

        add(new JLabel("Name:"));
        add(txtNurseName);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(btnRegister);

        btnRegister.addActionListener(e -> registerNurse());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void registerNurse() {
        String name = txtNurseName.getText();
        String password = new String(txtPassword.getPassword());
        String hashedPassword = Hashing.hashPassword(password);

        try (Connection conn = DBConnection.getConnection()) {
            int maxId = 0;
            try (PreparedStatement pstmtMax = conn.prepareStatement("SELECT MAX(nurseId) FROM nurse");
                 ResultSet rs = pstmtMax.executeQuery()) {
                if (rs.next()) {
                    maxId = rs.getInt(1);
                }
            }
            int newNurseId = maxId + 1;

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO nurse (nurseId, nurseName, nursePassword) VALUES (?, ?, ?)")) {
                pstmt.setInt(1, newNurseId);
                pstmt.setString(2, name);
                pstmt.setString(3, hashedPassword);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Nurse Registered. ID: " + newNurseId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in registration: " + ex.getMessage());
        }
    }
}
