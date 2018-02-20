package com.alliedtech.lollibotapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.alliedtech.lollibotapp.adapters.DailyScheduleAdapter;
import com.alliedtech.lollibotapp.adapters.DailyScheduleRecyclerAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DailyScheduleFragment extends Fragment {

    private View view;
    private Button addRunButton;
    private GridView gridView;
    private RecyclerView recyclerView;
    private ArrayList<Run> hours;
    private DailyScheduleAdapter dailyScheduleAdapter;
    private DailyScheduleRecyclerAdapter dailyScheduleRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.daily_schedule_fragment, container, false);

        setGridView();
        setAddRunButton();

        return view;
    }

    private void setAddRunButton() {
        addRunButton = view.findViewById(R.id.addRunButton);

        addRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                hours.add(new Run(new Date(), null));
                dailyScheduleRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Setting recycler view for the gridview
     */
    private void setGridView() {
        // TODO: Change to recyclerView with layout manager
        // TODO: as described in https://stackoverflow.com/questions/21203951/how-to-handle-gridview-with-cell-of-different-heights
        recyclerView = view.findViewById(R.id.dayGridView);
//        gridView = view.findViewById(R.id.dayGridView);
        hours = new ArrayList<>();

        dailyScheduleRecyclerAdapter = new DailyScheduleRecyclerAdapter(getActivity(), getContext(), hours);
        recyclerView.setAdapter(dailyScheduleRecyclerAdapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

//        dailyScheduleAdapter = new DailyScheduleAdapter(getActivity(), getContext(), hours);
//        gridView.setAdapter(dailyScheduleAdapter);
    }
}
