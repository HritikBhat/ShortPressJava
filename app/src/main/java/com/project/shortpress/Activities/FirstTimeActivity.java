package com.project.shortpress.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.project.shortpress.Adapters.ViewPagerAdapter;
import com.project.shortpress.Fragments.FirstTime.FirstTimeFragment1;
import com.project.shortpress.Fragments.FirstTime.FirstTimeFragment2;
import com.project.shortpress.Fragments.FirstTime.FirstTimeFragment3;
import com.project.shortpress.R;

public class FirstTimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FirstTimeFragment1(), "F1");
        adapter.addFrag(new FirstTimeFragment2(), "F2");
        adapter.addFrag(new FirstTimeFragment3(), "F3");
        viewPager.setAdapter(adapter);
    }
}