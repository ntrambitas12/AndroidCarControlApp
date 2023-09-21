package com.example.carapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class RequestPermissions extends Fragment {

    private final int PERMISSION_ALL = 1;
    private static final String[] PERMISSIONS_NEW = {
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final String[] PERMISSIONS_LEGACY= {
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                boolean allGranted = true;
                // Go through each of the permission results until we find a false
                for (Boolean val : isGranted.values()) {
                    if (!val) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    // Navigate user to the next screen
                    Navigation.findNavController(getView()).navigate(R.id.carSearch);
                } else {
                    // Reject user from going to app due to permissions
                    Toast.makeText(this.getContext(),"Manually accept permissions from settings to continue!", Toast.LENGTH_LONG).show();
                }
            });
    public RequestPermissions() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_permissions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Check if user accepted all the permissions. If they do, automatically go to next screen
        if (hasPermissions(this.getContext(), (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)? PERMISSIONS_NEW : PERMISSIONS_LEGACY)) {
            // Navigate to the Pair Device Fragment
            Navigation.findNavController(getView()).navigate(R.id.carSearch);
        }
        // If permissions aren't granted, continue setting up activity
        Button invokePermissions = view.findViewById(R.id.requestPermissions);
        invokePermissions.setOnClickListener( button -> {
            // Invoke Android to ask for all the necessary permissions
            requestPermissionsLauncher.launch((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)? PERMISSIONS_NEW : PERMISSIONS_LEGACY);
        });
    }

}