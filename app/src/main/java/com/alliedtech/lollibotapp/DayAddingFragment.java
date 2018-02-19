package com.alliedtech.lollibotapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.alliedtech.lollibotapp.adapters.DailyScheduleAdapter;

public class DayAddingFragment extends Fragment {

    private View view;
    private GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.day_adding_fragment, container, false);

        setGridView();

        return view;
    }

    /**
     * Setting recycler view for the gridview
     */
    private void setGridView() {
        gridView = view.findViewById(R.id.dayGridView);

        DailyScheduleAdapter dailyScheduleAdapter = new DailyScheduleAdapter(getActivity(), getContext(), null);
        gridView.setAdapter(dailyScheduleAdapter);
    }
}
