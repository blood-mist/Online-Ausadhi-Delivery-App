package com.dac.onlineausadhi.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dac.onlineausadhi.activities.LoginActivity;
import com.dac.onlineausadhi.adapters.OrderAdapter;
import com.dac.onlineausadhi.adapters.UploadAdapter;
import com.dac.onlineausadhi.classes.FontManager;
import com.dac.onlineausadhi.classes.OrderHistory;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blood-mist on 5/19/16.
 */

public class Order_Fragment extends Fragment {
    private RecyclerView order_history;
    private View view;
    private TextView empty;
    private static final String TAG = "order_fragment";
    SharedPreferences sharedPref;
    private String token;
    private LinearLayout alternateView;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.order_fragment, container, false);
        Typeface mTypeface = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);
        empty = (TextView) view.findViewById(R.id.sad);
        empty.setTypeface(mTypeface);
        sharedPref = getActivity().getSharedPreferences(getString(R.string.sharedPref), 0);
        token = sharedPref.getString("token", "");
        alternateView = (LinearLayout) view.findViewById(R.id.alternateView);
        setupProgressDialog();
        new AsyncFetch().execute();
        return view;

    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private class AsyncFetch extends AsyncTask<String, String, String> {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Fetching Data.. Please wait.");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                UploadAdapter uploadAdapter = new UploadAdapter(getString(R.string.order_url), "UTF-8", token, "GET");
                return uploadAdapter.finish("GET");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            List<OrderHistory> order = new ArrayList<>();
            if (result == null) {
                Toast.makeText(getContext(),getString(R.string.connection_failure), Toast.LENGTH_SHORT).show();

            } else {
                Log.d(TAG, result);
                try {
                    JSONObject orderObj = new JSONObject(result);
                    if (orderObj.getBoolean("success")) {
                        JSONArray orderArray = orderObj.getJSONArray("_data");

                        if (orderArray.length() == 0) {
                            alternateView.setVisibility(View.VISIBLE);

                        } else {
                            for (int i = 0; i < orderArray.length(); i++) {
                                JSONObject json_data = orderArray.getJSONObject(i);
                                OrderHistory orderHistory = new OrderHistory();
                                orderHistory.orderDate = json_data.getString("date");
                                orderHistory.deliveryDate = json_data.getString("delivery_date");
                                orderHistory.price = json_data.getInt("total_amount");
                                orderHistory.month = json_data.getString("month");
                                orderHistory.orderNo = json_data.getInt("orderCount");
                                orderHistory.year = json_data.getInt("year");
                                orderHistory.monthOrder = json_data.getInt("monthOrder");
                                order.add(orderHistory);

                                order_history = (RecyclerView) view.findViewById(R.id.list);
                                OrderAdapter adapter = new OrderAdapter(getActivity(), order);
                                order_history.setAdapter(adapter);
                                order_history.setLayoutManager(new LinearLayoutManager(getActivity()));
                            }
                            Log.d(TAG, order.size() + "");
                        }
                    } else {
                        Toast.makeText(getContext(), orderObj.getString("msg"), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
                progressDialog.dismiss();
            }

        }
    }
}






