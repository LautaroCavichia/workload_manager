package com.lc_unifi.models;

import java.time.LocalTime;
import java.util.Random;

public class Shift {
    private int id;
    private LocalTime startTime;
    private LocalTime endTime;
    //TODO: Add a breakTime field for the LONG shift type

    public Shift(LocalTime startTime, LocalTime endTime) {
        this.id = generateId();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int generateId() {
        return new Random().nextInt(1000); //FIXME: This is a temporary solution
    }
}
