import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class ShowDoctorsByTimeInterval extends JFrame {

    private JTextField txtStartDateTime, txtEndDateTime;
    private JButton btnSearch;
    private JTable table;

    public ShowDoctorsByTimeInterval() {
        setTitle("Show Doctors By Time Interval");
        setSize(500, 400);
        getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtStartDateTime = new JTextField(16);
        startPanel.add(new JLabel("Start Date & Time (YYYY-MM-DD HH:MM):"));
        startPanel.add(txtStartDateTime);

        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtEndDateTime = new JTextField(16);
        endPanel.add(new JLabel("End Date & Time (YYYY-MM-DD HH:MM):"));
        endPanel.add(txtEndDateTime);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnSearch = new JButton("Search");
        buttonPanel.add(btnSearch);

        panel.add(startPanel);
        panel.add(endPanel);
        panel.add(buttonPanel);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        btnSearch.addActionListener(this::searchDoctors);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }


    private void searchDoctors(ActionEvent event) {
        String startDateTimeStr = txtStartDateTime.getText();
        String endDateTimeStr = txtEndDateTime.getText();

        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT+3"));
            LocalDateTime oneYearLater = now.plusYears(1).toLocalDateTime();

            if (startDateTime.isBefore(now.toLocalDateTime()) || endDateTime.isBefore(now.toLocalDateTime())) {
                JOptionPane.showMessageDialog(this, "You cannot search for times in the past.");
                return;
            }

            if (startDateTime.isAfter(oneYearLater) || endDateTime.isAfter(oneYearLater)) {
                JOptionPane.showMessageDialog(this, "You cannot search for times more than one year into the future.");
                return;
            }

            if (endDateTime.isBefore(startDateTime)) {
                JOptionPane.showMessageDialog(this, "End time cannot be before start time.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT d.doctorId, d.doctorName, d.departmentId, d.doctorExpertise FROM doctor d " +
                        "WHERE NOT EXISTS (" +
                        "SELECT 1 FROM appointment a WHERE a.doctorId = d.doctorId AND " +
                        "(a.appointmentStart < ? AND a.appointmentFinish > ?)) AND NOT EXISTS (" +
                        "SELECT 1 FROM doctorschedule ds WHERE ds.doctorId = d.doctorId AND " +
                        "(ds.dayoffStart < ? AND ds.dayoffEnd > ?))";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(endDateTime));
                    pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(startDateTime));
                    pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(endDateTime));
                    pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(startDateTime));
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
