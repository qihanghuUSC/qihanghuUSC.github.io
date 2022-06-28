package com.example.stocktrading;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity)
    {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        switch (position)
        {

            case 0:
                return new hourly_highChart();

            case 1:
                return new history_highChart();

            default:
                return new hourly_highChart();

        }
    }

    @Override
    public int getItemCount()
    {
        return 2;
    }
}
