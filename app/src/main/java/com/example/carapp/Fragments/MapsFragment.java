package com.example.carapp.Fragments;


import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.Manifest;
import com.example.carapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.PrivateKey;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private SupportMapFragment mapView;
    private Button btnShowLocation;
    private Button btnShowCar;
    private Button btnBack;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        btnShowLocation = view.findViewById(R.id.btnShowLocation);
        btnShowLocation.setOnClickListener(v -> requestLocationUpdates());

        btnShowCar = view.findViewById(R.id.btnFindMyCar);
        btnShowCar.setOnClickListener(clk-> requestCarLocationUpdate());

        btnBack = view.findViewById(R.id.btnNavBack);
        btnBack.setOnClickListener(clk -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.Nav_Dashboard);
           navController.navigate(MapsFragmentDirections.actionMapFragmentToDashboardModern());
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        createLocationCallback();

        return view;
    }
    private void requestCarLocationUpdate() {
        // TODO: Add logic to read location from car module. For now, adding POC
        LatLng carLocation = new LatLng(39.999387, -83.02271); // Example PIN
        googleMap.addMarker(new MarkerOptions().position(carLocation).title("Car Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(carLocation, 15));
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (android.location.Location location : locationResult.getLocations()) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    updateMap(currentLocation);
                }
            }
        };
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                requestLocationPermission();
            }
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void updateMap(LatLng currentLocation) {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted
                    requestLocationUpdates();
                } else {
                    // User location is unavailable
                    Toast.makeText(getContext(), "Permissions denied, unable to show your location", Toast.LENGTH_LONG).show();
                }
            });
}
