package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.fonts.AppFonts;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.UtilsMethods;

public class TutorialFour extends Fragment {

    private View rootView ;

    private ImageView image ;
    private TextView text ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView    =   inflater.inflate(R.layout.tutorial_layout, container, false) ;

        image       =   (ImageView) rootView.findViewById(R.id.image) ;
        text        =   (TextView) rootView.findViewById(R.id.text) ;

        image.setImageDrawable(UtilsMethods.getSvgDrawable(getActivity(), R.drawable.t_four));

        text.setText(Constants.TUTORIAL_FOUR);
        text.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        return rootView ;
    }
}
