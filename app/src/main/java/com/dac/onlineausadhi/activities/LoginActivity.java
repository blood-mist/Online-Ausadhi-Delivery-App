package com.dac.onlineausadhi.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dac.onlineausadhi.classes.FontManager;
import com.dac.onlineausadhi.classes.InternetConnectionCheck;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.MinLength;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.annotations.RegExp;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

import static eu.inmite.android.lib.validations.form.annotations.RegExp.EMAIL;

@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    @NotEmpty(messageId = R.string.email, order = 1)
    @RegExp(value = EMAIL, messageId = R.string.valid_email, order = 2)
    private EditText editEmail;

    @NotEmpty(messageId = R.string.password, order = 3)
    @MinLength(value = 6, messageId = R.string.validation_password_length, order = 4)
    private EditText editPassword;
    private Button loginButton;
    private TextView registerText, forgotPassword, signUpText;
    LinearLayout linearLayout;
    private TextView content;
    private String android_id;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private Context context;
    InternetConnectionCheck ic;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_login);


        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        sharedPref = getSharedPreferences(getString(R.string.sharedPref), 0);

        findViewsById();
        setupProgressDialog();

        assert loginButton != null;
        loginButton.setOnClickListener(this);

        assert signUpText != null;
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent mainIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                LoginActivity.this.startActivity(mainIntent);
            }
        });

    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void findViewsById() {
        Typeface mTypeface = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editEmail.setTypeface(Typeface.DEFAULT);

        editPassword = (EditText) findViewById(R.id.editPassword);
        editPassword.setTypeface(Typeface.DEFAULT);

        loginButton = (Button) findViewById(R.id.loginButton);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        signUpText = (TextView) findViewById(R.id.signUpText);
        signUpText.setTypeface(mTypeface);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        registerText = (TextView) findViewById(R.id.registerText);

        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent forgotPw = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPw);
            }

        });
    }


    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.loginButton:
                if (new InternetConnectionCheck(getBaseContext()).checkInternetConnection()) {
                    validate();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.connection_failure, Toast.LENGTH_SHORT).show();
                }


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FormValidator.stopLiveValidation(this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    private void validate() {
        long start = SystemClock.elapsedRealtime();
        final boolean isValid = FormValidator.validate(this, new SimpleErrorPopupCallback(this, true));
        long time = SystemClock.elapsedRealtime() - start;
        Log.d(getClass().getName(), "validation finished in [ms] " + time);

        if (isValid) {
            new loginAsyncTask().execute(editEmail.getText().toString(), editPassword.getText().toString(), android_id, "android", sharedPref.getString("applicationId", ""));

        }
    }

    class loginAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Verifying..");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            String device_id = params[2];
            String device_type = params[3];
            String device_token = params[4];
            StringBuffer response__ = null;

            JSONObject send = new JSONObject(); //making json object for request
            try {
                send.put("email", email);
                send.put("password", password);
                send.put("device_id", device_id);
                send.put("device_type", device_type);
                send.put("device_token", device_token);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {

                URL url = new URL(getResources().getString(R.string.login_url));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(15 * 1000);
                conn.setReadTimeout(10 * 1000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoInput(true);
                conn.connect();


                OutputStream out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(send.toString());
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                Log.d("POST Response Code :: ", "" + responseCode);
                BufferedReader in;
                if (responseCode == HttpURLConnection.HTTP_OK) { //success
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else { //any other error code rather than 200
                    in = new BufferedReader(new InputStreamReader(conn.getResponseCode() / 100 == 2 ? conn.getInputStream() : conn.getErrorStream()));
                }

                String inputLine;
                response__ = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response__.append(inputLine);
                }
                in.close();

                conn.disconnect();

            } catch (java.net.SocketTimeoutException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getString(R.string.readTimeOut), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                return null;
            }
            return response__.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String password = editPassword.getText().toString();
            progressDialog.dismiss();
            if (result == null) {
                Toast.makeText(LoginActivity.this, getString(R.string.server_failure), Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, result);
                try {
                    JSONObject resultObject = new JSONObject(result);
                    if (resultObject.getBoolean("success")) {

                        JSONObject _data = resultObject.getJSONObject("_data");


                        editor = sharedPref.edit();
                        editor.putString("token", _data.getString("token"));
                        editor.putString("device_id", android_id);
                        editor.putString("password", password);
                        editor.putInt("unreadCount", _data.getInt(String.valueOf("unreadNotificationCount")));

                        JSONObject user = _data.getJSONObject("user");
                        editor.putString("id", user.getString("id"));
                        editor.putString("fullName", user.getString("fullname"));
                        editor.putString("email", user.getString("email"));
                        editor.putString("ward_no", user.getString("ward_no"));
                        editor.putString("street_name", user.getString("street_name"));
                        editor.putString("house_no", user.getString("house_no"));
                        editor.putString("place_name", user.getString("place_name"));
                        editor.putString("district", user.getString("district"));
                        editor.putString("zone", user.getString("zone"));
                        editor.putString("landline_number", user.getString("landline_number"));
                        editor.putString("mobile_number", user.getString("mobile_number"));
                        editor.apply();

                        Boolean toggleValue;
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        int toggle = _data.getInt("notification_status");
                        if (toggle == 1) {
                            toggleValue = true;
                        } else {
                            toggleValue = false;
                        }
                        editor.putBoolean("toggleValue", toggleValue);
                        editor.apply();

                        String t = sharedPref.getString("token", null);
                        Log.d(TAG, t);
                        Log.d(TAG, android_id);
                        Log.d(TAG, sharedPref.getString("applicationId", ""));

                        Intent intent = new Intent(LoginActivity.this, PrescriptionActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, resultObject.getString("msg"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }

}



