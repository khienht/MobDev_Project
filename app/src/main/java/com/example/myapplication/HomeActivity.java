package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class HomeActivity extends AppCompatActivity {
    private FragmentManager fm;
    public AnimatedBottomBar navbar;
    public HomeFragment homeFrag;
    private Fragment fragment = null;
    int selectedIndex;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // New back button handler system
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                int selectedIndex = navbar.getSelectedIndex();
                if (selectedIndex == 0) {
                    finish();
                }
                navbar.selectTabAt(0, true);
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InitVars();
        InitViews();
        InitEvents();

        fm.beginTransaction().add(R.id.main_frame, homeFrag, "home").commit();

        fragment = homeFrag;
        navbar.selectTabAt(0, false);
    }

    private void InitVars() {
        selectedIndex = 0;

        fm = getSupportFragmentManager();

        homeFrag = new HomeFragment(this);

        Utils.delayHandler = new Handler();
    }

    private void InitViews() {
        navbar = findViewById(R.id.bottom_bar);
    }

    private void InitEvents() {
        navbar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab lastTab, int newIndex, @NonNull AnimatedBottomBar.Tab newTab) {

                switch (newIndex) {
                    case 0:
                        if (homeFrag == null) { homeFrag = new HomeFragment(HomeActivity.this); }
                        fm.beginTransaction().hide(fragment).commit();
                        fragment = homeFrag;
                        break;
                }

                fm.beginTransaction()
                        .show(fragment)
                        .commit();
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        switch (navbar.getSelectedIndex()) {
//            case 0:
//                super.onBackPressed();
//            case 1:
//                if(!devicesFrag.selected_device_id.equals("")) {
//                    devicesFrag.changeSelectedDevice(-1, "");
//                    return;
//                }
//                break;
//            default:
//                break;
//        }
//
//        navbar.selectTabAt(0, true);
//    }
}