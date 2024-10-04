package com.lc_unifi.enums;

import com.lc_unifi.models.Shift;
import java.time.LocalTime;
import java.util.List;

import com.lc_unifi.models.Store;
import com.lc_unifi.models.Worker;

public enum SchedulePreset {
    TWO_SHORT("2 Persone (Corto)", (store) -> {
        LocalTime openingTime = store.getOpeningTime();
        LocalTime closingTime = store.getClosingTime();
        Worker worker1 = store.getWorkers().getFirst();

        Shift firstShift = new Shift(worker1, openingTime, openingTime.plusHours(5).plusMinutes(30));
        Shift secondShift = new Shift(worker1, openingTime.plusHours(5).plusMinutes(30), closingTime);

        return new Shift[] {firstShift, secondShift};
    }),
    TWO_LONG("2 Persone (Lungo)", (store) -> {
        LocalTime openingTime = store.getOpeningTime();
        LocalTime closingTime = store.getClosingTime();
        Worker worker1 = store.getWorkers().getFirst();

        Shift firstShift = new Shift(worker1, openingTime, openingTime.plusHours(8));
        Shift secondShift = new Shift(worker1, openingTime.plusHours(2), closingTime);

        return new Shift[] {firstShift, secondShift};
    }),
    THREE_SHORT("3 Persone (Corto)", (store) -> {
        LocalTime openingTime = store.getOpeningTime();
        LocalTime closingTime = store.getClosingTime();
        Worker worker1 = store.getWorkers().getFirst();

        Shift firstShift = new Shift(worker1, openingTime, openingTime.plusHours(5).plusMinutes(30));
        Shift secondShift = new Shift(worker1, openingTime.plusHours(2), openingTime.plusHours(7));
        Shift thirdShift = new Shift(worker1, openingTime.plusHours(5).plusMinutes(300), closingTime);

        return new Shift[] {firstShift, secondShift, thirdShift};
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

    public Shift[] generateShifts(Store store) {
        return shiftGenerator.generateShifts(store);
    }

    interface ShiftGenerator {
        Shift[] generateShifts(Store store);
    }
}
