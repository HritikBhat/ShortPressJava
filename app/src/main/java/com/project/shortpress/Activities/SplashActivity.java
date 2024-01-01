package com.project.shortpress.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.project.shortpress.R;
import com.project.shortpress.Config.SharedPrefFunctions;

public class SplashActivity extends AppCompatActivity {
    SharedPrefFunctions sharedPrefFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPrefFunctions = new SharedPrefFunctions(getApplicationContext());


        if (sharedPrefFunctions.getIsDarkModeOn()) {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);
        }


        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                if(sharedPrefFunctions.getUserId()==-1){
                    Intent i = new Intent(SplashActivity.this, FirstTimeActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 2000);
    }
}