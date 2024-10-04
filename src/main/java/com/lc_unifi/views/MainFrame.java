package com.lc_unifi.views;

import com.lc_unifi.models.Shift;
import com.lc_unifi.models.Worker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import java.awt.*;




public class MainFrame extends JFrame {
    public MainFrame(CalendarPanel calendarPanel){
        setTitle("Workload Management System");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        InitializeComponents(calendarPanel);

    }

    private void InitializeComponents(CalendarPanel calendarPanel){
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(calendarPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    public static void main(String[] args) {
       Map<LocalDate, List<Shift>> shiftsPerDay = new HashMap<>();

        // Example shifts for testing
        LocalTime startTime = LocalTime.of(9, 0);
        List<Shift> shiftsForDay1 = new ArrayList<>();
        shiftsForDay1.add(new Shift(new Worker("Worker", "Arbol",50), startTime, startTime.plusHours(8)));
        shiftsForDay1.add(new Shift(new Worker("Worker", "Burbuja",50), startTime.plusHours(8), startTime.plusHours(16)));
        shiftsForDay1.add(new Shift(new Worker("Worker", "Color",50), startTime.plusHours(16), startTime.plusHours(24)));
        shiftsPerDay.put(LocalDate.of(2024,10,4), shiftsForDay1);

        List<Shift> shiftsForDay2 = new ArrayList<>();
        shiftsForDay2.add(new Shift(new Worker("Worker", "Domingo",50), startTime, startTime.plusHours(8)));
        shiftsForDay2.add(new Shift(new Worker("Worker", "Empoli",50), startTime.plusHours(8), startTime.plusHours(16)));
        shiftsForDay2.add(new Shift(new Worker("Worker", "Firenze",50), startTime.plusHours(16), startTime.plusHours(24)));
        shiftsPerDay.put(LocalDate.of(2024,10,5), shiftsForDay2);

        CalendarPanel calendarPanel = new CalendarPanel(shiftsPerDay);
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(calendarPanel);
            mainFrame.setVisible(true);
        });
    }
}
