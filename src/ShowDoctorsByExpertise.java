import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

public class ShowDoctorsByExpertise extends JFrame {

    private JTextField txtExpertise;
    private JButton btnSearch;
    private JTable table;

    public ShowDoctorsByExpertise() {
        setTitle("Show Doctors By Expertise");
        setSize(500, 300);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        txtExpertise = new JTextField(20);
        btnSearch = new JButton("Search");

        panel.add(new JLabel("Expertise:"));
        panel.add(txtExpertise);
        panel.add(btnSearch);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> searchDoctors());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void searchDoctors() {
        String expertise = txtExpertise.getText();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT doctorId, doctorName, departmentId FROM doctor WHERE doctorExpertise = ?")) {

            pstmt.setString(1, expertise);
            try (ResultSet rs = pstmt.executeQuery()) {
                String[] columnNames = {"Doctor ID", "Name", "Department ID"};
                table.setModel(buildTableModel(rs, columnNames));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in fetching doctors: " + ex.getMessage());
        }
    }

    private DefaultTableModel buildTableModel(ResultSet rs, String[] columnNames) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Create vector of column names
        Vector<String> columnNamesVector = new Vector<>();
        for (int column = 0; column < columnCount; column++) {
            columnNamesVector.add(columnNames[column]);
        }

        // Create vector of data rows
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNamesVector);
    }
}
