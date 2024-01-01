package com.project.shortpress.Fragments.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.project.shortpress.Activities.AboutUsActivity;
import com.project.shortpress.Activities.FirstTimeActivity;
import com.project.shortpress.Activities.GetPreferenceActivity;
import com.project.shortpress.R;
import com.project.shortpress.Config.SharedPrefFunctions;

public class ProfileFragment extends Fragment {
    Button logoutButton;
    ImageView userProfileImage;
    TextView userProfileName;
    SharedPrefFunctions sharedPrefFunctions;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    CheckBox darkModeCheck;
    ImageView dayNightModeBtn,prefBtn,about_btn;
    CompoundButton.OnCheckedChangeListener checkBoxListener;
    //getJoinDate
    TextView joinDateTV;


    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener( getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        sharedPrefFunctions.setUser(-1,"","","", "");
                        Intent i = new Intent(getActivity(), FirstTimeActivity.class);
                        startActivity(i);
                        getActivity().finish();

                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.googleAccountWebClientID))
                .requestEmail()
                .build();

        checkBoxListener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
                if (isChecked) {
                    //darkModeCheck.setChecked(false);
                    dayNightModeBtn.performClick();
                    dayNightModeBtn.setImageDrawable(ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.ic_baseline_nights_stay_24, getActivity().getTheme()));
                } else {
                    //darkModeCheck.setChecked(true);
                    dayNightModeBtn.performClick();
                    dayNightModeBtn.setImageDrawable(ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.ic_baseline_wb_sunny_24, getActivity().getTheme()));
                }
            }
        };
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        logoutButton = view.findViewById(R.id.logoutButton);
        userProfileImage = view.findViewById(R.id.userProfileImage);
        userProfileName = view.findViewById(R.id.userProfileName);
        darkModeCheck = view.findViewById(R.id.darkmodeCheck);

        dayNightModeBtn= getActivity().findViewById(R.id.modeChangeBtn);

        prefBtn = view.findViewById(R.id.pref_btn);

        about_btn = view.findViewById(R.id.about_btn);
        prefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), GetPreferenceActivity.class);
                i.putExtra("act","Profile");
                startActivity(i);
            }
        });

        about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(i);
            }
        });

        sharedPrefFunctions = new SharedPrefFunctions(getActivity());
        String jStr ="Joined at "+sharedPrefFunctions.getJoinDate();

        joinDateTV = view.findViewById(R.id.joinDateTV);
        joinDateTV.setText(jStr);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        Glide.with(getActivity())
                .load(sharedPrefFunctions.getUImgURL())
                .circleCrop()
                .into(userProfileImage);

        userProfileName.setText(sharedPrefFunctions.getUImgName());

        if (sharedPrefFunctions.getIsDarkModeOn()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    darkModeCheck.setOnCheckedChangeListener (null);
                    darkModeCheck.setChecked(true);
                    darkModeCheck.setOnCheckedChangeListener (checkBoxListener);
                }
            },1000);


        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    darkModeCheck.setOnCheckedChangeListener (null);
                    darkModeCheck.setChecked(false);
                    darkModeCheck.setOnCheckedChangeListener (checkBoxListener);
                }
            },1000);

        }

        return view;
    }
}