package com.mobile.Smf.activities;

import com.mobile.Smf.fragments.SignUpFragment;

import android.support.v4.app.Fragment;

public class SignupActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new SignUpFragment();
    }
}
