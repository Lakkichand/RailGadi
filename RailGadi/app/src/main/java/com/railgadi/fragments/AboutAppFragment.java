package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.plus.PlusOneButton;
import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.fonts.AppFonts;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.UtilsMethods;

public class AboutAppFragment extends Fragment {

    private View rootView ;

    // views
    private TextView railGadiText, versionText, whatEverYouNeed ;
    private RatingBar ratingBar ;
    private PlusOneButton mPlusOneButton ;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView    =   inflater.inflate(R.layout.about_app_fragment, container, false) ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.about_app).toUpperCase());

        initializeAllViews() ;
        setTypeFace() ;

        return rootView ;
    }


    @Override
    public void onResume() {
        super.onResume();

        //MyApplication.getInstance().trackScreenView(AboutAppFragment.this.getClass().getName());

        mPlusOneButton.initialize(Constants.APP_PLUS_ONE_URL, 0) ;
    }

    private void initializeAllViews() {

        railGadiText        =   (TextView) rootView.findViewById(R.id.text_railgadi) ;
        versionText         =   (TextView) rootView.findViewById(R.id.version_text) ;
        whatEverYouNeed     =   (TextView) rootView.findViewById(R.id.whatever_you_need) ;

        mPlusOneButton      =   (PlusOneButton) rootView.findViewById(R.id.plus_one_button);

        try {
            versionText.setText("Version : "+UtilsMethods.getBuildVersion(getActivity()));
        } catch(Exception e) {

        }

        ratingBar           =   (RatingBar) rootView.findViewById(R.id.rating) ;
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                startActivity(UtilsMethods.rateUs(getActivity()));
            }
        });
    }

    private void setTypeFace() {

        railGadiText.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        versionText.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        whatEverYouNeed.setTypeface(AppFonts.getRobotoReguler(getActivity()));
    }
}
