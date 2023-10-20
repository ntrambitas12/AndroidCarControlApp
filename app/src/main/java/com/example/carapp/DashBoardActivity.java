package com.example.carapp;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;


import com.example.carapp.ViewModels.MyFragmentPagerAdapter;

public class DashBoardActivity extends AppCompatActivity {

    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get a reference to the navController and navigate user to the loginFragment
        this.navController = Navigation.findNavController(this, R.id.Nav_Dashboard);

    }
}


