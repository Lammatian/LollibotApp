package com.alliedtech.lollibotapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.alliedtech.lollibotapp.adapters.ScheduleDayAdapter;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment {

    private View view;
    private GridView gridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.schedule_fragment, container, false);

        setGridView();
        return view;

    }
    //Setting recycler view
    private void setGridView() {

        gridView = view.findViewById(R.id.gridview);
        ArrayList<ScheduleDay> days = new ArrayList<>();
        days.add(new ScheduleDay());
        days.add(new ScheduleDay());
        days.add(new ScheduleDay());
        ScheduleDayAdapter scheduleDayAdapter = new ScheduleDayAdapter(getActivity(), getContext(), days);
        gridView.setAdapter(scheduleDayAdapter);
    }
}