package com.project.shortpress.Fragments.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.project.shortpress.Activities.CategoryActivity;
import com.project.shortpress.R;


public class ExploreFragment extends Fragment implements View.OnClickListener {
    CardView politicsCardView,businessCardView,scienceCardView,healthCardView,sportsCardView,techCardView,celebCardView,travelCardView;
    TextView politicsTV,businessTV,scienceTV,healthTV,sportsTV,techTV,celebTV,travelTV;

    protected void proceedToCategoryArt(String cat){
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra("Cat",cat);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        politicsCardView =view.findViewById(R.id.politicsCardView);
        politicsCardView.setOnClickListener(this);
        businessCardView =view.findViewById(R.id.businessCardView);
        businessCardView.setOnClickListener(this);
        scienceCardView =view.findViewById(R.id.scienceCardView);
        scienceCardView.setOnClickListener(this);
        healthCardView =view.findViewById(R.id.healthCardView);
        healthCardView.setOnClickListener(this);
        sportsCardView =view.findViewById(R.id.sportsCardView);
        sportsCardView.setOnClickListener(this);
        techCardView =view.findViewById(R.id.techCardView);
        techCardView.setOnClickListener(this);
        celebCardView =view.findViewById(R.id.celebCardView);
        celebCardView.setOnClickListener(this);
        travelCardView =view.findViewById(R.id.travelCardView);
        travelCardView.setOnClickListener(this);

        politicsTV=view.findViewById(R.id.politicsTV);
        businessTV=view.findViewById(R.id.businessTV);
        scienceTV=view.findViewById(R.id.scienceTV);
        healthTV=view.findViewById(R.id.healthTV);
        sportsTV=view.findViewById(R.id.sportsTV);
        techTV=view.findViewById(R.id.techTV);
        celebTV=view.findViewById(R.id.celebTV);
        travelTV=view.findViewById(R.id.travelTV);


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.politicsCardView:
                proceedToCategoryArt(politicsTV.getText().toString());
                break;
            case R.id.businessCardView:
                proceedToCategoryArt(businessTV.getText().toString());
                break;
            case R.id.scienceCardView:
                proceedToCategoryArt(scienceTV.getText().toString());
                break;
            case R.id.healthCardView:
                proceedToCategoryArt(healthTV.getText().toString());
                break;
            case R.id.sportsCardView:
                proceedToCategoryArt(sportsTV.getText().toString());
                break;
            case R.id.techCardView:
                proceedToCategoryArt(techTV.getText().toString());
                break;
            case R.id.celebCardView:
                proceedToCategoryArt(celebTV.getText().toString());
                break;
            case R.id.travelCardView:
                proceedToCategoryArt(travelTV.getText().toString());
                break;


        }
    }
}