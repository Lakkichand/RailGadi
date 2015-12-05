package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;

public class SeatBirthCalculatorFrag extends Fragment {

    private View rootView ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView        =   inflater.inflate(R.layout.seat_birth_calculator, container, false) ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.seat_birth_calculator).toUpperCase());

        return rootView ;
    }
}
