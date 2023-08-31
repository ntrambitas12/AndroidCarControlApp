package com.example.carapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentFactory;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate the counter object
        Counter counter = new Counter();

        // Create a bundle for passing object reference
        Bundle args = new Bundle();
        args.putSerializable("CounterRef", counter);

        // Get the buttons and navController from the view
        Button frag1 = findViewById(R.id.btnFragment1);
        Button frag2 = findViewById(R.id.btnFragment2);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Wire up buttons to the onClick listener
        View.OnClickListener invokeNavigation = createInvokeNavigationListener(navController, args);
        frag1.setOnClickListener(invokeNavigation);
        frag2.setOnClickListener(invokeNavigation);

        // Pass the bundle onLoad
        navController.setGraph(R.navigation.navigationgraph, args);


    }

    // method that defines an onClickListener and defines an OnClick event
    private View.OnClickListener createInvokeNavigationListener(NavController navController, Bundle args) {
        return new View.OnClickListener() {
            // Navigate onClick and pass args to destination
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btnFragment1) {
                    navController.navigate(R.id.firstFragment, args);
                }
                else if (view.getId() == R.id.btnFragment2) {
                    navController.navigate(R.id.secondFragment, args);
                }
            }
        };
    }
}