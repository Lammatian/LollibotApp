package com.alliedtech.lollibotapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alliedtech.lollibotapp.R;
import com.alliedtech.lollibotapp.DaySchedule;

import java.util.ArrayList;
import java.util.Locale;

public class DayScheduleAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<DaySchedule> scheduled_days;
    private LayoutInflater inflater;

    public DayScheduleAdapter(Activity activity, Context context, ArrayList<DaySchedule> days) {
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
        DaySchedule daySchedule = scheduled_days.get(position);

        ScheduleDayViewHolder scheduleDayViewHolder;

        if (view == null) {
            scheduleDayViewHolder = new ScheduleDayViewHolder();
            view = inflater.inflate(R.layout.schedule_day_listitem, null);
            scheduleDayViewHolder.date = view.findViewById(R.id.schedule_day_date);
            scheduleDayViewHolder.scheduled_items = view.findViewById(R.id.schedule_day_items);
            view.setTag(scheduleDayViewHolder);
        }
        else {
            scheduleDayViewHolder = (ScheduleDayViewHolder)convertView.getTag();
        }

        scheduleDayViewHolder.date.setText(daySchedule.getFormattedDate());
        String scheduledItemsText = mContext.getString(R.string.schedule_items_text,
                String.format(Locale.ENGLISH,
                        "%d",
                        daySchedule.getScheduledRuns()));
        scheduleDayViewHolder.scheduled_items.setText(scheduledItemsText);

        return view;
    }


    private static class ScheduleDayViewHolder {
        TextView date;
        TextView scheduled_items;
    }
}
