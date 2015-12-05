package com.railgadi.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.adapters.LiveTrainAdapter;
import com.railgadi.async.LiveTrainNewTask;
import com.railgadi.beans.LiveTrainNewBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.ILiveTrainInterface;
import com.railgadi.preferences.LiveTrainTrackingPreferences;
import com.railgadi.utilities.DateAndMore;
import com.railgadi.utilities.InternetChecking;

import java.util.Date;


public class LiveTrainListViewFragment extends Fragment implements ILiveTrainInterface {

    private View rootView;

    private LiveTrainAdapter adapter;

    private LiveTrainNewTask liveTrainNewTask ;

    public static LiveTrainNewBean liveTrainNewBean ;
    public static TimeTableNewBean timeTableNewBean ;

    private LiveTrainTrackingPreferences liveTrackPreferences ;

    //views
    private ListView showLiveTrainListView;
    private TextView trainNumber, trainName, srcTiming;
    private TextView locationText, locationValue, nextStopText, lastUpdated, nextStopValue, trainRunning;



    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(liveTrainNewTask != null) {
            liveTrainNewTask.cancel(true) ;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.live_train_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem trackItem = menu.getItem(0) ;
        MenuItem refreshItem = menu.getItem(1) ;

        trackItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if( liveTrainNewBean.getNextStation() == null ) {
                    Toast.makeText(getActivity(), "Train is arrived at destination", Toast.LENGTH_SHORT).show() ;
                    return true ;
                }

/*
                if( liveTrainNewBean.isTrainNotDeparted() ) {
                    Toast.makeText(getActivity(), "Train is not departed from source", Toast.LENGTH_SHORT).show() ;
                    return true ;
                }
*/

                liveTrackPreferences = new LiveTrainTrackingPreferences(getActivity()) ;
                if(liveTrackPreferences.isAlreadyTracked(liveTrainNewBean.getMiniTimeTableBean().getTrainNumber())) {
                    Toast.makeText(getActivity(), "This Train is Already Tracked", Toast.LENGTH_SHORT).show() ;
                    return true ;
                }

                liveTrackPreferences.saveLiveTrackingToPref(liveTrainNewBean);

                Toast.makeText(getActivity(), "Train Tracked", Toast.LENGTH_SHORT).show() ;

                return true ;
            }
        }) ;

        refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                refreshLiveTrainList() ;
                return true ;
            }
        }) ;

    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.live_train_show_status, container, false);

        initializeAllViews();
        setDataOnViews();
        setTypeface();

        return rootView;
    }


    private void initializeAllViews() {

        Log.d("INIT start: ", DateAndMore.formatDateToString(new Date(), DateAndMore.SUPER_DATE_FORMAT)) ;

        trainNumber = (TextView) rootView.findViewById(R.id.train_number);
        trainName = (TextView) rootView.findViewById(R.id.train_name);
        srcTiming = (TextView) rootView.findViewById(R.id.timing_src);
        locationText = (TextView) rootView.findViewById(R.id.location_text);
        locationValue = (TextView) rootView.findViewById(R.id.location_value);
        nextStopText = (TextView) rootView.findViewById(R.id.next_stop_text);
        nextStopValue = (TextView) rootView.findViewById(R.id.next_stop_value);
        trainRunning = (TextView) rootView.findViewById(R.id.train_running);
        lastUpdated = (TextView) rootView.findViewById(R.id.last_updated) ;

        showLiveTrainListView = (ListView) rootView.findViewById(R.id.live_train_list_view);

        Log.d("INIT ends : ", DateAndMore.formatDateToString(new Date(), DateAndMore.SUPER_DATE_FORMAT)) ;

    }


    private void setTypeface() {

        Log.d("TYPEFACE start: ", DateAndMore.formatDateToString(new Date(), DateAndMore.SUPER_DATE_FORMAT)) ;

        trainNumber.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        trainName.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        srcTiming.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        locationText.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        locationValue.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        nextStopValue.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        nextStopText.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        trainRunning.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        lastUpdated.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        Log.d("TYPEFACE ENDS : ", DateAndMore.formatDateToString(new Date(), DateAndMore.SUPER_DATE_FORMAT)) ;
    }


    private void setDataOnViews() {

        trainNumber.setText(timeTableNewBean.getTrainNumber());
        trainName.setText(timeTableNewBean.getTrainName());

        String srcTimeStart = "Started at "+liveTrainNewBean.getMiniTimeTableBean().getTrainRouteList().get(0).stationName +
                                " on "+ DateAndMore.formatDateToString(liveTrainNewBean.getInputDateInstance(), DateAndMore.MMM_DD_YYYY) ;

        srcTiming.setText(srcTimeStart) ;

        LiveTrainNewBean.NextStation ns = liveTrainNewBean.getNextStation() ;
        LiveTrainNewBean.LastStation ls = liveTrainNewBean.getLastStation() ;

        if(ns == null) {
            nextStopValue.setText("Train on destination");
        } else {
            nextStopValue.setText(ns.stnName+" ( "+ns.nextDistAhead+" away ) ") ;
        }

        String [] arr= ls.delay.split(":") ;
        if("00".equals(arr[0]) && "00".equals(arr[1])) {
            trainRunning.setText("Train running on time");
        } else {
            trainRunning.setText("Train running "+arr[0]+" hrs "+arr[1]+" min late");
        }

        locationValue.setText(ls.stnName+" "+ls.depTime);
        lastUpdated.setText("Last Updated at "+liveTrainNewBean.getLastUpdatedTime());

        adapter     =   new LiveTrainAdapter(getActivity(), liveTrainNewBean) ;

        showLiveTrainListView.setAdapter(adapter) ;

        timerDelayRunForScroll(300, liveTrainNewBean.trainPosition) ;

        Log.d("SET DATA END : ", DateAndMore.formatDateToString(new Date(), DateAndMore.SUPER_DATE_FORMAT)) ;
    }



    public void timerDelayRunForScroll(long time, final int p) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    showLiveTrainListView.smoothScrollToPosition(p);
                } catch (Exception e) {}
            }
        }, time);
    }


    private void refreshLiveTrainList() {

        if(InternetChecking.isNetWorkOn(getActivity())) {
            liveTrainNewTask = new LiveTrainNewTask(getActivity(), timeTableNewBean.getTrainNumber(), liveTrainNewBean, LiveTrainListViewFragment.this) ;
            liveTrainNewTask.execute() ;
        } else {
            InternetChecking.noInterNetToast(getActivity()) ;
        }

    }


    @Override
    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show() ;
    }

    @Override
    public void updateLiveTrainData(LiveTrainNewBean bean, TimeTableNewBean timeTableNewBean) {

        this.liveTrainNewBean        =   bean ;
        this.timeTableNewBean        =   timeTableNewBean ;

        setDataOnViews() ;
    }
}
