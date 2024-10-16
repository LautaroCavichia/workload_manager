package com.lc_unifi.models;

import java.time.LocalTime;
import java.util.LinkedList;

public class Store {
    private String storeId;
    private String name;
    private String address;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private LinkedList<Worker> workers;

    // Constructors, getters, and setters

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public LinkedList<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(LinkedList<Worker> workers) {
        this.workers = workers;
    }

    public int getTotalAvailableHours() {
        int totalHours = 0;
        for (Worker worker : workers) {
            totalHours += worker.getHoursBank();
        }
        return totalHours;
    }
}