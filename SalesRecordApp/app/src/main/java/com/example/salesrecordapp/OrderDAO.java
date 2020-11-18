package com.example.salesrecordapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OrderDAO {

    @Insert
    void insert(Order order);

    @Delete
    int delete(Order o);

    @Query("Select * from `Order` where _id=:oId and user_id=:uid")
    Order findOrderWhereIdAndUserId(String oId, String uid);

    @Query("Select * from `Order` where user_id=:uid")
    LiveData<List<Order>> FindAllOrderForUser(String uid);

}
