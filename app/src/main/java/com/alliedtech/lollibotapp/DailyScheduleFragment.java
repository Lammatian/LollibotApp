package com.alliedtech.lollibotapp;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.alliedtech.lollibotapp.adapters.DailyScheduleAdapter;
import com.alliedtech.lollibotapp.decoration.SpacesItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyScheduleFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private View view;
    private Button addDateButton;
    private Button addRunButton;
    private RecyclerView recyclerView;
    private ArrayList<Run> runs;
    private DailyScheduleFragment fragment = this;
    private DailyScheduleAdapter dailyScheduleRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.daily_schedule_fragment, container, false);

        setAddDate();
        setGridView();
        setAddRunButton();

        return view;
    }

    private void setAddDate() {
        addDateButton = view.findViewById(R.id.addDateButton);
        addDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        fragment,
                        1970,
                        1,
                        1);
                datePickerDialog.show();
            }
        });
    }

    private void setAddRunButton() {
        addRunButton = view.findViewById(R.id.addRunButton);
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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, day);
        addDateButton.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                .format(date.getTime()));
    }
}
