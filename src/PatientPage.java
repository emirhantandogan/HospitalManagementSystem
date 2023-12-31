import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PatientPage extends JFrame {

    private int patientId;

    public PatientPage(int patientId) {
        this.patientId = patientId;

        setTitle("Patient Page");
        setSize(400, 300);
        setLayout(new FlowLayout());

        JButton btnShowDoctorsByExpertise = new JButton("Search Doctors By Expertise");
        btnShowDoctorsByExpertise.addActionListener(e -> new ShowDoctorsByExpertise());
        add(btnShowDoctorsByExpertise);

        JButton btnShowDoctorsByAvailableHours = new JButton("Search Doctors By Available Hours");
        btnShowDoctorsByAvailableHours.addActionListener(e -> new ShowDoctorsByAvailableHours());
        add(btnShowDoctorsByAvailableHours);

        JButton btnShowDoctorsByTimeInterval = new JButton("Search Doctors By Time Interval");
        btnShowDoctorsByTimeInterval.addActionListener(e -> new ShowDoctorsByTimeInterval());
        add(btnShowDoctorsByTimeInterval);

        JButton btnMakeAppointment = new JButton("Make Appointment");
        btnMakeAppointment.addActionListener(e -> new MakeAppointment(patientId));
        add(btnMakeAppointment);

        JButton btnListAppointments = new JButton("List Appointments");
        btnListAppointments.addActionListener(e -> new ListAppointments(patientId));
        add(btnListAppointments);

        JButton btnDeleteAppointment = new JButton("Cancel Appointment");
        btnDeleteAppointment.addActionListener(e -> new DeleteAppointment(patientId));
        add(btnDeleteAppointment);

        JButton btnShowAppointmentsPatient = new JButton("Show Upcoming Appointments");
        btnShowAppointmentsPatient.addActionListener(e -> new ShowAppointmentsPatient(patientId));
        add(btnShowAppointmentsPatient);

        JButton btnShowPastAppointmentsPatient = new JButton("Show Past Appointments");
        btnShowPastAppointmentsPatient.addActionListener(e -> new ShowPastAppointmentsPatient(patientId));
        add(btnShowPastAppointmentsPatient);




        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


}
