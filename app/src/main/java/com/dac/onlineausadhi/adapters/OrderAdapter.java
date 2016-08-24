package com.dac.onlineausadhi.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dac.onlineausadhi.activities.OrderDetailsActivity;
import com.dac.onlineausadhi.classes.OrderHistory;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by blood-mist on 5/23/16.
 */
public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "OrderAdapter";
    private Context context;
    private LayoutInflater inflator;
    List<OrderHistory> order = Collections.emptyList();
    OrderHistory current;
    int currentPos = 0;

    public OrderAdapter(Context context, List<OrderHistory> order) {
        this.context = context;
        inflator = LayoutInflater.from(context);
        this.order = order;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.item_order_history, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.d(TAG, "" + position);

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        current = order.get(position);
        myHolder.orderCount.setText(String.valueOf( current.orderNo));
        myHolder.price.setText("Rs. " + current.price);
        myHolder.month.setText(current.month + " " + current.year);

        final int s = current.year;
        final int d = current.monthOrder;
        final String month=current.month;

        myHolder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("year", s);
                bundle.putInt("monthOrder", d);
                bundle.putString("month",month);
                final Intent intent;
                intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return order.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView orderCount;
        TextView month;
        TextView price;
        TextView details;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            orderCount = (TextView) itemView.findViewById(R.id.orderCount);
            price = (TextView) itemView.findViewById(R.id.price);
            month = (TextView) itemView.findViewById(R.id.month);
            details = (TextView) itemView.findViewById(R.id.details);
        }

    }
}
