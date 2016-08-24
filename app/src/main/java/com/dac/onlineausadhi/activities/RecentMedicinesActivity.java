package com.dac.onlineausadhi.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;
import com.dac.onlineausadhi.adapters.ExpandableAdapter;
import com.dac.onlineausadhi.adapters.OrderDbAdapter;
import com.dac.onlineausadhi.classes.FontManager;
import com.dac.onlineausadhi.classes.MedicineDetails;
import com.dac.onlineausadhi.onlineaushadhilin.R;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecentMedicinesActivity extends AppCompatActivity {
    private Integer badgeCount;
    ExpandableLayoutListView expandableLayoutListView;
    ArrayList<MedicineDetails> medicineList;
   ExpandableAdapter adapter;
    Bundle bundle;
   LinearLayout reOrder;
    TextView reorder;
    Toolbar toolbar;
    String medList;
    OrderDbAdapter db = new OrderDbAdapter(this);
    SimpleDateFormat date = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    String currentdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typeface mTypeface = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        setContentView(R.layout.activity_recent_medicines);
        badgeCount = db.countjournals();
        toolbar = (Toolbar) findViewById(R.id.recentMedToolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Recent Order");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(Color.WHITE);
        }
        bundle = this.getIntent().getExtras();
        reOrder = (LinearLayout) findViewById(R.id.reAdd);
        reorder = (TextView) findViewById(R.id.reorder);
        reorder.setTypeface(mTypeface);
        Log.d("Bundle", bundle.getString("medList"));
        medList = bundle.getString("medList");
        currentdate = date.format(new Date());
        populateRecentOrders();

        reOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(RecentMedicinesActivity.this);
                builder1.setTitle("Confirm Order");
                builder1.setMessage(R.string.confirm_order);
                builder1.setCancelable(false);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    JSONObject cartObj = new JSONObject(medList);
                                    JSONArray cartArray = cartObj.getJSONArray("_data");

                                    for (int i = 0; i < cartArray.length(); i++) {
                                        JSONObject json_data = cartArray.getJSONObject(i);
                                        try {
                                            db.open();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        int count = db.checkMedicine(json_data.getString("medicine_name"));
                                        Log.d("count",""+count);
                                        if (count == 0) {
                                            long result = db.createOrder(json_data.getString("medicine_name"), json_data.getInt("quantity"), "regular", "12", currentdate);
                                        } else {
                                            db.addup(json_data.getString("medicine_name"), json_data.getInt("quantity"), "regular", "12", currentdate);
                                        }
                                    }

                                    Toast.makeText(RecentMedicinesActivity.this, cartArray.length() + " items successfully added to cart", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RecentMedicinesActivity.this, CartListActivity.class);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                builder1.setNegativeButton("No",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }

        });
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_itemCart:   //this item has your app icon
                finish();
                Intent intent = new Intent(RecentMedicinesActivity.this, CartListActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_notification:
                finish();
                Intent intent2 = new Intent(RecentMedicinesActivity.this,NotificationActivity.class);
                startActivity(intent2);
                return true;
            case R.id.menu_settings:
                finish();
                Intent intent3 = new Intent(RecentMedicinesActivity.this,SettingsActivity.class);
                startActivity(intent3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent i = new Intent(RecentMedicinesActivity.this, RecentMedicinesActivity.class);
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

    private void populateRecentOrders() {
        medicineList = new ArrayList<>();


        JSONObject medObj = null;
        try {
            medObj = new JSONObject(medList);
            JSONArray medArray = medObj.getJSONArray("_data");

            for (int i = 0; i < medArray.length(); i++) {
                JSONObject json_data = medArray.getJSONObject(i);
                MedicineDetails medicineDetails = new MedicineDetails();
                medicineDetails.medicineName = json_data.getString("medicine_name");
                medicineDetails.rate = json_data.getInt("Rate");
                medicineDetails.quantity = json_data.getInt("quantity");
                medicineDetails.price = json_data.getInt("total_amount");
                medicineList.add(medicineDetails);

            }
            expandableLayoutListView = (ExpandableLayoutListView) findViewById(R.id.listview);
            adapter = new ExpandableAdapter(this,medicineList);
            expandableLayoutListView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
