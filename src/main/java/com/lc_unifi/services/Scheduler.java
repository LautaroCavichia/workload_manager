package com.lc_unifi.services;

import com.lc_unifi.models.*;
import com.lc_unifi.enums.SchedulePreset;

import java.time.LocalDate;
import java.util.*;

public class Scheduler {

    private Store store;
    private List<Store> allStores;
    private SchedulePreset schedulePreset;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<LocalDate, List<Shift>> schedule;

    public Scheduler(Store store, List<Store> allStores, SchedulePreset schedulePreset, LocalDate startDate, LocalDate endDate) {
        this.store = store;
        this.allStores = allStores;
        this.schedulePreset = schedulePreset;
        this.startDate = startDate;
        this.endDate = endDate;
        this.schedule = new LinkedHashMap<>();
    }

    public Map<LocalDate, List<Shift>> generateSchedule() {
        generateShifts();
        assignWorkersToShifts();
        updateHoursBank();
        return schedule;
    }

    private void generateShifts() {
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Shift[] shifts = schedulePreset.generateShifts(store, date);
            schedule.put(date, Arrays.asList(shifts));
        }
    }

    private void assignWorkersToShifts() {
        List<Worker> allWorkers = new ArrayList<>(store.getWorkers());

        for (Map.Entry<LocalDate, List<Shift>> entry : schedule.entrySet()) {
            LocalDate date = entry.getKey();
            List<Shift> shifts = entry.getValue();

            for (Shift shift : shifts) {
                boolean assigned = assignWorkerToShift(shift, date, allWorkers);

                if (!assigned) {
                    assigned = attemptCopertura(shift, date);

                    if (!assigned) {
                        System.out.println("Unable to assign worker to shift on " + date + " from " +
                                shift.getStartTime() + " to " + shift.getEndTime());
                    }
                }
            }
        }
    }

    private boolean assignWorkerToShift(Shift shift, LocalDate date, List<Worker> workers) {
        List<Worker> availableWorkers = getAvailableWorkers(date, workers);

        availableWorkers.sort(Comparator.comparingInt(Worker::getScheduledHours));

        if (availableWorkers.isEmpty()) {
            return false;
        }

        int minHours = availableWorkers.getFirst().getScheduledHours();
        List<Worker> leastLoadedWorkers = new ArrayList<>();
        for (Worker worker : availableWorkers) {
            if (worker.getScheduledHours() == minHours) {
                leastLoadedWorkers.add(worker);
            } else {
                break;
            }
        }

        Collections.shuffle(leastLoadedWorkers);

        for (Worker worker : leastLoadedWorkers) {
            if (canWork(worker, shift, date)) {
                shift.setAssignedWorker(worker);
                updateWorkerAfterAssignment(worker, shift, date);
                return true;
            }
        }

        for (int i = leastLoadedWorkers.size(); i < availableWorkers.size(); i++) {
            Worker worker = availableWorkers.get(i);
            if (canWork(worker, shift, date)) {
                shift.setAssignedWorker(worker);
                updateWorkerAfterAssignment(worker, shift, date);
                return true;
            }
        }

        return false;
    }

    private List<Worker> getAvailableWorkers(LocalDate date, List<Worker> workers) {
        List<Worker> available = new ArrayList<>();
        for (Worker worker : workers) {
            if (!worker.getDaysOffRequests().contains(date) && !worker.isAssignedOnDate(date)) {
                available.add(worker);
            }
        }
        return available;
    }

    private boolean canWork(Worker worker, Shift shift, LocalDate date) {

        if (worker.getDaysOffRequests().contains(date)) {
            return false;
        }

        if (worker.isAssignedOnDate(date)) {
            return false;
        }

        if (worker.getLastWorkedDate() != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(worker.getLastWorkedDate(), date);
            if (daysBetween == 1) {
                if (worker.getContinuousWorkingDays() >= 5) {
                    return false;
                }
            } else if (daysBetween > 1) {
                worker.setContinuousWorkingDays(0);
            }
        }

        int shiftHours = shiftDurationInHours(shift);
        int projectedHours = worker.getScheduledHours() + shiftHours;
        int maxAllowedHours = worker.getTotalAvailableHours() + (worker.getContractHours() / 3);
        if (projectedHours > maxAllowedHours) {
            return false;
        }

        return true;
    }

    private int shiftDurationInHours(Shift shift) {
        return (int) java.time.Duration.between(shift.getStartTime(), shift.getEndTime()).toHours();
    }

    private void updateWorkerAfterAssignment(Worker worker, Shift shift, LocalDate date) {
        int shiftHours = shiftDurationInHours(shift);
        worker.addScheduledHours(shiftHours);

        if (worker.getLastWorkedDate() != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(worker.getLastWorkedDate(), date);
            if (daysBetween == 1) {
                worker.setContinuousWorkingDays(worker.getContinuousWorkingDays() + 1);
            } else {
                worker.setContinuousWorkingDays(1);
            }
        } else {
            worker.setContinuousWorkingDays(1);
        }
        worker.setLastWorkedDate(date);
        worker.addAssignedDate(date);
    }

    private boolean attemptCopertura(Shift shift, LocalDate date) {
        List<Worker> externalWorkers = getExternalWorkers(store);

        externalWorkers.sort(Comparator.comparingInt(Worker::getScheduledHours));

        for (Worker worker : externalWorkers) {
            if (canWork(worker, shift, date)) {
                shift.setAssignedWorker(worker);
                updateWorkerAfterAssignment(worker, shift, date);
                return true;
            }
        }
        return false;
    }

    private List<Worker> getExternalWorkers(Store currentStore) {
        List<Worker> externalWorkers = new ArrayList<>();
        for (Store otherStore : allStores) {
            if (!otherStore.equals(currentStore)) {
                for (Worker worker : otherStore.getWorkers()) {
                    if (worker.getScheduledHours() < worker.getTotalAvailableHours()) {
                        externalWorkers.add(worker);
                    }
                }
            }
        }
        return externalWorkers;
    }

    private void updateHoursBank() {
        List<Worker> allWorkers = new ArrayList<>(store.getWorkers());
        for (Worker worker : allWorkers) {
            int scheduledHours = worker.getScheduledHours();
            int totalContractHours = worker.getContractHours() * 4;
            int newHoursBank = worker.getHoursBank() + (scheduledHours - totalContractHours);
            worker.setHoursBank(newHoursBank);
        }
    }
}