package com.example.salesrecordapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TabLayoutActivity extends AppCompatActivity {

    TabLayout tabLayout;
    view_pager2_adapter adapter;
    ViewPager2 viewPager2;
    private static final String TAG = "okay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);

        tabLayout = findViewById(R.id.tab_layout_in_tabActivity);
        adapter = new view_pager2_adapter(this);
        viewPager2 = findViewById(R.id.view_pager2_in_tabLayout);
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout,viewPager2, (tab, position) -> {

            switch (position){
                case 0:
                    tab.setText(R.string.favorite);
                    break;
                case 1:
                    tab.setText(R.string.all_orders);
            }
        }).attach();

    }
}