package com.alliedtech.lollibotapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DaySchedule extends ArrayList<Run> {

    private Date date;
    private final DateFormat datetimeToDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    DaySchedule(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getScheduledRuns() {
        return super.size();
    }

    public String getFormattedDate() {
        return datetimeToDate.format(this.date);
    }

    public boolean isReady() { return date != null && !isEmpty() && get(size() - 1).isSetUp(); }
}
