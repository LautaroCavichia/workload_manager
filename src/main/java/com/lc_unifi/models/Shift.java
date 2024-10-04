package com.lc_unifi.models;

import java.time.LocalTime;
import java.util.Random;

public class Shift {
    private int id;
    private LocalTime startTime;
    private LocalTime endTime;
    //TODO: Add a breakTime field for the LONG shift type
    private Worker worker;

    public Shift(Worker worker ,LocalTime startTime, LocalTime endTime) {
        this.id = generateId();
        this.worker = worker;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public int generateId() {
        return new Random().nextInt(1000); //FIXME: This is a temporary solution
    }

    public Worker getWorker() {
        return worker;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
