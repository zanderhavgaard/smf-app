package com.mobile.Smf.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.support.design.widget.BottomNavigationView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.mobile.Smf.R;
import com.mobile.Smf.activities.FeedActivity;
import com.mobile.Smf.activities.MakePicturePostActivity;
import com.mobile.Smf.activities.MakeTextPostActivity;
import com.mobile.Smf.activities.ProfileActivity;
import com.mobile.Smf.model.Feed;

import android.support.v4.app.Fragment;

public class NavigationBarFragment extends Fragment {

    private BottomNavigationView navigation;
    private Dialog dialog;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_feed:
                    Intent feedIntent = new Intent(getContext(), FeedActivity.class);
                    startActivity(feedIntent);
                    return true;
                case R.id.navigation_makepost:
                    makePostDialog(getContext(), navigation);
                    return true;
                case R.id.navigation_account:
                    Intent accountIntent = new Intent(getContext(), ProfileActivity.class);
                    startActivity(accountIntent);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View navigationView = inflater.inflate(R.layout.fragment_navigationbar, container, false);

        navigation = (BottomNavigationView) navigationView.findViewById(R.id.navigation_bar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        return navigationView;
    }

    public void makePostDialog(Context context, BottomNavigationView view) {
        //make the dialog
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.makepost_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView text = dialog.findViewById(R.id.textpost);
        ImageView photo = dialog.findViewById(R.id.photopost);

        text.setScaleX(2.00f);
        text.setScaleY(2.00f);
        photo.setScaleX(2.00f);
        photo.setScaleY(2.00f);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        param.y = 140;
        window.setAttributes(param);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent textIntent = new Intent(getContext(), MakeTextPostActivity.class);
                startActivity(textIntent);
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(getContext(), MakePicturePostActivity.class);
                startActivity(photoIntent);
            }
        });

        view.setOnClickListener(new BottomNavigationView.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                view.setOnClickListener(null);
            }
        });

        dialog.show();
    }

}
