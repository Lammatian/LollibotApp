package com.alliedtech.lollibotapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.alliedtech.lollibotapp.adapters.DayScheduleAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ScheduleFragment extends Fragment {

    private View view;
    private GridView gridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.schedule_fragment, container, false);

        DaySchedule today = new DaySchedule(new Date(), new ArrayList<DaySchedule.DatePair>());
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

        setGridView();
        return view;
    }

    //Setting recycler view
    private void setGridView() {
        gridView = view.findViewById(R.id.gridview);
        ArrayList<DaySchedule> days = new ArrayList<>();
        ArrayList<DaySchedule.DatePair> times = new ArrayList<>();
        DaySchedule.DatePair dp = new DaySchedule.DatePair(new Date(), new Date());
        times.add(dp);
        DaySchedule ds = new DaySchedule(new Date(), times);
        days.add(ds);
        days.add(new DaySchedule(new Date()));
        days.add(new DaySchedule(new Date()));
        DayScheduleAdapter dayScheduleAdapter = new DayScheduleAdapter(getActivity(), getContext(), days);
        gridView.setAdapter(dayScheduleAdapter);
        days.add(new DaySchedule(new Date()));
    }
}