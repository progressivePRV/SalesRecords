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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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
    String globalQuery = "";
    int page = 1;
    ArrayList<Order> orderArrayList = new ArrayList<>();
    ArrayList<Order> memoryArrayList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "okay";
    private AppViewModel viewModel;

    private EditText numberEditTextCon, FilterOrderName;
    private Spinner conditionSpinner;

    private RadioButton radioButton, radioButton2;

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

        preferences = getActivity().getSharedPreferences("TokeyKey", 0);

        String token_key = preferences.getString("TOKEN_KEY", null);

        String pro =  preferences.getString("USER",null);
        user = gson.fromJson(pro, User.class);

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
        mAdapter = new OrderAdapter(memoryArrayList, All_order_frag.this);
        recyclerView.setAdapter(mAdapter);

        Spinner spinner = (Spinner) getView().findViewById(R.id.filterSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.filter_by, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Spinner condSpinner = (Spinner) getView().findViewById(R.id.conditionSpinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(),
                R.array.condition_spinner, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        condSpinner.setAdapter(adapter1);

        Spinner sortSpinner = (Spinner) getView().findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_by, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter2);

        numberEditTextCon = getView().findViewById(R.id.numberEditTextCon);
        FilterOrderName = getView().findViewById(R.id.FilterOrderName);
        conditionSpinner = getView().findViewById(R.id.conditionSpinner);

        numberEditTextCon.setVisibility(EditText.INVISIBLE);
        FilterOrderName.setVisibility(EditText.INVISIBLE);
        conditionSpinner.setVisibility(Spinner.INVISIBLE);

        radioButton = getView().findViewById(R.id.radioButton);
        radioButton2 = getView().findViewById(R.id.radioButton2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resetEveryField();
                if(position == 0){
                    FilterOrderName.setVisibility(EditText.INVISIBLE);
                    conditionSpinner.setVisibility(Spinner.INVISIBLE);
                    numberEditTextCon.setVisibility(EditText.INVISIBLE);
                } else if(position == 1 || position == 2){
                    Log.d("demo",position+" is entering inside");
                    FilterOrderName.setVisibility(EditText.VISIBLE);
                    conditionSpinner.setVisibility(Spinner.INVISIBLE);
                    numberEditTextCon.setVisibility(EditText.INVISIBLE);
                }else{
                    FilterOrderName.setVisibility(EditText.INVISIBLE);
                    conditionSpinner.setVisibility(Spinner.VISIBLE);
                    numberEditTextCon.setVisibility(EditText.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    radioButton.setChecked(false);
                    radioButton2.setChecked(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("demo","recyclerview refreshing");
                if(page > 2){
                    page = page - 1;
                    for(int i=memoryArrayList.size()-1; i<0; i++){
                        memoryArrayList.remove(i);
                        if(memoryArrayList.size() == 50){
                            break;
                        }
                    }
                    if(globalQuery.equals("")){
                        new GetOrdersFromServer(true).execute("");
                    }else{
                        new GetOrdersFromServer(true).execute(globalQuery);
                    }
                }else{
                    Toast.makeText(getActivity(), "No more records to be loaded. You are already on the top!", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        new GetOrdersFromServer(false).execute("");

        //Search filter button functionalities
        getView().findViewById(R.id.searchFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //This is for filter;
                String query = "";
//                filter=order_date&filterValue=24&sortBy=order_date&sortOrder=desc&page=1
                int position = spinner.getSelectedItemPosition();
                int condition = 0;
                switch (position){
                    case 0:
                        query = "";
                        break;
                    case 1:
                        query = "";
                        if(FilterOrderName.getText().toString().equals("")){
                            Toast.makeText(getActivity(), "Search Text cannot be empty", Toast.LENGTH_SHORT).show();
                            FilterOrderName.setError("Cannot be empty");
                        }else{
                            query = "filter=item_type&filterValue="+FilterOrderName.getText().toString();
                        }
                        break;
                    case 2:
                        query = "";
                        if(FilterOrderName.getText().toString().equals("")){
                            Toast.makeText(getActivity(), "Search Text cannot be empty", Toast.LENGTH_SHORT).show();
                            FilterOrderName.setError("Cannot be empty");
                        }else{
                            query = "filter=order_date&filterValue="+FilterOrderName.getText().toString();
                        }
                        break;
                    case 3:
                        query = "";
                        condition = condSpinner.getSelectedItemPosition();
                        if(numberEditTextCon.getText().toString().equals("")){
                            Toast.makeText(getActivity(), "Search Text cannot be empty", Toast.LENGTH_SHORT).show();
                            numberEditTextCon.setError("Cannot be empty");
                        }else{
                            String filterComparator = getFilterComparator(condition);
                            query = "filter=units_sold&filterValue="
                                    +numberEditTextCon.getText().toString()
                                    +"&filterComparator="+filterComparator;
                        }
                        break;
                    case 4:
                        query = "";
                        condition = condSpinner.getSelectedItemPosition();
                        if(numberEditTextCon.getText().toString().equals("")){
                            Toast.makeText(getActivity(), "Search Text cannot be empty", Toast.LENGTH_SHORT).show();
                            numberEditTextCon.setError("Cannot be empty");
                        }else{
                            String filterComparator = getFilterComparator(condition);
                            query = "filter=unit_cost&filterValue="
                                    +numberEditTextCon.getText().toString()
                                    +"&filterComparator="+filterComparator;
                        }
                        break;
                    case 5:
                        query = "";
                        condition = condSpinner.getSelectedItemPosition();
                        if(numberEditTextCon.getText().toString().equals("")){
                            Toast.makeText(getActivity(), "Search Text cannot be empty", Toast.LENGTH_SHORT).show();
                            numberEditTextCon.setError("Cannot be empty");
                        }else{
                            String filterComparator = "";

                            query += "filter=total&filterValue="
                                    +numberEditTextCon.getText().toString()
                                    +"&filterComparator="+filterComparator;
                        }
                        break;
                }

                //This is for sorting

                int sortPosition = sortSpinner.getSelectedItemPosition();
//                sortBy=order_date&sortOrder=desc
                String sortQuery = "";

                switch (sortPosition){
                    case 0:
                        sortQuery = "";
                        break;
                    case 1:
                        sortQuery = "";
                        if(radioButton.isChecked()){
                            sortQuery = "sortBy=item_type&sortOrder=desc";
                        }else{
                            sortQuery = "sortBy=item_type&sortOrder=asc";
                        }
                        break;
                    case 2:
                        sortQuery = "";
                        if(radioButton.isChecked()){
                            sortQuery = "sortBy=order_date&sortOrder=desc";
                        }else{
                            sortQuery = "sortBy=order_date&sortOrder=asc";
                        }
                        break;
                    case 3:
                        sortQuery = "";
                        if(radioButton.isChecked()){
                            sortQuery = "sortBy=units_sold&sortOrder=desc";
                        }else{
                            sortQuery = "sortBy=units_sold&sortOrder=asc";
                        }
                        break;
                    case 4:
                        sortQuery = "";
                        if(radioButton.isChecked()){
                            sortQuery = "sortBy=unit_cost&sortOrder=desc";
                        }else{
                            sortQuery = "sortBy=unit_cost&sortOrder=asc";
                        }
                        break;
                    case 5:
                        sortQuery = "";
                        if(radioButton.isChecked()){
                            sortQuery = "sortBy=total&sortOrder=desc";
                        }else{
                            sortQuery = "sortBy=total&sortOrder=asc";
                        }
                        break;
                }
                page = 1;
                memoryArrayList.clear();

                if(query.equals("")&&sortQuery.equals("")){
                    globalQuery = "";
                    new GetOrdersFromServer(false).execute("");
                }else if(query.equals("") && !sortQuery.equals("")){
                    globalQuery = "&"+sortQuery;
                    new GetOrdersFromServer(false).execute(globalQuery);
                }else if(!query.equals("") && sortQuery.equals("")){
                    globalQuery = "&"+query;
                    new GetOrdersFromServer(false).execute(globalQuery);
                }else if(!query.equals("") && !sortQuery.equals("")){
                    globalQuery = "&"+query+"&"+sortQuery;
                    new GetOrdersFromServer(false).execute(globalQuery);
                }

            }
        });

        //resetFilter button functionalities
        getView().findViewById(R.id.resetFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setSelection(0);
                resetEveryField();
                sortSpinner.setSelection(0);
                radioButton.setChecked(false);
                radioButton2.setChecked(false);
                memoryArrayList.clear();
                page = 1;
                new GetOrdersFromServer(false).execute("");
            }
        });

        getView().findViewById(R.id.buttonLoadMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(memoryArrayList.size() == 100){
                    for(int i=0; i<memoryArrayList.size(); i++){
                        memoryArrayList.remove(i);
                        if(i==49){
                            break;
                        }
                    }
                }
                page = page+1;
                if(globalQuery.equals("")){
                    new GetOrdersFromServer(false).execute("");
                }else{
                    new GetOrdersFromServer(false).execute(globalQuery);
                }
            }
        });
    }

    public String getFilterComparator(int condition){
        String filterComparator = "";
        if(condition == 0){
            filterComparator = "eq";
        }else if(condition == 1){
            filterComparator = "gte";
        }else if(condition == 2){
            filterComparator = "gt";
        }else if(condition == 3){
            filterComparator = "lte";
        }else if(condition == 4){
            filterComparator = "lt";
        }
        return filterComparator;
    }

    public void resetEveryField(){
        FilterOrderName.setText("");
        numberEditTextCon.setText("");
        conditionSpinner.setSelection(0);
    }

    @Override
    public void getDetails(Order order, String operation) {
        if(operation.equals("add")){
            viewModel.InsertOrder(order);
        }else if(operation.equals("delete")){
            viewModel.DeleteOrder(order);
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
        boolean isTop;
        GetOrdersFromServer(boolean isTop){
            this.isTop = isTop;
        }
        @Override
        protected String doInBackground(String... strs) {
            orderArrayList.clear();
            final OkHttpClient client = new OkHttpClient();
            Log.d("demo" , getResources().getString(R.string.endPointUrl)+"sales?page="+page+strs[0]);

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.endPointUrl)+"sales?page="+page+strs[0])
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
                            orderArrayList.add(order);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onPostExecute: error while parsing the jason array of orders");
                    }
                    if(orderArrayList.size() > 0){
                        if(isTop == true){
                            List<Order> temp = new ArrayList<>(memoryArrayList);
                            memoryArrayList.clear();
                            Log.d("demo","temp size is "+temp.size());
                            memoryArrayList.addAll(orderArrayList);
                            memoryArrayList.addAll(temp);
                        }else{
                            memoryArrayList.addAll(orderArrayList);
                        }
                        Log.d("demo","memoryArrayList size is "+memoryArrayList.size());
                        mAdapter.notifyDataSetChanged();
                        if(isTop == false){
                            recyclerView.scrollToPosition(51);
                        }else{
                            recyclerView.scrollToPosition(0);
                        }
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
                            case 102:
                                Toast.makeText(getActivity(), "No More Records Found", Toast.LENGTH_SHORT).show();
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


}