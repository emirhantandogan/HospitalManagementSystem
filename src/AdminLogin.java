import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminLogin extends JFrame {

    // Components
    private JTextField txtAdminName;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public AdminLogin() {
        setTitle("Admin Login");
        setSize(300, 200);
        setLayout(new GridLayout(0, 2));


        txtAdminName = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton("Login");


        add(new JLabel("Name:"));
        add(txtAdminName);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(btnLogin);


        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginAdmin();
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loginAdmin() {
        String name = txtAdminName.getText();
        String password = new String(txtPassword.getPassword());
        String hashedPassword = Hashing.hashPassword(password);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();


            String sql = "SELECT * FROM admin WHERE adminName = ? AND adminPassword = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, hashedPassword);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");

                new AdminPage().setVisible(true);
                this.dispose();
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
        new AdminLogin();
    }
}
