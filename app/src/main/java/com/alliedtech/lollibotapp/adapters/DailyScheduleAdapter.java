package com.alliedtech.lollibotapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alliedtech.lollibotapp.R;

import java.util.ArrayList;
import java.util.Date;

public class DailyScheduleAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Pair<Date, Date>> scheduled_hours;
    private LayoutInflater inflater;

    public DailyScheduleAdapter(Activity activity, Context context, ArrayList<Pair<Date, Date>> hours) {
        mContext = context;
        scheduled_hours = hours;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return 2*scheduled_hours.size();
    }

    public Object getItem(int position) {
        return position % 2 == 0 ? scheduled_hours.get(position/2).first : scheduled_hours.get(position/2).second;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Pair<Date, Date> running_hours = scheduled_hours.get(position / 2);

        ScheduleRunViewHolder scheduleRunViewHolder;

        if (view == null) {
            scheduleRunViewHolder = new ScheduleRunViewHolder();
            view = inflater.inflate(R.layout.start_end_time_listitem, null);
            scheduleRunViewHolder.start_end_text = view.findViewById(R.id.info_text);
            scheduleRunViewHolder.start_end_time = view.findViewById(R.id.start_end_time);
            view.setTag(scheduleRunViewHolder);
        }
        else {
            scheduleRunViewHolder = (ScheduleRunViewHolder)convertView.getTag();
        }

        String start_end_text = mContext.getString(position % 2 == 0 ? R.string.run_start_text : R.string.run_end_text);
        scheduleRunViewHolder.start_end_text.setText(start_end_text);
        Date start_end_time = position % 2 == 0 ? running_hours.first : running_hours.second;

        if (start_end_time != null) {
            scheduleRunViewHolder.start_end_time.setText(Long.toString(start_end_time.getTime()));
            scheduleRunViewHolder.start_end_time.setVisibility(View.VISIBLE);
        }
        else {
            scheduleRunViewHolder.start_end_time.setVisibility(View.GONE);
        }

        return view;
    }


    private static class ScheduleRunViewHolder {
        TextView start_end_text;
        TextView start_end_time;
    }
}
