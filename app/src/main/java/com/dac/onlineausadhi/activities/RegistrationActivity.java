package com.dac.onlineausadhi.activities;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dac.onlineausadhi.onlineaushadhilin.R;

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

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.MinLength;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.annotations.RegExp;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

import static eu.inmite.android.lib.validations.form.annotations.RegExp.EMAIL;

@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
/**
 * Created by asus on 04-May-16.
 */

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegistrationActivity";
    @NotEmpty(messageId = R.string.username, order = 1)
    @MinLength(value = 6, messageId = R.string.validation_username_length, order = 2)
    private EditText editRegUsername;
    @NotEmpty(messageId = R.string.valid_email, order = 3)
    @RegExp(value = EMAIL, messageId = R.string.valid_email)
    private EditText editRegEmail;
    @NotEmpty(messageId = R.string.password, order = 4)
    @MinLength(value = 6, messageId = R.string.validation_password_length, order = 5)
    private EditText editRegPassword;
    @NotEmpty(messageId = R.string.re_password, order = 6)
    private EditText editRegRePass;
    @NotEmpty(messageId = R.string.phone_no, order = 9)
    @MinLength(value = 10, messageId = R.string.validation_phone_length, order = 10)
    private EditText editMobPhoneno;
    @NotEmpty(messageId = R.string.place, order = 7)
    private EditText editPlace;
    @NotEmpty(messageId = R.string.district, order = 8)
    private EditText editDistrict;
    @NotEmpty(messageId = R.string.username, order = 1)
    private EditText input;
    private RadioGroup editReference;
    private RadioButton fbAd,googleSearch,friends,other;
    private String refType;
    private Toolbar toolbar;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private EditText editWard, editStreet, editZone, editHouseno, editResPhone;
    private Button submit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_registration);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        editRegUsername = (EditText) findViewById(R.id.edit_RegName);
        editRegEmail = (EditText) findViewById(R.id.editRegEmail);
        editRegPassword = (EditText) findViewById(R.id.editRegPass);
        editRegRePass=(EditText) findViewById(R.id.editRegRePass);
        editPlace = (EditText) findViewById(R.id.editPlace);
        editDistrict = (EditText) findViewById(R.id.editDistrict);
        editMobPhoneno = (EditText) findViewById(R.id.editMobPhone);
        editReference = (RadioGroup) findViewById(R.id.reference);
        editResPhone = (EditText) findViewById(R.id.editResPhone);
        editStreet = (EditText) findViewById(R.id.editStreet);
        editZone = (EditText) findViewById(R.id.editZone);
        editHouseno = (EditText) findViewById(R.id.editHouseno);
        editWard = (EditText) findViewById(R.id.editWard);

        fbAd= (RadioButton) findViewById(R.id.fbAd);
        googleSearch= (RadioButton) findViewById(R.id.googleSearch);
        friends= (RadioButton) findViewById(R.id.friends);
        other= (RadioButton) findViewById(R.id.others);
        refType=fbAd.getText().toString();
        editReference.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (fbAd.isChecked()) {
                    refType = fbAd.getText().toString();
                } else if (googleSearch.isChecked()) {
                    refType = googleSearch.getText().toString();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                    builder.setMessage("Specify Source");
                    builder.setCancelable(false);

                    input = new EditText(RegistrationActivity.this);
                    builder.setView(input);

                    builder.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }

                            });

                    final AlertDialog alertBuilder = builder.create();
                    alertBuilder.show();

                    alertBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Boolean wantToCloseDialog = false;
                            if (input.getText().toString().length()!=0) {
                                refType = input.getText().toString();
                                wantToCloseDialog=true;

                            }else{
                               Toast.makeText(RegistrationActivity.this,"Please specify source",Toast.LENGTH_SHORT).show();

                            }
                            if(wantToCloseDialog)
                                alertBuilder.dismiss();

                        }
                    });
                }
            }
                });

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                validate();
                break;
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


        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void validate() {
        long start = SystemClock.elapsedRealtime();
        final boolean isValid = FormValidator.validate(this, new SimpleErrorPopupCallback(this, true));

        if (isValid) {
            if (editRegPassword.getText().toString().equals(editRegRePass.getText().toString())) {
                new registerAsyncTask().execute(
                        editRegUsername.getText().toString(),
                        editRegEmail.getText().toString(),
                        editRegPassword.getText().toString(),
                        editHouseno.getText().toString(),
                        editPlace.getText().toString(),
                        editWard.getText().toString(),
                        editStreet.getText().toString(),
                        editDistrict.getText().toString(),
                        editZone.getText().toString(),
                        editResPhone.getText().toString(),
                        editMobPhoneno.getText().toString(),
                       refType);
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(RegistrationActivity.this);
                builder1.setMessage(R.string.confirm_password);
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

                editRegRePass.requestFocus();
                editRegRePass.setText("");
            }
            long time = SystemClock.elapsedRealtime() - start;
            Log.d(getClass().getName(), "validation finished in [ms] " + time);
        }
    }

    class registerAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String email = params[1];
            String fullname = params[0];
            String password = params[2];
            String house_no = params[3];
            String place_name = params[4];
            String ward_no = params[5];
            String street_name = params[6];
            String district = params[7];
            String zone = params[8];
            String landline_number = params[9];
            String mobile_number = params[10];
            String reference = params[11];
            StringBuffer response__;

            JSONObject send = new JSONObject();
            try {
                send.put("email", email);
                send.put("fullname", fullname);
                send.put("password", password);
                send.put("house_no", house_no);
                send.put("street_name", street_name);
                send.put("ward_no", ward_no);
                send.put("place_name", place_name);
                send.put("district", district);
                send.put("zone", zone);
                send.put("landline_number", landline_number);
                send.put("mobile_number", mobile_number);
                send.put("reference", reference);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                URL url = new URL(getResources().getString(R.string.register_url));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
                conn.setDoInput(true);
                conn.connect();

                OutputStream out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(send.toString());
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                BufferedReader in;
                if (responseCode == HttpURLConnection.HTTP_OK) {
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
            } catch (IOException e) {

                e.printStackTrace();
                return null;
            }
            return response__.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, result);
            try {
                JSONObject reg_result=new JSONObject(result);
                if(reg_result.getString("success").equals("true")){
                    Toast.makeText(RegistrationActivity.this,reg_result.getString("msg"),Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegistrationActivity.this);
                    builder1.setTitle("Confirm Registration");
                    builder1.setMessage(getString(R.string.confirm_registration));
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    RegistrationActivity.this.finish();
                                    Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }else{
                    Toast.makeText(RegistrationActivity.this,reg_result.getString("msg"),Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}


