package com.example.carapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewFlipper;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carapp.Adapters.DashboardRCViewAdapter;
import com.example.carapp.DashboardLinkModel;
import com.example.carapp.R;
import com.example.carapp.ViewModels.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardModern extends Fragment implements DashboardRCViewAdapter.OnItemClickListener {
    private NavController navController;
    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private RecyclerView sublinks;
    private Button addCar;
    private ViewFlipper viewFlipper;
    private LiveData<HashMap<String, Object>> userData;
    private Observer<HashMap<String, Object>> userDataObserver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = new ViewModelProvider(this).get(FirebaseManager.class);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_modern, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Save the navController
        navController = Navigation.findNavController(requireActivity(), R.id.Nav_Dashboard);

        // Set the ViewFlipper
        viewFlipper = view.findViewById(R.id.view_flipper_dashboardModern);

        /* Save Dashboard modern elements here*/

        // Set the RecyclerView
        sublinks = view.findViewById(R.id.recyclerViewSublinks);
        List<DashboardLinkModel> dashboardLinks = new ArrayList<>();
        populateSublinks(dashboardLinks);
        DashboardRCViewAdapter adapter = new DashboardRCViewAdapter(getContext(), dashboardLinks);
        adapter.setOnItemClickListener(this); // Setup callback
        sublinks.setLayoutManager(new LinearLayoutManager(getContext()));
        sublinks.setAdapter(adapter);

        // TODO: Set the rest of the elements from Dashboard modern here

        /* Set the elements of the No Car layout here*/

        addCar = view.findViewById(R.id.AddCarNoCar);
        addCar.setOnClickListener(clickListener());


        // Load in user data
        firebaseManager.loadProfile(user.getUid());
        userData = firebaseManager.getUserData();
        userDataObserver = this::checkData;
        userData.observe(getViewLifecycleOwner(), userDataObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the observer when no longer needed
        userData.removeObserver(userDataObserver);
    }

    private void populateSublinks(List<DashboardLinkModel> dashboardLinks) {
        //TODO: refactor to generate based on vehicle type
        dashboardLinks.add(new DashboardLinkModel("Controls", R.drawable.controls, R.navigation.dashboard_navigation_graph));
        dashboardLinks.add(new DashboardLinkModel("Location", R.drawable.controls, R.navigation.dashboard_navigation_graph));
        dashboardLinks.add(new DashboardLinkModel("Charging", R.drawable.controls, R.navigation.dashboard_navigation_graph));
        dashboardLinks.add(new DashboardLinkModel("More", R.drawable.controls, R.id.carInfoFragment));
    }

    private void checkData(HashMap<String, Object> userData)
    {
        // Check that user has at least one car in their profile
        if ((userData != null && userData.containsKey("cars"))) {
            // Show the dashboard normally
            viewFlipper.setDisplayedChild(0);
        }
        else {
            // Show the no car layout
            viewFlipper.setDisplayedChild(1);
        }
    }

    private View.OnClickListener clickListener() {
        return view -> {
            if (view.getId() == R.id.AddCarNoCar) {
                // go to the pair car screen
                NavDirections actionGoToCarSearch = DashboardModernDirections.actionDashboardFragment2ToCarSearch2();
                navController.navigate(actionGoToCarSearch);

                //for now, to debug, add a fake car
                //FirebaseManager firebaseManager = new ViewModelProvider(getActivity()).get(FirebaseManager.class);

            }
        };
    }


    @Override
    public void onItemClick(int itemId) {
        // Navigate to the correct destination based on link pressed
        navController.navigate(itemId);

    }
}
