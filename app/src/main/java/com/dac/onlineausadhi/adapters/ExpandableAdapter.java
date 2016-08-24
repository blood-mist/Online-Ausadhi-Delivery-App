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

/**
 * Created by Sagepawan on 10/27/2015.
 */
public class ExpandableAdapter extends BaseAdapter {
    private Activity activity;
    private Context context;
    private ArrayList<MedicineDetails> medDetails;
    private LayoutInflater layoutInflater;

    public ExpandableAdapter(Context context, ArrayList<MedicineDetails> medDetails) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.medDetails = medDetails;
    }

    @Override
    public int getCount() {
        return medDetails.size();
    }

    @Override
    public Object getItem(int i) {
        return medDetails.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        MedicineDetails details = medDetails.get(i);
            convertView = layoutInflater.inflate(R.layout.view_row, viewGroup, false);
        holder = new ViewHolder();
        holder.medName = (TextView) convertView.findViewById(R.id.header_medName);
        holder.rate = (TextView) convertView.findViewById(R.id.medi_rate);
        holder.quantity = (TextView) convertView.findViewById(R.id.medi_quantity);
        convertView.setTag(holder);
        holder.medName.setText(details.medicineName);
        holder.rate.setText("" + details.rate);
        holder.quantity.setText("" + details.quantity);


        return convertView;
    }

    static class ViewHolder {
        TextView medName;
        TextView rate;
        TextView quantity;
    }
}
