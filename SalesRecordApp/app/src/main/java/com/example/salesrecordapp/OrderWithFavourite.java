package com.example.salesrecordapp;

public class OrderWithFavourite {
    Order order;
    boolean isFavorite;

    @Override
    public String toString() {
        return "OrderWithFavourite{" +
                "order=" + order.toString() +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
