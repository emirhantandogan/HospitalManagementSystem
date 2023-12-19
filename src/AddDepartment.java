import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddDepartment extends JFrame {

    private JTextField txtDepartmentName;
    private JButton btnAdd;

    public AddDepartment() {
        setTitle("Add Department");
        setSize(400, 100);
        setLayout(new GridLayout(0, 2));

        txtDepartmentName = new JTextField();
        btnAdd = new JButton("Add");

        add(new JLabel("Department Name:"));
        add(txtDepartmentName);
        add(btnAdd);

        btnAdd.addActionListener(e -> addDepartment());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void addDepartment() {
        String departmentName = txtDepartmentName.getText();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmtGetMax = conn.prepareStatement("SELECT MAX(departmentId) FROM department");
             ResultSet rs = pstmtGetMax.executeQuery()) {

            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            int newDepartmentId = maxId + 1;

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO department (departmentId, departmentName) VALUES (?, ?)")) {
                pstmt.setInt(1, newDepartmentId);
                pstmt.setString(2, departmentName);
                pstmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Department Added Successfully with ID: " + newDepartmentId);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in adding department: " + ex.getMessage());
        }
    }
}
