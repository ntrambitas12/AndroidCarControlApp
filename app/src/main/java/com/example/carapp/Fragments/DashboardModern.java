package com.example.carapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carapp.Adapters.DashboardRCViewAdapter;
import com.example.carapp.DashboardLinkModel;
import com.example.carapp.R;
import com.example.carapp.ViewModels.FirebaseManager;

import java.util.ArrayList;
import java.util.List;

public class DashboardModern extends Fragment {
    private NavController navController;
    private FirebaseManager firebaseManager;
    private RecyclerView sublinks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = new ViewModelProvider(this).get(FirebaseManager.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_modern_layout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Save the navController
        //navController = Navigation.findNavController(requireActivity(), R.id.dashboard_navigation_graph);

        // Set the RecyclerView
        sublinks = view.findViewById(R.id.recyclerViewSublinks);
        sublinks.setLayoutManager(new LinearLayoutManager(getContext()));
        List<DashboardLinkModel> dashboardLinks = new ArrayList<>();
        populateSublinks(dashboardLinks);
        DashboardRCViewAdapter adapter = new DashboardRCViewAdapter(getContext(), dashboardLinks);
        sublinks.setAdapter(adapter);

    }

    private void populateSublinks(List<DashboardLinkModel> dashboardLinks) {
        //TODO: refactor to generate based on vehicle type
        dashboardLinks.add(new DashboardLinkModel("Controls", R.drawable.controls, R.navigation.dashboard_navigation_graph));
        dashboardLinks.add(new DashboardLinkModel("Location", R.drawable.controls, R.navigation.dashboard_navigation_graph));
        dashboardLinks.add(new DashboardLinkModel("Charging", R.drawable.controls, R.navigation.dashboard_navigation_graph));
        dashboardLinks.add(new DashboardLinkModel("More", R.drawable.controls, R.navigation.dashboard_navigation_graph));
    }


}
