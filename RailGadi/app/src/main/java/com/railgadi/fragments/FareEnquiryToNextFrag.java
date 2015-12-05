package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.FareBreakUpListNewAdapter;
import com.railgadi.async.FareEnqNewTask;
import com.railgadi.beans.FareEnqInput;
import com.railgadi.beans.FareEnquiryNewBean;
import com.railgadi.beans.MiniTimeTableBean;
import com.railgadi.beans.TrainClassTypeHolder;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IFareEnqWithClassQuota;
import com.railgadi.utilities.InternetChecking;

import java.util.ArrayList;
import java.util.List;

public class FareEnquiryToNextFrag extends Fragment implements IFareEnqWithClassQuota {

    private View rootView;

    private FareBreakUpListNewAdapter adapter;
    private FareEnqNewTask fareEnqNewTask ;

    // views
    private TextView trainNameNumber, fromStation, toStation, arrDepTravelTime, totalFare, trainType;
    private TextView sun, mon, tue, wed, thu, fri, sat, normal, tatkal, preTatkal;
    private ListView fareDetailsListView;
    private LinearLayout classListLayout;
    private View normalLine, tatkalLine, preTatkalLine;

    private String selectedConcession;
    private List<String> avlClasses;
    private List<TrainClassTypeHolder> buttonList;

    private FareEnquiryNewBean bean;
    private MiniTimeTableBean miniTimeTable;
    private FareEnqInput fareInput;


