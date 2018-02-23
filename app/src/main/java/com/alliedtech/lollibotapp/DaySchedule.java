package com.alliedtech.lollibotapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DaySchedule extends ArrayList<Run> {

    private Date date;
    private ArrayList<Run> runs;
    private final DateFormat datetimeToDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    DaySchedule(Date date, ArrayList<Run> times) {
        this.date = date;
        this.runs = times;
    }

    DaySchedule(Date date) {
        this.date = date;
        this.runs = new ArrayList<>();
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getScheduledRuns() {
        return this.runs.size();
    }

    public String getFormattedDate() {
        return datetimeToDate.format(this.date);
    }

    public boolean isReady() { return date != null && !runs.isEmpty() && runs.get(runs.size() - 1).isSetUp(); }
}
