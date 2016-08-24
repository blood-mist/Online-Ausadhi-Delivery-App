package com.dac.onlineausadhi.activities;

import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dac.onlineausadhi.adapters.CartAdapter;
import com.dac.onlineausadhi.adapters.OrderDbAdapter;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by blood-mist on 6/2/16.
 */
public class CartListActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    int count = 0;
    private RecyclerView view;
    private OrderDbAdapter db;
    private Toolbar toolbar;
    private Button order;
    SharedPreferences sharedPref;
    private String token;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_cart);
        view = (RecyclerView) findViewById(R.id.cartList);
        toolbar = (Toolbar) findViewById(R.id.cartToolbar);
        setupProgressDialog();
        order = (Button) findViewById(R.id.orderButton);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Your Cart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(Color.WHITE);
        }
        sharedPref = this.getSharedPreferences(getString(R.string.sharedPref), 0);
        token = sharedPref.getString("token", "");
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setItemAnimator(new DefaultItemAnimator());
        db = new OrderDbAdapter(this);
        view.setAdapter(new CartAdapter(this, db.getAllOrder()));

            order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (db.getAllOrder().isEmpty()) {
                        Toast.makeText(CartListActivity.this, "No items ordered", Toast.LENGTH_SHORT).show();
                    } else

                    {
                        new OrderAsyncTask().execute();
                    }

                }
            });


    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {


        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent intent=new Intent(CartListActivity.this,PrescriptionActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class OrderAsyncTask extends AsyncTask<String, String[], String> {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Completing your orders, Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response__;
            try {
                Log.d("Cart_Url",getResources().getString(R.string.cart_url));
                URL url = new URL(getResources().getString(R.string.cart_url));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoInput(true);
                conn.connect();

                OutputStream out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                Log.d("TAG_NAME", getResult().toString());

                writer.write(getResult().toString());
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                BufferedReader in;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else { //any other error code rather than 200
                    in = new BufferedReader(new InputStreamReader(conn.getResponseCode() / 100 == 2 ? conn.getInputStream() : conn.getErrorStream()));
                }
                Log.d("response code", "" + responseCode);

                String inputLine;
                response__ = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response__.append(inputLine);

                }
                in.close();
                conn.disconnect();
            } catch (IOException e) {

                e.printStackTrace();
                return null;
            }
            Log.d("response", response__.toString());
            return response__.toString();
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result == null) {
                Toast.makeText(CartListActivity.this, getString(R.string.changes_not_applied), Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, result);
                try {
                    JSONObject resultObject = new JSONObject(result);
                    if (resultObject.getBoolean("success")) {
                        Log.d("Success", resultObject.getString("msg"));
                        db.deleteTable();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(CartListActivity.this);
                        builder1.setTitle("Order Successful");
                        builder1.setMessage(getString(R.string.order_successful));
                        builder1.setCancelable(false);

                        builder1.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(CartListActivity.this, OrderMedicinesActivity.class);
                                        startActivity(intent);
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();


                    }else{
                            Toast.makeText(CartListActivity.this,"Client and server out of sync",Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent =new Intent(CartListActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

        private JSONArray getResult() {
            String myPath = "/data/data/com.dac.onlineausadhi.onlineaushadhilin/databases/" + "orderinfo";
            String myTable = "orders";
            SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            String searchQuery = "SELECT medicine_name,quantity,medicine_type FROM " + myTable;
            Cursor cursor = myDataBase.rawQuery(searchQuery, null);

            JSONArray resultSet = new JSONArray();
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();

                for (int i = 0; i < totalColumn; i++) {
                    Log.d("column", "" + totalColumn);
                    if (cursor.getColumnName(i) != null) {
                        try {
                            if (cursor.getString(i) != null) {
                                Log.d("TAG_NAME", cursor.getString(i));
                                rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                            } else {
                                rowObject.put(cursor.getColumnName(i), "");
                            }
                        } catch (Exception e) {
                            Log.d("TAG_NAME", e.getMessage());
                        }
                    }
                }
                resultSet.put(rowObject);
                cursor.moveToNext();
            }
            cursor.close();
            Log.d("TAG_NAME", resultSet.toString());
            return resultSet;
        }


    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }
}


