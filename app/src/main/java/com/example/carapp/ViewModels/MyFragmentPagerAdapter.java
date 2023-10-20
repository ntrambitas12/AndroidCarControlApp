package com.example.carapp.ViewModels;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.carapp.FirebaseRepository;
import com.example.carapp.Fragments.CarDashboardFragment;

import java.util.ArrayList;


public class MyFragmentPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> fragments;
    private ArrayList<Long> itemIds;
    private FirebaseRepository repo;

    public MyFragmentPagerAdapter(FragmentActivity fm) {
        super(fm);
        fragments = new ArrayList<Fragment>();
        fragments.add(new CarDashboardFragment());
        fragments.add(new CarDashboardFragment());
        //get user id with repo but for now (since not implemented) just use user 0
       // HashMap<String,Object> temp = repo.getUserData("0");
        //System.out.println(temp);
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
        CarDashboardFragment newFrag = new CarDashboardFragment();
        //fragments.add(position, newFrag);
        return newFrag;
    }


    public void removeFragment(int index) {
        fragments.remove(index);
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

}
