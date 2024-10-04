package com.lc_unifi.views;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;

public class CalendarPanel extends JPanel{
    private YearMonth currentYearMonth;
    private JPanel calendarGrid;
    private JLabel monthLabel;

    public CalendarPanel(){
        currentYearMonth = YearMonth.now();
        initializeComponents();
    }

    private void initializeComponents(){
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        JButton previousButton = new JButton("<");
        JButton nextButton = new JButton(">");

        monthLabel = new JLabel("", SwingConstants.CENTER);
        updateMonthLabel();

        previousButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendar();
        });

        nextButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendar();
        });

        headerPanel.add(previousButton, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        calendarGrid = new JPanel(new GridLayout(0, 7));
        add(calendarGrid, BorderLayout.CENTER);

        updateCalendar();
    }

    private void updateMonthLabel(){
        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear()); //TODO: Translate month name to Italian
    }

    private void updateCalendar(){
        calendarGrid.removeAll();
        updateMonthLabel();

        String[] daysOfWeek = {"Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"}; //Starts from Monday (requirement)
        for(String day : daysOfWeek){
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            calendarGrid.add(dayLabel);
        }

        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int DayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

        int Offset = DayOfWeek % 7;
        for(int i = 0; i < Offset; i++){
            calendarGrid.add(new JLabel(""));
        }

        int daysInMonth = currentYearMonth.lengthOfMonth();
        for(int i = 1; i <= daysInMonth; i++){
            LocalDate date = currentYearMonth.atDay(i);
            JButton dayButton = new JButton(String.valueOf(i));
            dayButton .addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Hai selezionato " + date);
            });
            calendarGrid.add(dayButton);
        }

        int totalCells = 42; // 6 rows * 7 columns
        int cellsAdded = Offset + daysInMonth;
        for (int i = cellsAdded; i < totalCells; i++){
            calendarGrid.add(new JLabel(""));
        }

        revalidate();
        repaint();
    }
}
