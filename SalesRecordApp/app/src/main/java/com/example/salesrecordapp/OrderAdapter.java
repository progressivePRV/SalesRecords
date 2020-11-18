package com.example.salesrecordapp;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private ArrayList<OrderWithFavourite> mDataset;
    public static InteractWithRecyclerView interact;

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderAdapter(ArrayList<OrderWithFavourite> myDataset, All_order_frag ctx) {
        mDataset = myDataset;
        interact = (InteractWithRecyclerView) ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        OrderWithFavourite orderWithFavourite = mDataset.get(position);
        Order order = orderWithFavourite.order;

        holder.itemTypeText.setText(order.item_type);
        holder.orderDateText.setText(order.order_date);
        holder.unitsSoldText.setText(String.valueOf(order.units_sold));
        holder.unitCostText.setText("$"+order.unit_cost);
        holder.totalText.setText("$"+order.total);

        if(orderWithFavourite.isFavorite == false){
            holder.imageButtonFav.setVisibility(ImageButton.INVISIBLE);
            holder.imageButton.setVisibility(ImageButton.VISIBLE);
        }else{
            holder.imageButton.setVisibility(ImageButton.INVISIBLE);
            holder.imageButtonFav.setVisibility(ImageButton.VISIBLE);
        }

//        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("Demo","Selected Position is :" + mDataset.get(position));
//                interact.getDetails(mDataset.get(position));
//            }
//        });

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageButton.setVisibility(ImageButton.INVISIBLE);
                holder.imageButtonFav.setVisibility(ImageButton.VISIBLE);
                interact.getDetails(mDataset.get(position).order, "add");
            }
        });

        holder.imageButtonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageButtonFav.setVisibility(ImageButton.INVISIBLE);
                holder.imageButton.setVisibility(ImageButton.VISIBLE);
                interact.getDetails(mDataset.get(position).order, "delete");
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        TextView itemTypeText, orderDateText, unitsSoldText, unitCostText, totalText;
        ImageButton imageButton, imageButtonFav;
        ConstraintLayout constraintLayout;
        public MyViewHolder(View view) {
            super(view);
            itemTypeText = view.findViewById(R.id.itemTypeText);
            orderDateText = view.findViewById(R.id.orderDateText);
            unitsSoldText = view.findViewById(R.id.unitsSoldText);
            unitCostText = view.findViewById(R.id.unitCostText);
            totalText = view.findViewById(R.id.totalText);
            imageButton = view.findViewById(R.id.imageButton);
            imageButtonFav = view.findViewById(R.id.imageButtonFav);
            constraintLayout = view.findViewById(R.id.constraintLayout);
        }
    }

    public interface InteractWithRecyclerView{
        public void getDetails(Order order, String Operation);
    }
}

