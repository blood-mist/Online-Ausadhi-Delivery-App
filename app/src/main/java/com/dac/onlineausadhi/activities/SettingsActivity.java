package com.dac.onlineausadhi.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dac.onlineausadhi.adapters.OrderDbAdapter;
import com.dac.onlineausadhi.adapters.UploadAdapter;
import com.dac.onlineausadhi.classes.FontManager;
import com.dac.onlineausadhi.fragments.DialogFragmentChngPassword;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

/**
 * Created by blood-mist on 5/19/16.
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Logout";
    private TextView editProfile;
    private TextView changePassowrd;
    private TextView notifications;
    private TextView enquiry;
    private TextView signout;
    private SwitchCompat toggle;
    private Toolbar toolbar;
    private TextView username;
    private TextView email;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private String device_token;
    private String mode;
    private int status;
    OrderDbAdapter db = new OrderDbAdapter(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_fragment);
        sharedPref = this.getSharedPreferences(getString(R.string.sharedPref), 0);
        Typeface mTypeface = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        username = (TextView) findViewById(R.id.userName);
        email = (TextView) findViewById(R.id.userEmail);
        username.setText(sharedPref.getString("fullName", null));
        email.setText(sharedPref.getString("email", null));
        toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(Color.WHITE);
        }
        editProfile = (TextView) findViewById(R.id.profile);
        editProfile.setTypeface(mTypeface);
        changePassowrd = (TextView) findViewById(R.id.passwordSettings);
        changePassowrd.setTypeface(mTypeface);
        notifications = (TextView) findViewById(R.id.notificationSettings);
        notifications.setTypeface(mTypeface);
        toggle = (SwitchCompat) findViewById(R.id.notificationToggle);
        Log.d("notification", sharedPref.getString("pushMessage", ""));
        toggle.setChecked(getDefaults("toggleValue", this));
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isChecked()) {
                    status = 1;
                    setDefaults("toggleValue", buttonView.isChecked(), SettingsActivity.this);

                } else {
                    status = 0;
                    setDefaults("toggleValue", false, SettingsActivity.this);
                }
                new AsyncNotifyTask().execute();
            }
        });
        enquiry = (TextView) findViewById(R.id.makeEnquiry);
        enquiry.setTypeface(mTypeface);
        signout = (TextView) findViewById(R.id.logout);
        signout.setTypeface(mTypeface);
        device_token = sharedPref.getString("applicationId", "");

        editProfile.setOnClickListener(this);
        changePassowrd.setOnClickListener(this);
        signout.setOnClickListener(this);
        enquiry.setOnClickListener(this);
    }

    private void setDefaults(String toggleValue, boolean checked, Context context) {
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(toggleValue, checked);
            editor.apply();
        }
    }

    public static Boolean getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, true);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {


        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                Intent intent=new Intent(SettingsActivity.this,PrescriptionActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile:
                Intent intent = new Intent(SettingsActivity.this, ChangeProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.passwordSettings:
                DialogFragmentChngPassword dialog = new DialogFragmentChngPassword();
                dialog.show(getSupportFragmentManager(), "fragmentDialog");
                break;
            case R.id.makeEnquiry:
                Intent enq = new Intent(SettingsActivity.this, EnquiryActivity.class);
                startActivity(enq);
                break;
            case R.id.logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout?");
                builder.setCancelable(false);
                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new logoutAsyncTask().execute();

                            }
                        });
                builder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert11 = builder.create();
                alert11.show();
                break;


        }

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Intent i = new Intent(SettingsActivity.this, SettingsActivity.class);
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

    private class logoutAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response__ = null;
            String token = sharedPref.getString("token", "");
            try {
                String NEW_URL = getString(R.string.logout_url)+"/"+device_token;
                Log.d("HUS", "JSON RESPONSE URL " + NEW_URL);

                URL url = new URL(NEW_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Authorization", "Bearer " + token);

                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                StringBuffer sb = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    response__ = sb.append(line).append("\n");
                }
                return response__.toString();


            } catch (Exception e) {
                Log.d("HUS", "EXCEPTION " + e);
                return null;
            }
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                Toast.makeText(SettingsActivity.this, getString(R.string.unsuccesfull_logout), Toast.LENGTH_SHORT).show();

            } else {
                Log.d(TAG, result);
                try {
                    JSONObject resultObject = new JSONObject(result);
                    if (resultObject.getString("success").equals("true")) {
                        editor = sharedPref.edit();
                        editor.clear();
                        editor.putString("applicationId", device_token);
                        editor.apply();
                        db.open();
                        db.deleteTable();
                        Toast.makeText(SettingsActivity.this, resultObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        SettingsActivity.this.finish();
                        Intent logout = new Intent(SettingsActivity.this, LoginActivity.class);
                        startActivity(logout);
                    } else {
                        Toast.makeText(SettingsActivity.this, "Client and server out of sync", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }


        }

    }

    private class AsyncNotifyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String token = sharedPref.getString("token", "");

            try {
                UploadAdapter uploadAdapter = new UploadAdapter(getString(R.string.notify_url) + "/" + status, "UTF-8", token, "GET");
                return uploadAdapter.finish("GET");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(TAG, result);
                try {
                    JSONObject resObj = new JSONObject(result);
                    Toast.makeText(SettingsActivity.this, resObj.getString("msg"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }

        }
    }
}



