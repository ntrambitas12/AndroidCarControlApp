package com.example.carapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.carapp.DashBoardActivity;
import com.example.carapp.R;
import com.example.carapp.ViewModels.MyFragmentPagerAdapter;

public class ParentDashboardFragment extends Fragment {

    private NavController navController;
    private ViewPager2 viewPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_swipe_activity, container, false);
        this.viewPager = rootView.findViewById(R.id.CarView);
        this.viewPager.setAdapter(new MyFragmentPagerAdapter(getActivity()));

        Button addCar = rootView.findViewById(R.id.AddCar);
        Button removeCar = rootView.findViewById(R.id.RemoveCar);

        addCar.setOnClickListener(this.createListener());
        removeCar.setOnClickListener(this.createListener());

        //this.viewPager.setAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get navigation controller
        navController = Navigation.findNavController(requireActivity(), R.id.Nav_Dashboard);
    }

    private View.OnClickListener createListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.AddCar) {
                    // Get email and password text and call signIn method
                    NavDirections actionGoToCarSearch = ParentDashboardFragmentDirections.actionDashboardFragment2ToCarSearch2();
                    navController.navigate(actionGoToCarSearch);
                }
                else if (view.getId() == R.id.RemoveCar) {
                    MyFragmentPagerAdapter adapter = (MyFragmentPagerAdapter)viewPager.getAdapter();
                    adapter.removeFragment(viewPager.getCurrentItem());
                }
            }
        };
    }

}