package com.dac.onlineausadhi.classes;

import android.content.SharedPreferences;
import android.util.Log;

import com.dac.onlineausadhi.onlineaushadhilin.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by blood-mist on 6/28/16.
 */
public class MyFireBAseInstanceIDService extends FirebaseInstanceIdService{
    private static final String TAG = "MyFirebaseIIDService";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String refreshedToken) {
        sharedPref=getSharedPreferences(getString(R.string.sharedPref),0);
        editor=sharedPref.edit();
        editor.putString("applicationId",refreshedToken);
        editor.apply();

    }
}
