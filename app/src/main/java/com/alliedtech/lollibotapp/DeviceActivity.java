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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private ScheduleFragment scheduleFragment;
    private TreeMap<Date, DaySchedule> allSchedules;
    private boolean addingDayToSchedule = false;
    private final Handler timeHandler = new Handler();
    //endregion

    //region On start/create/destroy
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        allSchedules = new TreeMap<>();
        //region Professional schedule testing
//        Calendar date = Calendar.getInstance();
//        ArrayList<Date> dates = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            dates.add(date.getTime());
//            date.add(Calendar.DATE, 1);
//            DaySchedule daySchedule = new DaySchedule(dates.get(i));
//            for (int j = 0; j < 20; j++) {
//                daySchedule.add(new Run(date.getTime(), date.getTime()));
//            }
//            allSchedules.put(dates.get(i), daySchedule);
//        }
        //endregion

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
    private void handleMessageFromRobot(String data) {
        Pattern commandPattern = Pattern.compile("^\\[(...)(\\*(.*)\\*)?\\]$");
        Log.d("Received command", data);
        Matcher commandMatcher = commandPattern.matcher(data);
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
                TextView currentOperation = findViewById(R.id.current_operation);
                currentOperation.setText(argument);
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
                allSchedules.put(new DaySchedule(argument).getDate(), new DaySchedule(argument));
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
            mService.write(RobotCommand.OUT_COMMAND_GET_SCHEDULE);

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
                if (!addingDayToSchedule)
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
            allSchedules.put(dayScheduleFragment.getSchedule().getDate(), dayScheduleFragment.getSchedule());
            scheduleFragment.notifyDateSetChanged();
            mService.write(RobotCommand.OUT_COMMAND_UPDATE_SCHEDULE, dayScheduleFragment.getSchedule().toString());
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

    //region Helper methods
    private int getBatteryPercentage(int reading) {
        //TODO: Get more accurate reading, especially close to min value
        return 100*(reading - Constants.MIN_VOLTAGE)/(Constants.MAX_VOLTAGE - Constants.MIN_VOLTAGE);
    }
    //endregion

    //TODO: Remove
    //region Move lines
    public void forward(View view) {
        NumberPicker picker = findViewById(R.id.line_count);
        mService.write(RobotCommand.OUT_COMMAND_MOVE_LINES, Integer.toString(picker.getValue()));
    }

    public void back(View view) {

        NumberPicker picker = findViewById(R.id.line_count);
        mService.write(RobotCommand.OUT_COMMAND_MOVE_LINES, Integer.toString(-picker.getValue()));
    }
    //endregion
}
