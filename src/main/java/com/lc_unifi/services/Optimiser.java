package com.lc_unifi.services;

import com.lc_unifi.models.Shift;
import com.lc_unifi.models.Worker;

import java.time.LocalDate;
import java.util.*;

public class Optimiser {

    private Map<LocalDate, List<Shift>> schedule;
    private Set<Worker> assignedWorkers;

    public Optimiser(Map<LocalDate, List<Shift>> schedule, Set<Worker> assignedWorkers) {
        this.schedule = schedule;
        this.assignedWorkers = assignedWorkers;
    }

    public void optimize() {
        boolean improvement = true;
        while (improvement) {
            improvement = false;
            for (LocalDate date : schedule.keySet()) {
                List<Shift> shifts = schedule.get(date);
                for (int i = 0; i < shifts.size(); i++) {
                    Shift shiftA = shifts.get(i);
                    Worker workerA = shiftA.getAssignedWorker();
                    for (int j = i + 1; j < shifts.size(); j++) {
                        Shift shiftB = shifts.get(j);
                        Worker workerB = shiftB.getAssignedWorker();

                        if (workerA != null && workerB != null && !workerA.equals(workerB)) {
                            if (attemptSwap(shiftA, workerA, shiftB, workerB)) {
                                improvement = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean attemptSwap(Shift shiftA, Worker workerA, Shift shiftB, Worker workerB) {
        if (canSwap(shiftA, workerA, shiftB, workerB)) {
            shiftA.setAssignedWorker(workerB);
            shiftB.setAssignedWorker(workerA);

            updateWorkerAfterSwap(workerA, workerB, shiftA, shiftB);

            int afterSwapDeviation = calculateTotalHoursBankDeviation();

            shiftA.setAssignedWorker(workerA);
            shiftB.setAssignedWorker(workerB);
            revertWorkerAfterSwap(workerA, workerB, shiftA, shiftB);

            int beforeSwapDeviation = calculateTotalHoursBankDeviation();

            if (afterSwapDeviation < beforeSwapDeviation) {
                shiftA.setAssignedWorker(workerB);
                shiftB.setAssignedWorker(workerA);
                updateWorkerAfterSwap(workerA, workerB, shiftA, shiftB);

                assignedWorkers.add(workerA);
                assignedWorkers.add(workerB);

                return true;
            }
        }
        return false;
    }

    private boolean canSwap(Shift shiftA, Worker workerA, Shift shiftB, Worker workerB) {
        LocalDate dateA = shiftA.getDate();
        LocalDate dateB = shiftB.getDate();

        if (workerA.isAssignedOnDate(dateB) || workerB.isAssignedOnDate(dateA)) {
            return false;
        }

        workerA.removeAssignedDate(dateA);
        workerB.removeAssignedDate(dateB);

        boolean canAssignAtoB = workerA.canWork(shiftB, dateB);
        boolean canAssignBtoA = workerB.canWork(shiftA, dateA);

        workerA.addAssignedDate(dateA);
        workerB.addAssignedDate(dateB);

        return canAssignAtoB && canAssignBtoA;
    }

    private void updateWorkerAfterSwap(Worker workerA, Worker workerB, Shift shiftA, Shift shiftB) {
        int shiftHoursA = shiftDurationInHours(shiftA);
        int shiftHoursB = shiftDurationInHours(shiftB);

        workerA.addScheduledHours(-shiftHoursA + shiftHoursB);
        workerB.addScheduledHours(-shiftHoursB + shiftHoursA);

        workerA.removeAssignedDate(shiftA.getDate());
        workerA.addAssignedDate(shiftB.getDate());

        workerB.removeAssignedDate(shiftB.getDate());
        workerB.addAssignedDate(shiftA.getDate());

        workerA.updateContinuousWorkingDays(shiftB.getDate());
        workerA.setLastWorkedDate(shiftB.getDate());

        workerB.updateContinuousWorkingDays(shiftA.getDate());
        workerB.setLastWorkedDate(shiftA.getDate());

        assignedWorkers.add(workerA);
        assignedWorkers.add(workerB);
    }

    private void revertWorkerAfterSwap(Worker workerA, Worker workerB, Shift shiftA, Shift shiftB) {
        int shiftHoursA = shiftDurationInHours(shiftA);
        int shiftHoursB = shiftDurationInHours(shiftB);

        workerA.addScheduledHours(shiftHoursA - shiftHoursB);
        workerB.addScheduledHours(shiftHoursB - shiftHoursA);

        workerA.removeAssignedDate(shiftB.getDate());
        workerA.addAssignedDate(shiftA.getDate());

        workerB.removeAssignedDate(shiftA.getDate());
        workerB.addAssignedDate(shiftB.getDate());

        workerA.updateContinuousWorkingDays(shiftA.getDate());
        workerA.setLastWorkedDate(shiftA.getDate());

        workerB.updateContinuousWorkingDays(shiftB.getDate());
        workerB.setLastWorkedDate(shiftB.getDate());
    }

    private int calculateTotalHoursBankDeviation() {
        int totalDeviation = 0;
        for (Worker worker : assignedWorkers) {
            int contractHours = worker.getContractHours() * 4;
            int scheduledHours = worker.getScheduledHours();
            int hoursBank = worker.getHoursBank() + (scheduledHours - contractHours);
            totalDeviation += Math.abs(hoursBank);
        }
        return totalDeviation;
    }

    private int shiftDurationInHours(Shift shift) {
        return (int) java.time.Duration.between(shift.getStartTime(), shift.getEndTime()).toHours();
    }
}