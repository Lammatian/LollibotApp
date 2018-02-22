package com.alliedtech.lollibotapp.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        extends RecyclerView.Adapter<DailyScheduleAdapter.DailyViewHolder>
        implements TimePickerFragment.TimePickedListener {

    private ArrayList<Run> runs;
    private Context mContext;
    private Activity mActivity;
    private int positionToSet;
    private TextView viewToSet;
    // This is so hacky and ugly but works :)
    private DailyScheduleAdapter fragment = this;
    private SimpleDateFormat timeOfDay = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    class DailyViewHolder extends RecyclerView.ViewHolder {
        private TextView run_time, run_info;

        DailyViewHolder(View itemView) {
            super(itemView);
            this.run_time = itemView.findViewById(R.id.run_time);
            this.run_info = itemView.findViewById(R.id.run_info);
        }
    }

    public DailyScheduleAdapter(Activity activity, Context context, ArrayList<Run> runs) {
        this.runs = runs;
        this.mContext = context;
        this.mActivity = activity;
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
//                TimePickerFragment timePickerFragment = new TimePickerFragment();
//                timePickerFragment.setTimePickedListener(fragment);
//                timePickerFragment.show(mActivity.getFragmentManager(), "TimePicker");
                showTimePickerDialog();
            }
        });
    }

    private void showTimePickerDialog() {
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
                // TODO: Set proper date here
                Calendar time = Calendar.getInstance();
                time.set(Calendar.HOUR_OF_DAY, hoursPicker.getValue());
                time.set(Calendar.MINUTE, minutesPicker.getValue());
                time.set(Calendar.SECOND, secondsPicker.getValue());
                onTimePicked(time.getTime());
                timePicker.dismiss();
            }
        });

        timePicker.show();
    }

    @Override
    public int getItemCount() {
        return runs.size() * 2;
    }

    @Override
    public void onTimePicked(Date time) {
        Run run = runs.get(positionToSet / 2);
        if (positionToSet % 2 == 0)
            run.setStartDate(time);
        else
            run.setEndDate(time);

        runs.set(positionToSet / 2, run);
        viewToSet.setText(timeOfDay.format(time));
    }
}
