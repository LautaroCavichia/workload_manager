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
    private Map<LocalDate, List<Shift>> shiftsPerDay;
    private JPanel calendarView;
    public MainFrame(Map<LocalDate, List<Shift>> shiftsPerDay){
        setTitle("Workload Management System");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.shiftsPerDay = shiftsPerDay;

        InitializeComponents();

    }

    private void InitializeComponents(){
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel toggleViewPanel = new JPanel();
        JButton toggleViewButton = new JButton("Switch to Weekly View");
        JMenuItem toggleViewMenuItem = new JMenuItem("Switch to Weekly View");
        fileMenu.add(toggleViewMenuItem);
        toggleViewMenuItem.addActionListener(e -> {
            if (calendarView instanceof MonthlyView){
                switchToWeeklyView();
                toggleViewButton.setText("Switch to Monthly View");
            }
            else{
                switchToMonthlyView();
                toggleViewButton.setText("Switch to Weekly View");
            }
        });
        toggleViewPanel.add(toggleViewButton);
        mainPanel.add(toggleViewPanel, BorderLayout.NORTH);

        calendarView = new MonthlyView(shiftsPerDay);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(calendarView, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private void switchToMonthlyView(){
        MonthlyView monthlyView = new MonthlyView(shiftsPerDay);
        setContentPane(monthlyView);
        revalidate();
        repaint();
    }

    private void switchToWeeklyView(){
        LocalDate currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        WeeklyView weeklyView = new WeeklyView(shiftsPerDay, currentWeekStart);

        setContentPane(weeklyView);
        revalidate();
        repaint();
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

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(shiftsPerDay);
            mainFrame.setVisible(true);
        });
    }
}
