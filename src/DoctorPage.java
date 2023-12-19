import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


}
