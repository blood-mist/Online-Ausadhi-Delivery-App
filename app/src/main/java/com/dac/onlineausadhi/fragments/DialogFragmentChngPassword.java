package com.dac.onlineausadhi.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dac.onlineausadhi.activities.LoginActivity;
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
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;


/**
 * Created by blood-mist on 6/10/16.
 */
public class DialogFragmentChngPassword extends DialogFragment {
    private static final String TAG ="PasswordChange";
    SharedPreferences sharedPref;
    private String checkPassword;
    @NotEmpty(messageId = R.string.empty_current_password, order = 1)
    private EditText currentPassword;
    @NotEmpty(messageId = R.string.password, order = 2)
    @MinLength(value = 6, messageId = R.string.validation_password_length, order = 3)
    private EditText newPassword;
    @NotEmpty(messageId = R.string.re_password, order = 4)
    private EditText confirmNewPassword;
    String ID="";
    String access_token;
    ProgressDialog progressDialog;
    private Activity mActivity;

    public DialogFragmentChngPassword() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.change_password_dialog, new LinearLayout(getActivity()), false);

        // Retrieve layout elements
        currentPassword = (EditText) view.findViewById(R.id.currentPassword);
        newPassword = (EditText) view.findViewById(R.id.newPassword);
        confirmNewPassword = (EditText) view.findViewById(R.id.confirmNewPassword);
        sharedPref = getActivity().getSharedPreferences(getString(R.string.sharedPref), 0);
        checkPassword = sharedPref.getString("password", "");
        setupProgressDialog();

        // Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Password");
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create();
        return builder.create();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Do stuff, possibly set wantToCloseDialog to true then...
                    validate();
                }

            });

        }
    }

    private void validate() {
        long start = SystemClock.elapsedRealtime();
        final boolean isvalid=FormValidator.validate(this, new SimpleErrorPopupCallback(getContext(), true));
        if(isvalid){
            Boolean wantToCloseDialog = false;
            if (checkPasswordMatch()) {

                if (checkConfirmation()) {
                    new PasswordAsync().execute((newPassword.getText().toString()));
                    wantToCloseDialog = true;
                } else {
                    Toast.makeText(getContext(), "Password and confirmed password do not match", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Incorrect Current Password,Please Enter correct Password", Toast.LENGTH_SHORT).show();

            }
            if (wantToCloseDialog)

                dismiss();
            //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
        }

        long time = SystemClock.elapsedRealtime() - start;
        Log.d(getClass().getName(), "validation finished in [ms] " + time);
    }

    private boolean checkPasswordMatch() {
        if ((currentPassword.getText().toString()).equals(checkPassword)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkConfirmation() {
        if ((newPassword.getText().toString()).equals((confirmNewPassword).getText().toString())) {
            return true;

        } else {
            return false;
        }
    }
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }
    private class PasswordAsync extends AsyncTask<String, Void, String> {
        Handler handler;
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Applying Changes. Please Wait...");
            progressDialog.show();
            mActivity=getActivity();
        }

        @Override
        protected String doInBackground(String... params) {
            String password = params[0];
            StringBuffer response__;
            access_token = sharedPref.getString("token", null);
            ID = sharedPref.getString("id", null);
            JSONObject send = new JSONObject();
            try {
                send.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                URL url = new URL(getResources().getString(R.string.edit_password) + "/" + ID);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setConnectTimeout(15 * 1000);
                conn.setReadTimeout(10 * 1000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + access_token);
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

            } catch (Exception e) {
                return null;
            }
            Log.d(TAG, response__.toString());
            Log.d("show_password",password);
            return response__.toString();

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result == null) {
                Toast.makeText(mActivity, getString(R.string.changes_not_applied), Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, result);
                try {
                    JSONObject resultObject = new JSONObject(result);
                    if (resultObject.getBoolean("success")) {
                        Log.d("Success", resultObject.getString("success"));

                                Toast.makeText(mActivity,"Password Changed Successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mActivity,resultObject.getString("msg"),Toast.LENGTH_SHORT).show();
                        mActivity.finish();
                        Intent intent =new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

