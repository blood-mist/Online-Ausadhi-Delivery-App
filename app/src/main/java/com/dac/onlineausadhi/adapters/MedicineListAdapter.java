package com.dac.onlineausadhi.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dac.onlineausadhi.classes.MedicineDetails;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by blood-mist on 5/27/16.
 */
public class MedicineListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MedicineList";
    private Context context;
    private LayoutInflater inflater;

    List<MedicineDetails> medicineList = Collections.emptyList();
    MedicineDetails current;
    int currentPos = 0;

    public MedicineListAdapter(Context context, List<MedicineDetails> medicineList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.medicineList = medicineList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_medicine_details, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        current = medicineList.get(position);
        myHolder.medicineName.setText(current.medicineName);
        myHolder.rate.setText(String.valueOf(current.rate));
        myHolder.quantity.setText(String.valueOf(current.quantity));
        myHolder.price.setText("Rs." + current.price);

    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView medicineName;
        TextView rate;
        TextView quantity;
        TextView price;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            medicineName = (TextView) itemView.findViewById(R.id.medicine);
            rate = (TextView) itemView.findViewById(R.id.rate);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            price = (TextView) itemView.findViewById(R.id.price);
        }

    }
}