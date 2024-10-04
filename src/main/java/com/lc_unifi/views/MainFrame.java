package com.lc_unifi.views;

import javax.swing.*;
import java.awt.*;




public class MainFrame extends JFrame {
    public MainFrame(){
        setTitle("Workload Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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
        CalendarPanel calendarPanel = new CalendarPanel();
        mainPanel.add(calendarPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
