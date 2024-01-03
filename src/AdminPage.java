import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class AdminPage extends JFrame {

    private JTable table;

    public AdminPage() {
        setTitle("Admin Dashboard");
        setSize(1400, 600);
        setLayout(new BorderLayout());

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 15));

        // Adding placeholder buttons
        JButton btnAddDoctor = new JButton("Add Doctor");
        btnAddDoctor.addActionListener(e -> new DoctorRegister());
        buttonPanel.add(btnAddDoctor);

        JButton btnDeleteDoctor = new JButton("Delete Doctor");
        btnDeleteDoctor.addActionListener(e -> new DeleteDoctor());
        buttonPanel.add(btnDeleteDoctor);

        JButton btnAddNurse = new JButton("Add Nurse");
        btnAddNurse.addActionListener(e -> new NurseRegister());
        buttonPanel.add(btnAddNurse);

        JButton btnDeleteNurse = new JButton("Delete Nurse");
        btnDeleteNurse.addActionListener(e -> new DeleteNurse());
        buttonPanel.add(btnDeleteNurse);

        JButton btnShowDoctors = new JButton("Show Doctors");
        btnShowDoctors.addActionListener(e -> showDoctors());
        buttonPanel.add(btnShowDoctors);

        JButton btnShowNurses = new JButton("Show Nurses");
        btnShowNurses.addActionListener(e -> showNurses());
        buttonPanel.add(btnShowNurses);

        JButton btnAddRoom = new JButton("Add Room");
        btnAddRoom.addActionListener(e -> new AddRoom());
        buttonPanel.add(btnAddRoom);

        JButton btnAddDepartment = new JButton("Add Department");
        btnAddDepartment.addActionListener(e -> new AddDepartment());
        buttonPanel.add(btnAddDepartment);

        JButton btnShowRooms = new JButton("Show Rooms");
        btnShowRooms.addActionListener(e -> showRooms());
        buttonPanel.add(btnShowRooms);

        JButton btnShowDepartments = new JButton("Show Departments");
        btnShowDepartments.addActionListener(e -> showDepartments());
        buttonPanel.add(btnShowDepartments);

        JButton btnDeleteRoom = new JButton("Delete Room");
        btnDeleteRoom.addActionListener(e -> new DeleteRoom());
        buttonPanel.add(btnDeleteRoom);

        JButton btnDeleteDepartment = new JButton("Delete Department");
        btnDeleteDepartment.addActionListener(e -> new DeleteDepartment());
        buttonPanel.add(btnDeleteDepartment);

        JButton btnShowPatients = new JButton("Show Patients");
        btnShowPatients.addActionListener(e -> showPatients());
        buttonPanel.add(btnShowPatients);

        JButton btnPatientStatistics = new JButton("Patient Statistics");
        btnPatientStatistics.addActionListener(e -> new PatientStatistics());
        buttonPanel.add(btnPatientStatistics);

        JButton btnDeptPatientRoomStats = new JButton("Dept Patient Room Stats");
        btnDeptPatientRoomStats.addActionListener(e -> new DepartmentPatientRoomStatistics());
        buttonPanel.add(btnDeptPatientRoomStats);

        JButton btnNurseAppointmentRatio = new JButton("Nurse Appointment Ratios");
        btnNurseAppointmentRatio.addActionListener(e -> new NurseAppointmentRatioStatistics());
        buttonPanel.add(btnNurseAppointmentRatio);

        JButton btnMostBookedRooms = new JButton("Most Booked Rooms");
        btnMostBookedRooms.addActionListener(e -> new MostBookedRoomStatistics());
        buttonPanel.add(btnMostBookedRooms);

        JButton btnMostRoomsBookedDoctors = new JButton("Most Rooms Booked by Doctors");
        btnMostRoomsBookedDoctors.addActionListener(e -> new MostRoomsBookedDoctorStatistics());
        buttonPanel.add(btnMostRoomsBookedDoctors);



        table = new JTable();

        JScrollPane scrollPane = new JScrollPane(table);



        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);



        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }


    public void setTableData(Object[][] data, String[] columnNames) {
        table.setModel(new DefaultTableModel(data, columnNames));
    }

    private void showPatients() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT patientId, patientName, dob, gender, address, contactInfo FROM patient";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            Object[][] data = resultSetToObjectArray(rs);
            String[] columnNames = {"Patient ID", "Name", "Date of Birth", "Gender", "Address", "Contact Info"};
            setTableData(data, columnNames);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in fetching patients: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showDoctors() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT doctorId, doctorName, doctorExpertise, departmentId, roomId FROM doctor";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            Object[][] data = resultSetToObjectArray(rs);
            String[] columnNames = {"Doctor ID", "Name", "Expertise", "Department ID", "Room ID"};
            setTableData(data, columnNames);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in fetching doctors: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    private void showNurses() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT nurseId, nurseName FROM nurse");
             ResultSet rs = pstmt.executeQuery()) {

            Object[][] data = resultSetToObjectArray(rs);
            String[] columnNames = {"Nurse ID", "Name"};
            setTableData(data, columnNames);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in fetching nurses: " + ex.getMessage());
        }
    }

    private Object[][] resultSetToObjectArray(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Object[]> records = new ArrayList<>();
        while (rs.next()) {
            Object[] record = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                record[i] = rs.getObject(i + 1);
            }
            records.add(record);
        }
        return records.toArray(new Object[0][]);
    }

    private void showRooms() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT roomId, roomType, availability, nurseId FROM room");
             ResultSet rs = pstmt.executeQuery()) {

            Object[][] data = resultSetToObjectArray(rs);
            String[] columnNames = {"Room ID", "Room Type", "Availability", "Nurse ID"};
            setTableData(data, columnNames);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in fetching rooms: " + ex.getMessage());
        }
    }

    private void showDepartments() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT departmentId, departmentName FROM department");
             ResultSet rs = pstmt.executeQuery()) {

            Object[][] data = resultSetToObjectArray(rs);
            String[] columnNames = {"Department ID", "Department Name"};
            setTableData(data, columnNames);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in fetching departments: " + ex.getMessage());
        }
    }


    public static void main(String[] args) {
        new AdminPage();
    }
}
