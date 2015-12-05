package com.railgadi.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.AutoLoginActivity;
import com.railgadi.activities.MainActivity;
import com.railgadi.async.FareSeatSingleTrainTask;
import com.railgadi.beans.FareEnqInput;
import com.railgadi.beans.SeatFareBeanNew;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.beans.TrainClassTypeHolder;
import com.railgadi.dbhandlers.StationsDBHandler;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.ISeatAvlFare;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SeatAvlSingleTrainFragment extends Fragment implements ISeatAvlFare {


    private View rootView;

    private IFragReplaceCommunicator comm;
    public static LeftDrawerFragment leftDrawerFragment;
    public static RightDrawerFragment rightDrawerFragment;

    private TimeTableNewBean savedTimeTable;

    private SeatFareBeanNew seatFareBeanNew;
    private SeatFareBeanNew.FareBreakup fareBreakupBean;
    private SeatFareBeanNew.SeatAvl seatAvlBean;
    private FareEnqInput input;
    private Map<String, TimeTableNewBean.AllStations> allStationMap;

    private FareSeatSingleTrainTask fareSeatSingleTrainTask;

    private StationsDBHandler dbHandler;

    private List<String> classList;
    private String lowerClassCode;
    private String selectedClassCode;

    // class holder list
    private static List<TrainClassTypeHolder> buttonList;
    private static String SELECTED_CLASS;

    // views
    private TextView trainNameNumber, trainType, depArrTravelTime, fromStation, toStation, totalFareTextView;
    private TextView sun, mon, tue, wed, thu, fri, sat, selectClassTextView, nextSix, prevSix;
    private TextView dateOne, dateTwo, dateThree, dateFour, dateFive, dateSix, avlOne, avlTwo, avlThree, avlFour, avlFive, avlSix;
    private LinearLayout classListLayout;
    private ImageView popupMenu;

    private TimeTableNewBean.AllStations source;
    private TimeTableNewBean.AllStations destination;

    public SeatAvlSingleTrainFragment(SeatFareBeanNew bean, TimeTableNewBean savedTimeTable, FareEnqInput input) {

        this.seatFareBeanNew = bean;
        this.fareBreakupBean = bean.getFareBreakup();
        this.seatAvlBean = bean.getSeatAvl();
        this.savedTimeTable = savedTimeTable;
        this.input = input;

        allStationMap = new HashMap<>();
        for (TimeTableNewBean.AllStations as : savedTimeTable.getAllStationsList()) {
            allStationMap.put(as.stnCode, as);
            if (as.stnCode.equals(input.getSrcCode())) {
                source = as;
            }
            if (as.stnCode.equals(input.getDestCode())) {
                destination = as;
            }
        }

        savedData = new HashMap<>() ;
        Container cont = new Container() ;
        cont.seatFareBeanNew = bean ;
        cont.fei = input ;
        savedData.put(input.getClassCode(), cont) ;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (fareSeatSingleTrainTask != null) {
            fareSeatSingleTrainTask.cancel(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.seat_avl_to_single_train, container, false);

        comm = (IFragReplaceCommunicator) getActivity();

        dbHandler = new StationsDBHandler(getActivity());

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.seat_availability).toUpperCase());

        rightDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        leftDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);

        try {
            this.selectedClassCode = this.savedTimeTable.getAvlClasses().get(savedTimeTable.getAvlClasses().size() - 1);
        } catch (Exception e) {

        }

        initializeViews();
        setDataOnViews();
        setUpClassLayout();
        setFontOnViews();

        updateSeatAvailabilityUI(seatFareBeanNew.getSeatAvl(), seatFareBeanNew.getFareBreakup());

        return rootView;
    }


    private void initializeViews() {

        trainNameNumber = (TextView) rootView.findViewById(R.id.train_name_and_number);
        trainType = (TextView) rootView.findViewById(R.id.train_type);
        depArrTravelTime = (TextView) rootView.findViewById(R.id.arr_dep_travel_time);
        fromStation = (TextView) rootView.findViewById(R.id.via_station_code);
        toStation = (TextView) rootView.findViewById(R.id.to_station_code);
        totalFareTextView = (TextView) rootView.findViewById(R.id.total_fare_textview);
        selectClassTextView = (TextView) rootView.findViewById(R.id.select_class_textview);
        sun = (TextView) rootView.findViewById(R.id.sun);
        mon = (TextView) rootView.findViewById(R.id.mon);
        tue = (TextView) rootView.findViewById(R.id.tue);
        wed = (TextView) rootView.findViewById(R.id.wed);
        thu = (TextView) rootView.findViewById(R.id.thu);
        fri = (TextView) rootView.findViewById(R.id.fri);
        sat = (TextView) rootView.findViewById(R.id.sat);
        dateOne = (TextView) rootView.findViewById(R.id.date_one);
        dateTwo = (TextView) rootView.findViewById(R.id.date_two);
        dateThree = (TextView) rootView.findViewById(R.id.date_three);
        dateFour = (TextView) rootView.findViewById(R.id.date_four);
        dateFive = (TextView) rootView.findViewById(R.id.date_five);
        dateSix = (TextView) rootView.findViewById(R.id.date_six);
        avlOne = (TextView) rootView.findViewById(R.id.avl_one);
        avlTwo = (TextView) rootView.findViewById(R.id.avl_to);
        avlThree = (TextView) rootView.findViewById(R.id.avl_three);
        avlFour = (TextView) rootView.findViewById(R.id.avl_four);
        avlFive = (TextView) rootView.findViewById(R.id.avl_five);
        avlSix = (TextView) rootView.findViewById(R.id.avl_six);
        nextSix = (TextView) rootView.findViewById(R.id.next_six_days);
        prevSix = (TextView) rootView.findViewById(R.id.prev_six_days);

        classListLayout = (LinearLayout) rootView.findViewById(R.id.class_list_layout);

        popupMenu = (ImageView) rootView.findViewById(R.id.popup_menu_button);

    }

    private void setDataOnViews() {

        trainNameNumber.setText(savedTimeTable.getTrainName() + " - " + seatAvlBean.trainNumber);
        fromStation.setText(dbHandler.getStationNameFromCode(input.getSrcCode()));
        toStation.setText(dbHandler.getStationNameFromCode(input.getDestCode()));
        trainType.setText(fareBreakupBean.trainType);

        String duration = "";
        if (source != null && destination != null) {
            int[] d = Utilities.getTimeDiffBtwStation(source.deptTime, source.day, destination.deptTime, destination.day);
            duration = d[0] + "h " + d[1] + "m";
        }

        // setting running days
        boolean[] runningDays = savedTimeTable.getRunningDays();
        TextView[] daysArr = {mon, tue, wed, thu, fri, sat, sun};
        for (int i = 0; i < runningDays.length && i < daysArr.length; i++) {
            if (runningDays[i]) {
                daysArr[i].setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            } else {
                daysArr[i].setTextColor(getActivity().getResources().getColor(R.color.light_gray));
            }
        }

        depArrTravelTime.setText(allStationMap.get(input.getSrcCode()).deptTime + " - " + allStationMap.get(input.getDestCode()).arrTime + " ( " + duration + " )");

        popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPopupMenu(v);
            }
        });

        nextSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPrevious(Constants.NEXT);
            }
        });

        prevSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPrevious(Constants.PREV);
            }
        });
    }

    private void nextPrevious(String flag) {

        if(InternetChecking.isNetWorkOn(getActivity())) {
            if (flag.equals(Constants.NEXT)) {

                input.setDay(seatAvlBean.next.nextDay);
                input.setMonth(seatAvlBean.next.nextMonth);

                fareSeatSingleTrainTask = new FareSeatSingleTrainTask(getActivity(), input, SeatAvlSingleTrainFragment.this);
                fareSeatSingleTrainTask.execute();
            } else if (flag.equals(Constants.PREV)) {

                input.setDay(seatAvlBean.previous.nextDay);
                input.setMonth(seatAvlBean.previous.nextMonth);

                fareSeatSingleTrainTask = new FareSeatSingleTrainTask(getActivity(), input, SeatAvlSingleTrainFragment.this);
                fareSeatSingleTrainTask.execute();
            }
        } else {
            InternetChecking.noInterNetToast(getActivity());
        }
    }


    private void setFontOnViews() {

        trainNameNumber.setTypeface(AppFonts.getRobotoLight(getActivity()));
        fromStation.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        toStation.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        depArrTravelTime.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        mon.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        tue.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        wed.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        thu.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        fri.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        sat.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        totalFareTextView.setTypeface(AppFonts.getRobotoLight(getActivity()));
        selectClassTextView.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        nextSix.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        prevSix.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        sun.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        avlOne.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        avlTwo.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        avlThree.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        avlFour.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        avlFive.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        avlSix.setTypeface(AppFonts.getRobotoMedium(getActivity()));

        dateOne.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        dateTwo.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        dateThree.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        dateFour.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        dateFive.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        dateSix.setTypeface(AppFonts.getRobotoReguler(getActivity()));

    }


    public void setUpPopupMenu(View v) {

        PopupMenu popup = new PopupMenu(getActivity(), v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.avaiable_train_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {

                String title = item.getTitle().toString();

                if (title.equals(getActivity().getResources().getString(R.string.time_table))) {
                    comm.respond(new RouteMapTabsFragment(savedTimeTable));
                } else if (title.equals(getActivity().getResources().getString(R.string.fare_breakup))) {
                    comm.respond(new FareBreakupFragmentFT(fareBreakupBean));
                } else if (title.equals(getActivity().getResources().getString(R.string.irctc_booking))) {
                    startActivity(new Intent(getActivity(), AutoLoginActivity.class));
                }

                return true;
            }
        });
        popup.show();
    }


    private void setUpClassLayout() {


        if(buttonList == null) {
            buttonList = new ArrayList<>() ;
        }

        try {
            classList = savedTimeTable.getAvlClasses();
        } catch (Exception e) {

        }

        classListLayout.removeAllViews();

        if (fareBreakupBean.netAmount == null || fareBreakupBean.netAmount.isEmpty()) {
            totalFareTextView.setText(getActivity().getResources().getString(R.string.rs_symbol) + " " + fareBreakupBean.totalAmount);
        } else {
            totalFareTextView.setText(getActivity().getResources().getString(R.string.rs_symbol) + " " + fareBreakupBean.netAmount);
        }

        for (int i = 0; i < classList.size(); i++) {

            lowerClassCode = classList.get(i);

            TextView classButton = new TextView(getActivity());
            classButton.setGravity(Gravity.CENTER);
            classButton.setTypeface(AppFonts.getRobotoLight(getActivity()));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(75, 75);
            layoutParams.setMargins(0, 0, 50, 0);
            classButton.setLayoutParams(layoutParams);
            classButton.setTextSize(15);
            classButton.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            classButton.setText(lowerClassCode);
            classButton.setTextColor(getActivity().getResources().getColor(R.color.light_gray));
            classButton.setBackground(null);
            classButton.setTextColor(getActivity().getResources().getColor(R.color.black));

            classButton.setOnClickListener(new ClickOnClasses(input));

            TrainClassTypeHolder cHolder = null;

            if (i == classList.size() - 1) {
                cHolder = new TrainClassTypeHolder();
                cHolder.setButton(classButton);
                cHolder.setActivation(true);
            } else {
                cHolder = new TrainClassTypeHolder();
                cHolder.setButton(classButton);
                cHolder.setActivation(false);
            }

            classListLayout.addView(classButton);

            buttonList.add(cHolder);
        }
    }



    public void updateSeatAvailabilityUI(SeatFareBeanNew.SeatAvl seatAvl, SeatFareBeanNew.FareBreakup fareBreakup) {

        if ((seatAvl.next.nextMonth == null || seatAvl.next.nextMonth.isEmpty()) || (seatAvl.next.nextDay == null || seatAvl.next.nextDay.isEmpty())) {
            nextSix.setClickable(false);
            nextSix.setTextColor(getActivity().getResources().getColor(R.color.light_gray));
        } else {
            nextSix.setClickable(true);
            nextSix.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        }

        if ((seatAvl.previous.nextMonth == null || seatAvl.previous.nextMonth.isEmpty()) || (seatAvl.previous.nextDay == null || seatAvl.previous.nextDay.isEmpty())) {
            prevSix.setClickable(false);
            prevSix.setTextColor(getActivity().getResources().getColor(R.color.light_gray));
        } else {
            prevSix.setClickable(true);
            prevSix.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        }

        View allDates[] = {dateOne, dateTwo, dateThree, dateFour, dateFive, dateSix};
        View allStatus[] = {avlOne, avlTwo, avlThree, avlFour, avlFive, avlSix};

        if (fareBreakup.netAmount == null || fareBreakup.netAmount.isEmpty()) {
            totalFareTextView.setText(getActivity().getResources().getString(R.string.rs_symbol) + " " + fareBreakup.totalAmount);
        } else {
            totalFareTextView.setText(getActivity().getResources().getString(R.string.rs_symbol) + " " + fareBreakup.netAmount);
        }

        for (TrainClassTypeHolder ch : buttonList) {
            if (ch.getActivation()) {
                ch.getButton().setTextColor(getActivity().getResources().getColor(R.color.white));
                ch.getButton().setBackground(getActivity().getResources().getDrawable(R.drawable.round_red_selected));
            } else {
                ch.getButton().setTextColor(getActivity().getResources().getColor(R.color.light_gray));
                ch.getButton().setBackground(null);
            }
        }

        List<SeatFareBeanNew.SeatAvl.AvlStatus> seatAvlList = seatAvl.avlStatusList;

        for (int i = 0; i < seatAvlList.size(); i++) {

            SeatFareBeanNew.SeatAvl.AvlStatus sa = seatAvlList.get(i);

            String ds = sa.date;
            ((TextView) allDates[i]).setText(ds);

            String status = sa.status[0];
            String noOfSeat = sa.status[1];

            ((TextView) allStatus[i]).setText(status + " " + noOfSeat);

            if (((TextView) allStatus[i]).getText().toString().contains("DEPART")) {
                (allStatus[i]).setBackground(getActivity().getResources().getDrawable(R.drawable.black));
                ((TextView) allStatus[i]).setText("Departed" + " " + noOfSeat);
            } else if (((TextView) allStatus[i]).getText().toString().contains("NOT AVAIL")) {
                (allStatus[i]).setBackground(getActivity().getResources().getDrawable(R.drawable.black));
                ((TextView) allStatus[i]).setText("NA");
            } else if (((TextView) allStatus[i]).getText().toString().contains("AVAIL")) {
                (allStatus[i]).setBackground(getActivity().getResources().getDrawable(R.drawable.green));
                ((TextView) allStatus[i]).setText("AVL" + " " + noOfSeat);
            } else if (((TextView) allStatus[i]).getText().toString().contains("NOT AV")) {
                (allStatus[i]).setBackground(getActivity().getResources().getDrawable(R.drawable.black));
                ((TextView) allStatus[i]).setText("Not AVL" + " " + noOfSeat);
            } else if (((TextView) allStatus[i]).getText().toString().contains("WL")) {
                (allStatus[i]).setBackground(getActivity().getResources().getDrawable(R.drawable.light_red));
            } else if (((TextView) allStatus[i]).getText().toString().contains("RAC")) {
                (allStatus[i]).setBackground(getActivity().getResources().getDrawable(R.drawable.yellow));
            } else if (((TextView) allStatus[i]).getText().toString().contains("CANCEL")) {
                (allStatus[i]).setBackground(getActivity().getResources().getDrawable(R.drawable.black));
                ((TextView) allStatus[i]).setText("Canceled");
            }
        }
    }


    @Override
    public void updateException(String exceptionMsg) {
        Toast.makeText(getActivity(), exceptionMsg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void updateSearchTrainUi(SeatFareBeanNew seatFareBeanNew, FareEnqInput fei) {
        this.input = fei;
        this.seatFareBeanNew = seatFareBeanNew;
        this.fareBreakupBean = seatFareBeanNew.getFareBreakup();
        this.seatAvlBean = seatFareBeanNew.getSeatAvl();

        Container container = new Container() ;
        container.seatFareBeanNew = seatFareBeanNew ;
        container.fei = fei ;
        savedData.put(fei.getClassCode(), container) ;

        updateSeatAvailabilityUI(seatFareBeanNew.getSeatAvl(), seatFareBeanNew.getFareBreakup());
    }


    private Map<String, Container> savedData ;

    private class Container {
        SeatFareBeanNew seatFareBeanNew ;
        FareEnqInput fei ;
    }


    @Override
    public void onResume() {
        super.onResume();

        String classCode = "" ;

        if(buttonList != null) {
            for(TrainClassTypeHolder holder : buttonList)
            {
                if(holder.getActivation()) {
                    classCode = holder.getButton().getText().toString() ;
                    break ;
                }
            }
        } else {
            buttonList = new ArrayList<>() ;
        }


        if(savedData != null) {
            if(savedData.keySet().contains(classCode)) {
                Container container = savedData.get(classCode);
                updateSearchTrainUi(container.seatFareBeanNew, container.fei);
            }
        } else {
            savedData = new HashMap<>() ;
        }
    }

    private class ClickOnClasses implements View.OnClickListener {

        FareEnqInput fei;

        ClickOnClasses(FareEnqInput fei) {
            this.fei = fei;
        }

        @Override
        public void onClick(View v) {

            String clickedClass = ((TextView) v).getText().toString();

            fei.setClassCode(clickedClass);

            for (TrainClassTypeHolder holder : buttonList) {
                if (holder.getButton().getText().toString().equals(clickedClass)) {
                    holder.setActivation(true);
                } else {
                    holder.setActivation(false);
                }
            }

            Set<String> keySet = savedData.keySet() ;
            if (keySet.contains(clickedClass)) {
                Container cont = savedData.get(clickedClass) ;
                updateSearchTrainUi(cont.seatFareBeanNew, cont.fei) ;
                return ;
            }

            if(InternetChecking.isNetWorkOn(getActivity())) {
                fareSeatSingleTrainTask = new FareSeatSingleTrainTask(getActivity(), fei, SeatAvlSingleTrainFragment.this);
                fareSeatSingleTrainTask.execute();
            } else {
                InternetChecking.noInterNetToast(getActivity());
            }
        }
    }
}
