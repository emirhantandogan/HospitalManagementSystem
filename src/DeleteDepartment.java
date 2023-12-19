import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
public class DeleteDepartment extends JFrame {

    private JTextField txtDepartmentId;
    private JButton btnDelete;

    public DeleteDepartment() {
        setTitle("Delete Department");
        setSize(300, 100);
        setLayout(new GridLayout(0, 2));

        txtDepartmentId = new JTextField();
        btnDelete = new JButton("Delete");

        add(new JLabel("Department ID:"));
        add(txtDepartmentId);
        add(btnDelete);

        btnDelete.addActionListener(e -> deleteDepartment());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void deleteDepartment() {
        int departmentId = Integer.parseInt(txtDepartmentId.getText());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM department WHERE departmentId = ?")) {

            pstmt.setInt(1, departmentId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Department Deleted Successfully");
            } else {
                JOptionPane.showMessageDialog(this, "No Department Found with ID: " + departmentId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in deleting department: " + ex.getMessage());
        }
    }
}
