package com.lc_unifi.views;

import com.lc_unifi.models.Worker;
import com.lc_unifi.models.Shift;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class CalendarPanel extends JPanel{
    private YearMonth currentYearMonth;
    private JPanel calendarGrid;
    private JLabel monthLabel;
    private Map<LocalDate, List<Shift>> shiftsPerDay;

    public CalendarPanel(Map<LocalDate, List<Shift>> shiftsPerDay){
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
        add(calendarGrid, BorderLayout.CENTER);

        updateCalendar();
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
            dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
            dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            JLabel dayLabel = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            dayPanel.add(dayLabel);

            if(shiftsPerDay.containsKey(date)){
                List<Shift> shifts = shiftsPerDay.get(date);
                for (Shift shift : shifts) {
                    Worker worker = shift.getWorker();
                    JLabel workerLabel = new JLabel(worker.getLastName() + " " + shift.getStartTime().toString()
                            + " - " + shift.getEndTime().toString());
                    workerLabel.setOpaque(true);
                    workerLabel.setForeground(Color.WHITE);
                    workerLabel.setBackground(getWorkerColor(worker));
                    dayPanel.add(workerLabel);
                }
            }
            else {
                JLabel noShiftLabel = new JLabel("No shift", SwingConstants.CENTER);
                dayPanel.add(noShiftLabel, BorderLayout.CENTER);
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
        return new Color((hash >> 16) & 0xFF, (hash >> 8) & 0xFF, hash & 0xFF);
    }
}
