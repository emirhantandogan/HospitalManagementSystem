import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PatientPage extends JFrame {

    public PatientPage() {
        setTitle("Patient Page");
        setSize(300, 200);
        setLayout(new FlowLayout());

        JButton btnShowDoctorsByExpertise = new JButton("Show Doctors By Expertise");
        btnShowDoctorsByExpertise.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ShowDoctorsByExpertise();
            }
        });

        add(btnShowDoctorsByExpertise);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new PatientPage();
    }
}
