package com.dac.onlineausadhi.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by blood-mist on 6/7/16.
 */
public class InternetConnectionCheck {
    Context context;

    public InternetConnectionCheck(Context context) {
        this.context = context;
    }

    public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            Log.v("Internet Connection", "Internet Connection Not Present");
            return false;
        }
    }
}
