package com.dac.onlineausadhi.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dac.onlineausadhi.activities.CartListActivity;
import com.dac.onlineausadhi.activities.LoginActivity;
import com.dac.onlineausadhi.activities.RecentMedicinesActivity;
import com.dac.onlineausadhi.adapters.ListCustomAdapter;
import com.dac.onlineausadhi.adapters.OrderDbAdapter;
import com.dac.onlineausadhi.adapters.PlaceSlidesFragmentAdapter;
import com.dac.onlineausadhi.adapters.RecyclerViewDataAdapter;
import com.dac.onlineausadhi.adapters.UploadAdapter;
import com.dac.onlineausadhi.classes.CustomScrollView;
import com.dac.onlineausadhi.classes.FontManager;
import com.dac.onlineausadhi.classes.MedicineDetails;
import com.dac.onlineausadhi.classes.SectionDataClass;
import com.dac.onlineausadhi.classes.SingleItemClass;
import com.dac.onlineausadhi.onlineaushadhilin.R;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by blood-mist on 6/30/16.
 */
public class DashboardFragment extends ListFragment implements View.OnClickListener {
    OrderDbAdapter db;
    ArrayList<SectionDataClass> allSampleData;
    ArrayList<MedicineDetails> values;
    CustomScrollView listview;
    private View view;
    PlaceSlidesFragmentAdapter adapter;
    ViewPager pager;
    PageIndicator indicator;
    SharedPreferences sharedPref;
    String token;
    private LinearLayout reorder,layoutRecentOrder,noRecentOrder;
    private TextView loadAll, reOrder;
    JSONObject medObj;
    Intent intent;
    SimpleDateFormat date = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    String currentdate;
    private int[] mImageResources = {
            R.drawable.banner1,
            R.drawable.bg_login,
            R.drawable.prescription_sample,
            R.drawable.logo5,
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Typeface mTypeface = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);
        allSampleData = new ArrayList<SectionDataClass>();
        layoutRecentOrder= (LinearLayout) view.findViewById(R.id.layoutRecentOrder);
        noRecentOrder= (LinearLayout) view.findViewById(R.id.noRecentOrder);
        createDummyData();
        sharedPref = getActivity().getSharedPreferences(getString(R.string.sharedPref), 0);
        token = sharedPref.getString("token", "");
        new AsyncRecent().execute();

        adapter = new PlaceSlidesFragmentAdapter(getActivity()
                .getSupportFragmentManager(), getContext(), mImageResources);

        pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        indicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        ((CirclePageIndicator) indicator).setSnap(true);

        indicator
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        Toast.makeText(DashboardFragment.this.getActivity(),
                                "Changed to page " + position,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPageScrolled(int position,
                                               float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

        currentdate = date.format(new Date());

        loadAll = (TextView) view.findViewById(R.id.loadAll);
        reOrder = (TextView) view.findViewById(R.id.reOrder);
        reOrder.setTypeface(mTypeface);
        reorder = (LinearLayout) view.findViewById(R.id.dashReorder);


        RecyclerView my_recycler_view = (RecyclerView) view.findViewById(R.id.dashList1);

        my_recycler_view.setHasFixedSize(true);

        RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(getActivity(), allSampleData);

        my_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        my_recycler_view.setAdapter(adapter);

        loadAll.setOnClickListener(this);
        reorder.setOnClickListener(this);

        return view;

    }

    public void createDummyData() {
        for (int i = 1; i <= 2; i++) {

            SectionDataClass dm = new SectionDataClass();

            dm.setHeaderTitle("Section " + i);

            ArrayList<SingleItemClass> singleItem = new ArrayList<SingleItemClass>();
            for (int j = 0; j <= 5; j++) {
                singleItem.add(new SingleItemClass("Item " + j, "URL " + j));
            }

            dm.setAllItemsInSection(singleItem);

            allSampleData.add(dm);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loadAll:
                intent = new Intent(getActivity(), RecentMedicinesActivity.class);
                Bundle b = new Bundle();
                b.putString("medList", medObj.toString());
                Log.d("medlist", medObj.toString());
                intent.putExtras(b);
                startActivity(intent);
                break;
            case R.id.dashReorder:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setTitle("Confirm Order");
                builder1.setMessage(R.string.confirm_order);
                builder1.setCancelable(false);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db = new OrderDbAdapter(getContext());
                                try {
                                    JSONArray cartArray = medObj.getJSONArray("_data");

                                    for (int i = 0; i < cartArray.length(); i++) {
                                        JSONObject json_data = cartArray.getJSONObject(i);
                                        try {
                                            db.open();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        int count = db.checkMedicine(json_data.getString("medicine_name"));
                                        Log.d("count", "" + count);
                                        if (count == 0) {
                                            long result = db.createOrder(json_data.getString("medicine_name"), json_data.getInt("quantity"), "regular", "12", currentdate);
                                        } else {
                                            db.addup(json_data.getString("medicine_name"), json_data.getInt("quantity"), "regular", "12", currentdate);
                                        }
                                    }

                                    Toast.makeText(getActivity(), cartArray.length() + " items successfully added to cart", Toast.LENGTH_SHORT).show();
                                    intent = new Intent(getActivity(), CartListActivity.class);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert11 = builder1.create();
                alert11.show();


        }
    }

    private class AsyncRecent extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                UploadAdapter uploadAdapter = new UploadAdapter(getString(R.string.recent_url), "UTF-8", token, "GET");
                return uploadAdapter.finish("GET");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            int loopCount;
            values = new ArrayList<MedicineDetails>();
            values.clear();
            if (result != null) {
                Log.d("ListRecent", result);
                try {
                    medObj = new JSONObject(result);
                    if (medObj.getBoolean("success")) {
                        JSONArray medArray = medObj.getJSONArray("_data");
                        if(medArray.length()==0){
                            layoutRecentOrder.setVisibility(View.GONE);
                            noRecentOrder.setVisibility(View.VISIBLE);

                        }else {
                            if(medArray.length()>4){
                                loopCount=4;
                            }else{
                                loopCount=medArray.length();
                            }

                            for (int i = 0; i < loopCount; i++) {
                                JSONObject json_data = medArray.getJSONObject(i);
                                MedicineDetails medicineDetails = new MedicineDetails();
                                medicineDetails.medicineName = json_data.getString("medicine_name");
                                medicineDetails.rate = json_data.getInt("Rate");
                                medicineDetails.quantity = json_data.getInt("quantity");
                                medicineDetails.price = json_data.getInt("total_amount");

                                values.add(medicineDetails);

                                listview = (CustomScrollView) view.findViewById(android.R.id.list);
                                ListCustomAdapter listAdapter;
                                listAdapter = new ListCustomAdapter(getActivity(), values);
                                setListAdapter(listAdapter);
                                listview.setExpanded(true);
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), medObj.getString("msg"), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getActivity(),getString(R.string.connection_failure), Toast.LENGTH_SHORT).show();

            }

        }
    }
}

