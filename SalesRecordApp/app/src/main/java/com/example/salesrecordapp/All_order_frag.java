package com.example.salesrecordapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link All_order_frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class All_order_frag extends Fragment implements OrderAdapter.InteractWithRecyclerView {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SharedPreferences preferences;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    Gson gson = new Gson();
    User user;
    ArrayList<OrderWithFavourite> orderArrayList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "okay";
    private AppViewModel viewModel;


    public All_order_frag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment All_order_frag.
     */
    // TODO: Rename and change types and number of parameters
    public static All_order_frag newInstance(String param1, String param2) {
        All_order_frag fragment = new All_order_frag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_order_frag, container, false);
        // Inflate the layout for this fragment


        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

//                Order o = new Order();
//                o.user_id="user1";
//                o._id="2";
//                o.item_type="type1";
//                o.order_date="11/11/2020";
//                o.unit_cost= (float) 10.0;
//                o.units_sold=2;
//                o.total=20.0;
//                  viewModel.InsertOrder(o);   ////////// pass order to insert


        //********************************** FindSpecificOrder testing
        preferences = getActivity().getSharedPreferences("TokeyKey", 0);

        String token_key = preferences.getString("TOKEN_KEY", null);

        String pro =  preferences.getString("USER",null);
        user = gson.fromJson(pro, User.class);



        // this is for finsing a specific order.
//        new FindSpecificOrder().execute("1",);

        //As this is creating a new view, so getting all the orders from the server.
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.orderRecylerView);
        swipeRefreshLayout = getView().findViewById(R.id.swipe);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new OrderAdapter(orderArrayList, All_order_frag.this);
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("demo","recyclerview refreshing");
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        new GetOrdersFromServer().execute();
    }

    @Override
    public void getDetails(Order order, String operation) {
        if(operation.equals("add")){
            viewModel.InsertOrder(order);
        }else if(operation.equals("delete")){
            viewModel.DeleteOrder(order);
        }
    }

    class FindSpecificOrder extends AsyncTask<String,Void,Order> {
        Order orderPrevious;
        int position;

        FindSpecificOrder(Order order, int position){
            this.orderPrevious = order;
            this.position = position;
        }
        @Override
        protected Order doInBackground(String... strs) {
            return viewModel.FindOrderWhereIdAndUserId(orderPrevious._id,orderPrevious.user_id);
        }
        @Override
        protected void onPostExecute(Order order) {
            super.onPostExecute(order);
            Log.d("demo",orderPrevious.toString());
            Log.d("demo",order+"");
            if (order==null){
                boolean isFavorite = true;
                Log.d(TAG, "onPostExecute: order not found");
            }else{
                Log.d("demo","Entered the onpost execute of order "+order.toString());
                Log.d(TAG, "onPostExecute: after searching for order in db=>"+order);
                OrderWithFavourite orderWithFavourite = new OrderWithFavourite();
                orderWithFavourite.order = orderPrevious;
                orderWithFavourite.isFavorite = true;
                orderArrayList.set(position, orderWithFavourite);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    boolean isStatus = true;
    private boolean IsConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    class GetOrdersFromServer extends AsyncTask<String ,Void,String> {
        String result = "", error = "";

        @Override
        protected String doInBackground(String... strs) {
            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.endPointUrl)+"sales?page=1")
                    .header("Authorization", "Bearer "+ preferences.getString("TOKEN_KEY", null))
                    .build();
            String responseValue = null;
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    isStatus = true;
                } else {
                    isStatus = false;
                }
                responseValue = response.body().string();
            } catch (IOException e) {
                Log.d("demo", "Update cart exception");
                e.printStackTrace();
            }

            return responseValue;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                if (isStatus) {
                    try {
                        JSONArray rootarr = new JSONArray(response);
                        Log.d(TAG, "onPostExecute: got order arr of size=>" + rootarr.length());
                        for (int i = 0; i < rootarr.length(); i++) {
                            JSONObject jb = rootarr.getJSONObject(i);
                            Order order = new Order();
                            order._id = jb.getString("_id");
                            order.item_type = jb.getString("item_type");
                            order.order_date = jb.getString("order_date");
                            order.units_sold = jb.getInt("units_sold");
                            order.unit_cost = jb.getDouble("unit_cost");
                            order.total = jb.getDouble("total");
                            order.user_id = user._id;
                            OrderWithFavourite order1 = new OrderWithFavourite();
                            order1.order =  order;
                            order1.isFavorite = false;
                            orderArrayList.add(order1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onPostExecute: error while parsing the jason array of orders");
                    }
                    if(orderArrayList.size() > 0){
                        mAdapter.notifyDataSetChanged();
                        getFavoriteOrder();
                    }else{
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Sorry no orders available!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d(TAG, "onPostExecute: error occurred in fetching orders");
                    try {
                        JSONObject root = new JSONObject(error);
                        int errorCode = root.getInt("errorCode");
                        switch (errorCode) {
                            case 103:
                                Toast.makeText(getActivity(), "Token was not provided for request", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onPostExecute: token was not provided for request");
                                break;
                            case 104:
                                Toast.makeText(getActivity(), "Session expired Login again", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onPostExecute: Token Expired");
                            default:
                                Log.d(TAG, "onPostExecute: error code was not provided");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onPostExecute: error while parsing error for fetching orders");
                    }
                }
            } else {
                Toast.makeText(getActivity(), "Some error occured. Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getFavoriteOrder() {
        for(int i=0; i<orderArrayList.size(); i++) {
            new FindSpecificOrder(orderArrayList.get(i).order, i).execute();
        }
    }

}