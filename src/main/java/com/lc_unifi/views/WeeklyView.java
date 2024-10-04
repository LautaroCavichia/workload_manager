package com.lc_unifi.views;

import com.lc_unifi.models.Shift;
import com.lc_unifi.models.Worker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class WeeklyView extends JPanel {
    private Map<LocalDate, List<Shift>> shiftsPerDay;
    private LocalDate currentWeekStart;

    public WeeklyView(Map<LocalDate, List<Shift>> shiftsPerDay, LocalDate currentWeekStart) {
        this.shiftsPerDay = shiftsPerDay;
        this.currentWeekStart = currentWeekStart;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new GridLayout(1, 7));

        for (int i = 0; i < 7; i++) {
            LocalDate date = currentWeekStart.plusDays(i);
            JPanel dayPanel = new JPanel(new GridLayout(5,1));
            dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            JLabel dateLabel = new JLabel(date.toString(), SwingConstants.CENTER);
            dayPanel.add(dateLabel);

            if (shiftsPerDay.containsKey(date)) {
                List<Shift> shifts = shiftsPerDay.get(date);
                for (Shift shift : shifts) {
                    Worker worker = shift.getWorker();
                    JLabel shiftLabel = new JLabel(shift.getWorker().getLastName() + " " + shift.getStartTime() + " - " + shift.getEndTime());
                    shiftLabel.setOpaque(true);
                    shiftLabel.setBackground(getWorkerColor(worker));
                    dayPanel.add(shiftLabel);
                }
            }
            else {
                JLabel noShiftLabel = new JLabel("No shifts");
                dayPanel.add(noShiftLabel);
            }

            add(dayPanel);
        }
    }

    private Color getWorkerColor(Worker worker) {
        int hash = worker.getLastName().hashCode();
        return new Color(hash);
    }
}
