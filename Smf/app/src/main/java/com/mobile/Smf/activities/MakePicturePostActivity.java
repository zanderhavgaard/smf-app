package com.mobile.Smf.activities;

import android.support.v4.app.Fragment;

import com.mobile.Smf.fragments.MakePicturePostFragment;

public class MakePicturePostActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new MakePicturePostFragment();
    }
}
