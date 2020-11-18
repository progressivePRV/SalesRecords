package com.example.salesrecordapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {
    private List<Order> mDataset;
    public static FavoriteAdapter.InteractWithRecyclerView interact;

    // Provide a suitable constructor (depends on the kind of dataset)
    public FavoriteAdapter(List<Order> myDataset, Favorite_frag ctx) {
        mDataset = myDataset;
        interact = (FavoriteAdapter.InteractWithRecyclerView) ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FavoriteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_list, parent, false);
        FavoriteAdapter.MyViewHolder vh = new FavoriteAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final FavoriteAdapter.MyViewHolder holder, final int position) {
        Order order = mDataset.get(position);
        Log.d("demo" ,order.toString());

        holder.itemTypeFavText.setText(order.item_type);
        holder.orderDateFavText.setText(order.order_date);
        holder.unitsSoldFavText.setText(String.valueOf(order.units_sold));
        holder.unitCostFavText.setText("$"+order.unit_cost);
        holder.totalFavText.setText("$"+order.total);
        holder.imageFavButton.setVisibility(ImageButton.INVISIBLE);

//        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("Demo","Selected Position is :" + mDataset.get(position));
//                interact.getDetails(mDataset.get(position));
//            }
//        });

        holder.imageFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageFavButton.setVisibility(ImageButton.INVISIBLE);
                holder.imageFavButtonFav.setVisibility(ImageButton.VISIBLE);
                interact.getDetails(mDataset.get(position), "add");
            }
        });

        holder.imageFavButtonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageFavButtonFav.setVisibility(ImageButton.INVISIBLE);
                holder.imageFavButton.setVisibility(ImageButton.VISIBLE);
                interact.getDetails(mDataset.get(position), "delete");
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
        TextView itemTypeFavText, orderDateFavText, unitsSoldFavText, unitCostFavText, totalFavText;
        ImageButton imageFavButton, imageFavButtonFav;
//        ConstraintLayout constraintLayout;
        public MyViewHolder(View view) {
            super(view);
            itemTypeFavText = view.findViewById(R.id.itemTypeFavText);
            orderDateFavText = view.findViewById(R.id.orderDateFavText);
            unitsSoldFavText = view.findViewById(R.id.unitsSoldFavText);
            unitCostFavText = view.findViewById(R.id.unitCostFavText);
            totalFavText = view.findViewById(R.id.totalFavText);
            imageFavButton = view.findViewById(R.id.imageFavButton);
            imageFavButtonFav = view.findViewById(R.id.imageFavButtonFav);
//            constraintLayout = view.findViewById(R.id.constraintLayout);
        }
    }

    public interface InteractWithRecyclerView{
        public void getDetails(Order order, String Operation);
    }
}

