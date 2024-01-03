import javax.swing.*;
import java.awt.*;


public class DoctorPage extends JFrame {

    private int doctorId;

    public DoctorPage(int doctorId) {
        this.doctorId = doctorId;
        setTitle("Doctor Page - ID: " + doctorId);
        setSize(300, 200);
        setLayout(new FlowLayout());

        JButton btnUnavailableTime = new JButton("Unavailable Time");
        btnUnavailableTime.addActionListener(e -> new SetUnavailableTime(doctorId));
        add(btnUnavailableTime);

        JButton btnShowAvailableRooms = new JButton("Show Available Rooms");
        btnShowAvailableRooms.addActionListener(e -> new ShowAvailableRooms());
        add(btnShowAvailableRooms);

        JButton btnAssignRoomToAppointment = new JButton("Assign Room to Appointment");
        btnAssignRoomToAppointment.addActionListener(e -> new AssignRoomToAppointment(doctorId));
        add(btnAssignRoomToAppointment);

        JButton btnShowAppointments = new JButton("Show Upcoming Appointments");
        btnShowAppointments.addActionListener(e -> new ShowAppointmentsDoctor(doctorId));
        add(btnShowAppointments);

        JButton btnShowPastAppointments = new JButton("Show Past Appointments");
        btnShowPastAppointments.addActionListener(e -> new ShowPastAppointmentsDoctor(doctorId));
        add(btnShowPastAppointments);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


}
