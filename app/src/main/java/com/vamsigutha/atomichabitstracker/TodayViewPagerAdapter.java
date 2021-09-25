package com.vamsigutha.atomichabitstracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TodayViewPagerAdapter extends FragmentStateAdapter {
    public TodayViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new TodayHabitFragment();
            case 1:
                return new TodayTaskFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
