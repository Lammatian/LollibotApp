package com.alliedtech.lollibotapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alliedtech.lollibotapp.R;
import com.alliedtech.lollibotapp.DaySchedule;

import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

public class ScheduleAdapter extends BaseAdapter {

    private Context mContext;
    private TreeMap<Date, DaySchedule> scheduled_days;
    private LayoutInflater inflater;
    private Date mapKeys[];

    public ScheduleAdapter(Activity activity, Context context, TreeMap<Date, DaySchedule> days) {
        mContext = context;
        scheduled_days = days;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mapKeys = days.keySet().toArray(new Date[days.size()]);
    }

    public int getCount() {
        return scheduled_days.size();
    }

    public DaySchedule getItem(int position) {
        return scheduled_days.get(mapKeys[position]);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final DaySchedule daySchedule = getItem(position);

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
        String scheduledItemsText;

        if (daySchedule.getScheduledRuns() != 1) {
            scheduledItemsText = mContext.getString(R.string.schedule_items_text_plural,
                    String.format(Locale.ENGLISH,
                            "%d",
                            daySchedule.getScheduledRuns()));
        }
        else {
            scheduledItemsText = mContext.getString(R.string.schedule_items_text_singular,
                    String.format(Locale.ENGLISH,
                            "%d",
                            daySchedule.getScheduledRuns()));
        }

        scheduleDayViewHolder.scheduled_items.setText(scheduledItemsText);

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        mapKeys = scheduled_days.keySet().toArray(new Date[scheduled_days.size()]);
        super.notifyDataSetChanged();
    }


    private static class ScheduleDayViewHolder {
        TextView date;
        TextView scheduled_items;
    }
}
