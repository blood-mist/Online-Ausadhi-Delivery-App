package com.dac.onlineausadhi.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dac.onlineausadhi.adapters.OrderDbAdapter;
import com.dac.onlineausadhi.adapters.UploadAdapter;
import com.dac.onlineausadhi.classes.MedicineDetails;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NotificationDetailsActivity extends AppCompatActivity {
    private static final String TAG = "RefillActivity";
    OrderDbAdapter db;
    private Toolbar toolbar;
    public RecyclerView refillRecycler;
    SharedPreferences sharedPref;
    private String token;
    int sales_id;
    String refillList;
    Bundle bundle;
    private Button refillOrder;
    SimpleDateFormat date = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    String currentdate;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);
        bundle = this.getIntent().getExtras();
        toolbar = (Toolbar) findViewById(R.id.refillToolbar);
        refillOrder = (Button) findViewById(R.id.refillOrder);
        setupProgressDialog();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Refill");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(Color.WHITE);
        }
        currentdate = date.format(new Date());

        sales_id = bundle.getInt("sales_id");
        sharedPref = this.getSharedPreferences(getString(R.string.sharedPref), 0);
        token = sharedPref.getString("token", "");
        new RefillAsyncTask().execute();

        refillOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(NotificationDetailsActivity.this);
                builder1.setTitle("Refill Order");
                builder1.setMessage(R.string.confirm_order);
                builder1.setCancelable(false);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                db = new OrderDbAdapter(NotificationDetailsActivity.this);
                                try {
                                    JSONObject refillOrderObj = new JSONObject(refillList);
                                    JSONArray cartArray = refillOrderObj.getJSONArray("_data");

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

                                    Toast.makeText(NotificationDetailsActivity.this, cartArray.length() + " items successfully added to cart", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(NotificationDetailsActivity.this, CartListActivity.class);
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
        });

    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(NotificationDetailsActivity.this);
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


    private class RefillAsyncTask extends AsyncTask<String, Void, String> {
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
            List<MedicineDetails> details = new ArrayList<>();
            if (result != null) {
                Log.d(TAG, result);
                refillList = result;
                progressDialog.dismiss();
            }
            try {
                JSONObject detailObj = new JSONObject(result);
                if (detailObj.getBoolean("success")) {
                    JSONArray refillArray = detailObj.getJSONArray("_data");

                    for (int i = 0; i < refillArray.length(); i++) {
                        JSONObject json_data = refillArray.getJSONObject(i);
                        MedicineDetails refillDetails = new MedicineDetails();
                        refillDetails.medicineName = json_data.getString("medicine_name");
                        refillDetails.quantity = json_data.getInt(String.valueOf("quantity"));
                        details.add(refillDetails);
                        refillRecycler = (RecyclerView) findViewById(R.id.recycleList);
                        RefillAdapter adapter = new RefillAdapter(NotificationDetailsActivity.this, details);
                        refillRecycler.setAdapter(adapter);
                        refillRecycler.setLayoutManager(new LinearLayoutManager(NotificationDetailsActivity.this));
                    }

                } else {
                    Toast.makeText(NotificationDetailsActivity.this, detailObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(NotificationDetailsActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class RefillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<MedicineDetails> refillList = Collections.emptyList();
        private LayoutInflater inflater;
        MedicineDetails refills;


        public RefillAdapter(Context context, List<MedicineDetails> refillList) {
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
            refills = refillList.get(position);
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