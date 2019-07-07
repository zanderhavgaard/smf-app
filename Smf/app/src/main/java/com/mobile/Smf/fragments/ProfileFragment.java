package com.mobile.Smf.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.Smf.R;
import com.mobile.Smf.activities.LoginActivity;
import com.mobile.Smf.database.DataInterface;
import com.mobile.Smf.model.User;

public class ProfileFragment extends Fragment {

    private TextView textViewUsernameDesc;
    private TextView textViewUsernameData;
    private TextView textViewEmailDesc;
    private TextView textViewEmailData;
    private TextView textViewCountryDesc;
    private TextView textViewCountryData;
    private TextView textViewBirthYearDesc;
    private TextView textViewBirthYearData;
    private Button buttonLogout;

    private User user;
    private DataInterface dataInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View profileView = inflater.inflate(R.layout.fragment_profile,container,false);

        dataInterface = DataInterface.getDataInterface(getContext());
        user = dataInterface.getLoggedInUser();

        textViewUsernameDesc = profileView.findViewById(R.id.profile_textview_usernamedesc);
        textViewUsernameData = profileView.findViewById(R.id.profile_textview_usernamedata);
        textViewEmailDesc = profileView.findViewById(R.id.profile_textview_emaildesc);
        textViewEmailData = profileView.findViewById(R.id.profile_textview_emaildata);
        textViewCountryDesc = profileView.findViewById(R.id.profile_textview_countrydesc);
        textViewCountryData = profileView.findViewById(R.id.profile_textview_countrydata);
        textViewBirthYearDesc = profileView.findViewById(R.id.profile_textview_birthyeardesc);
        textViewBirthYearData = profileView.findViewById(R.id.profile_textview_birthyeardata);


        textViewUsernameDesc.setText(R.string.profile_username_desc);
        textViewEmailDesc.setText(R.string.profile_email_desc);
        textViewCountryDesc.setText(R.string.profile_country_desc);
        textViewBirthYearDesc.setText(R.string.profile_birthyear_desc);

        textViewUsernameData.setText(user.getUserName());
        textViewEmailData.setText(user.getEmail());
        textViewCountryData.setText(user.getCountry());
        textViewBirthYearData.setText(user.getBirthYearAsString());

        buttonLogout = profileView.findViewById(R.id.profile_button_logout);
        buttonLogout.setText(R.string.profile_button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataInterface.killBackgroundSync();
                dataInterface.setScrollFlag();
                dataInterface.interruptBackgroundThread();
                if (dataInterface.logCurrentUserOut()){
                    if(!dataInterface.checkSqLiteTable("profile_info")) {
                        Toast.makeText(getContext(), "You have been logged out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getContext(),"Could not log out...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return profileView;
    }

}
