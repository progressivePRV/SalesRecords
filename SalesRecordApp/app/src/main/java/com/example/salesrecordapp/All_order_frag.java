package com.example.salesrecordapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link All_order_frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class All_order_frag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AppViewModel viewModel;
    private static final String TAG = "okay";

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

//        //********************************** delete order testing
//        Order o = new Order();
//        o.user_id="user"+1;
//        o._id=""+3;
//        o.item_type="type1";
//        o.order_date="11/11/2020";
//        o.unit_cost= (float) 10.0;
//        o.units_sold=2;
//        o.total=20.0;
//          viewModel.DeleteOrder(o);   /////////  provide order to delete the from database

        //********************************** getting every order for user
        // change static user id
        viewModel.GetOrdersForUser("user1").observe(getActivity(), new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                Log.d(TAG, "onChanged: got "+orders.size()+" orders for user1");
                if (!orders.isEmpty()){
                    Log.d(TAG, "onChanged: first order for user1=>"+orders.get(0));
                }
            }
        });

        return view;
    }
}