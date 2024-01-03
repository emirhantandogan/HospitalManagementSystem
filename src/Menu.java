import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {

    public Menu() {
        setTitle("Hospital Management System - Main Menu");
        setSize(400, 200);
        setLayout(new FlowLayout());

        JButton btnRegisterPatient = new JButton("Register Patient");
        JButton btnLoginPatient = new JButton("Login Patient");
        JButton btnRegisterAdmin = new JButton("Register Admin");
        JButton btnLoginAdmin = new JButton("Login Admin");
        JButton btnLoginDoctor = new JButton("Login Doctor");
        JButton btnLoginNurse = new JButton("Login Nurse");

        btnRegisterPatient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterPatient();
            }
        });

        btnLoginPatient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PatientLogin();
            }
        });

        btnRegisterAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterAdmin();
            }
        });

        btnLoginAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminLogin();
            }
        });
        btnLoginDoctor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DoctorLogin();
            }
        });
        btnLoginNurse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NurseLogin();
            }
        });

        add(btnRegisterPatient);
        add(btnLoginPatient);
        add(btnRegisterAdmin);
        add(btnLoginAdmin);
        add(btnLoginDoctor);
        add(btnLoginNurse);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Menu();
    }
}
