package com.alliedtech.lollibotapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DaySchedule {

    private Date date;
    private ArrayList<DatePair> times;
    private final DateFormat datetimeToDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    public DaySchedule(Date date, ArrayList<DatePair> times) {
        this.date = date;
        this.times = times;
    }

    public DaySchedule(Date date) {
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
        public final Date start;
        public final Date end;

        public DatePair(Date start, Date end) {
            this.start = start;
            this.end = end;
        }
    }
}
