package com.dac.onlineausadhi.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dac.onlineausadhi.activities.OrderMedicinesActivity;
import com.dac.onlineausadhi.classes.CartList;
import com.dac.onlineausadhi.onlineaushadhilin.R;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Created by blood-mist on 6/2/16.
 */
public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CartAdapter";
    private List<CartList> items = Collections.emptyList();
    private Context context;
    private LayoutInflater inflator;
    private OrderDbAdapter db;

    public CartAdapter(Context context, List<CartList> items) {
        this.context = context;
        inflator = LayoutInflater.from(context);
        this.items = items;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.item_cart_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        db = new OrderDbAdapter(context);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder myHolder = (ViewHolder) holder;
        CartList item = items.get(position);
        myHolder.medicineName.setText(item.getMedicineName());
        myHolder.quantity.setText(String.valueOf(item.getQuantity()));
        myHolder.medicineType.setText(item.getMedicineType());
        myHolder.currentDate.setText(item.getCurrentDate());
        final String medicine = item.getMedicineName();
        final Integer quantity = item.getQuantity();
        myHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderMedicinesActivity.class);
                intent.putExtra("medicine", medicine);
                intent.putExtra("quantity", quantity);
                Log.d(TAG, medicine);
                context.startActivity(intent);
            }
        });
        myHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, medicine);
                    AlertDialog.Builder dialogBox = new AlertDialog.Builder(context);
                dialogBox.setMessage(R.string.remove_item);
                dialogBox.setCancelable(true);

                dialogBox.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        db.open();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    if (db.deleteOrder(medicine))
                                        delete(myHolder.getAdapterPosition());


                                    dialog.cancel();
                                    Toast.makeText(context,"Item successfullyremoved",Toast.LENGTH_LONG).show();
                                }
                            });
                dialogBox.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }

                            });

                    AlertDialog alertBox = dialogBox.create();
                alertBox.show();
        }


        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void delete(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView medicineName;
        public TextView quantity;
        public TextView medicineType;
        public TextView currentDate;
        public TextView edit;
        public TextView remove;

        public ViewHolder(View itemView) {
            super(itemView);
            medicineName = (TextView) itemView.findViewById(R.id.medicineName);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            medicineType = (TextView) itemView.findViewById(R.id.type);
            currentDate = (TextView) itemView.findViewById(R.id.date);
            edit = (TextView) itemView.findViewById(R.id.edit);
            remove = (TextView) itemView.findViewById(R.id.remove);
        }
    }
}

