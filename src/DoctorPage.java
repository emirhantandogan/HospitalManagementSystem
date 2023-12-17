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

        JButton btnDetermineSchedule = new JButton("Determine Schedule");
        btnDetermineSchedule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DetermineSchedule(doctorId);
            }
        });

        add(btnDetermineSchedule);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

}
