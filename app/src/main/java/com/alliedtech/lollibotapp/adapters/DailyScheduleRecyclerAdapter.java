package com.alliedtech.lollibotapp.adapters;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alliedtech.lollibotapp.TimePickerFragment;
import com.alliedtech.lollibotapp.R;
import com.alliedtech.lollibotapp.Run;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DailyScheduleRecyclerAdapter
        extends RecyclerView.Adapter<DailyScheduleRecyclerAdapter.DailyViewHolder>
        implements TimePickerFragment.TimePickedListener {

    private ArrayList<Run> runs;
    private Context mContext;
    private Activity mActivity;
    private TextView viewToSet;
    private SimpleDateFormat timeOfDay = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    class DailyViewHolder extends RecyclerView.ViewHolder {
        private TextView run_time, run_info;

        DailyViewHolder(View itemView) {
            super(itemView);
            this.run_time = itemView.findViewById(R.id.run_time);
            this.run_info = itemView.findViewById(R.id.run_info);
        }
    }

    public DailyScheduleRecyclerAdapter(Activity activity, Context context, ArrayList<Run> runs) {
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
                viewToSet = view.findViewById(R.id.run_time);
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                // TODO: How to set the listener properly?
//                timePickerFragment.setTimePickedListener((TimePickerFragment.TimePickedListener) g);
                timePickerFragment.show(mActivity.getFragmentManager(), "TimePicker");
            }
        });
    }

    @Override
    public int getItemCount() {
        return runs.size() * 2;
    }

    @Override
    public void onTimePicked(String time) {
        viewToSet.setText(time);
    }
}
