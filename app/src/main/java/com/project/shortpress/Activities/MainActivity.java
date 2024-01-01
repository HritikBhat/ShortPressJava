package com.project.shortpress.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.project.shortpress.Config.SharedPrefFunctions;
import com.project.shortpress.Fragments.Home.ExploreFragment;
import com.project.shortpress.Fragments.Home.FeedFragment;
import com.project.shortpress.Fragments.Home.ProfileFragment;
import com.project.shortpress.Fragments.Home.SavedFragment;
import com.project.shortpress.R;
import com.project.shortpress.Adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    Toolbar toolbar;
    ImageView dayNightModeBtn,searchButton;
    SharedPrefFunctions sharedPrefFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefFunctions=new SharedPrefFunctions(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        searchButton = findViewById(R.id.backBtn_ab);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });


        dayNightModeBtn= findViewById(R.id.modeChangeBtn);

        if (sharedPrefFunctions.getIsDarkModeOn()) {
            dayNightModeBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_wb_sunny_24,getTheme()));
        }
        else {
            dayNightModeBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_nights_stay_24,getTheme()));

        }


        dayNightModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedPrefFunctions.getIsDarkModeOn()) {
                    // if dark mode is on it
                    // will turn it off
                    dayNightModeBtn.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this,R.drawable.ic_baseline_nights_stay_24));
                    sharedPrefFunctions.setIsDarkModeOn(false);
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_NO);

                }
                else {
                    sharedPrefFunctions.setIsDarkModeOn(true);
                    dayNightModeBtn.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this,R.drawable.ic_baseline_wb_sunny_24));
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_YES);

                }

            }
        });

        // using toolbar as ActionBar


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        viewPager = findViewById(R.id.viewpager_home);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FeedFragment(), "Feed");
        adapter.addFrag(new ExploreFragment(), "Explore");
        adapter.addFrag(new SavedFragment(), "Saved");
        adapter.addFrag(new ProfileFragment(), "Profile");
        viewPager.setAdapter(adapter);


        viewPager.setCurrentItem(0);

        bottomNavigationView.setOnItemSelectedListener(botNavViewItemSelectedListener());
    }

    //Bottom Navigation item selected Listener
    public NavigationBarView.OnItemSelectedListener botNavViewItemSelectedListener() {
        return new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.feed:
                        viewPager.setCurrentItem(0);
                        break;

                    case R.id.explore:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.saved:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.profile:
                        viewPager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        };
    }

    }