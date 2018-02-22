package com.alliedtech.lollibotapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.alliedtech.lollibotapp.adapters.DailyScheduleAdapter;
import com.alliedtech.lollibotapp.decoration.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DailyScheduleFragment extends Fragment {

    private View view;
    private Button addRunButton;
    private RecyclerView recyclerView;
    private ArrayList<Run> runs;
    private DailyScheduleAdapter dailyScheduleRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.daily_schedule_fragment, container, false);

        setGridView();
        setAddRunButton();

        return view;
    }

    private void setAddRunButton() {
        addRunButton = view.findViewById(R.id.addRunButton);

        // TODO: array hours gets cleared when adding new item
        // TODO: that's because we're not actually adding date to the array
        addRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                runs.add(new Run(null, null));
                dailyScheduleRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Setting recycler view for the gridview
     */
    private void setGridView() {
        recyclerView = view.findViewById(R.id.dayGridView);
        runs = new ArrayList<>();

        dailyScheduleRecyclerAdapter = new DailyScheduleAdapter(getActivity(), getContext(), runs);
        recyclerView.setAdapter(dailyScheduleRecyclerAdapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new SpacesItemDecoration(1));
    }
}
