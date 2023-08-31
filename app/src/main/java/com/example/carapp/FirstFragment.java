package com.example.carapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment {


    private static final String ARG_PARAM1 = "CounterRef";

    // Variable to store Counter ref
    private Counter mParam1;


    public FirstFragment() {
        // Provide the XML layout of the fragment to base constructor here
        super(R.layout.fragment_first);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // if any arguments are passed, pull them out here
        if (getArguments() != null) {
            mParam1 = (Counter) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the button and textview
        Button increment = view.findViewById(R.id.incrementCounter);
        Button decrement = view.findViewById(R.id.decrementCounter);
        TextView countDisplay = view.findViewById(R.id.counterDisplayFragmentFirst);

        // Set Counter Value:
        if (mParam1 != null) {
            int count = mParam1.getCount();
            countDisplay.setText(String.valueOf(count));

            // Set onClickEvent Listener
            increment.setOnClickListener(this.createListener(countDisplay));
            decrement.setOnClickListener(this.createListener(countDisplay));
        }
    }

    // Creates a listener that will increment the counter and update countDisplay onClick
    private View.OnClickListener createListener(TextView countDisplay) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.incrementCounter) {
                    mParam1.increment();
                } else if (view.getId() == R.id.decrementCounter) {
                    mParam1.decrement();
                }
                int count = mParam1.getCount();
                countDisplay.setText(String.valueOf(count));
            }
        };
    }
}