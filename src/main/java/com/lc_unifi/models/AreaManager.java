package com.lc_unifi.models;

import java.util.List;

public class AreaManager extends Worker {
    private List<Store> managedStores;

    public AreaManager() {
    super();
    }

    public AreaManager(int id, String name, String lastName, String email, String phoneNumber, Store mainStore, int contractHours, List<Request> daysOffRequests, List<Request> holidayRequests, List<Store> managedStores) {
        super(id, name, lastName, email, phoneNumber, mainStore, contractHours, daysOffRequests, holidayRequests);
        this.managedStores = managedStores;
    }
}


