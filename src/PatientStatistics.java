import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Vector;

public class PatientStatistics extends JFrame {

    private JTextField txtDays;
    private JButton btnShow;
    private JTable tableStatistics;

    public PatientStatistics() {
        setTitle("Patient Statistics");
        setSize(600, 300);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        txtDays = new JTextField(5);
        btnShow = new JButton("Show Statistics");

        inputPanel.add(new JLabel("Number of patients given medical care in past (days):"));
        inputPanel.add(txtDays);
        inputPanel.add(btnShow);

        tableStatistics = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableStatistics);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnShow.addActionListener(this::showStatisticsAction);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void showStatisticsAction(ActionEvent event) {
        String daysStr = txtDays.getText();
        try {
            int days = Integer.parseInt(daysStr);
            fetchStatistics(days);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of days.");
        }
    }

    private void fetchStatistics(int days) {
        ZonedDateTime endDateTimeZoned = ZonedDateTime.now(ZoneId.of("GMT+3")).minusDays(days);
        Timestamp startTime = Timestamp.valueOf(endDateTimeZoned.toLocalDateTime());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT d.departmentName, COUNT(*) as patientCount " +
                             "FROM appointment a " +
                             "JOIN doctor doc ON a.doctorId = doc.doctorId " +
                             "JOIN department d ON doc.departmentId = d.departmentId " +
                             "WHERE a.appointmentStart >= ? " +
                             "GROUP BY d.departmentName")) {

            pstmt.setTimestamp(1, startTime);
            ResultSet rs = pstmt.executeQuery();

            DefaultTableModel model = buildTableModel(rs);
            tableStatistics.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching patient statistics: " + e.getMessage());
        }
    }

    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

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
