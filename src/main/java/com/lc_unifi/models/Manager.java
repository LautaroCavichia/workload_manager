package com.lc_unifi.models;

import java.time.LocalDate;
import java.util.List;

public class Manager extends Worker {
    public Manager() {
        super();
    }

    public List<Worker> getAvailableWorkers(LocalDate date) {
        return this.getStore().getWorkers();
    }
}
