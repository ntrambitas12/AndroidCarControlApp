package com.example.carapp.ViewModels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.carapp.FirebaseRepository;
import com.example.carapp.Fragments.CarDashboardFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;


public class CarViewPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> fragments;
    private ArrayList<Long> itemIds;

    private ArrayList<HashMap<String,Object>> cars;
    private FirebaseManager repo;

    private String userID;


    public CarViewPagerAdapter(FragmentActivity fm) {
        super(fm);
        //read data from data store
        fragments = new ArrayList<Fragment>();
        repo = new ViewModelProvider(fm).get(FirebaseManager.class);
        this.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        this.setCarList();
    }

    private void updateItemIds() {
        itemIds = new ArrayList<Long>();
        for(Fragment frag : fragments)
        {
            itemIds.add(new Long("" + frag.hashCode()));
        }
    }

    @Override
    public Fragment createFragment(int position)
    {
        CarDashboardFragment newFrag = new CarDashboardFragment((String) cars.get(position).get("nickName"), "Car Make and Model");
        //fragments.add(position, newFrag);
        return newFrag;
    }


    public void removeFragment(int index) {
        fragments.remove(index);
        repo.deleteCar(userID, index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, fragments.size());
        updateItemIds();
    }

    @Override
    public int getItemCount() {

        return fragments.size();
    }

   @Override
   public long getItemId(int position)
   {

       return fragments.get(position).hashCode();
   }

   @Override
   public boolean containsItem(long itemId)
   {

       return itemIds.contains(itemId);
   }

   private void setCarList()
   {
       HashMap<String,Object> data = repo.getUserData().getValue();
       //load in cars
       this.cars = (ArrayList<HashMap<String, Object>>) data.get("cars");
       System.out.println(this.cars);
       for(HashMap<String, Object> car : this.cars) {
           fragments.add(new CarDashboardFragment((String) car.get("nickName"), "Car Make and Model"));
       }
   }

}
