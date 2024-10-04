package com.lc_unifi.models;

import java.util.List;

public class Worker {
    private int id;
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Store mainStore;
    private int contractHours;
    private List<Request> daysOffRequests;
    private List<Request> holidayRequests;

    public Worker(String name, String lastName, int contractHours) {
        this.name = name;
        this.lastName = lastName;
        this.contractHours = contractHours;
    }

    public Worker() {

    }

    protected Store getStore() {
        return this.mainStore;
    }

    public String getLastName() {
        return lastName;
    }
}


