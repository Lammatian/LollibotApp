package com.alliedtech.lollibotapp;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class DeviceActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private AnimatedFloatingActionButton fabAddDay;
    private DailyScheduleFragment dayScheduleFragment;
    private ScheduleFragment scheduleFragment;
    private TreeMap<Date, DaySchedule> allSchedules;
    private boolean addingDayToSchedule = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        allSchedules = new TreeMap<>();
        //region Professional schedule testing
        Calendar date = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            dates.add(date.getTime());
            date.add(Calendar.DATE, 1);
            allSchedules.put(dates.get(i), new DaySchedule(dates.get(i)));
        }
        //endregion

        appBarLayout = findViewById(R.id.appBarLayout);

        viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        fabAddDay = findViewById(R.id.fabAddDay);
        setUpFabAddDay(fabAddDay);

        tabLayout = findViewById(R.id.tabLayout);
        setUpTabLayout(tabLayout);
    }

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

    //region Tabs Handling
    // Setting View Pager
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                openDailySchedule(position);
            }
        };
        scheduleFragment = new ScheduleFragment();
        // Fragments are stupid and cannot be instantiated with arguments so we have to bind them
        scheduleFragment.bind(allSchedules);
        // Set the item click listener to open the correct item's daily schedule
        scheduleFragment.bindOnItemClickListener(onItemClickListener);
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
}
