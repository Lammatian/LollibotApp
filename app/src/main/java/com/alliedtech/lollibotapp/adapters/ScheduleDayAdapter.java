package com.alliedtech.lollibotapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.alliedtech.lollibotapp.R;
import com.alliedtech.lollibotapp.ScheduleDay;

import java.util.ArrayList;

public class ScheduleDayAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ScheduleDay> scheduled_days;
    private LayoutInflater inflater;

    public ScheduleDayAdapter(Activity activity, Context context, ArrayList<ScheduleDay> days) {
        mContext = context;
        scheduled_days = days;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return scheduled_days.size();
    }

    public Object getItem(int position) {
        return scheduled_days.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        ScheduleDayViewHolder scheduleDayViewHolder;

        if (view == null) {
            scheduleDayViewHolder = new ScheduleDayViewHolder();
            view = inflater.inflate(R.layout.schedule_day_listitem, null);
            TextView date = view.findViewById(R.id.schedule_day_date);
            date.setText("Test date");
            TextView nums = view.findViewById(R.id.schedule_day_items);
            nums.setText("Test nums");
            view.setTag(scheduleDayViewHolder);
        }
        else {
            scheduleDayViewHolder = (ScheduleDayViewHolder)convertView.getTag();
        }

        return view;
    }

    private static class ScheduleDayViewHolder {
        TextView date;
        TextView schedule_amount;
    }
}
