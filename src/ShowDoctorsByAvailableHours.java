import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class ShowDoctorsByAvailableHours extends JFrame {

    private JTextField txtDateTime;
    private JButton btnSearch;
    private JTable table;

    public ShowDoctorsByAvailableHours() {
        setTitle("Show Doctors By Available Hours");
        setSize(600, 400);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        txtDateTime = new JTextField(16);
        btnSearch = new JButton("Search");

        panel.add(new JLabel("Date & Time (YYYY-MM-DD HH:MM):"));
        panel.add(txtDateTime);
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
        String dateTimeStr = txtDateTime.getText();
        try {
            LocalDateTime chosenDateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT+3"));
            LocalDateTime oneYearLater = now.plusYears(1).toLocalDateTime();

            if (chosenDateTime.isBefore(now.toLocalDateTime())) {
                JOptionPane.showMessageDialog(this, "You cannot search for times in the past.");
                return;
            }

            if (chosenDateTime.isAfter(oneYearLater)) {
                JOptionPane.showMessageDialog(this, "You cannot search for times more than one year into the future.");
                return;
            }

            LocalDateTime endDateTime = chosenDateTime.plusMinutes(15);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT d.doctorId, d.doctorName, d.departmentId, d.doctorExpertise FROM doctor d " +
                        "WHERE NOT EXISTS (" +
                        "SELECT 1 FROM appointment a WHERE a.doctorId = d.doctorId AND " +
                        "((a.appointmentStart <= ? AND a.appointmentFinish > ?) OR (a.appointmentStart < ? AND a.appointmentFinish >= ?))) AND NOT EXISTS (" +
                        "SELECT 1 FROM doctorschedule ds WHERE ds.doctorId = d.doctorId AND " +
                        "((ds.dayoffStart <= ? AND ds.dayoffEnd > ?) OR (ds.dayoffStart < ? AND ds.dayoffEnd >= ?)))";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(chosenDateTime));
                    pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(chosenDateTime));
                    pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(endDateTime));
                    pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(endDateTime));
                    pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(chosenDateTime));
                    pstmt.setTimestamp(6, java.sql.Timestamp.valueOf(chosenDateTime));
                    pstmt.setTimestamp(7, java.sql.Timestamp.valueOf(endDateTime));
                    pstmt.setTimestamp(8, java.sql.Timestamp.valueOf(endDateTime));
                    ResultSet rs = pstmt.executeQuery();

                    String[] columnNames = {"Doctor ID", "Name", "Department ID", "Expertise"};
                    table.setModel(buildTableModel(rs, columnNames));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private DefaultTableModel buildTableModel(ResultSet rs, String[] columnNames) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        Vector<String> columnNamesVector = new Vector<>();
        for (int column = 0; column < columnCount; column++) {
            columnNamesVector.add(columnNames[column]);
        }

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