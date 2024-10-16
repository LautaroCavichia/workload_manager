package com.lc_unifi.enums;

import com.lc_unifi.models.Shift;
import com.lc_unifi.models.Store;

import java.time.LocalDate;
import java.time.LocalTime;

public enum SchedulePreset {
    TWO_SHORT("2 Persone (Corto)", (store, date) -> {
        LocalTime openingTime = store.getOpeningTime();
        LocalTime closingTime = store.getClosingTime();

        Shift firstShift = new Shift(openingTime, openingTime.plusHours(5).plusMinutes(30), store, date);
        Shift secondShift = new Shift(openingTime.plusHours(5).plusMinutes(30), closingTime, store, date);

        return new Shift[] { firstShift, secondShift };
    }),
    TWO_LONG("2 Persone (Lungo)", (store, date) -> {
        LocalTime openingTime = store.getOpeningTime();
        LocalTime closingTime = store.getClosingTime();

        Shift firstShift = new Shift(openingTime, openingTime.plusHours(8), store, date);
        Shift secondShift = new Shift(LocalTime.of(11, 30), closingTime, store, date);

        return new Shift[] { firstShift, secondShift };
    }),
    THREE_SHORT("3 Persone (Corto)", (store, date) -> {
        LocalTime openingTime = store.getOpeningTime();
        LocalTime closingTime = store.getClosingTime();

        Shift firstShift = new Shift(openingTime, openingTime.plusHours(5).plusMinutes(30), store, date);
        Shift secondShift = new Shift(openingTime.plusHours(2), openingTime.plusHours(10), store, date);
        Shift thirdShift = new Shift(openingTime.plusHours(5).plusMinutes(30), closingTime, store, date);

        return new Shift[] { firstShift, secondShift, thirdShift };
    });

    private String name;
    private ShiftGenerator shiftGenerator;

    SchedulePreset(String name, ShiftGenerator shiftGenerator) {
        this.name = name;
        this.shiftGenerator = shiftGenerator;
    }

    public String getName() {
        return name;
    }

    public Shift[] generateShifts(Store store, LocalDate date) {
        return shiftGenerator.generateShifts(store, date);
    }

    interface ShiftGenerator {
        Shift[] generateShifts(Store store, LocalDate date);
    }
}