import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

public class AddDepartment extends JFrame {

    private JTextField txtDepartmentName;
    private JButton btnAdd;

    // List of valid department names
    private final List<String> validDepartments = Arrays.asList(
            "Emergency", "Forensic Medicine", "Child Mental Health", "Pediatrics",
            "Dermatology", "Infectious Diseases", "Chest Diseases", "Internal Medicine",
            "Cardiology", "Neurology", "Radiology", "Psychiatry",
            "Anesthesiology and Reanimation", "Neurosurgery", "General Surgery",
            "Pediatric Surgery", "Cardiovascular Surgery", "Thoracic Surgery",
            "Ophthalmology", "Otolaryngology", "Orthopedics", "Pathology", "Urology"
    );

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
        String departmentName = txtDepartmentName.getText().trim().toLowerCase(); // Convert input department name to lowercase

        // Check if the department name is valid (case-insensitive check)
        boolean isValidDepartment = validDepartments.stream()
                .map(String::toLowerCase) // Convert each department in the list to lowercase
                .anyMatch(d -> d.equals(departmentName)); // Check for a match

        if (!isValidDepartment) {
            JOptionPane.showMessageDialog(this, "Such department name is not accepted.");
            return;
        }

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
                pstmt.setString(2, departmentName); // Here, departmentName is already in lowercase
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Department Added Successfully with ID: " + newDepartmentId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in adding department: " + ex.getMessage());
        }
    }

}
