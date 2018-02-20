package com.alliedtech.lollibotapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.alliedtech.lollibotapp.adapters.DailyScheduleRecyclerAdapter;
import com.alliedtech.lollibotapp.decoration.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DailyScheduleFragment extends Fragment {

    private View view;
    private Button addRunButton;
    private RecyclerView recyclerView;
    private ArrayList<Run> hours;
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
                hours.add(new Run(null, null));
                dailyScheduleRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Setting recycler view for the gridview
     */
    private void setGridView() {
        recyclerView = view.findViewById(R.id.dayGridView);
        hours = new ArrayList<>();

        dailyScheduleRecyclerAdapter = new DailyScheduleRecyclerAdapter(getActivity(), getContext(), hours);
        recyclerView.setAdapter(dailyScheduleRecyclerAdapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new SpacesItemDecoration(1));
    }
}
