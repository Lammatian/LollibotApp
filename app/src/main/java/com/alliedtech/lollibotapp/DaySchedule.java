package com.alliedtech.lollibotapp;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DaySchedule extends ArrayList<Run> {

    private Date date;
    private final DateFormat datetimeToDate =
            new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private final DateFormat dateToHours =
            new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    private Pattern datePattern = Pattern.compile("^<(../../....)\\|(\\|(..:..:..-..:..:..))*>$");

    DaySchedule(Date date) {
        this.date = date;
    }

    DaySchedule(String schedule) {
        Matcher dateMatcher = datePattern.matcher(schedule);
        String date = dateMatcher.group(1);
        String times = dateMatcher.group(2);

        try {
            setDate(datetimeToDate.parse(date));
        } catch (ParseException e) {
            Log.d("Day Schedule", "Couldn't parse date");
        }

        ArrayList<String> timeList = new ArrayList<>(
                Arrays.asList(times.split("|")).subList(1, times.length()));

        for (String s: timeList) {
            String[] startEnd = s.split("-");
            try {
                this.add(new Run(dateToHours.parse(startEnd[0]), dateToHours.parse(startEnd[1])));
            } catch (ParseException e) {
                Log.d("Day Schedule", "Couldn't parse hours");
            }
        }
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

    @Override
    public String toString() {
        String result = "<" + getFormattedDate() + "|";

        for (Run r: this) {
            result += "|" + r.getFormattedRun();
        }

        return result + ">";
    }
}
