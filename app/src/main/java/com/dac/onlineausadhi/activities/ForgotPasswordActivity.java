package com.dac.onlineausadhi.activities;

import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dac.onlineausadhi.onlineaushadhilin.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.annotations.RegExp;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

import static eu.inmite.android.lib.validations.form.annotations.RegExp.EMAIL;

public class ForgotPasswordActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private static final String TAG = "ForgotPasword";
    private ProgressDialog progressDialog;
    @NotEmpty(messageId = R.string.email, order = 1)
    @RegExp(value = EMAIL, messageId = R.string.valid_email, order = 2)
    private EditText forgotPassword;
    private Button retrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        toolbar = (Toolbar) findViewById(R.id.forgotPasswordToolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Retrieve Password!");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(Color.WHITE);
        }

        setupProgressDialog();
        forgotPassword = (EditText) findViewById(R.id.forgotPasswordEditText);
        retrieve = (Button) findViewById(R.id.retrieve);
        retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void validate() {
        long start = SystemClock.elapsedRealtime();
        final boolean isValid = FormValidator.validate(this, new SimpleErrorPopupCallback(this, true));
        long time = SystemClock.elapsedRealtime() - start;
        Log.d(getClass().getName(), "validation finished in [ms] " + time);

        if (isValid) {
            new forgotPasswordAsyncTask().execute();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    class forgotPasswordAsyncTask extends AsyncTask<String, Void, String> {
        String email_id = forgotPassword.getText().toString();
        URL url = null;
        StringBuffer response = null;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Processing...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {


            Log.d(TAG, getString(R.string.forgot_password_url) + "/" + email_id);
            try {
                url = new URL(getResources().getString(R.string.forgot_password_url) + "/" + email_id);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(15 * 1000);
                conn.setReadTimeout(10 * 1000);
                conn.setRequestProperty("Accept-Charset", "charset=UTF-8");
                conn.setDoInput(true);
                conn.connect();
                OutputStream out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                int responseCode = conn.getResponseCode();
                Log.d("POST Response Code :: ", "" + responseCode);
                BufferedReader in;
                if (responseCode == HttpURLConnection.HTTP_OK) { //success
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else { //any other error code rather than 200
                    in = new BufferedReader(new InputStreamReader(conn.getResponseCode() / 100 == 2 ? conn.getInputStream() : conn.getErrorStream()));
                }

                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result == null) {
                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.server_failure), Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, result);
                try {
                    JSONObject passChngObj = new JSONObject(result);
                    if (passChngObj.getBoolean("success")) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ForgotPasswordActivity.this);
                        builder1.setTitle("Retrieve Password");
                        builder1.setMessage(getString(R.string.check_email));
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, passChngObj.getString("msg"), Toast.LENGTH_LONG).show();
                        finish();
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}



