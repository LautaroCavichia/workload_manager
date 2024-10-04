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

    public Worker(int id, String name, String lastName, String email, String phoneNumber, Store mainStore,
                  int contractHours, List<com.lc_unifi.models.Request> daysOffRequests, List<com.lc_unifi.models.Request> holidayRequests) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.mainStore = mainStore;
        this.contractHours = contractHours;
        this.daysOffRequests = daysOffRequests;
        this.holidayRequests = holidayRequests;

    }

    public Worker() {

    }

    protected Store getStore() {
        return this.mainStore;
    }
}


