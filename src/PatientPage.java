import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PatientPage extends JFrame {

    private int patientId;

    public PatientPage(int patientId) {
        this.patientId = patientId;

        setTitle("Patient Page");
        setSize(300, 200);
        setLayout(new FlowLayout());

        JButton btnShowDoctorsByExpertise = new JButton("Show Doctors By Expertise");
        btnShowDoctorsByExpertise.addActionListener(e -> new ShowDoctorsByExpertise());
        add(btnShowDoctorsByExpertise);

        JButton btnMakeAppointment = new JButton("Make Appointment");
        btnMakeAppointment.addActionListener(e -> new MakeAppointment(patientId));
        add(btnMakeAppointment);

        JButton btnListAppointments = new JButton("List Appointments");
        btnListAppointments.addActionListener(e -> new ListAppointments(patientId));
        add(btnListAppointments);

        JButton btnDeleteAppointment = new JButton("Delete Appointment");
        btnDeleteAppointment.addActionListener(e -> new DeleteAppointment(patientId));
        add(btnDeleteAppointment);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new PatientPage(1); // Example patient ID, replace with actual logic to determine patient ID
    }
}
