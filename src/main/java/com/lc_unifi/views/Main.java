package com.lc_unifi.views;

import com.lc_unifi.models.*;
import com.lc_unifi.services.Scheduler;
import com.lc_unifi.enums.SchedulePreset;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Worker worker1 = new Worker("1", "Uno", 35);
        Worker worker2 = new Worker("2", "Due", 35);
        Worker worker3 = new Worker("3", "Tre", 30);
        Worker worker4 = new Worker("4", "Quattro", 25);

        Store store = new Store();
        store.setStoreId("S001");
        store.setName("Main Store");
        store.setOpeningTime(LocalTime.of(9, 30));
        store.setClosingTime(LocalTime.of(21, 0));
        store.setWorkers(new LinkedList<>(Arrays.asList(worker1, worker2, worker3, worker4)));

        Scheduler scheduler = new Scheduler(
                store,
                List.of(store),
                SchedulePreset.THREE_SHORT,
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 30)
        );

        Map<LocalDate, List<Shift>> schedule = scheduler.generateSchedule();

        for (Map.Entry<LocalDate, List<Shift>> entry : schedule.entrySet()) {
            LocalDate date = entry.getKey();
            List<Shift> shifts = entry.getValue();

            System.out.println("Date: " + date);
            for (Shift shift : shifts) {
                Worker assignedWorker = shift.getAssignedWorker();
                String workerName = (assignedWorker != null) ? assignedWorker.getFirstName() + " " + assignedWorker.getLastName() : "Unassigned";
                System.out.println("Shift: " + shift.getStartTime() + " - " + shift.getEndTime() + " | Assigned to: " + workerName);
            }
            System.out.println();
        }
        for ( Worker worker : store.getWorkers()) {
            System.out.println("Worker: " + worker.getFirstName() + " " + worker.getHoursBank());
        }

    }
}