package com.lc_unifi.views;

import com.lc_unifi.models.Worker;
import com.lc_unifi.models.Shift;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class MonthlyView extends JPanel{
    private YearMonth currentYearMonth;
    private JPanel calendarGrid;
    private JLabel monthLabel;
    private Map<LocalDate, List<Shift>> shiftsPerDay;
    private JScrollPane scrollPane;

    public MonthlyView(Map<LocalDate, List<Shift>> shiftsPerDay){
        currentYearMonth = YearMonth.now();
        this.shiftsPerDay = shiftsPerDay;
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
        updateCalendar();

        scrollPane = new JScrollPane(calendarGrid);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateMonthLabel(){
        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear()); //TODO: Translate month name to Italian
    }

    private void updateCalendar(){
        calendarGrid.removeAll();
        updateMonthLabel();

        //String[] daysOfWeek = {"Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"}; //Starts from Monday (requirement)
        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
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
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new GridLayout(5,1));
            dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            JPanel dayNumberPanel = new JPanel();
            dayNumberPanel.setLayout(new BorderLayout());
            JLabel dayLabel = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            dayNumberPanel.add(dayLabel, BorderLayout.CENTER);
            dayPanel.add(dayNumberPanel);

            if(shiftsPerDay.containsKey(date)){
                List<Shift> shifts = shiftsPerDay.get(date);
                for (int j = 1; j <= 4; j++){
                    if (j <= shifts.size()){
                        Worker worker = shifts.get(j - 1).getWorker();
                        JLabel workerLabel = new JLabel(worker.getLastName(), SwingConstants.CENTER);
                        workerLabel.setBackground(getWorkerColor(worker));
                        workerLabel.setOpaque(true);
                        dayPanel.add(workerLabel);
                    }
                    else {
                        dayPanel.add(new JLabel(""));
                }
            }
            }
            else {
                for (int j = 1; j <= 4; j++){
                    dayPanel.add(new JLabel(""));
                }
            }

            calendarGrid.add(dayPanel);
        }

        int totalCells = 42; // 6 rows * 7 columns
        int cellsAdded = Offset + daysInMonth;
        for (int i = cellsAdded; i < totalCells; i++){
            calendarGrid.add(new JLabel(""));
        }

        revalidate();
        repaint();
    }

    private Color getWorkerColor(Worker worker){
        int hash = worker.getLastName().hashCode();
        return new Color(hash);
    }
}
