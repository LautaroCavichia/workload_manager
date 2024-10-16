package com.lc_unifi.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Shift {
    private String shiftId;
    private Worker assignedWorker;
    private LocalTime startTime;
    private LocalTime endTime;
    private Store store;
    private LocalDate date;

    public Shift(LocalTime startTime, LocalTime endTime, Store store, LocalDate date) {
        this.shiftId = java.util.UUID.randomUUID().toString();
        this.assignedWorker = null;
        this.startTime = startTime;
        this.endTime = endTime;
        this.store = store;
        this.date = date;
    }

    // Getters and setters

    public String getShiftId() {
        return shiftId;
    }

    public Worker getAssignedWorker() {
        return assignedWorker;
    }

    public void setAssignedWorker(Worker assignedWorker) {
        this.assignedWorker = assignedWorker;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Store getStore() {
        return store;
    }

    public LocalDate getDate() {
        return date;
    }
}