package com.dac.onlineausadhi.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dac.onlineausadhi.fragments.HistoryDetailsFrag;
import com.dac.onlineausadhi.onlineaushadhilin.R;

/**
 * Created by blood-mist on 5/24/16.
 */
public class OrderDetailsActivity extends AppCompatActivity {
    Bundle bundle;
    public Toolbar toolbar;
    private static final String TAG = "OrderDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history_details);
        bundle = getIntent().getExtras();
        String month = bundle.getString("month");
        toolbar = (Toolbar) findViewById(R.id.toolbar2);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order History of " + month);
            toolbar.setTitleTextColor(Color.WHITE);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        HistoryDetailsFrag fragment = new HistoryDetailsFrag();
        fragmentTransaction.add(R.id.orderHistoryFragment, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
               if(getFragmentManager().getBackStackEntryCount() == 1) {
                   moveTaskToBack(false);
               }
               else {
                   super.onBackPressed();
               }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


