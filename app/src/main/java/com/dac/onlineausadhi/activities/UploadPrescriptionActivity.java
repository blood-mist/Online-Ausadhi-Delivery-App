package com.dac.onlineausadhi.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dac.onlineausadhi.adapters.UploadAdapter;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class UploadPrescriptionActivity extends AppCompatActivity {
    AlertDialog dialog;
    private static final int IMAGE_PICK = 1;
    private static final int IMAGE_CAPTURE = 2;
    private static Uri fileUri = null;
    private Bitmap profile_imageBitmap;
    private static final String TAG = "UploadPrescription";
    private Button prescription;
    private Toolbar toolbar;
    private Button approve;
    private Button editPrescript;
    private ProgressDialog progressDialog;
    private LinearLayout uploadLayout, editLayout;
    File uploadFile;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_prescription);
        toolbar = (Toolbar) findViewById(R.id.uploadToolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(Color.WHITE);
        }
        setupProgressDialog();
        sharedPref = getSharedPreferences(getString(R.string.sharedPref), 0);
        approve = (Button) findViewById(R.id.approveUpload);

        editPrescript = (Button) findViewById(R.id.editPrescription);

        prescription = (Button) findViewById(R.id.uploadPrescription);


        prescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();

            }
        });

        editPrescript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncUpload().execute();
            }
        });
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(UploadPrescriptionActivity.this);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void openImageIntent() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(UploadPrescriptionActivity.this);

        builder.setTitle("Select Source");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))

                {
                    String root = Environment.getExternalStorageDirectory() + "/DCIM";
                    String imageFolderPath = root + "/saved_images";
                    File imagesFolder = new File(imageFolderPath);
                    imagesFolder.mkdirs();
                    String imageName = "img" + System.currentTimeMillis() + ".jpeg";

                    File image = new File(imageFolderPath, imageName);

                    fileUri = Uri.fromFile(image);

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                    startActivityForResult(takePictureIntent,
                            1);

                } else if (options[item].equals("Choose from Gallery"))

                {

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);


                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                String picturePath = (fileUri.getPath());
                ImageView uploadPrescription = (ImageView) findViewById(R.id.prescriptionImage);
                uploadPrescription.setImageBitmap(decodeSampledBitmapFromFile(picturePath, 500, 500));
                uploadLayout = (LinearLayout) findViewById(R.id.uploadLayout);
                uploadLayout.setVisibility(View.GONE);

                editLayout = (LinearLayout) findViewById(R.id.editLayout);
                editLayout.setVisibility(View.VISIBLE);

                approve.setVisibility(View.VISIBLE);
                uploadFile = new File(picturePath);

            } else {
                Uri selectedImage = data.getData();
                // h=1;
                //imgui = selectedImage;
                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);

                c.moveToFirst();

                int columnIndex = c.getColumnIndex(filePath[0]);

                String picturePath = c.getString(columnIndex);

                c.close();
                ImageView uploadPrescription = (ImageView) findViewById(R.id.prescriptionImage);
                uploadPrescription.setImageBitmap(decodeSampledBitmapFromFile(picturePath, 250, 250));

                uploadLayout = (LinearLayout) findViewById(R.id.uploadLayout);
                uploadLayout.setVisibility(View.GONE);

                editLayout = (LinearLayout) findViewById(R.id.editLayout);
                editLayout.setVisibility(View.VISIBLE);
                uploadFile = new File(picturePath);
                approve.setVisibility(View.VISIBLE);

            }
        }
    }


    private Bitmap decodeSampledBitmapFromFile(String picturePath, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, options);

        // Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }

        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }


        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(picturePath, options);
    }


    private class AsyncUpload extends AsyncTask<String, String, String> {

        String token = sharedPref.getString("token", "");

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Uploading Prescription...Please wait");
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            try {
                UploadAdapter uploadAdapter = new UploadAdapter(getString(R.string.upload_url), "UTF-8", token, "POST");

                uploadAdapter.addFilePart("image_name", uploadFile);
                return uploadAdapter.finish("POST");

            } catch (IOException e2) {
                e2.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result != null) {
                Log.d(TAG, result);
                JSONObject resultObject = null;
                try {
                    resultObject = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (resultObject.getBoolean("success")) {
                        Log.d("Success", resultObject.getString("msg"));

                        Toast.makeText(UploadPrescriptionActivity.this, getString(R.string.prescription_uploaded), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UploadPrescriptionActivity.this, OrderMedicinesActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(UploadPrescriptionActivity.this, resultObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(UploadPrescriptionActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(UploadPrescriptionActivity.this, "Connection to server failed.", Toast.LENGTH_SHORT).show();

            }
        }
    }
}

