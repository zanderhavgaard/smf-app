package com.mobile.Smf.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mobile.Smf.R;

public class DevActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dev);

        button1 = findViewById(R.id.dev_button_1);
        button2 = findViewById(R.id.dev_button_2);
        button3 = findViewById(R.id.dev_button_3);
        button4 = findViewById(R.id.dev_button_4);
        button5 = findViewById(R.id.dev_button_5);
        button6 = findViewById(R.id.dev_button_6);

        button1.setText("Login");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        button2.setText("Signup");
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        button3.setText("Feed");
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                startActivity(intent);
            }
        });

        button4.setText("Profile");
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        button5.setText("MakeTextPost");
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MakeTextPostActivity.class);
                startActivity(intent);
            }
        });

        button6.setText("MakePicturePost");
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MakePicturePostActivity.class);
                startActivity(intent);
            }
        });

    }

}
