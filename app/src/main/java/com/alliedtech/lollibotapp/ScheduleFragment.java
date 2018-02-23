package com.alliedtech.lollibotapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.alliedtech.lollibotapp.adapters.ScheduleAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

public class ScheduleFragment extends Fragment {

    private View view;
    private GridView gridView;
    private TreeMap<Date, DaySchedule> daySchedules;
    private ScheduleAdapter scheduleAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.schedule_fragment, container, false);

        setTodayView();

        setGridView();
        return view;
    }

    public void bind(TreeMap<Date, DaySchedule> schedules) {
        this.daySchedules = schedules;
    }

    private void setTodayView() {
        DaySchedule today = new DaySchedule(new Date());

        TextView todayDate = view.findViewById(R.id.todayScheduleDate);
        String todayDateText = getContext().getString(R.string.schedule_today_date_text,
                String.format(Locale.ENGLISH,
                        "%s",
                        today.getFormattedDate()));

        todayDate.setText(todayDateText);

        TextView todayItems = view.findViewById(R.id.todayScheduledItems);
        String todayItemsText;

        if (today.getScheduledRuns() != 1) {
            todayItemsText = getContext().getString(R.string.schedule_items_text_plural,
                    String.format(Locale.ENGLISH,
                            "%d",
                            today.getScheduledRuns()));
        }
        else {
            todayItemsText = getContext().getString(R.string.schedule_items_text_singular,
                    String.format(Locale.ENGLISH,
                            "%d",
                            today.getScheduledRuns()));
        }

        todayItems.setText(todayItemsText);
    }

    private void setGridView() {
        gridView = view.findViewById(R.id.gridview);
        scheduleAdapter = new ScheduleAdapter(getActivity(), getContext(), daySchedules);
        gridView.setAdapter(scheduleAdapter);
    }

    public void notifyDateSetChanged() {
        scheduleAdapter.notifyDataSetChanged();
    }
}