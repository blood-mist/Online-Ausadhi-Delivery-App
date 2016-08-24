package com.dac.onlineausadhi.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dac.onlineausadhi.classes.MedicineDetails;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import java.util.ArrayList;

public class ListCustomAdapter extends BaseAdapter {


    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<MedicineDetails> medicines;

    public ListCustomAdapter(Activity activity, ArrayList<MedicineDetails> medicines) {
        this.activity = activity;
        this.medicines = medicines;
    }

/*
get count of the barangList
 */

    @Override
    public int getCount() {
        return medicines.size();
    }

    @Override
    public Object getItem(int location) {
        return medicines.get(location);
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
        ViewHolder holder = null;

        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.recent_medicines, null);
        }


    /*
    creating objects to access the views
     */
        MedicineDetails barang = medicines.get(position);
        holder = new ViewHolder();
        holder.name = (TextView) convertView.findViewById(R.id.med_name);
        holder.quantity = (TextView) convertView.findViewById(R.id.med_quantity);
        holder.rate = (TextView) convertView.findViewById(R.id.med_rate);


        // getting barang data for the row


        holder.name.setText(barang.medicineName);

        holder.quantity.setText(String.valueOf(barang.quantity));

        holder.rate.setText(String.valueOf(barang.rate));

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView quantity;
        TextView rate;

    }
}