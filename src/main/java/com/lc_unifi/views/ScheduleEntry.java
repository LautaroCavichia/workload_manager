package com.lc_unifi.views;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class ScheduleEntry {
    private SimpleObjectProperty<LocalDate> date;
    private SimpleStringProperty workerName;
    private SimpleStringProperty shiftTime;
    private boolean isRoot;

    public ScheduleEntry(LocalDate date, boolean isRoot) {
        this.date = new SimpleObjectProperty<>(date);
        this.workerName = new SimpleStringProperty("");
        this.shiftTime = new SimpleStringProperty("");
        this.isRoot = isRoot;
    }

    public ScheduleEntry(String workerName, String shiftTime) {
        this.date = new SimpleObjectProperty<>(null);
        this.workerName = new SimpleStringProperty(workerName);
        this.shiftTime = new SimpleStringProperty(shiftTime);
        this.isRoot = false;
    }

    // Getters and setters
    public LocalDate getDate() { return date.get(); }
    public String getWorkerName() { return workerName.get(); }
    public String getShiftTime() { return shiftTime.get(); }
    public boolean isRoot() { return isRoot; }
}