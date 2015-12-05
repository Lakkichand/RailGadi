package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.CoachCompositionAdapter;
import com.railgadi.beans.CoachCompositionBean;
import com.railgadi.fonts.AppFonts;

public class CoachCompToNextFrag extends Fragment {

    private View rootView ;

    private CoachCompositionBean bean ;

    // views
    private TextView trainName, trainNumber ;
    private ListView coachCompListView ;


    public CoachCompToNextFrag(CoachCompositionBean bean) {
        this.bean       =   bean ;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView        =   inflater.inflate(R.layout.coach_composition_to_next, container, false) ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.coach_composition).toUpperCase());

        initializeAllViews() ;

        return rootView ;
    }


    private void initializeAllViews() {

        coachCompListView   =   (ListView) rootView.findViewById(R.id.coach_composition_list) ;

        trainName           =   (TextView) rootView.findViewById(R.id.train_name) ;
        trainName.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        trainName.setText(bean.getTrainName());

        coachCompListView.setAdapter(new CoachCompositionAdapter(getActivity(), bean.getCoachBean()));
    }
}
