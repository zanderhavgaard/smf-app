package com.mobile.Smf.activities;

import android.support.v4.app.Fragment;

import com.mobile.Smf.fragments.NavigationBarFragment;
import com.mobile.Smf.fragments.ProfileFragment;

public class ProfileActivity extends VerticalStackedTwoFragmentActivity {

    @Override
    protected Fragment createUpperFragment(){
        return new ProfileFragment();
    }

    @Override
    public Fragment createLowerFragment(){
        return new NavigationBarFragment();
    }

}
