package com.dac.onlineausadhi.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dac.onlineausadhi.adapters.OrderDbAdapter;
import com.dac.onlineausadhi.classes.Medicines;
import com.dac.onlineausadhi.onlineaushadhilin.R;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

public class OrderMedicinesActivity extends AppCompatActivity {
    private Integer badgeCount;
    private int notificationCount;

    private Toolbar toolbar;
    private OrderDbAdapter DbHelper;
    @NotEmpty(messageId = R.string.medicine_name, order = 1)
    private AutoCompleteTextView medicine;
    @NotEmpty(messageId = R.string.quantity, order = 2)
    private EditText quantity;
    private RadioGroup medicineType;
    private Button cart;
    private Long rowId;
    private String type;
    private RadioButton rb;
    OrderDbAdapter db = new OrderDbAdapter(this);
    SimpleDateFormat date = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    String currentdate;
    String med;
    int number;
    private ArrayList<Medicines> medicine_list;
    SharedPreferences sharedPref;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbHelper = new OrderDbAdapter(this);
        badgeCount = DbHelper.countjournals();

        sharedPref = this.getSharedPreferences(getString(R.string.sharedPref), 0);
        notificationCount = sharedPref.getInt("unreadCount", 0);

        setContentView(R.layout.activity_order_medicines);
        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras.getString("medicine") != null && extras.getInt("quantity") != 0) {
                med = extras.getString("medicine").toString();
                number = extras.getInt("quantity");
            }
        }
        toolbar = (Toolbar) findViewById(R.id.orderToolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Place your order!");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(Color.WHITE);
        }
        medicine = (AutoCompleteTextView) findViewById(R.id.medicine);

        final AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);
        medicine.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        medicine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String medicineName = adapter.getItem(position).getName();
                medicine.setText(medicineName);
                adapter.clear();
            }
        });


        medicine.setText(med);
        quantity = (EditText) findViewById(R.id.quantity);
        if (number != 0) {
            quantity.setText("" + number);
        }
        medicineType = (RadioGroup) findViewById(R.id.medicineType);
        cart = (Button) findViewById(R.id.cart);
        currentdate = date.format(new Date());
        rowId = savedInstanceState != null ?
                savedInstanceState.getLong(OrderDbAdapter.ID) : null;
        registerButtonListenersAndSetDefaultText();

    }



    private void registerButtonListenersAndSetDefaultText() {
        {
            cart.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    validate();


                }
            });
        }
    }

    private void doIncrease() {
        badgeCount = DbHelper.countjournals();
        invalidateOptionsMenu();
    }

    private void validate() {
        long start = SystemClock.elapsedRealtime();
        final boolean isValid = FormValidator.validate(this, new SimpleErrorPopupCallback(this, true));

        if (isValid) {
            long time = SystemClock.elapsedRealtime() - start;
            Log.d(getClass().getName(), "validation finished in [ms] " + time);
            Log.d("rowId", " " + rowId);
            int selectedId = medicineType.getCheckedRadioButtonId();
            rb = (RadioButton) findViewById(selectedId);
            type = rb.getText().toString();
            try {
                db.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (med != null) {
                Log.d("rowId", " " + rowId);
                if (!(med.equals(medicine.getText().toString()))) {
                    int count = db.checkMedicine(medicine.getText().toString());
                    if (count == 0) {
                        db.updateOrder(med, medicine.getText().toString(), Integer.parseInt(quantity.getText().toString()), type, "12", currentdate.toString());
                    } else {
                        db.deleteOrder(med);
                        db.addup(medicine.getText().toString(), Integer.parseInt(quantity.getText().toString()), type, "12", currentdate.toString());
                    }
                } else {
                    db.updateOrder(medicine.getText().toString(), medicine.getText().toString(), Integer.parseInt(quantity.getText().toString()), type, "12", currentdate.toString());
                }
            } else {
                int count = db.checkMedicine(medicine.getText().toString());
                Log.d("count", "" + count);
                if (count == 0) {
                    long result = db.createOrder(medicine.getText().toString(), Integer.parseInt(quantity.getText().toString()), type, "12", currentdate.toString());
                } else {
                    db.addup(medicine.getText().toString(), Integer.parseInt(quantity.getText().toString()), type, "12", currentdate.toString());
                }
            }
            Toast.makeText(OrderMedicinesActivity.this,
                    "Successfully Added to cart",
                    Toast.LENGTH_SHORT).show();
            doIncrease();
            if (med != null) {
                Intent intent = new Intent(OrderMedicinesActivity.this, CartListActivity.class);
                startActivity(intent);

            } else {
                medicine.setText("");
                quantity.setText("");
                medicine.requestFocus();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent i = new Intent(OrderMedicinesActivity.this, OrderMedicinesActivity.class);
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
        if (med != null) {
            ActionItemBadge.hide(menu.findItem(R.id.menu_itemCart));
            ActionItemBadge.hide(menu.findItem(R.id.menu_notification));
        } else {
            if (badgeCount > 0) {
                ActionItemBadge.update(this, menu.findItem(R.id.menu_itemCart), menu.findItem(R.id.menu_itemCart).getIcon(), ActionItemBadge.BadgeStyles.RED, badgeCount);
                badgeCount = badgeCount++;
            } else {
                ActionItemBadge.hide(menu.findItem(R.id.menu_itemCart));
            }
            if (notificationCount > 0) {
                ActionItemBadge.update(this, menu.findItem(R.id.menu_notification), menu.findItem(R.id.menu_notification).getIcon(), ActionItemBadge.BadgeStyles.RED, notificationCount);
                notificationCount = notificationCount--;

            }
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_itemCart:   //this item has your app icon
                Intent intent = new Intent(OrderMedicinesActivity.this, CartListActivity.class);
                startActivity(intent);
                return true;

            case android.R.id.home:  //other menu items if you have any
                //add any action here
                Intent intent1 = new Intent(OrderMedicinesActivity.this, PrescriptionActivity.class);
                startActivity(intent1);
                this.finish();
                return true;
            case R.id.menu_notification:
                Intent intent2 = new Intent(OrderMedicinesActivity.this, NotificationActivity.class);
                startActivity(intent2);
                return true;
            case R.id.menu_settings:
                Intent intent3 = new Intent(OrderMedicinesActivity.this, SettingsActivity.class);
                startActivity(intent3);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private class AutoCompleteAdapter extends ArrayAdapter implements Filterable {

        SharedPreferences sharedPref;

        public AutoCompleteAdapter(Context context, int resource) {
            super(context, resource);
            sharedPref = context.getSharedPreferences("ApplicationPreference", 0);
            medicine_list = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return medicine_list.size();
        }

        @Override
        public Medicines getItem(int position) {
            return (Medicines) medicine_list.get(position);
        }

        @Override
        public Filter getFilter() {
            Filter myFilter = new Filter() {
                @Override
                protected synchronized FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        try {
                            //get data from the web
                            String term = constraint.toString();
                            new GetMedicinesList().execute(term).get();
                        } catch (Exception e) {
                            Log.d("HUS", "EXCEPTION " + e);
                        }

                        filterResults.values = medicine_list;
                        filterResults.count = medicine_list.size();

                    }
                    return filterResults;
                }

                @Override
                protected synchronized void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };

            return myFilter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.auto_complete_layout, parent, false);

            //get Country
            Medicines medicine = (Medicines) medicine_list.get(position);

            TextView medicineList = (TextView) view.findViewById(R.id.medicineList);

            medicineList.setText(medicine.getName());

            return view;
        }

        //download mCountry list
        class GetMedicinesList extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String token = sharedPref.getString("token", "");
                StringBuilder sb;

                try {
                    //Create a new COUNTRY SEARCH url Ex "search.php?term=india"
                    String NEW_URL = getContext().getString(R.string.autocomplete_url) + "/" + URLEncoder.encode(params[0], "UTF-8");
                    Log.d("HUS", "JSON RESPONSE URL " + NEW_URL);

                    URL url = new URL(NEW_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + token);

                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    reader.close();
                    connection.disconnect();

                   /* //parse JSON and store it in the list
                    String jsonString = sb.toString();
                    ArrayList medList = new ArrayList<>();
                    JSONObject medObj = new JSONObject(jsonString);

                    JSONArray jsonArray = medObj.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        //store the country name
                        Medicines medicine_items = new Medicines();
                        medicine_items.setName(jo.getString("medicine_name"));
                        medList.add(medicine_items);
                    }

                    //return the countryList
                    return medList;*/

                } catch (Exception e) {
                    Log.d("HUS", "EXCEPTION " + e);
                    return null;
                }
                return sb.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result == null) {
                    Toast.makeText(OrderMedicinesActivity.this, getString(R.string.server_failure), Toast.LENGTH_LONG).show();
                } else {
                    try {
                        medicine_list.clear();
                        JSONObject obj = new JSONObject(result);
                        if (obj.getBoolean("success")) {
                            JSONArray jsonArray = obj.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jo = jsonArray.getJSONObject(i);
                                //store the country name
                                Medicines medicine_items = new Medicines();
                                medicine_items.setName(jo.getString("medicine_name"));
                                medicine_list.add(medicine_items);
                            }

                        } else {
                            Toast.makeText(OrderMedicinesActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(OrderMedicinesActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }


    }
}






