package com.railgadi.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.FareAdapterFindTrain;
import com.railgadi.beans.SeatFareBeanNew;
import com.railgadi.fonts.AppFonts;

public class FareBreakupFragmentFT extends Fragment {

    private View rootView;
    private ListView fareDetailsListView;
    private TextView fareBreakuptitle;
    private TextView cross;

    private SeatFareBeanNew.FareBreakup fdBean;

    public static RightDrawerFragment rightDrawerFragment;
    public static LeftDrawerFragment leftDrawerFragment;
    public static MainActivity mainActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity.toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity.toolbar.setVisibility(View.VISIBLE);
    }

    public FareBreakupFragmentFT(SeatFareBeanNew.FareBreakup fdBean) {

        this.fdBean = fdBean;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fare_breakup_find_train, container, false);

        rightDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        leftDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);

        initilizeAllViews();
        collectData();
        setDataOnViews();

        return rootView;
    }

    private void initilizeAllViews() {

        fareDetailsListView = (ListView) rootView.findViewById(R.id.fare_breakup_listview);
        fareBreakuptitle = (TextView) rootView.findViewById(R.id.fare_breakup_title);
        cross = (TextView) rootView.findViewById(R.id.close_fare_breakup_frag);

        fareBreakuptitle.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        cross.setTypeface(AppFonts.getRobotoReguler(getActivity()));
    }

    private void collectData() {

        if(fdBean.netAmount == null || fdBean.netAmount.isEmpty()) {
            SeatFareBeanNew.FareBreakup.FareDetails fd = new SeatFareBeanNew.FareBreakup.FareDetails() ;
            fd.key = getActivity().getResources().getString(R.string.net_amount) ;
            fd.value = fdBean.totalAmount ;
            fdBean.fareList.add(fd);
        } else {
            SeatFareBeanNew.FareBreakup.FareDetails fd = new SeatFareBeanNew.FareBreakup.FareDetails() ;
            fd.key = getActivity().getResources().getString(R.string.net_amount) ;
            fd.value = fdBean.netAmount ;
            fdBean.fareList.add(fd);
        }
    }

    private void setDataOnViews() {

        fareDetailsListView.setAdapter(new FareAdapterFindTrain(getActivity(), fdBean.fareList));

        fareBreakuptitle.setText(Html.fromHtml("<small>" + getActivity().getResources().getString(R.string.fare_breakup) + "</small>"));

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.fragManager.popBackStack();
            }
        });
    }
}
