package com.alliedtech.lollibotapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alliedtech.lollibotapp.R;
import com.alliedtech.lollibotapp.Run;

import java.util.ArrayList;

public class DailyScheduleRecyclerAdapter extends RecyclerView.Adapter<DailyScheduleRecyclerAdapter.DailyViewHolder> {

    private ArrayList<Run> runs;
    private Context mContext;

    public class DailyViewHolder extends RecyclerView.ViewHolder {
        private TextView run_time, run_info;

        public DailyViewHolder(View itemView) {
            super(itemView);
            this.run_time = itemView.findViewById(R.id.run_time);
            this.run_info = itemView.findViewById(R.id.run_info);
        }
    }

    public DailyScheduleRecyclerAdapter(ArrayList<Run> runs, Context context) {
        this.runs = runs;
        this.mContext = context;
    }

    @Override
    public DailyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.run_item, parent, false);

        return new DailyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DailyViewHolder holder, int position) {
        Run run = runs.get(position / 2);

        String run_info, run_time = "";

        if (position % 2 == 0) {
            run_info = mContext.getString(R.string.run_start_text);
            if (run.getStartDate() != null)
                run_time = Integer.toString(run.getStartDate().getHours());
        }
        else {
            run_info = mContext.getString(R.string.run_end_text);
            if (run.getEndDate() != null)
                run_time = Integer.toString(run.getEndDate().getHours());
        }

        holder.run_info.setText(run_info);
        holder.run_time.setText(run_time);
    }

    @Override
    public int getItemCount() {
        return runs.size() * 2;
    }
}
