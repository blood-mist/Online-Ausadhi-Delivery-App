package com.dac.onlineausadhi.classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dac.onlineausadhi.activities.SplashActivity;
import com.dac.onlineausadhi.onlineaushadhilin.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by blood-mist on 6/28/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG ="Messaging Service" ;
    SharedPreferences sharedPref;
    SharedPreferences sf;
    int unreadCount;
    SharedPreferences.Editor editor;
    @Override
    public void onMessageReceived(RemoteMessage message) {
           //Getting the message from the bundle
        sharedPref=this.getSharedPreferences(getResources().getString(R.string.sharedPref),0);
        unreadCount=sharedPref.getInt("unreadCount",0);
        Log.d("RecieverUnreadCount",""+unreadCount);
        unreadCount++;
        editor=sharedPref.edit();
        editor.putInt("unreadCount",unreadCount);
        Log.d("RecicverUnreadCount",""+unreadCount);
        editor.apply();
           String from =message.getFrom();
        Log.d(TAG, "From: " + message.getFrom());
        Log.d(TAG,"Data: "+message.getData());
       String notification=message.getData().get("message");
        sf= PreferenceManager.getDefaultSharedPreferences(this);
       Boolean value= sf.getBoolean("toggleValue",false);
        Log.d("toggleValue",""+value);
        if(value) {
            //Displaying a notiffication with the message
            sendNotification(notification);
        }

    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String message) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }
}
