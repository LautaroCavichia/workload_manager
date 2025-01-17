package com.lc_unifi.services;

import com.lc_unifi.models.*;
import com.lc_unifi.enums.SchedulePreset;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class Scheduler {

    private Store store;
    private List<Store> allStores;
    private SchedulePreset schedulePreset;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean thirdPersonOnWeekends;
    private Map<LocalDate, List<Shift>> schedule;
    private Set<Worker> assignedWorkers;
    private int totalShiftHours;
    private boolean isCoperturaNeeded;

    public Scheduler(Store store, List<Store> allStores, SchedulePreset schedulePreset, LocalDate startDate, LocalDate endDate, boolean addThirdPersonOnWeekends) {
        this.store = store;
        this.allStores = allStores;
        this.schedulePreset = schedulePreset;
        this.startDate = startDate;
        this.endDate = endDate;
        this.thirdPersonOnWeekends = addThirdPersonOnWeekends;
        this.schedule = new LinkedHashMap<>();
        this.assignedWorkers = new HashSet<>();
        this.totalShiftHours = 0;
        this.isCoperturaNeeded = false;
    }

    public Map<LocalDate, List<Shift>> generateSchedule() {
        generateShifts();
        assignWorkersToShifts();
        Optimiser optimiser = new Optimiser(schedule, assignedWorkers);
        optimiser.optimize();
        updateHoursBank();
        return schedule;
    }

    private void generateShifts() {
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            SchedulePreset presetToUse = schedulePreset;

            if (thirdPersonOnWeekends && isWeekend(date) && (schedulePreset == SchedulePreset.TWO_SHORT || schedulePreset == SchedulePreset.TWO_LONG)) {
                presetToUse = SchedulePreset.THREE_SHORT;
            }

            Shift[] shifts = presetToUse.generateShifts(store, date);
            schedule.put(date, Arrays.asList(shifts));

            for (Shift shift : shifts) {
                totalShiftHours += shiftDurationInHours(shift);
            }
        }
    }

    private void determineCoperturaNeed() {
        int totalHours = store.getTotalAvailableHours();
        isCoperturaNeeded = totalHours < totalShiftHours;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
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
        if (isCoperturaNeeded) {
            Random random = new Random();
            double coperturaProbability = 0.2;
            if (random.nextDouble() < coperturaProbability) {
                List<Worker> externalWorkers = getExternalWorkers(store);
                if (!externalWorkers.isEmpty()) {
                    Worker externalWorker = externalWorkers.get(random.nextInt(externalWorkers.size()));
                    if (externalWorker.canWork(shift, date)) {
                        shift.setAssignedWorker(externalWorker);
                        updateWorkerAfterAssignment(externalWorker, shift, date);
                        assignedWorkers.add(externalWorker);
                        return true;
                    }
                }
            }
        }
        for (Worker worker : availableWorkers) {
            if (worker.getScheduledHours() == minHours) {
                leastLoadedWorkers.add(worker);
            } else {
                break;
            }
        }

        Collections.shuffle(leastLoadedWorkers);

        for (Worker worker : leastLoadedWorkers) {
            if (worker.canWork(shift, date)) {
                shift.setAssignedWorker(worker);
                updateWorkerAfterAssignment(worker, shift, date);
                assignedWorkers.add(worker);
                return true;
            }
        }

        for (int i = leastLoadedWorkers.size(); i < availableWorkers.size(); i++) {
            Worker worker = availableWorkers.get(i);
            if (worker.canWork(shift, date)) {
                shift.setAssignedWorker(worker);
                updateWorkerAfterAssignment(worker, shift, date);
                assignedWorkers.add(worker);
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

    private void updateWorkerAfterAssignment(Worker worker, Shift shift, LocalDate date) {
        int shiftHours = shiftDurationInHours(shift);
        worker.addScheduledHours(shiftHours);
        worker.updateContinuousWorkingDays(date);
        worker.setLastWorkedDate(date);
        worker.addAssignedDate(date);
    }

    private boolean attemptCopertura(Shift shift, LocalDate date) {
        List<Worker> externalWorkers = getExternalWorkers(store);

        externalWorkers.sort(Comparator.comparingInt(Worker::getScheduledHours));

        for (Worker worker : externalWorkers) {
            if (worker.canWork(shift, date)) {
                shift.setAssignedWorker(worker);
                updateWorkerAfterAssignment(worker, shift, date);
                assignedWorkers.add(worker);
                return true;
            }
        }
        return false;
    }

    private List<Worker> getExternalWorkers(Store currentStore) {
        List<Worker> externalWorkers = new ArrayList<>();
        for (Store otherStore : allStores) {
            if (!otherStore.equals(currentStore)) {
                externalWorkers.addAll(otherStore.getWorkers());
            }
        }
        return externalWorkers;
    }

    private int shiftDurationInHours(Shift shift) {
        return (int) java.time.Duration.between(shift.getStartTime(), shift.getEndTime()).toHours();
    }

    private void updateHoursBank() {
        for (Worker worker : assignedWorkers) {
            worker.updateHoursBank();
        }
    }
}