import javax.swing.*;
import java.awt.*;


public class NursePage extends JFrame {

    private int nurseId;

    public NursePage(int nurseId) {
        this.nurseId = nurseId;
        setTitle("Nurse Page - ID: " + nurseId);
        setSize(300, 200);
        setLayout(new FlowLayout());

        JButton btnShowAvailableRooms = new JButton("Room Availability");
        btnShowAvailableRooms.addActionListener(e -> new ShowAvailableRooms());
        add(btnShowAvailableRooms);

        JButton btnShowUpcomingAssignments = new JButton("Show Upcoming Assignments");
        btnShowUpcomingAssignments.addActionListener(e -> new ShowUpcomingAssignments(nurseId));
        add(btnShowUpcomingAssignments);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
