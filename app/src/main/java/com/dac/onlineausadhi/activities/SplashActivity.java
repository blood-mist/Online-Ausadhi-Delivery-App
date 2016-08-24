package com.dac.onlineausadhi.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dac.onlineausadhi.onlineaushadhilin.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by blood-mist on 5/10/16.
 */

public class SplashActivity extends AppCompatActivity {
    private ProgressBar bar;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String session;
    private static final String TAG = "SplashActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;


    private final int SPLASH_DISPLAY_LENGTH = 3000;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sharedPref = getSharedPreferences(getString(R.string.sharedPref), 0);
        //Getting the registration token from the intent
        //if the intent is not with success then displaying error messages
        bar=(ProgressBar)this.findViewById(R.id.progressBar);
        startRegistrationService();
    }

    private void startRegistrationService() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(code) &&
                    api.showErrorDialogFragment(this, code, REQUEST_GOOGLE_PLAY_SERVICES)) {
                // wait for onActivityResult call (see below)
                Toast.makeText(this, api.getErrorString(code), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    session = sharedPref.getString("token", "");
                    Log.d(TAG, session);

                    if (session.isEmpty()) {
                        Intent mainIntent = new Intent(SplashActivity.this, TutorialActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();

                    } else {
                        Intent mainIntent = new Intent(SplashActivity.this, PrescriptionActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();

                    }

                }

            }, SPLASH_DISPLAY_LENGTH);
        }
    }

}

