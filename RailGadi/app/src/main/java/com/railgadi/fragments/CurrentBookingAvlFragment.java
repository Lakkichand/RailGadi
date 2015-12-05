package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.CurrentBookListAdapter;
import com.railgadi.adapters.StationNameCodeAdapter;
import com.railgadi.async.CurrentBookingTask;
import com.railgadi.beans.CurrentBookingAvailabilityBean;
import com.railgadi.beans.StationListDataHolder;
import com.railgadi.beans.StationNameCodeBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.ICloseEverything;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.List;

public class CurrentBookingAvlFragment extends Fragment implements ICloseEverything {

    private View rootView ;

    private IFragReplaceCommunicator comm ;

    private CurrentBookListAdapter adapter ;
    private List<StationListDataHolder> data ;
    private StationNameCodeAdapter stationAdapter ;

    private CurrentBookingTask currentBookingTask ;

    // views
    private ListView currBookAvlListView ;
    private TextView stationRed, stationName, trainDeparting, selectedStation ;
    private LinearLayout superLayout, stationChooserLayout, stationClickLayout ;
    private ExpandableListView allStationList ;
    private EditText searchView ;
    private ImageView stationIcon ;
    private TextView closeCross ;
    private TextView stationListTitle, getAvailability;

    private CurrentBookingAvailabilityBean currentBean ; ;

    private String selectedStationCode, selectedStationName ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView    =   inflater.inflate(R.layout.current_booking_avl_frag, container, false) ;

        MainActivity.closeFragmentObject = CurrentBookingAvlFragment.this ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.curr_book_avl).toUpperCase());

        comm        =   (IFragReplaceCommunicator) getActivity() ;

        initializeAllViews();
        setDataOnViews();
        setFonts();

        return rootView ;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(currentBookingTask != null)
            currentBookingTask.cancel(true) ;
    }

    private void initializeAllViews() {

        currBookAvlListView         =   (ListView) rootView.findViewById(R.id.curr_book_avl_listview) ;

        stationRed                  =   (TextView) rootView.findViewById(R.id.station_red);
        stationName                 =   (TextView) rootView.findViewById(R.id.station_name);
        trainDeparting              =   (TextView) rootView.findViewById(R.id.train_in_next);
        selectedStation             =   (TextView) rootView.findViewById(R.id.selected_station);
        stationListTitle            =   (TextView) rootView.findViewById(R.id.station_list_title) ;
        getAvailability             =   (TextView) rootView.findViewById(R.id.get_availability) ;

        stationIcon                 =   (ImageView) rootView.findViewById(R.id.station_icon) ;

        superLayout                 =   (LinearLayout) rootView.findViewById(R.id.super_layout) ;
        stationChooserLayout        =   (LinearLayout) rootView.findViewById(R.id.station_chooser_layout) ;
        stationClickLayout          =   (LinearLayout) rootView.findViewById(R.id.station_click_layout) ;

        allStationList              =   (ExpandableListView) rootView.findViewById(R.id.station_chooser_list) ;

        searchView                  =   (EditText) rootView.findViewById(R.id.search_train_searchview);

        closeCross                  =   (TextView) rootView.findViewById(R.id.close_station_list_cross) ;
        closeCross.setTypeface(AppFonts.getRobotoReguler(getActivity()));

    }


    private void setDataOnViews() {

        allStationList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return parent.isGroupExpanded(groupPosition);
            }
        });

        allStationList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                StationListDataHolder cont = (StationListDataHolder) stationAdapter.getSelectedItemGroup(groupPosition);

                StationNameCodeBean c = cont.getNameCodeData().get(childPosition);

                selectedStationCode =   c.getStationCode() ;
                selectedStationName = c.getStationName() ;

                if(c.getStationIcon() == 0) {
                    stationIcon.setImageResource(R.drawable.shape_logo);
                } else {
                    stationIcon.setImageResource(c.getStationIcon());
                }
                selectedStation.setText(selectedStationName);

                closeStationlist();
                return false;
            }
        });

        stationClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStationList();
            }
        });

        closeCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeStationlist();
            }
        });

        getAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedStationCode != null) {

                    if(InternetChecking.isNetWorkOn(getActivity())) {
                        currentBookingTask = new CurrentBookingTask(getActivity(), selectedStationCode, CurrentBookingAvlFragment.this);
                        currentBookingTask.execute();
                    } else {
                        InternetChecking.noInterNetToast(getActivity());
                    }

                } else {
                    Toast.makeText(getActivity(), "Select Station", Toast.LENGTH_SHORT).show();
                }
            }
        });


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(stationAdapter != null) {
                    stationAdapter.filterData(s.toString().toLowerCase());
                    expandAll();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(stationAdapter != null) {
                    stationAdapter.filterData(s.toString().toLowerCase());
                    expandAll();
                }
            }
        });
    }

    private void expandAll() {

        try {

            int count = stationAdapter.getGroupCount();
            for (int i = 0; i < count; i++) {
                allStationList.expandGroup(i);
            }

        } catch( Exception e ){

            e.printStackTrace();
        }
    }

    private void openStationList() {

        MainActivity.toolbar.setVisibility(View.GONE);

        data = UtilsMethods.collectStations(getActivity());

        stationAdapter = new StationNameCodeAdapter(getActivity(), data) ;

        allStationList.setAdapter(stationAdapter);
        expandAll();

        superLayout.setVisibility(View.GONE);
        stationChooserLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        stationChooserLayout.setAnimation(animation);
        stationChooserLayout.animate();
        animation.start();
    }

    public void closeStationlist() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);

        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(Html.fromHtml("<small>" + getActivity().getResources().getString(R.string.curr_book_avl).toUpperCase() + "</small>"));

        stationChooserLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        stationChooserLayout.setAnimation(animation);
        stationChooserLayout.animate();
        animation.start();
    }


    private void setFonts() {

        stationRed.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        stationName.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        trainDeparting.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        selectedStation.setTypeface(AppFonts.getRobotoReguler(getActivity()));
    }


    public void updateUI(CurrentBookingAvailabilityBean bean) {

        currentBean = bean ;

        stationName.setText(selectedStationName);

        List<CurrentBookingAvailabilityBean.CurrentBooking> bookingList = bean.getCurrentBookingList() ;

        if(bookingList != null && bookingList.size() > 0) {

            if (adapter != null) {
                adapter.changeDataSet(bookingList);
                currBookAvlListView.setAdapter(adapter) ;
            } else {
                adapter = new CurrentBookListAdapter(getActivity(), bookingList);
                currBookAvlListView.setAdapter(adapter);
            }
        }
    }


    public void updateException(String exception) {

        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show() ;
    }


    @Override
    public boolean closeEveryThing() {

        if(stationChooserLayout.getVisibility() == View.VISIBLE) {
            closeStationlist();
            return false ;
        }

        return true ;
    }
}