    public FareEnquiryToNextFrag(FareEnquiryNewBean bean, MiniTimeTableBean miniTimeTable, FareEnqInput fei) {

        this.bean = bean;
        this.miniTimeTable = miniTimeTable;
        this.fareInput = fei;

        selectedConcession = fei.getConcCode();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fareEnqNewTask != null) {
            fareEnqNewTask.cancel(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fare_enquiry_to_next_frag, container, false);

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.fare_enquiry).toUpperCase());

        initializeAllViews();
        createClasses();
        setDataOnViews();
        setFonts();
        refreshAdapter(fareInput, bean);

        return rootView;
    }


    public void initializeAllViews() {

        normalLine = rootView.findViewById(R.id.normal_line);
        tatkalLine = rootView.findViewById(R.id.tatkal_line);
        preTatkalLine = rootView.findViewById(R.id.pre_tatkal_line);

        trainNameNumber = (TextView) rootView.findViewById(R.id.train_name_and_number);
        fromStation = (TextView) rootView.findViewById(R.id.via_station_code);
        toStation = (TextView) rootView.findViewById(R.id.to_station_code);
        arrDepTravelTime = (TextView) rootView.findViewById(R.id.arr_dep_travel_time);
        totalFare = (TextView) rootView.findViewById(R.id.total_fare_textview);
        trainType = (TextView) rootView.findViewById(R.id.train_type);
        sun = (TextView) rootView.findViewById(R.id.sun);
        mon = (TextView) rootView.findViewById(R.id.mon);
        tue = (TextView) rootView.findViewById(R.id.tue);
        wed = (TextView) rootView.findViewById(R.id.wed);
        thu = (TextView) rootView.findViewById(R.id.thu);
        fri = (TextView) rootView.findViewById(R.id.fri);
        sat = (TextView) rootView.findViewById(R.id.sat);
        normal = (TextView) rootView.findViewById(R.id.normal_textview);
        tatkal = (TextView) rootView.findViewById(R.id.tatkal_textview);
        preTatkal = (TextView) rootView.findViewById(R.id.pre_tatkal_textview);

        classListLayout = (LinearLayout) rootView.findViewById(R.id.class_list_layout);

        fareDetailsListView = (ListView) rootView.findViewById(R.id.fare_enquiry_details_listview);
    }


    private void setDataOnViews() {

        boolean[] runningDays = miniTimeTable.getRunningDays();
        TextView[] daysArr = {mon, tue, wed, thu, fri, sat, sun};

        for (int i = 0; i < daysArr.length; i++) {
            if (runningDays[i]) {
                daysArr[i].setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            } else {
                daysArr[i].setTextColor(getActivity().getResources().getColor(R.color.light_gray));
            }
        }

        String tNameNumber = miniTimeTable.getTrainName() + " - " + miniTimeTable.getTrainNumber();
        String srcName = miniTimeTable.getSrcStnName();
        String destName = miniTimeTable.getDestStnName();
        String depArrTime = miniTimeTable.getSrcDepTime() + " - " + miniTimeTable.getDestArrTime();

        String duration = miniTimeTable.getTotalTravelTime();

        trainNameNumber.setText(tNameNumber);
        fromStation.setText(srcName);
        toStation.setText(destName);
        arrDepTravelTime.setText(depArrTime + " ( " + duration + " )");

        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fareInput.setQuota("GN");
                fareInput.setConcCode(selectedConcession);

                if (InternetChecking.isNetWorkOn(getActivity())) {
                    fareEnqNewTask  = new FareEnqNewTask(getActivity(), fareInput, FareEnquiryToNextFrag.this) ;
                    fareEnqNewTask.execute();
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });

        tatkal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //FareEnqInput fei = new FareEnqInput();
                //fei.assign(fareInput);

                if (fareInput.isSetConcCode()) {
                    Toast.makeText(getActivity(), "Concession is not applicable", Toast.LENGTH_SHORT).show();
                    //fei.setConcCode(null);
                    return ;
                }

                if (InternetChecking.isNetWorkOn(getActivity())) {
                    fareInput.setQuota("CK");
                    fareEnqNewTask  = new FareEnqNewTask(getActivity(), fareInput, FareEnquiryToNextFrag.this) ;
                    fareEnqNewTask.execute();
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });

        preTatkal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //FareEnqInput fei = new FareEnqInput();
                //fei.assign(fei);

                if (fareInput.isSetConcCode()) {
                    Toast.makeText(getActivity(), "Concession is not applicable", Toast.LENGTH_SHORT).show();
                    //fei.setConcCode(null);
                    return ;
                }

                if (InternetChecking.isNetWorkOn(getActivity())) {
                    fareInput.setQuota("PT");
                    fareEnqNewTask  = new FareEnqNewTask(getActivity(), fareInput, FareEnquiryToNextFrag.this) ;
                    fareEnqNewTask.execute();
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });
    }

    private void updateUI(String quota) {

        switch (quota) {

            case "GN": {

                normal.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                tatkal.setTextColor(getActivity().getResources().getColor(R.color.light_gray));
                preTatkal.setTextColor(getActivity().getResources().getColor(R.color.light_gray));
                normalLine.setVisibility(View.VISIBLE);
                tatkalLine.setVisibility(View.GONE);
                preTatkalLine.setVisibility(View.GONE);

                break;
            }

            case "CK": {

                normal.setTextColor(getActivity().getResources().getColor(R.color.light_gray));
                tatkal.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                preTatkal.setTextColor(getActivity().getResources().getColor(R.color.light_gray));
                normalLine.setVisibility(View.GONE);
                tatkalLine.setVisibility(View.VISIBLE);
                preTatkalLine.setVisibility(View.GONE);

                break;
            }

            case "PT": {

                normal.setTextColor(getActivity().getResources().getColor(R.color.light_gray));
                tatkal.setTextColor(getActivity().getResources().getColor(R.color.light_gray));
                preTatkal.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                normalLine.setVisibility(View.GONE);
                tatkalLine.setVisibility(View.GONE);
                preTatkalLine.setVisibility(View.VISIBLE);

                break;
            }
        }
    }

    private void setFonts() {

        trainNameNumber.setTypeface(AppFonts.getRobotoLight(getActivity()));
        fromStation.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        toStation.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        arrDepTravelTime.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        trainType.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        sun.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        mon.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        tue.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        wed.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        thu.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        fri.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        sat.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        normal.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        tatkal.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        preTatkal.setTypeface(AppFonts.getRobotoReguler(getActivity()));
    }


    private void createClasses() {

        buttonList = new ArrayList<>();

        try {
            avlClasses = miniTimeTable.getAvlClasses();
        } catch (Exception e) {
        }

        if (bean.getTotalAmount() != null) {
            totalFare.setText(bean.getNetAmount());
        }

        for (int i = 0; i < avlClasses.size(); i++) {

            String selectedClassCode = avlClasses.get(i);

            TextView classButton = new TextView(getActivity());

            classButton.setGravity(Gravity.CENTER);
            classButton.setTypeface(AppFonts.getRobotoLight(getActivity()));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(75, 75);
            layoutParams.setMargins(0, 0, 50, 0);
            classButton.setLayoutParams(layoutParams);
            classButton.setTextSize(15);
            classButton.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            classButton.setText(selectedClassCode);
            classButton.setTextColor(getActivity().getResources().getColor(R.color.light_gray));
            classButton.setBackground(null);
            classButton.setTextColor(getActivity().getResources().getColor(R.color.black));
            classButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String cc = ((TextView) v).getText().toString();

                    for (TrainClassTypeHolder ch : buttonList) {
                        if (ch.getButton().getText().toString().equals(cc)) {
                            ch.setActivation(true);
                        } else {
                            ch.setActivation(false);
                        }
                    }

                    fareInput.setClassCode(cc);

                    if (InternetChecking.isNetWorkOn(getActivity())) {
                        fareEnqNewTask  = new FareEnqNewTask(getActivity(), fareInput, FareEnquiryToNextFrag.this) ;
                        fareEnqNewTask.execute();
                    } else {
                        InternetChecking.noInterNetToast(getActivity());
                    }
                }
            });


            TrainClassTypeHolder cHolder = null;

            if (i == avlClasses.size() - 1) {
                cHolder = new TrainClassTypeHolder();
                cHolder.setButton(classButton);
                cHolder.setActivation(true);
                cHolder.getButton().setBackground(getActivity().getResources().getDrawable(R.drawable.round_red_selected));
            } else {
                cHolder = new TrainClassTypeHolder();
                cHolder.setButton(classButton);
                cHolder.setActivation(false);
                cHolder.getButton().setBackground(null);
            }

            classListLayout.addView(classButton);
            buttonList.add(cHolder);
        }
    }

    private void refreshAdapter(FareEnqInput fei, FareEnquiryNewBean bean) {

        this.fareInput = fei;

        updateUI(fei.getQuota());

        if (bean != null) {

            String totalAmount = bean.getNetAmount();
            if(totalAmount == null || totalAmount.isEmpty()) {
                totalAmount = bean.getTotalAmount() ;
            }
            totalFare.setText(getActivity().getResources().getString(R.string.rs_symbol) + " " + totalAmount);
            adapter = new FareBreakUpListNewAdapter(getActivity(), bean.getFareList());
            fareDetailsListView.setAdapter(adapter);
            fareDetailsListView.setDivider(getActivity().getResources().getDrawable(R.drawable.dashed_divider));
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
    }

    @Override
    public void updateFareOnClassAndQuota(FareEnquiryNewBean bean, FareEnqInput fei) {

        if(bean != null) {
            refreshAdapter(fei, bean);
        }
    }

    @Override
    public void updateException(String exceptionType) {
        Toast.makeText(getActivity(), exceptionType, Toast.LENGTH_SHORT).show();
    }
}
