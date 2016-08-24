package com.dac.onlineausadhi.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dac.onlineausadhi.activities.ChangeProfileActivity;
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
import java.util.regex.Pattern;

/**
 * Created by blood-mist on 6/10/16.
 */
public class DialogFragmentEditProfile extends DialogFragment {
    private static final String TAG = "ChangedShared";
    private ProgressDialog progressDialog;
    private EditText editProfile;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String ID = "";
    String access_token;
    String previous;
    private ChangeProfileActivity cp;
    String saved_text = "";
    private Activity activity;

    public DialogFragmentEditProfile() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onAttach(Activity activity) {
        this.activity=activity;
        super.onAttach(activity);
        try {
            cp = (ChangeProfileActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FeedbackListener");
        }
    }
    /*create a method to recreate the parent activity*/
    public void onButtonPushed(View view) {
        cp.setTextViews();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.edit_profile_dialog, new LinearLayout(getActivity()), false);
        editProfile = (EditText) view.findViewById(R.id.changeProfile);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        saved_text = getArguments().getString("saved", "");
        setupProgressDialog();
        setup();

        String editValue = getArguments().getString("value", "");
        editProfile.setText(editValue);


        String title = getArguments().getString("title", "");
        builder.setTitle(title);
        builder.setView(view);
        builder.setCancelable(false);
        sharedPref = getActivity().getSharedPreferences(getString(R.string.sharedPref), 0);

        builder.setPositiveButton("Apply Changes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {




            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        return builder.create();
    }
        @Override
        public void onStart() {
            super.onStart();
            AlertDialog d = (AlertDialog) getDialog();
            if (d != null) {
                Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor = sharedPref.edit();
                        previous = sharedPref.getString(getArguments().getString("saved"), null);
                        Boolean wantToCloseDialog = false;
                        if (validate()) {
                            editor.putString(getArguments().getString("saved", ""), editProfile.getText().toString());
                            editor.apply();
                            wantToCloseDialog = true;
                            ID = sharedPref.getString("id", null);
                            access_token = sharedPref.getString("token", null);
                            Log.d(TAG, getArguments().getString("saved", ""));
                            new ChangeAsync().execute(sharedPref.getString("fullName", null),
                                    sharedPref.getString("email", null),
                                    sharedPref.getString("ward_no", null),
                                    sharedPref.getString("house_no", null),
                                    sharedPref.getString("street_name", null),
                                    sharedPref.getString("place_name", null),
                                    sharedPref.getString("district", null),
                                    sharedPref.getString("zone", null),
                                    sharedPref.getString("landline_number", null),
                                    sharedPref.getString("mobile_number", null));


                        }
                        if(wantToCloseDialog){
                            dismiss();
                        }
                    }
                });
            }
        }

    private void setup() {
        switch (saved_text) {
            default:
                
                editProfile.setInputType(InputType.TYPE_CLASS_TEXT);
                break;

            case "email":

                editProfile.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS );
                break;

            case "ward_no":

                editProfile.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;

            case "house_no":
                
                editProfile.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;

            case "landline_number":
                
                editProfile.setInputType(InputType.TYPE_CLASS_PHONE);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(9);
                editProfile.setFilters(filterArray);
                break;

            case "mobile_number":
                
                editProfile.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE);
                InputFilter[] filterArray2 = new InputFilter[1];
                filterArray2[0] = new InputFilter.LengthFilter(10);
                editProfile.setFilters(filterArray2);
                break;

        }
    }


    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }


    public static DialogFragmentEditProfile newInstance(String title, String value, String saved) {

        DialogFragmentEditProfile frag = new DialogFragmentEditProfile();

        Bundle args = new Bundle();

        args.putString("title", title);
        args.putString("value", value);
        args.putString("saved", saved);

        frag.setArguments(args);

        return frag;

    }

    class ChangeAsync extends AsyncTask<String, Void, String> {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Applying Changes. Please Wait...");
            progressDialog.show();
            activity=getActivity();
        }


        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String email = params[1];
            String ward_no = params[2];
            String house_no = params[3];
            String street_name = params[4];
            String place_name = params[5];
            String district = params[6];
            String zone = params[7];
            String landline_number = params[8];
            String mobile_number = params[9];
            StringBuffer response__ = null;

            JSONObject send = new JSONObject(); //making json object for request
            try {
                send.put("fullname", name);
                send.put("email", email);
                send.put("ward_no", ward_no);
                send.put("house_no", house_no);
                send.put("street_name", street_name);
                send.put("place_name", place_name);
                send.put("district", district);
                send.put("zone", zone);
                send.put("landline_number", landline_number);
                send.put("mobile_number", mobile_number);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {

                URL url = new URL(getResources().getString(R.string.edit_profile) + "/" + ID);
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
            return response__.toString();

        }

        protected void onPostExecute(String result) {   
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result == null) {
                editor.putString(getArguments().getString("saved"), previous);
                Toast.makeText(activity, getString(R.string.changes_not_applied), Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, result);
                try {
                    JSONObject resultObject = new JSONObject(result);
                    if (resultObject.getBoolean("success")) {
                        Log.d("Success", resultObject.getString("success"));

                       onButtonPushed(getView());
                        if(isAdded()){
                            Toast.makeText(activity,getString(R.string.changes_applied),Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(activity,resultObject.getString("msg"),Toast.LENGTH_SHORT).show();
                        activity.finish();
                        Intent intent =new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public boolean validate() {
        switch ((getArguments().getString("saved"))) {
            default:
                if((editProfile.getText().toString().length()==0)) {
                    Toast.makeText(getContext(), "field cannot be empty", Toast.LENGTH_SHORT).show();

                    return false;
                }
                return true;

            case "email":
                if( Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(editProfile.getText().toString().trim()).matches()) {
                    return true;
                }
                else {
                    Toast.makeText(getContext(),"Invalid Email Address",Toast.LENGTH_LONG).show();
                    return false;
                }

            case "landline_number":
                if((editProfile.getText().toString().length()!=9)){
                Toast.makeText(getContext(),"Invalid Landline number length",Toast.LENGTH_SHORT).show();
                    return false;
            }
                return true;

            case "mobile_number":

                if((editProfile.getText().toString().length()!=10)){
                    Toast.makeText(getContext(),"Invalid Mobile number length",Toast.LENGTH_SHORT).show();
                    return false;
                }

                return true;

        }
    }
}
