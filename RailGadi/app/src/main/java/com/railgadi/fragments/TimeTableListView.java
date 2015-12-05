package com.railgadi.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.adapters.TimeTableExpandableAdapter;
import com.railgadi.async.ParseStationsTask;
import com.railgadi.beans.RouteChildBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IUpdateStationBean;

import java.util.List;

public class TimeTableListView extends Fragment implements IUpdateStationBean {

    private View rootView;

    public static TimeTableNewBean bean;

    private TimeTableExpandableAdapter adapter;

    private ParseStationsTask parseStationsTask;

    // views
    private ImageView shapeArrow;

    private TextView trainNumber, trainName, fromCode, toCode, fromName, toName, departure, arrival, trainType, classes;
    private TextView sun, mon, tue, wed, thu, fri, sat;
    private TextView typeText, classText, runText, stnFull, arrFull, depFull, adtFull, distFull, platFull ;
    private TextView timeDistance, arr, dep, station, dist, plat, adtTime;

    private TextView pantryImage, eCatering;
    private ExpandableListView allStationExpList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.time_table_list_view, container, false);

        RouteMapTabsFragment.timeTableListView = TimeTableListView.this;

            parseStationsTask = new ParseStationsTask(getActivity(), bean, TimeTableListView.this);
            parseStationsTask.execute();

        initializeAllViews();
        setTypeFace();

        //runThread();

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (parseStationsTask != null) {
            parseStationsTask.cancel(true);
        }
    }


    public void initializeAllViews() {

        // textviews
        trainNumber = (TextView) rootView.findViewById(R.id.time_table_train_number);
        trainName = (TextView) rootView.findViewById(R.id.time_table_train_name);
        fromCode = (TextView) rootView.findViewById(R.id.time_table_from_code);
        toCode = (TextView) rootView.findViewById(R.id.time_table_to_code);
        fromName = (TextView) rootView.findViewById(R.id.time_table_from_name);
        toName = (TextView) rootView.findViewById(R.id.time_table_to_name);
        departure = (TextView) rootView.findViewById(R.id.time_table_dep_time);
        arrival = (TextView) rootView.findViewById(R.id.time_table_arr_time);
        trainType = (TextView) rootView.findViewById(R.id.time_table_train_type);
        classes = (TextView) rootView.findViewById(R.id.time_table_classes);

        shapeArrow = (ImageView) rootView.findViewById(R.id.shape_arrow);

        sun = (TextView) rootView.findViewById(R.id.sun_timetable);
        mon = (TextView) rootView.findViewById(R.id.mon_timetable);
        tue = (TextView) rootView.findViewById(R.id.tue_timetable);
        wed = (TextView) rootView.findViewById(R.id.wed_timetable);
        thu = (TextView) rootView.findViewById(R.id.thu_timetable);
        fri = (TextView) rootView.findViewById(R.id.fri_timetable);
        sat = (TextView) rootView.findViewById(R.id.sat_timetable);

        typeText    =   (TextView) rootView.findViewById(R.id.type_text) ;
        classText   =   (TextView) rootView.findViewById(R.id.class_text) ;
        runText     =   (TextView) rootView.findViewById(R.id.runn_days_text) ;

        timeDistance = (TextView) rootView.findViewById(R.id.time_table_time_distance_stop);

        arr = (TextView) rootView.findViewById(R.id.arr_title);
        dep = (TextView) rootView.findViewById(R.id.dep_title);
        station = (TextView) rootView.findViewById(R.id.station_title);
        dist = (TextView) rootView.findViewById(R.id.distance_title);
        plat = (TextView) rootView.findViewById(R.id.platforme_title);
        adtTime = (TextView) rootView.findViewById(R.id.adt_title);
        arrFull = (TextView) rootView.findViewById(R.id.arr_full) ;
        depFull = (TextView) rootView.findViewById(R.id.dep_full) ;
        stnFull = (TextView) rootView.findViewById(R.id.station_full) ;
        distFull = (TextView) rootView.findViewById(R.id.distance_full) ;
        platFull  = (TextView) rootView.findViewById(R.id.platform_full) ;
        adtFull = (TextView) rootView.findViewById(R.id.adt_full) ;


        // setDataOnViews();

        // imageViews
        pantryImage = (TextView) rootView.findViewById(R.id.time_table_pantry_image);
        eCatering = (TextView) rootView.findViewById(R.id.time_table_catering);

        // lists
        allStationExpList = (ExpandableListView) rootView.findViewById(R.id.timetable_expanndable_list);

        adapter = new TimeTableExpandableAdapter(getActivity(), bean.getGroupBeans());
    }


    public void setTypeFace() {

        trainName.setTypeface(AppFonts.getRobotoLight(getActivity()));
        trainNumber.setTypeface(AppFonts.getRobotoLight(getActivity()));
        fromCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        toCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        fromName.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        toName.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        departure.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        arrival.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        trainType.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        classes.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        timeDistance.setTypeface(AppFonts.getRobotoLight(getActivity()));
        sun.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        mon.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        tue.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        wed.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        thu.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        fri.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        sat.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        arr.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        dep.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        station.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        dist.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        plat.setTypeface(AppFonts.getRobotoMedium(getActivity()));

    }


    @Override
    public void updateStationBeans(List<RouteChildBean> beans) {
        setDataOnViews(beans);
    }


    public void setDataOnViews(List<RouteChildBean> childList) {

/*

        childList = new ArrayList<>();
        for (RouteGroupBean rgb : bean.getGroupBeans()) {
            for (RouteChildBean cb : rgb.getChildList()) {
                childList.add(cb);
            }
        }

*/


        RouteChildBean start = childList.get(0);
        RouteChildBean end = childList.get(childList.size() - 1);

        trainName.setText(bean.getTrainName());
        trainNumber.setText(bean.getTrainNumber());
        trainType.setText(bean.getTrainType().toUpperCase());
        toCode.setText(end.getStationCode());
        toName.setText(end.getStationName());
        fromCode.setText(start.getStationCode());
        fromName.setText(start.getStationName());
        departure.setText(start.getDeparture());
        arrival.setText(end.getArrival());


        arr = (TextView) rootView.findViewById(R.id.arr_title);
        dep = (TextView) rootView.findViewById(R.id.dep_title);
        station = (TextView) rootView.findViewById(R.id.station_title);
        dist = (TextView) rootView.findViewById(R.id.distance_title);
        plat = (TextView) rootView.findViewById(R.id.platforme_title);
        adtTime = (TextView) rootView.findViewById(R.id.adt_title);
        arrFull = (TextView) rootView.findViewById(R.id.arr_full) ;
        depFull = (TextView) rootView.findViewById(R.id.dep_full) ;
        stnFull = (TextView) rootView.findViewById(R.id.station_full) ;
        distFull = (TextView) rootView.findViewById(R.id.distance_full) ;
        platFull  = (TextView) rootView.findViewById(R.id.platform_full) ;
        adtFull = (TextView) rootView.findViewById(R.id.adt_full) ;

        typeText.setText("Type");
        classText.setText("Class");
        runText.setText("Run Days");
        arr.setText("ARR");
        dep.setText("DEP");
        dist.setText("DIST");
        station.setText("STN");
        plat.setText("PLAT");
        adtTime.setText("A.D.T");
        arrFull.setText("Arrival");
        depFull.setText("Departure");
        distFull.setText("Distance");
        stnFull.setText("Station");
        platFull.setText("Platform");
        adtFull.setText("Avg. Delay Time");
        sun.setText("S");
        mon.setText("M");
        tue.setText("T");
        wed.setText("W");
        thu.setText("T");
        fri.setText("F");
        sat.setText("S");



        shapeArrow.setImageResource(R.drawable.shape_black_arrow);

        boolean[] pantries = bean.getPantries();

        if(pantries.length == 3) {

            if (pantries[0]) {
                pantryImage.setBackground(getActivity().getResources().getDrawable(R.drawable.home_pantry_rest));
                pantryImage.setText("");
            } else {
                pantryImage.setBackground(null);
                pantryImage.setText("");
            }
            if (pantries[2]) {
                eCatering.setBackground(getActivity().getResources().getDrawable(R.drawable.home_internet_rest));
                eCatering.setText("");
            } else {
                eCatering.setBackground(null);
                eCatering.setText("");
            }
        }

        boolean[] runningDays = bean.getRunningDays();

        TextView[] daysArr = {mon, tue, wed, thu, fri, sat, sun};

        for (int i = 0; i < runningDays.length && i < daysArr.length; i++) {

            if (runningDays[i]) {
                daysArr[i].setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            } else {
                daysArr[i].setTextColor(getActivity().getResources().getColor(R.color.light_gray));
            }
        }

        String c = "";
        try {
            for (String s : bean.getAvlClasses()) {
                c = c.concat(s + ",");
            }
            c = new StringBuffer(c).deleteCharAt(c.length() - 1).toString();
            classes.setText(c);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String km = bean.getDistance();
        String time = bean.getTotalTravelTime();
        String stops = bean.getTotalHalts();

        String timeStop = km + " - " + time + " - " + stops + " stops";

        timeDistance.setText(timeStop);
        allStationExpList.setAdapter(adapter);

        allStationExpList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                return parent.isGroupExpanded(groupPosition);
            }
        });
        expandAll();
    }

/*

    private void runThread() {

        new Thread() {
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            initializeAllViews();
                            setTypeFace();
                            setDataOnViews();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
*/


    private void expandAll() {
        int count = adapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            allStationExpList.expandGroup(i);
        }
    }

}
