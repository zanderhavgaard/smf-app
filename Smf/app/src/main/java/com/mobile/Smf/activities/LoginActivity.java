package com.mobile.Smf.activities;

import com.mobile.Smf.fragments.LoginFragment;

import android.support.v4.app.Fragment;

public class LoginActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new LoginFragment();
    }
}
