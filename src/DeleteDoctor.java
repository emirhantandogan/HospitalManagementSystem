import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeleteDoctor extends JFrame {

    private JTextField txtDoctorId;
    private JButton btnDelete;

    public DeleteDoctor() {
        setTitle("Delete Doctor");
        setSize(300, 100);
        setLayout(new GridLayout(0, 2));

        // Initialize components
        txtDoctorId = new JTextField();
        btnDelete = new JButton("Delete");

        // Add components to the frame
        add(new JLabel("Doctor ID:"));
        add(txtDoctorId);
        add(btnDelete);

        // Delete button action
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteDoctor();
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void deleteDoctor() {
        int doctorId = Integer.parseInt(txtDoctorId.getText());

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();

            // Delete the doctor with the given ID
            String sql = "DELETE FROM doctor WHERE doctorId = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, doctorId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Doctor Deleted Successfully");
            } else {
                JOptionPane.showMessageDialog(this, "No Doctor Found with ID: " + doctorId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in deletion: " + ex.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new DeleteDoctor();
    }
}
