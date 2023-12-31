import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Vector;

public class ListAppointments extends JFrame {

    private int patientId;
    private JComboBox<String> cbExpertise;
    private JTable table;
    private JButton btnFilter;

    public ListAppointments(int patientId) {
        this.patientId = patientId;

        setTitle("Your Appointments");
        setSize(600, 400);
        setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel();
        cbExpertise = new JComboBox<>(new String[]{"All","Emergency Doctor", "Forensic Medicine Specialist", "Child Psychiatrist", "Pediatrician", "Dermatologist",
                "Infectious Disease Specialist", "Pulmonologist", "Internist", "Cardiologist", "Neurologist", "Radiologist",
                "Psychiatrist", "Anesthesiologist", "Neurosurgeon", "General Surgeon", "Pediatric Surgeon", "Cardiovascular Surgeon",
                "Thoracic Surgeon", "Ophthalmologist", "Otolaryngologist", "Orthopedic Surgeon", "Pathologist", "Urologist",
                "Plastic Surgeon", "Sports Medicine Physician", "Neonatologist", "Geriatrician", "Physiatrist", "Allergist/Immunologist",
                "Pathologist", "Plastic Surgeon"});
        btnFilter = new JButton("Filter");

        filterPanel.add(new JLabel("Filter by Expertise:"));
        filterPanel.add(cbExpertise);
        filterPanel.add(btnFilter);

        add(filterPanel, BorderLayout.NORTH);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        btnFilter.addActionListener(this::filterAppointments);

        loadAppointments(patientId, "All");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void filterAppointments(ActionEvent event) {
        String expertise = cbExpertise.getSelectedItem().toString();
        loadAppointments(patientId, expertise);
    }

    private void loadAppointments(int patientId, String expertise) {
        String sql = "SELECT a.appointmentId, d.doctorName, d.doctorExpertise, a.appointmentStart, a.appointmentFinish, a.roomId " +
                "FROM appointment a JOIN doctor d ON a.doctorId = d.doctorId " +
                "WHERE a.patientId = ?";
        if (!"All".equals(expertise)) {
            sql += " AND d.doctorExpertise = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            if (!"All".equals(expertise)) {
                pstmt.setString(2, expertise);
            }

            ResultSet rs = pstmt.executeQuery();
            table.setModel(buildTableModel(rs));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in fetching appointments: " + ex.getMessage());
        }
    }

    private DefaultTableModel buildTableModel(ResultSet rs) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Column names
        Vector<String> columnNames = new Vector<>();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // Data of the table
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }
}
