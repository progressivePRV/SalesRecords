package com.example.salesrecordapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TabLayoutActivity extends AppCompatActivity {

    TabLayout tabLayout;
    FragmentContainerView frag_container;
    view_pager2_adapter adapter;
    ViewPager2 viewPager2;
    private static final String TAG = "okay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);

        frag_container = findViewById(R.id.frag_container_intablayout);
        tabLayout = findViewById(R.id.tab_layout_in_tabActivity);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                Log.d(TAG, "onTabSelected: tab tag=>"+tab.getTag());
                String tag_text = tab.getText().toString();
                Log.d(TAG, "onTabSelected: tab text=>"+tag_text);
                switch(tag_text){
                    case "Favorite":
                        Fragment f = getSupportFragmentManager().findFragmentByTag("FAV");
                        if (f==null){
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frag_container_intablayout,new Favorite_frag(),"FAV")
                                    .addToBackStack("FAV_TO_BACK")
                                    .commit();
                        }else{
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frag_container_intablayout,f)
                                    .commit();
                        }

                        break;
                    case "All Orders":
                        Fragment f1 = getSupportFragmentManager().findFragmentByTag("ALL");
                        if (f1==null){
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frag_container_intablayout,new All_order_frag(),"ALL")
                                    .addToBackStack("ALL_TO_BACk")
                                    .commit();
                        }else{
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frag_container_intablayout,f1)
                                    .commit();
                        }

                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        adapter = new view_pager2_adapter(this);
//        viewPager2 = findViewById(R.id.view_pager2_in_tabLayout);
//        viewPager2.setAdapter(adapter);
//        new TabLayoutMediator(tabLayout,viewPager2, (tab, position) -> {
//
//            switch (position){
//                case 0:
//                    tab.setText(R.string.favorite);
//                    break;
//                case 1:
//                    tab.setText(R.string.all_orders);
//            }
//        }).attach();

    }
}