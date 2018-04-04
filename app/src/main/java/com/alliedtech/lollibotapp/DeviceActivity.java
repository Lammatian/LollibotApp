package com.alliedtech.lollibotapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceActivity extends AppCompatActivity {

    //region Variables
    private interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
    }

    boolean mBounded;
    BluetoothService mService;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private AnimatedFloatingActionButton fabAddDay;
    private DailyScheduleFragment dayScheduleFragment;
    private OverrideFragment overrideFragment;
    private ScheduleFragment scheduleFragment;
    private TreeMap<Date, DaySchedule> allSchedules;
    private String currentOp;
    private boolean addingDayToSchedule = false;
    private boolean inOverride = false;
    private final Handler timeHandler = new Handler();
    private int lastBatteryReading = 100;
    private int[] batteryReadings = new int[5];
    private int[] times = {0,
            Constants.BATTERY_UPDATE_PERIOD / 1000,
            Constants.BATTERY_UPDATE_PERIOD * 2 / 1000,
            Constants.BATTERY_UPDATE_PERIOD * 3 / 1000,
            Constants.BATTERY_UPDATE_PERIOD * 4 / 1000,
            Constants.BATTERY_UPDATE_PERIOD * 5 / 1000};
    private int batteryReadingCount = 0;
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    //endregion

    //region On start/create/destroy
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        Comparator<Date> comp = new Comparator<Date>() {
            @Override
            public int compare(Date s1, Date s2) {
                return dayFormat.format(s1).compareTo(dayFormat.format(s2));
            }
        };

        allSchedules = new TreeMap<>(comp);

        appBarLayout = findViewById(R.id.appBarLayout);

        viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        fabAddDay = findViewById(R.id.fabAddDay);
        setUpFabAddDay(fabAddDay);

        tabLayout = findViewById(R.id.tabLayout);
        setUpTabLayout(tabLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mConnection);
    }
    //endregion

    //region Bluetooth handling
    private void setCurrentOperation(String operation) {
        TextView currentOperation = findViewById(R.id.current_operation);
        currentOperation.setText(operation);

        if (inOverride) {
            TextView currentOperationOverride = findViewById(R.id.current_status);
            currentOperationOverride.setText(operation);
        }

        currentOp = operation;
    }

    private void handleMessageFromRobot(String data) {
        Pattern commandPattern = Pattern.compile("^\\[(...)(\\*(.*)\\*)?\\]$");
        Log.d("Received command", data);
        Matcher commandMatcher = commandPattern.matcher(data);
        // Make pattern matching actually work
        commandMatcher.matches();
        String command = commandMatcher.group(1);
        String argument = commandMatcher.group(3);

        switch (command) {
            case RobotCommand.IN_COMMAND_BATTERY_STATUS_UPDATE:
                TextView batteryLevel = findViewById(R.id.battery_level);
                int battery = Integer.parseInt(argument);
                batteryLevel.setText(String.format(Locale.ENGLISH,
                        "%d%%",
                        getBatteryPercentage(battery)));
                break;
            case RobotCommand.IN_COMMAND_STATE_CHANGE:
                setCurrentOperation(argument);
                break;
            case RobotCommand.IN_COMMAND_WARNING:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Warning");
                builder.setMessage(argument);
                builder.show();
                break;
            case RobotCommand.IN_COMMAND_SCHEDULE_START:
                break;
            case RobotCommand.IN_COMMAND_SCHEDULE_DAY:
                DaySchedule daySchedule = new DaySchedule(argument);
                allSchedules.put(daySchedule.getDate(), daySchedule);

                // Update information about today's schedule if necessary
                Calendar today = Calendar.getInstance();

                if (dayFormat.format(daySchedule.getDate()).equals(
                        dayFormat.format(today.getTime()))) {
                    TextView todayItems = findViewById(R.id.todayScheduledItems);
                    todayItems.setText((daySchedule.size() == 1) ?
                            getString(R.string.schedule_items_text_singular,
                                    "1") :
                            getString(R.string.schedule_items_text_plural,
                                    Integer.toString(daySchedule.size())));
                }

                break;
            case RobotCommand.IN_COMMAND_SCHEDULE_END:
                Toast.makeText(DeviceActivity.this,
                        "Schedule updated",
                        Toast.LENGTH_LONG).show();
                scheduleFragment.notifyDateSetChanged();
                break;
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageConstants.MESSAGE_READ:
                    handleMessageFromRobot((String)msg.obj);
                    break;
                case Constants.MESSAGE_DISCONNECTED:
                    startActivity(new Intent(getApplicationContext(), DevicesListActivity.class));
                    break;
            }
        }
    };

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            BluetoothService.LocalBinder mLocalBinder = (BluetoothService.LocalBinder)service;
            mService = mLocalBinder.getServerInstance();
            mService.setHandler(mHandler);
            // Send current date to EV3
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:SS",
                    Locale.ENGLISH);
            mService.write(RobotCommand.OUT_COMMAND_GET_SCHEDULE,
                    format.format(Calendar.getInstance().getTime()));

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    timeHandler.post(new Runnable() {
                        public void run() {
                            mService.write(RobotCommand.OUT_COMMAND_BATTERY_STATUS);
                        }
                    });
                }
            }, Constants.BATTERY_UPDATE_DELAY, Constants.BATTERY_UPDATE_PERIOD);
        }
    };
    //endregion

    //region Floating action button animations
    protected void animateFab(final int tab) {
        fabAddDay.clearAnimation();

        ScaleAnimation shrink = new ScaleAnimation(0.2f + tab*0.8f,
                1.0f - tab*0.8f,
                0.2f + tab*0.8f,
                1.0f - tab*0.8f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        shrink.setDuration(100);
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                fabAddDay.setVisibility(tab*4);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        fabAddDay.startAnimation(shrink);
    }
    //endregion

    //region Returning from daily schedule
    @Override
    public void onBackPressed() {
        if (addingDayToSchedule) {
            addingDayToSchedule = false;

            appBarLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }
    //endregion

    //region Tabs Handling
    // Setting View Pager
    private void setupViewPager(ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                openDailySchedule(position);
            }
        };
        AdapterView.OnItemLongClickListener onItemLongClickListener= new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Date[] keys = allSchedules.keySet().toArray(new Date[allSchedules.keySet().size()]);
                allSchedules.remove(keys[position]);
                mService.write(RobotCommand.OUT_COMMAND_REMOVE_SCHEDULE,
                        new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(keys[position]));
                scheduleFragment.notifyDateSetChanged();
                return true;
            }
        };
        scheduleFragment = new ScheduleFragment();
        // Fragments are stupid and cannot be instantiated with arguments so we have to bind them
        scheduleFragment.bind(allSchedules);
        // Set the item click listeners to open the correct item's daily schedule
        scheduleFragment.bindOnItemClickListener(onItemClickListener);
        scheduleFragment.bindOnItemLongClickListener(onItemLongClickListener);
        adapter.addFrag(scheduleFragment, "Schedule");
        StatusFragment statusFragment = new StatusFragment();
        adapter.addFrag(statusFragment, "Status");
        viewPager.setAdapter(adapter);
    }


    // View Pager fragments setting adapter class
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();//fragment arraylist
        private final List<String> mFragmentTitleList = new ArrayList<>();//title arraylist

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        //adding fragments and title method
        private void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    //endregion

    //region UI setup
    private void setUpFabAddDay(AnimatedFloatingActionButton fabAddDay) {
        fabAddDay.setUpDrawables(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Device activity", "Fab pressed");
                if (inOverride)
                    closeOverride();
                else if (!addingDayToSchedule)
                    openDailySchedule(-1);
                else
                    closeDailySchedule();
            }
        });
    }

    private void setUpTabLayout(TabLayout tabLayout) {
        tabLayout.setupWithViewPager(viewPager);//setting tab over viewpager
        //Implementing tab selected listener over tab layout
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());//setting current selected item over viewpager
                switch (tab.getPosition()) {
                    case 0:
                        Log.i("tab-change", "Tab 1");
                        break;
                    case 1:
                        Log.i("tab-change", "Tab 2");
                        break;
                }

                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }
    //endregion

    //region Daily schedule open/close
    private void openDailySchedule(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        appBarLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        dayScheduleFragment = new DailyScheduleFragment();
        if (position >= 0) {
            Date scheduleDate = allSchedules.keySet().toArray(new Date[allSchedules.size()])[position];
            dayScheduleFragment.bind(allSchedules.get(scheduleDate));
        }
        fragmentTransaction.replace(R.id.fragment_container, dayScheduleFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        Log.d("Fab transition", "Transition from + to X");
        fabAddDay.transition(R.drawable.ic_close_custom, FabState.CLOSE);

        addingDayToSchedule = !addingDayToSchedule;
    }

    private void closeDailySchedule() {
        if (dayScheduleFragment.isReady()) {
            allSchedules.put(dayScheduleFragment.getSchedule().getDate(),
                    dayScheduleFragment.getSchedule());
            scheduleFragment.notifyDateSetChanged();
            mService.write(RobotCommand.OUT_COMMAND_UPDATE_SCHEDULE,
                    dayScheduleFragment.getSchedule().toString());

            // Update today's number if necessary
            Calendar today = Calendar.getInstance();

            if (dayFormat.format(dayScheduleFragment.getSchedule().getDate()).equals(
                    dayFormat.format(today.getTime()))) {
                TextView todayItems = findViewById(R.id.todayScheduledItems);
                todayItems.setText((dayScheduleFragment.getSchedule().size() == 1) ?
                        getString(R.string.schedule_items_text_singular,
                                "1") :
                        getString(R.string.schedule_items_text_plural,
                                Integer.toString(dayScheduleFragment.getSchedule().size())));
            }
        }

        appBarLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();

        Log.d("Fab transition", "Transition from ? to +");
        fabAddDay.transition(R.drawable.ic_add_custom, FabState.ADD);

        addingDayToSchedule = !addingDayToSchedule;
    }
    //endregion

    //region Override open/close
    public void openOverride(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        appBarLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        overrideFragment = new OverrideFragment();
        fragmentTransaction.replace(R.id.fragment_container, overrideFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        Log.d("Fab transition", "Transition from + to X");
        fabAddDay.transition(R.drawable.ic_close_custom, FabState.CLOSE);
        fabAddDay.setVisibility(View.VISIBLE);

        inOverride = !inOverride;

        // TODO: Implement status changes
//        TextView currentOperationOverride = findViewById(R.id.current_status);
//        currentOperationOverride.setText(currentOp);
    }

    public void closeOverride() {
        appBarLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();

        Log.d("Fab transition", "Transition from X to +");
        fabAddDay.transition(R.drawable.ic_add_custom, FabState.ADD);
        fabAddDay.setVisibility(View.GONE);

        inOverride = !inOverride;
    }
    //endregion

    //region Override
    public void forward(View view) {
        EditText numberOfLines = findViewById(R.id.number_of_lines);
        mService.write(RobotCommand.OUT_COMMAND_MOVE_LINES, numberOfLines.getText().toString());
    }

    public void back(View view) {
        EditText numberOfLines = findViewById(R.id.number_of_lines);
        mService.write(RobotCommand.OUT_COMMAND_MOVE_LINES,
                "-" + numberOfLines.getText().toString());
    }

    public void resume(View view) {
        mService.write(RobotCommand.OUT_COMMAND_RESUME_SCHEDULE);
    }

    public void shutdown(View view) {
        mService.write(RobotCommand.OUT_COMMAND_SHUTDOWN);
    }
    //endregion

    //region Helper methods
    private int getBatteryPercentage(int reading) {
        int newBatteryLevel = 100*(reading - Constants.MIN_VOLTAGE)/
                (Constants.MAX_VOLTAGE - Constants.MIN_VOLTAGE);

        if (lastBatteryReading > 20 && newBatteryLevel < 20) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Low battery")
                    .setMessage("Battery below 20%");
            builder.show();
        } else if (lastBatteryReading > 10 && newBatteryLevel < 10) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Low battery")
                    .setMessage("Battery below 10%");
            builder.show();
        }

        lastBatteryReading = newBatteryLevel;

        System.arraycopy(batteryReadings, 1, batteryReadings, 0,
                Math.max(batteryReadingCount - 1, 0));

        batteryReadingCount = Math.min(batteryReadingCount + 1, 5);
        batteryReadings[batteryReadingCount - 1] = newBatteryLevel;

//        int estimatedLifetime = (504 * newBatteryLevel);
//        int hours = estimatedLifetime / 3600;
//        int minutes = (estimatedLifetime % 3600) / 60;
//
//        TextView lifetime = findViewById(R.id.estimated_lifetime);
//        lifetime.setText(getString(R.string.lifetime,
//                Integer.toString(hours),
//                Integer.toString(minutes)));

        if (batteryReadingCount > 1)
            estimateLifetime();

        return 100*(reading - Constants.MIN_VOLTAGE)/(Constants.MAX_VOLTAGE - Constants.MIN_VOLTAGE);
    }

    public void estimateLifetime() {
        long cov_xy = 0;
        long var_x = 0;
        double mean_x = 0, mean_y = 0;

        for (int i = 0; i < batteryReadingCount; ++i) {
            mean_x += times[i];
            mean_y += batteryReadings[i];
        }

        mean_x /= times.length;
        mean_y /= batteryReadings.length;

        for (int i = 0; i < batteryReadingCount; ++i) {
            cov_xy += (times[i] - mean_x) * (batteryReadings[i] - mean_y);
            var_x += (times[i] - mean_x) * (times[i] - mean_x);
        }

        double a = cov_xy / (double)var_x;
        double b = mean_y - a * mean_x;

        double x = -b / a;

        Log.d("Device Activity", "Estimated lifetime " + Double.toString(x));

        TextView lifetime = findViewById(R.id.estimated_lifetime);

        if (x <= 0 || x > 3600000) {
            lifetime.setText(getString(R.string.infinity));
        } else {
            int hours = (int)x / 3600;
            int minutes = ((int)x % 3600) / 60;

            lifetime.setText(getString(R.string.lifetime,
                    Integer.toString(hours),
                    Integer.toString(minutes)));
        }
    }
    //endregion
}
