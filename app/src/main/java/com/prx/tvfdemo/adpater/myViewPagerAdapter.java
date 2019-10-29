package com.prx.tvfdemo.adpater;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.prx.tvfdemo.R;
import com.prx.tvfdemo.fragment.AmusementFragment;
import com.prx.tvfdemo.fragment.GymFragment;
import com.prx.tvfdemo.fragment.JkNewsFragment;
import com.prx.tvfdemo.fragment.ScienceNewsFragment;
import com.prx.tvfdemo.fragment.SocialNewsFragment;

public class myViewPagerAdapter  extends FragmentPagerAdapter {

    private Context myContext;

    public myViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        myContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new SocialNewsFragment();
        }else if (position == 1) {
            return new ScienceNewsFragment();
        }else if (position == 2) {
            return new GymFragment();
        }else if(position == 3){
            return new AmusementFragment();
        } else  {
            return new JkNewsFragment();
        }

    }

    @Override
    public int getCount() {
        return 5;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return myContext.getString(R.string.view_one);
        }else if (position == 1) {
            return myContext.getString(R.string.view_two);
        }else if (position == 2) {
            return myContext.getString(R.string.view_three);
        }else if(position == 3){
            return myContext.getString(R.string.view_four);
        } else {
            return myContext.getString(R.string.view_five);
        }
    }
}
