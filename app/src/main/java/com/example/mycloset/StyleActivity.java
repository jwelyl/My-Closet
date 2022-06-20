package com.example.mycloset;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class StyleActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_activity_style);

        // Convert page when button is clicked
        // home page
        ImageButton home_button = (ImageButton) findViewById(R.id.home_button);
        home_button.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });

        // style page
        ImageButton style_button = (ImageButton) findViewById(R.id.style_button);
        style_button.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), StyleActivity.class);
            startActivity(intent);
        });

        // closet page
        ImageButton closet_button = (ImageButton) findViewById(R.id.closet_button);
        closet_button.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), ClosetActivity.class);
            startActivity(intent);
        });

        // diary page
        ImageButton diary_button = (ImageButton) findViewById(R.id.diary_button);
        diary_button.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), DiaryActivity.class);
            startActivity(intent);
        });



    }
}
