package com.dac.onlineausadhi.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dac.onlineausadhi.adapters.OrderDbAdapter;
import com.dac.onlineausadhi.fragments.DashboardFragment;
import com.dac.onlineausadhi.fragments.Order_Fragment;
import com.dac.onlineausadhi.onlineaushadhilin.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionActivity extends AppCompatActivity {
    private Integer badgeCount;
    private Integer notificationCount;
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    ViewPagerAdapter adapter;
    FloatingActionMenu orderButton;
    FloatingActionButton order;
    FloatingActionButton prescription;
    FloatingActionButton phone;
    OrderDbAdapter DbHelper;
    SharedPreferences sharedPref;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();


    public static boolean fabVisible;

    private boolean FAB_Status = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        viewPager = (ViewPager) findViewById(R.id.tabPager);
        DbHelper = new OrderDbAdapter(this);
        badgeCount = DbHelper.countjournals();
        sharedPref = this.getSharedPreferences(getString(R.string.sharedPref), 0);
        notificationCount=sharedPref.getInt("unreadCount",0);
        orderButton = (FloatingActionMenu) findViewById(R.id.menu);
        order = (FloatingActionButton) findViewById(R.id.menu_item_order);
        prescription = (FloatingActionButton) findViewById(R.id.menu_item_prescription);
        phone = (FloatingActionButton) findViewById(R.id.menu_item_call);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DashboardFragment(),getString(R.string.fa_icon_dashboard)+" Dashboard");
        adapter.addFragment(new Order_Fragment(), getString(R.string.fa_order_history) + " History");
        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    default:
                        fabVisible = true;
                        orderButton.showMenu(true);

                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
        }

        orderButton.setClosedOnTouchOutside(true);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FAB_Status == false) {
                    //Display FAB menu
                    FAB_Status = true;

                } else {
                    //Close FAB menu
                    FAB_Status = false;
                }
            }
        });
     order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrescriptionActivity.this, OrderMedicinesActivity.class);
                startActivity(intent);
            }
        });

        prescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrescriptionActivity.this, UploadPrescriptionActivity.class);
                startActivity(intent);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "9841568568";
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);


                if (ActivityCompat.checkSelfPermission(PrescriptionActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_itemCart:   //this item has your app icon
                Intent intent = new Intent(PrescriptionActivity.this, CartListActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_notification:
                Intent intent2 = new Intent(PrescriptionActivity.this,NotificationActivity.class);
                startActivity(intent2);
                return true;
            case R.id.menu_settings:
                Intent intent3 = new Intent(PrescriptionActivity.this,SettingsActivity.class);
                startActivity(intent3);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Intent i = new Intent(PrescriptionActivity.this, PrescriptionActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (badgeCount > 0) {
            ActionItemBadge.update(this, menu.findItem(R.id.menu_itemCart), menu.findItem(R.id.menu_itemCart).getIcon(), ActionItemBadge.BadgeStyles.RED, badgeCount);
            badgeCount = badgeCount++;
        } else {
            ActionItemBadge.hide(menu.findItem(R.id.menu_itemCart));
        }
        if(notificationCount>0){
            ActionItemBadge.update(this, menu.findItem(R.id.menu_notification), menu.findItem(R.id.menu_notification).getIcon(), ActionItemBadge.BadgeStyles.RED, notificationCount);
            notificationCount = notificationCount--;

        }
        return super.onCreateOptionsMenu(menu);
    }
}




