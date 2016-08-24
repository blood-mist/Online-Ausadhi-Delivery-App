package com.dac.onlineausadhi.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dac.onlineausadhi.activities.OrderDetailsActivity;
import com.dac.onlineausadhi.classes.OrderDetails;
import com.dac.onlineausadhi.fragments.MedicineDetailsFragment;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import java.util.Collections;
import java.util.List;


/**
 * Created by blood-mist on 5/23/16.
 */
public class DetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "OrderDetails";
    private Context context;
    private LayoutInflater inflater;

    List<OrderDetails> details = Collections.emptyList();
    OrderDetails current;
    int currentPos = 0;

    public DetailsAdapter(Context context, List<OrderDetails> details) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.details = details;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_history_details, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.d(TAG, "" + position);

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        current = details.get(position);
        myHolder.deliverydate.setText(current.deliveryDate);
        myHolder.orderDate.setText(current.orderDate);
        myHolder.amount.setText("Rs." + current.amount);
        myHolder.discountAmount.setText("Rs. "+current.discountAmount);
        myHolder.netAmount.setText("Rs. "+current.netAmount);

        final int id = current.orderId;
        Log.d(TAG,""+id);

        myHolder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MedicineDetailsFragment();

                FragmentManager fragmentManager = ((OrderDetailsActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle= new Bundle();
                bundle.putInt("id", id);
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.orderHistoryFragment, fragment).addToBackStack(null);
                fragmentTransaction.commit();
                fragmentTransaction.addToBackStack(null);

            }
        });


    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView deliverydate;
        TextView orderDate;
        TextView amount;
        TextView details;
        TextView discountAmount;
        TextView netAmount;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            deliverydate = (TextView) itemView.findViewById(R.id.deliveryDate);
            orderDate = (TextView) itemView.findViewById(R.id.orderDate);
            amount = (TextView) itemView.findViewById(R.id.price);
            details = (TextView) itemView.findViewById(R.id.details);
            discountAmount= (TextView) itemView.findViewById(R.id.discount);
            netAmount= (TextView) itemView.findViewById(R.id.netAmount);

        }

    }
}


