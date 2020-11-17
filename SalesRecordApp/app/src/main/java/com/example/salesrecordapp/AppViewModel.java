package com.example.salesrecordapp;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AppViewModel extends AndroidViewModel {

    private static final String TAG = "okay";
    OrderDAO orderDao;


    public AppViewModel(@NonNull Application application) {
        super(application);
        RoomDataBase db = RoomDataBase.getDatabase(application);
        orderDao = db.orderDAO();
    }

    public void InsertOrder(Order order){
        new InsertOrder(orderDao).execute(order);
    }

    public LiveData<List<Order>> GetOrdersForUser(String uid){
        return orderDao.FindAllOrderForUser(uid);
    }
    public void DeleteOrder(Order order){
        new DeleteOrder(orderDao).execute(order);
    }

    public Order FindOrderWhereIdAndUserId(String id,String uid){
        return orderDao.findOrderWhereIdAndUserId(id,uid);
    }

    class InsertOrder extends AsyncTask<Order,Void,Void> {
        OrderDAO dao;
        String result="",error="";
        public InsertOrder(OrderDAO dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(Order... orders) {
            Log.d(TAG, "doInBackground: calling for insert orders in App View Model");
            try{
                dao.insert(orders[0]);
                result = "Insert query successful";
            }catch (SQLiteConstraintException e){
                error = "Id should be unique for insert query";
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute: InertOrder called");
            if (error.isEmpty()){
                Toast.makeText(getApplication(), result, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplication(), error, Toast.LENGTH_SHORT).show();
            }
        }
    }


    class DeleteOrder extends AsyncTask<Order,Void,Void> {
        OrderDAO dao;
        public DeleteOrder(OrderDAO dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(Order... orders) {
            int i = dao.delete(orders[0]);
            Log.d(TAG, "doInBackground: "+i+" rows deleted");
            return null;
        }
    }

}
