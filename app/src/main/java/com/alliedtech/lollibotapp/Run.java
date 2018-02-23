package com.alliedtech.lollibotapp;

import java.util.Date;

public class Run {

    private Date startDate, endDate;

    Run(Date start, Date end) {
        startDate = start;
        endDate = end;
    }

    public Date getStartDate() { return startDate; }

    public Date getEndDate() { return endDate; }

    public void setStartDate(Date date) {
        startDate = date;
    }

    public void setEndDate(Date date) {
        endDate = date;
    }

    boolean isSetUp() { return startDate != null && endDate != null; }
}
