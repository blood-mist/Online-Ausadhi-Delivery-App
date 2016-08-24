package com.dac.onlineausadhi.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dac.onlineausadhi.activities.LoginActivity;
import com.dac.onlineausadhi.adapters.DetailsAdapter;
import com.dac.onlineausadhi.adapters.UploadAdapter;
import com.dac.onlineausadhi.classes.OrderDetails;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by blood-mist on 5/25/16.
 */
public class HistoryDetailsFrag extends Fragment {
    private RecyclerView order_details;
    private View view;
    private static final String TAG = "details_fragment";
    SharedPreferences sharedPref;
    private String token;
    int monthOrder;
    String month;
    int year;
    Bundle b;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_details, container, false);
        b = getActivity().getIntent().getExtras();
        monthOrder = b.getInt("monthOrder");
        year = b.getInt("year");
        month = b.getString("month");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Order Details of " + month);
        setupProgressDialog();
        sharedPref = getActivity().getSharedPreferences(getString(R.string.sharedPref), 0);
        token = sharedPref.getString("token", "");

        new AsyncDetail().execute();
        return view;
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private class AsyncDetail extends AsyncTask<String, String, String> {
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yy");
        Date date;


        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Fetching Data..");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, getString(R.string.order_url) + "/" + year + "/" + monthOrder);

            try {
                UploadAdapter uploadAdapter = new UploadAdapter(getString(R.string.order_url) + "/" + year + "/" + monthOrder, "UTF-8", token, "GET");
                return uploadAdapter.finish("GET");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            List<OrderDetails> details = new ArrayList<>();
            if (result == null) {
                Toast.makeText(getActivity(), getString(R.string.connection_failure), Toast.LENGTH_SHORT).show();

            }else{
                Log.d(TAG, result);
            }
            try {
                JSONObject detailObj = new JSONObject(result);
                if (detailObj.getBoolean("success")) {
                    JSONArray detailArray = detailObj.getJSONArray("_data");

                    for (int i = 0; i < detailArray.length(); i++) {
                        JSONObject json_data = detailArray.getJSONObject(i);
                        OrderDetails orderDetails = new OrderDetails();
                        String dateTime = json_data.getString("date");
                        date = formatter1.parse(dateTime);
                        String temp = formatter2.format(date);
                        orderDetails.orderDate = temp;
                        orderDetails.amount = json_data.getDouble("total_amount");
                        String deliveryTime = json_data.getString("delivery_date");
                        date = formatter1.parse(deliveryTime);
                        String temp2 = formatter2.format(date);
                        orderDetails.deliveryDate = temp2;
                        orderDetails.orderId = json_data.getInt("sales_id");
                        orderDetails.discountAmount = json_data.getString("discount_amount");
                        orderDetails.netAmount = json_data.getDouble("net_amount");

                        details.add(orderDetails);

                        order_details = (RecyclerView) view.findViewById(R.id.list);
                        DetailsAdapter adapter = new DetailsAdapter(getActivity(), details);
                        order_details.setAdapter(adapter);
                        order_details.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                } else {
                    Toast.makeText(getActivity(),detailObj.getString("msg") , Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();

            } catch (ParseException e) {
                e.printStackTrace();
            }


            progressDialog.dismiss();
        }

    }
}

