package com.lc_unifi.models;

import java.time.LocalDate;
import java.util.*;

public class Worker {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Store mainStore;
    private int contractHours;
    private int hoursBank;
    private List<LocalDate> daysOffRequests;
    private List<LocalDate> holidayRequests;

    // Scheduling state
    private int scheduledHours;
    private int continuousWorkingDays;
    private LocalDate lastWorkedDate;
    private Set<LocalDate> assignedDates;

    public Worker(String firstName, String lastName, int contractHours) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.contractHours = contractHours;
        this.daysOffRequests = new ArrayList<>();
        this.holidayRequests = new ArrayList<>();
        this.hoursBank = 0;
        this.scheduledHours = 0;
        this.continuousWorkingDays = 0;
        this.lastWorkedDate = null;
        this.assignedDates = new HashSet<>();
    }

    public Worker() {
        this.daysOffRequests = new ArrayList<>();
        this.holidayRequests = new ArrayList<>();
        this.hoursBank = 0;
        this.scheduledHours = 0;
        this.continuousWorkingDays = 0;
        this.lastWorkedDate = null;
        this.assignedDates = new HashSet<>();
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Store getMainStore() {
        return mainStore;
    }

    public void setMainStore(Store mainStore) {
        this.mainStore = mainStore;
    }

    public int getContractHours() {
        return contractHours;
    }

    public void setContractHours(int contractHours) {
        this.contractHours = contractHours;
    }

    public int getHoursBank() {
        return hoursBank;
    }

    public void setHoursBank(int hoursBank) {
        this.hoursBank = hoursBank;
    }

    public List<LocalDate> getDaysOffRequests() {
        return daysOffRequests;
    }

    public void addDayOffRequest(LocalDate date) {
        daysOffRequests.add(date);
    }

    public void removeDayOffRequest(LocalDate date) {
        daysOffRequests.remove(date);
    }

    public List<LocalDate> getHolidayRequests() {
        return holidayRequests;
    }

    public void addHolidayRequest(LocalDate date) {
        holidayRequests.add(date);
    }

    public void removeHolidayRequest(LocalDate date) {
        holidayRequests.remove(date);
    }

    public int getTotalAvailableHours() {
        return (contractHours * 4) + hoursBank;
    }

    // Scheduling state methods

    public int getScheduledHours() {
        return scheduledHours;
    }

    public void addScheduledHours(int hours) {
        this.scheduledHours += hours;
    }

    public int getContinuousWorkingDays() {
        return continuousWorkingDays;
    }

    public void setContinuousWorkingDays(int continuousWorkingDays) {
        this.continuousWorkingDays = continuousWorkingDays;
    }

    public LocalDate getLastWorkedDate() {
        return lastWorkedDate;
    }

    public void setLastWorkedDate(LocalDate lastWorkedDate) {
        this.lastWorkedDate = lastWorkedDate;
    }

    public Set<LocalDate> getAssignedDates() {
        return assignedDates;
    }

    public void addAssignedDate(LocalDate date) {
        assignedDates.add(date);
    }

    public boolean isAssignedOnDate(LocalDate date) {
        return assignedDates.contains(date);
    }

    protected Store getStore() {
        return mainStore;
    }
}