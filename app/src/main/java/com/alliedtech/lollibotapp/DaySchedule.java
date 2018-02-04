package com.alliedtech.lollibotapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DaySchedule {

    private Date date;
    private ArrayList<DatePair> times;
    private final DateFormat datetimeToDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    DaySchedule(Date date, ArrayList<DatePair> times) {
        this.date = date;
        this.times = times;
    }

    DaySchedule(Date date) {
        this.date = date;
        this.times = new ArrayList<>();
    }

    public int getScheduledRuns() {
        return this.times.size();
    }

    public String getFormattedDate() {
        return datetimeToDate.format(this.date);
    }

    static class DatePair {
        final Date start;
        final Date end;

        DatePair(Date start, Date end) {
            this.start = start;
            this.end = end;
        }
    }
}
