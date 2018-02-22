package com.alliedtech.lollibotapp.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.alliedtech.lollibotapp.TimePickerFragment;
import com.alliedtech.lollibotapp.R;
import com.alliedtech.lollibotapp.Run;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyScheduleAdapter
        extends RecyclerView.Adapter<DailyScheduleAdapter.DailyViewHolder> {

    private ArrayList<Run> runs;
    private Context mContext;
    private Activity mActivity;
    private Calendar date;
    private int positionToSet;
    private TextView viewToSet;
    private SimpleDateFormat timeOfDay = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    //region View holder
    class DailyViewHolder extends RecyclerView.ViewHolder {
        private TextView run_time, run_info;

        DailyViewHolder(View itemView) {
            super(itemView);
            this.run_time = itemView.findViewById(R.id.run_time);
            this.run_info = itemView.findViewById(R.id.run_info);
        }
    }
    //endregion

    //region Overridden adapter methods
    public DailyScheduleAdapter(Activity activity, Context context, ArrayList<Run> runs, Calendar date) {
        this.runs = runs;
        this.mContext = context;
        this.mActivity = activity;
        this.date = date;
    }

    @Override
    public DailyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.run_item, parent, false);

        return new DailyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DailyViewHolder holder, int position) {
        Run run = runs.get(position / 2);

        String run_info, run_time = "";

        if (position % 2 == 0) {
            run_info = mContext.getString(R.string.run_start_text);
            if (run.getStartDate() != null)
                run_time = timeOfDay.format(run.getStartDate());
        }
        else {
            run_info = mContext.getString(R.string.run_end_text);
            if (run.getEndDate() != null)
                run_time = timeOfDay.format(run.getEndDate());
        }

        holder.run_info.setText(run_info);
        holder.run_time.setText(run_time);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                positionToSet = holder.getAdapterPosition();
                viewToSet = view.findViewById(R.id.run_time);
                showTimePickerDialog();
            }
        });
    }

    @Override
    public int getItemCount() {
        return runs.size() * 2;
    }
    //endregion

    private void showTimePickerDialog() {
        // Make the numbers be displayed as two digit numbers (leading 0 if necessary)
        NumberPicker.Formatter timeFormatter = new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format(Locale.ENGLISH,"%02d", i);
            }
        };

        final Dialog timePicker = new Dialog(mActivity);
        timePicker.setTitle("Set time");
        timePicker.setContentView(R.layout.time_picker_dialog);

        final NumberPicker hoursPicker = timePicker.findViewById(R.id.hours_picker);
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(23);
        hoursPicker.setWrapSelectorWheel(true);
        hoursPicker.setFormatter(timeFormatter);

        final NumberPicker minutesPicker = timePicker.findViewById(R.id.minutes_picker);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        minutesPicker.setWrapSelectorWheel(true);
        minutesPicker.setFormatter(timeFormatter);

        final NumberPicker secondsPicker = timePicker.findViewById(R.id.seconds_picker);
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);
        secondsPicker.setWrapSelectorWheel(true);
        secondsPicker.setFormatter(timeFormatter);

        Button setTime = timePicker.findViewById(R.id.set_time);
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.set(Calendar.HOUR_OF_DAY, hoursPicker.getValue());
                date.set(Calendar.MINUTE, minutesPicker.getValue());
                date.set(Calendar.SECOND, secondsPicker.getValue());
                onTimePicked(date.getTime());
                timePicker.dismiss();
            }
        });

        timePicker.show();
    }

    private void onTimePicked(Date time) {
        if (!correctRunTime(time)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle("Incorrect time")
                    .setMessage("Runs should be in chronological order with start before end")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            builder.show();
        }
        else {
            Run run = runs.get(positionToSet / 2);

            if (positionToSet % 2 == 0)
                run.setStartDate(time);
            else
                run.setEndDate(time);

            runs.set(positionToSet / 2, run);
            viewToSet.setText(timeOfDay.format(time));
        }
    }

    //region Time checking
    private boolean correctRunTime(Date time) {
        // TODO: Proper testing of this utter fuckery
        int runNumber = positionToSet / 2;
        boolean start = positionToSet % 2 == 0;
        Run run = runs.get(runNumber);

        if (runNumber == 0) {
            if (runs.size() == 1)
                return isStartBeforeEnd(time, run, start);
            else {
                return isStartBeforeEnd(time, run, start) && isRunBeforeNext(time, runNumber);
            }
        }
        else if (runNumber == runs.size() - 1) {
            return isStartBeforeEnd(time, run, start) && isRunAfterPrevious(time, runNumber);
        }
        else {
            return isStartBeforeEnd(time, run, start) &&
                    isRunAfterPrevious(time, runNumber) &&
                    isRunBeforeNext(time, runNumber);
        }
    }

    private boolean isStartBeforeEnd(Date time, Run run, boolean start) {
        if (start) {
            return run.getEndDate() == null || run.getEndDate().after(time);
        }
        else {
            return run.getStartDate() == null || run.getStartDate().before(time);
        }
    }

    private boolean isRunAfterPrevious(Date time, int runNumber) {
        return runs.get(runNumber - 1).getEndDate().before(time);
    }

    private boolean isRunBeforeNext(Date time, int runNumber) {
        return runs.get(runNumber + 1).getStartDate().after(time);
    }
    //endregion
}
