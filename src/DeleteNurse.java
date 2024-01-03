import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeleteNurse extends JFrame {

    private JTextField txtNurseId;
    private JButton btnDelete;

    public DeleteNurse() {
        setTitle("Delete Nurse");
        setSize(300, 100);
        setLayout(new GridLayout(0, 2));

        txtNurseId = new JTextField();
        btnDelete = new JButton("Delete");

        add(new JLabel("Nurse ID:"));
        add(txtNurseId);
        add(btnDelete);

        btnDelete.addActionListener(e -> deleteNurse());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void deleteNurse() {
        int nurseId = Integer.parseInt(txtNurseId.getText());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM nurse WHERE nurseId = ?")) {

            pstmt.setInt(1, nurseId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Nurse Deleted Successfully");
            } else {
                JOptionPane.showMessageDialog(this, "No Nurse Found with ID: " + nurseId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in deletion: " + ex.getMessage());
        }
    }
}
