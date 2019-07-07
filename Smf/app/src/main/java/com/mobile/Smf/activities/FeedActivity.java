package com.mobile.Smf.activities;

import com.mobile.Smf.fragments.FeedFragment;
import com.mobile.Smf.fragments.NavigationBarFragment;

import android.support.v4.app.Fragment;

public class FeedActivity extends VerticalStackedTwoFragmentActivity {

    @Override
    protected Fragment createUpperFragment(){
        return new FeedFragment();
    }

    @Override
    protected Fragment createLowerFragment(){
        return new NavigationBarFragment();
    }
}
