package com.dac.onlineausadhi.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dac.onlineausadhi.adapters.UploadAdapter;
import com.dac.onlineausadhi.classes.MedicineDetails;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeliveryDetailsActivity extends AppCompatActivity {
    private static final String TAG = "RefillActivity";
    private Toolbar toolbar;
    public RecyclerView deliveredRecycler;
    SharedPreferences sharedPref;
    private String token;
    int sales_id;
    Bundle bundle;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);
        bundle = this.getIntent().getExtras();
        setupProgressDialog();
        toolbar= (Toolbar) findViewById(R.id.deliveredToolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Delivered Items");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(Color.WHITE);
        }

        sales_id = bundle.getInt("sales_id");
        sharedPref = this.getSharedPreferences(getString(R.string.sharedPref), 0);
        token = sharedPref.getString("token", "");
        new DeliveryDetailsAsyncTask().execute();
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(DeliveryDetailsActivity.this);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:  //other menu items if you have any
                //add any action here
                this.finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class DeliveryDetailsAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading..");
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                UploadAdapter uploadAdapter = new UploadAdapter(getString(R.string.refill_url) + "/" + sales_id, "UTF-8", token, "GET");
                return uploadAdapter.finish("GET");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            List<MedicineDetails> delivery_details = new ArrayList<>();
            if (result != null) {
                Log.d(TAG, result);
                progressDialog.dismiss();
            }
            try {
                JSONObject detailObj = new JSONObject(result);
                if (detailObj.has("msg") && detailObj.getString("msg").equals("token expired")) {
                    Toast.makeText(DeliveryDetailsActivity.this, "Client and server out of sync", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(DeliveryDetailsActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    JSONArray refillArray = detailObj.getJSONArray("_data");

                    for (int i = 0; i < refillArray.length(); i++) {
                        JSONObject json_data = refillArray.getJSONObject(i);
                        MedicineDetails deliveryDetails = new MedicineDetails();
                        deliveryDetails.medicineName = json_data.getString("medicine_name");
                        deliveryDetails.quantity = json_data.getInt(String.valueOf("quantity"));
                        delivery_details.add(deliveryDetails);
                        deliveredRecycler = (RecyclerView) findViewById(R.id.recyclerDelivery);
                        DeliveryDetailAdapter adapter = new DeliveryDetailAdapter(DeliveryDetailsActivity.this, delivery_details);
                        deliveredRecycler.setAdapter(adapter);
                        deliveredRecycler.setLayoutManager(new LinearLayoutManager(DeliveryDetailsActivity.this));
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class DeliveryDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<MedicineDetails> refillList= Collections.emptyList();
        private LayoutInflater inflater;
        MedicineDetails refills;


        public DeliveryDetailAdapter(Context context, List<MedicineDetails> refillList) {
            inflater = LayoutInflater.from(context);
            this.refillList = refillList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_notification_details, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyHolder myHolder = (MyHolder) holder;
            refills=refillList.get(position);
            myHolder.medicine.setText(refills.medicineName);
            myHolder.quantity.setText(String.valueOf(refills.quantity));

        }

        @Override
        public int getItemCount() {
            return refillList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {

            TextView medicine;
            TextView quantity;
            TextView refillOrder;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                medicine = (TextView) itemView.findViewById(R.id.refill_medicine);
                quantity = (TextView) itemView.findViewById(R.id.refill_quantity);
            }

        }
    }
}
