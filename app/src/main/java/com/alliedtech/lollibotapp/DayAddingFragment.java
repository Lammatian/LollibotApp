package com.alliedtech.lollibotapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.alliedtech.lollibotapp.adapters.DailyScheduleAdapter;

import java.util.ArrayList;
import java.util.Date;

public class DayAddingFragment extends Fragment {

    private View view;
    private Button addRunButton;
    private GridView gridView;
    private ArrayList<Pair<Date, Date>> hours;
    private DailyScheduleAdapter dailyScheduleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.day_adding_fragment, container, false);

        setGridView();
        setAddRunButton();

        return view;
    }

    private void setAddRunButton() {
        addRunButton = view.findViewById(R.id.addRunButton);

        addRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hours.add(new Pair<Date, Date>(new Date(), new Date()));
                dailyScheduleAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Setting recycler view for the gridview
     */
    private void setGridView() {
        gridView = view.findViewById(R.id.dayGridView);
        hours = new ArrayList<>();

        dailyScheduleAdapter = new DailyScheduleAdapter(getActivity(), getContext(), hours);
        gridView.setAdapter(dailyScheduleAdapter);
    }
}
