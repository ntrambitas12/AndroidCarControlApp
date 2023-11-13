package com.example.carapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

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
import androidx.viewpager2.widget.ViewPager2;

import com.example.carapp.Adapters.CarAdapter;
import com.example.carapp.Adapters.DashboardRCViewAdapter;
import com.example.carapp.Model.Car;
import com.example.carapp.Model.DashboardLinkModel;
import com.example.carapp.R;
import com.example.carapp.VehicleConnections.ConnectionManager;
import com.example.carapp.ViewModels.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardModern extends Fragment implements DashboardRCViewAdapter.OnItemClickListener {
    final int IndexOfMoreLink = 3;

    private NavController navController;
    private FirebaseManager firebaseManager;
    private FirebaseUser user;
    private ViewFlipper viewFlipper;
    private LiveData<HashMap<String, Object>> userData;
    private Observer<HashMap<String, Object>> userDataObserver;
    private LiveData<JSONObject> receivedCarData;
    private Observer<JSONObject> receivedFromCarObserver;
    private List<Car> usersCars;
    private int selectedCar; // variable that holds which car in the usersCars list is currently selected
    private ConnectionManager connectionManager;

    /* UI Dashboard Elements */
    private TextView nickNameDisplay;
    private TextView batteryRangeDisplay;
    private TextView totalRangeDisplay;
    private TextView statusDisplay;
    private ProgressBar batterySOCDisplay;
    private CarAdapter carPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = new ViewModelProvider(this).get(FirebaseManager.class);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        usersCars = new ArrayList<>();
        connectionManager = new ViewModelProvider(requireActivity()).get(ConnectionManager.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard_modern, container, false);
        // Load in user data
        firebaseManager.loadProfile(user.getUid());
        userData = firebaseManager.getUserData();
        userDataObserver = this::checkData;
        userData.observe(getViewLifecycleOwner(), userDataObserver);

        // Set receivedFromCarObserver callback
        receivedCarData = connectionManager.getReceivedFromCarListener();
        receivedFromCarObserver = this::setUIData;
        receivedCarData.observe(getViewLifecycleOwner(),receivedFromCarObserver);
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
        RecyclerView sublinks = view.findViewById(R.id.recyclerViewSublinks);
        List<DashboardLinkModel> dashboardLinks = new ArrayList<>();
        populateSublinks(dashboardLinks);
        DashboardRCViewAdapter adapter = new DashboardRCViewAdapter(getContext(), dashboardLinks);
        adapter.setOnItemClickListener(this); // Setup callback
        sublinks.setLayoutManager(new LinearLayoutManager(getContext()));
        sublinks.setAdapter(adapter);

        // Set ViewPager
        carPagerAdapter = new CarAdapter(usersCars);
        ViewPager2 swipeableCars = view.findViewById(R.id.viewPager2);
        swipeableCars.setAdapter(carPagerAdapter);
        swipeableCars.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                selectedCar = position; // Switch the position of the selected car
                Car newCar= usersCars.get(selectedCar); // Get the new selected car
                nickNameDisplay.setText(newCar.getNickName());
                connectionManager.ConnectToCar(newCar); // Connect to the new car
                List<DashboardLinkModel> links = adapter.getItems();
                links.set(IndexOfMoreLink, new DashboardLinkModel("More", R.drawable.controls, DashboardModernDirections.actionDashboardFragment2ToCarInfoFragment().setCar(newCar)));
                adapter.setItems(links);
                sublinks.setAdapter(adapter);
             }
        });

        // Set rest of widgets on dashboard
        nickNameDisplay = view.findViewById(R.id.nickNameDashboard);
        batteryRangeDisplay = view.findViewById(R.id.evRange);
        totalRangeDisplay = view.findViewById(R.id.totalRange);
        statusDisplay = view.findViewById(R.id.carStatus);
        batterySOCDisplay = view.findViewById(R.id.batteryCharge);

        /* Set the elements of the No Car layout here*/
        Button addCar = view.findViewById(R.id.AddCarNoCar);
        addCar.setOnClickListener(clickListener());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the observer when no longer needed
        userData.removeObserver(userDataObserver);
        receivedCarData.removeObserver(receivedFromCarObserver);
    }

    private void populateSublinks(List<DashboardLinkModel> dashboardLinks) {
        // TODO: refactor to generate based on vehicle type
        dashboardLinks.add(new DashboardLinkModel("Controls", R.drawable.controls, DashboardModernDirections.actionDashboardFragment2ToCarInfoFragment()));
        dashboardLinks.add(new DashboardLinkModel("Location", R.drawable.controls, DashboardModernDirections.actionDashboardFragment2ToCarInfoFragment()));
        dashboardLinks.add(new DashboardLinkModel("Charging", R.drawable.controls, DashboardModernDirections.actionDashboardFragment2ToCarInfoFragment()));
        dashboardLinks.add(new DashboardLinkModel("More", R.drawable.controls, DashboardModernDirections.actionDashboardFragment2ToCarInfoFragment()));
    }

    // Callback function to set/update UI whenever data is received from car
    private void setUIData(JSONObject carResp) {
        try {
            if (usersCars.size() > 0) {
                nickNameDisplay.setText(usersCars.get(selectedCar).getNickName()); // Added as a sanity check to make sure that nickName will eventually get set
            }
            batterySOCDisplay.setProgress((Integer) carResp.get("batteryChargePercent"));
            String totalRange = carResp.get("fuelRange") + "mi total";
            totalRangeDisplay.setText(totalRange);
            String status = ((Integer) carResp.get("chargingVoltage") > 30) ? "Charging " + carResp.get("chargingVoltage") + "V, " + carResp.get("chargingCurrent") + "A" : (String) carResp.get("transmissionRange");
            statusDisplay.setText(status);
            //TODO : Add EV range in parameters returned by ESP32
        }  catch(JSONException e){
            // Don't update data
        }
    }

    // Callback function that executes when userData from Firebase changes
    private void checkData(HashMap<String, Object> userData)
    {
        // Check that user has at least one car in their profile
        if ((userData != null && userData.containsKey("cars"))) {
            usersCars.clear(); // Empty out the list first to write new data
            // get the user's default car
            // (this is the car that will first be selected on load
            selectedCar = Math.toIntExact((Long) userData.get("defaultCar"));
            // Get all the user's cars
            List<HashMap<String, Object>> retrievedCars = (List<HashMap<String, Object>>) userData.get("cars");

            //Iterate through all the cars retrieved
            assert retrievedCars != null;
            for (HashMap<String, Object> car: retrievedCars) {
                // Create a new car object that holds all the values contained within the hashmap
                String VIN = (String) car.get("VIN").toString();
                String BTAddress = (String) car.get("BTMacAddress").toString();
                Car newCar = new Car(BTAddress, VIN);

                // Add the optional params to car object
                if (car.containsKey("Color")) {
                    newCar.setColor((String) car.get("Color"));
                }
                if (car.containsKey("nickName")) {
                    newCar.setNickName((String) car.get("nickName"));
                }
                // Write the created carObject to the list
                usersCars.add(newCar);
            }

            // Set the text for nickName based from Firebase data
            if (nickNameDisplay != null) {
                nickNameDisplay.setText(usersCars.get(selectedCar).getNickName());
            }
            // Connect to the loaded in car using connectionManager
            connectionManager.ConnectToCar(usersCars.get(selectedCar));
            // Notify the viewPager that the data changed
            carPagerAdapter.notifyDataSetChanged();
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
            }
        };
    }

    @Override
    public void onItemClick(NavDirections navAction) {
        // Navigate to the correct destination based on link pressed
        navController.navigate(navAction);
    }
}
