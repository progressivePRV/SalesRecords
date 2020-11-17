package com.example.salesrecordapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Favorite_frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Favorite_frag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "okay";
    private AppViewModel viewModel;


    public Favorite_frag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Favorite_frag.
     */
    // TODO: Rename and change types and number of parameters
    public static Favorite_frag newInstance(String param1, String param2) {
        Favorite_frag fragment = new Favorite_frag();
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
        View view = inflater.inflate(R.layout.fragment_favorite_frag, container, false);
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
        new FindSpecificOrder().execute("1","user0");

        return view;
    }

    class FindSpecificOrder extends AsyncTask<String,Void,Order> {
        @Override
        protected Order doInBackground(String... strs) {
            return viewModel.FindOrderWhereIdAndUserId(strs[0],strs[1]);
        }
        @Override
        protected void onPostExecute(Order order) {
            super.onPostExecute(order);
            if (order==null){
                Log.d(TAG, "onPostExecute: order not found");
            }else{
                Log.d(TAG, "onPostExecute: after searching for order in db=>"+order);
            }
        }
    }

    private boolean IsConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    class GetOrdersFromServer extends AsyncTask<String ,Void,Void> {
        String result="",error="";
        @Override
        protected Void doInBackground(String... strs) {
            final OkHttpClient client = new OkHttpClient();

            String url = "http://64.227.27.167:3000/api/v1/sales";
            if (strs.length>0){
                url += "?";
                for (String s : strs){
                    url += s+"&";
                }
            }


            Request request = new Request.Builder()
                    .url(url)
//                    .addHeader("Authorization","Bearer "+auth)
                    .build();
            try{
                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.d(TAG, "doInBackground: response =>"+result);
                if (!response.isSuccessful()){
                    error = result;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: error while fetching orders from server in main activity");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(error.isEmpty()){
                try {
                    JSONArray rootarr = new JSONArray(result);
                    Log.d(TAG, "onPostExecute: got order arr of size=>"+rootarr.length());
                    for (int i=0;i<rootarr.length();i++){
//                        JSONObject jb =  rootarr.getJSONObject(i);
//                        Order o = gson.fromJson(jb.toString(),Order.class);
//                        // set then add o.user_id
//                        orders.add(o);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onPostExecute: error while parsing the jason array of orders");
                }
            }else{
                Log.d(TAG, "onPostExecute: error occurred in fetching orders");
                try {
                    JSONObject root =  new JSONObject(error);
                    int errorCode = root.getInt("errorCode");
                    switch (errorCode){
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
        }
    }

}