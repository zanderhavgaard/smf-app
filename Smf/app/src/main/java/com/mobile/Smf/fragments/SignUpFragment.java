package com.mobile.Smf.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.Smf.R;
import com.mobile.Smf.activities.FeedActivity;
import com.mobile.Smf.activities.LoginActivity;
import com.mobile.Smf.database.DataInterface;
import com.mobile.Smf.model.User;

public class SignUpFragment extends Fragment {

    private String country;

    private TextView usernameTextView;
    private EditText usernameEditText;
    private TextView passwordTextView;
    private EditText passwordEditText;
    private TextView emailTextView;
    private EditText emailEditText;
    private TextView countryTextView;
    private Spinner countrySpinner;
    private TextView birthYearTextView;
    private EditText birthYearEditText;

    private Button createAccountButton;
    private Button backToLoginButton;

    private DataInterface dataInterface;

    @Override
    public void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View signupView = inflater.inflate(R.layout.fragment_signup, container, false);

        dataInterface = DataInterface.getDataInterface(getContext());

        usernameTextView = (TextView) signupView.findViewById(R.id.signup_textview_username);
        usernameTextView.setText(R.string.signup_username);
        passwordTextView = (TextView) signupView.findViewById(R.id.signup_textview_password);
        passwordTextView.setText(R.string.signup_password);
        emailTextView = (TextView) signupView.findViewById(R.id.signup_textview_email);
        emailTextView.setText(R.string.signup_email);
        countryTextView = (TextView) signupView.findViewById(R.id.signup_textview_country);
        countryTextView.setText(R.string.signup_country);
        birthYearTextView = (TextView) signupView.findViewById(R.id.signup_textview_birthyear);
        birthYearTextView.setText(R.string.signup_birthYear);

        usernameEditText = (EditText) signupView.findViewById(R.id.signup_edittext_username);
        passwordEditText = (EditText) signupView.findViewById(R.id.signup_edittext_password);
        emailEditText = (EditText) signupView.findViewById(R.id.signup_edittext_email);
        birthYearEditText = (EditText) signupView.findViewById(R.id.signup_edittext_birthyear);

        createAccountButton = (Button) signupView.findViewById(R.id.signup_button_createaccount);
        createAccountButton.setText(R.string.signup_button_createaccount);
        backToLoginButton = (Button) signupView.findViewById(R.id.signup_button_backtologin);
        backToLoginButton.setText(R.string.signup_button_backtologin);

        countrySpinner = (Spinner) signupView.findViewById(R.id.signup_spinner_country);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                country = parent.getItemAtPosition(pos).toString();
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                Toast.makeText(getContext(), "Your country of residency has to be chosen (You must give us your data!!)", Toast.LENGTH_SHORT).show();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();
                int birthYear = Integer.parseInt(birthYearEditText.getText().toString());

                //todo make check. is the email a valid email, is the user more then lets say 13 years etc.


                //Below (isConnected) can only be tested live due to bug in emulator (does not see pc's connection)
                if(!dataInterface.isConnected())
                    Toast.makeText(getContext(), "You need internet connection to sign up",Toast.LENGTH_SHORT).show();
                else {

                    if (dataInterface.checkIfValidNewUser(username, email)) {

                            if(dataInterface.addNewUser(username, password, email, country, birthYear)){
                                Toast.makeText(getContext(), "Welcome to SMF "+username,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getContext(), FeedActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Error while syncing", Toast.LENGTH_SHORT).show();
                            }
                    } else {
                        Toast.makeText(getContext(), "Username or email is already taken.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        backToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return signupView;
    }

}
