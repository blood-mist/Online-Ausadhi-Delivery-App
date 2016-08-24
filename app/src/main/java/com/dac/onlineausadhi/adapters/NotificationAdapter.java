package com.dac.onlineausadhi.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dac.onlineausadhi.classes.NotificationModel;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import java.util.ArrayList;

/**
 * Created by blood-mist on 7/14/16.
 */
public class NotificationAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<NotificationModel> notification;

    public NotificationAdapter(Activity activity, ArrayList<NotificationModel> notification) {
        this.activity = activity;
        this.notification = notification;
    }

/*
get count of the barangList
 */

    @Override
    public int getCount() {
        return notification.size();
    }

    @Override
    public Object getItem(int location) {
        return notification.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
    inflate the items in the list view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_notification, null);
        }


    /*
    creating objects to access the views
     */
        TextView notificationText = (TextView) convertView.findViewById(R.id.notification);
        TextView date = (TextView) convertView.findViewById(R.id.date);



        // getting barang data for the row

       NotificationModel not = notification.get(position);

        notificationText.setText(not.notification);

        date.setText(String.valueOf(not.date));


        return convertView;
    }}
