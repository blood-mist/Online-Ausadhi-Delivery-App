package com.dac.onlineausadhi.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dac.onlineausadhi.activities.LoginActivity;
import com.dac.onlineausadhi.adapters.MedicineListAdapter;
import com.dac.onlineausadhi.adapters.UploadAdapter;
import com.dac.onlineausadhi.classes.MedicineDetails;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blood-mist on 5/25/16.
 */
public class MedicineDetailsFragment extends Fragment {
    private RecyclerView medicine_list;
    private View view;
    Bundle b;
    int id;
    SharedPreferences sharedPref;
    private String token;
    private ProgressDialog progressDialog;
    private static final String TAG = "MedicineDetailsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_medicine_details, container, false);
        b = this.getArguments();
        id = b.getInt("id");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Medicine Items");
        setupProgressDialog();
        sharedPref = getActivity().getSharedPreferences(getString(R.string.sharedPref), 0);
        token = sharedPref.getString("token", "");
        Log.d(TAG, String.valueOf(id));
        new AsyncMedFetch().execute();
        return view;
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private class AsyncMedFetch extends AsyncTask<String, String, String> {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Fetching Data..");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, getString(R.string.medicine_url) + "/" + id);
            try {
                UploadAdapter uploadAdapter = new UploadAdapter(getString(R.string.medicine_url) + "/" + id, "UTF-8", token, "GET");
                return uploadAdapter.finish("GET");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            List<MedicineDetails> medicineList = new ArrayList<>();
            if (result == null) {
                Toast.makeText(getActivity(), getString(R.string.connection_failure), Toast.LENGTH_SHORT).show();

            }else{

            }
            Log.d(TAG, result);
            try {
                JSONObject medObj = new JSONObject(result);
                if (medObj.getBoolean("success")) {
                    JSONArray medArray = medObj.getJSONArray("_data");

                    for (int i = 0; i < medArray.length(); i++) {
                        JSONObject json_data = medArray.getJSONObject(i);
                        MedicineDetails medicineDetails = new MedicineDetails();
                        medicineDetails.medicineName = json_data.getString("medicine_name");
                        medicineDetails.rate = json_data.getInt("rate");
                        medicineDetails.quantity = json_data.getInt("quantity");
                        medicineDetails.price = json_data.getInt("total_amount");

                        medicineList.add(medicineDetails);

                        medicine_list = (RecyclerView) view.findViewById(R.id.list3);
                        MedicineListAdapter adapter = new MedicineListAdapter(getActivity(), medicineList);
                        medicine_list.setAdapter(adapter);
                        medicine_list.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                } else {
                    Toast.makeText(getActivity(), medObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }

    }
}