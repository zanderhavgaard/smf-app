package com.mobile.Smf.activities;

import android.support.v4.app.Fragment;

import com.mobile.Smf.fragments.MakeTextPostFragment;

public class MakeTextPostActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new MakeTextPostFragment();
    }
}
