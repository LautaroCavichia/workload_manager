package com.lc_unifi.models;

import com.lc_unifi.enums.RequestType;
import java.time.LocalDate;

public class Request {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private RequestType type;
    //TODO: Add a field to store the status of the request
}
