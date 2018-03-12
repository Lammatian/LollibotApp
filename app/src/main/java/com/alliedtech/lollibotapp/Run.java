package com.alliedtech.lollibotapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Run {

    private Date startDate, endDate;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

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

    String getFormattedRun() {
        return simpleDateFormat.format(getStartDate()) + "-" + simpleDateFormat.format(getEndDate());
    }

    boolean isSetUp() { return startDate != null && endDate != null; }
}
