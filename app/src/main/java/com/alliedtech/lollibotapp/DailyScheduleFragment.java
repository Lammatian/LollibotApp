package com.alliedtech.lollibotapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.alliedtech.lollibotapp.adapters.DailyScheduleAdapter;
import com.alliedtech.lollibotapp.decoration.SpacesItemDecoration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyScheduleFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private boolean bound = false;
    private View view;
    private Button addDateButton;
    private Button addRunButton;
    private AnimatedFloatingActionButton fabAddDay;
    private RecyclerView recyclerView;
    private DaySchedule runs;
    private DailyScheduleFragment fragment = this;
    private DailyScheduleAdapter dailyScheduleRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.daily_schedule_fragment, container, false);

        fabAddDay = getActivity().findViewById(R.id.fabAddDay);
        setAddDate();
        setAddRunButton();

        return view;
    }

    public void bind(DaySchedule schedule) {
        runs = schedule;
        bound = true;
    }

    private void setAddDate() {
        addDateButton = view.findViewById(R.id.addDateButton);
        addDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        fragment,
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        if (bound) {
            addDateButton.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                    .format(runs.getDate()));
            Calendar date = Calendar.getInstance();
            date.setTime(runs.getDate());
            setGridView(date);
        }
    }

    private void setAddRunButton() {
        addRunButton = view.findViewById(R.id.addRunButton);
        addRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addDateButton.getText() == getString(R.string.set_date)) {
                    alertDialog("Date not chosen",
                            "Please choose date for this schedule");
                }
                else if (!runs.isEmpty() && !runs.get(runs.size() - 1).isSetUp()) {
                    alertDialog("Run not set",
                            "Please set start and end time of last run");
                }
                else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    runs.add(new Run(null, null));
                    dailyScheduleRecyclerAdapter.notifyDataSetChanged();
                    Log.d("Fab transition", "Transition from ? to X");
                    fabAddDay.transition(R.drawable.ic_close_custom, FabState.CLOSE);
                }
            }
        });
    }

    private void setGridView(Calendar date) {
        recyclerView = view.findViewById(R.id.dayGridView);
        if (runs == null)
            runs = new DaySchedule(date.getTime());

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
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);

        if (date.before(Calendar.getInstance())) {
            alertDialog("Incorrect date",
                    "Date should be either today or future date");
        }
        else {
            addDateButton.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                    .format(date.getTime()));
            setGridView(date);
        }
    }

    public boolean isReady() {
        // We know that schedules before the last one have to be set up
        return runs != null && runs.isReady();
    }

    public DaySchedule getSchedule() {
        return runs;
    }

    private void alertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext())
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        builder.show();
    }
}
