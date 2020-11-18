package com.example.salesrecordapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"_id","user_id"})
public class Order {

    @NonNull
    String _id;

    @NonNull
    String item_type;

    @NonNull
    String order_date;

    @NonNull
    double unit_cost;

    @NonNull
    int units_sold;

    @NonNull
    double total;

    @NonNull
    String user_id;

    public Order() {
    }

    @Override
    public String toString() {
        return "Order{" +
                "_id='" + _id + '\'' +
                ", item_type='" + item_type + '\'' +
                ", order_date='" + order_date + '\'' +
                ", unit_cost=" + unit_cost +
                ", units_sold=" + units_sold +
                ", total=" + total +
                ", user_id='" + user_id + '\'' +
                '}';
    }

    public Order(@NonNull String _id, @NonNull String item_type, @NonNull String order_date, float unit_cost, int units_sold, double total, @NonNull String user_id) {
        this._id = _id;
        this.item_type = item_type;
        this.order_date = order_date;
        this.unit_cost = unit_cost;
        this.units_sold = units_sold;
        this.total = total;
        this.user_id = user_id;
    }

}
