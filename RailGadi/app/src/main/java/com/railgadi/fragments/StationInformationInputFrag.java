package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.StationNameCodeAdapter;
import com.railgadi.async.StationInformationTask;
import com.railgadi.beans.StationInformationBean;
import com.railgadi.beans.StationListDataHolder;
import com.railgadi.beans.StationNameCodeBean;
import com.railgadi.dbhandlers.StationsDBHandler;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.ICloseEverything;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IStationInformationInterface;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.List;

public class StationInformationInputFrag extends Fragment implements ICloseEverything, IStationInformationInterface {

    private View rootView;

    private StationNameCodeAdapter listAdapter;

    private IFragReplaceCommunicator comm;

    private StationsDBHandler dbHandler;

    private StationInformationTask informationTask ;

    private LinearLayout stationChooserLayout, selectFromLayout, superLayout;
    private ExpandableListView stationList;
    private TextView listTitle, closeListCross;
    private EditText searchView;
    private TextView stationCode, stationName;
    private ImageView stationIcon;
    private TextView getStationInformation;

    private String inputSrcCode, inputSrcName;

    private List<StationListDataHolder> data;


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(informationTask != null) {
            informationTask.cancel(true) ;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.station_information_input, container, false);

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.station_information).toUpperCase());

        MainActivity.closeFragmentObject = StationInformationInputFrag.this ;

        comm = (IFragReplaceCommunicator) getActivity();

        dbHandler = new StationsDBHandler(getActivity());

        inputSrcCode = null ;

        initializeAllViews();
        setDataOnViews();

        return rootView;
    }


    private void initializeAllViews() {

        superLayout = (LinearLayout) rootView.findViewById(R.id.super_layout);
        selectFromLayout    =   (LinearLayout) rootView.findViewById(R.id.select_from_station_layout) ;
        stationChooserLayout = (LinearLayout) rootView.findViewById(R.id.station_chooser_layout);

        searchView = (EditText) rootView.findViewById(R.id.search_train_searchview);

        stationList = (ExpandableListView) rootView.findViewById(R.id.station_chooser_list);

        listTitle = (TextView) rootView.findViewById(R.id.station_list_title);
        closeListCross = (TextView) rootView.findViewById(R.id.close_station_list_cross);

        searchView = (EditText) rootView.findViewById(R.id.search_train_searchview);

        stationCode = (TextView) rootView.findViewById(R.id.from_station_code_text);
        stationName = (TextView) rootView.findViewById(R.id.from_station_name_text);
        stationIcon = (ImageView) rootView.findViewById(R.id.from_icon);

        getStationInformation = (TextView) rootView.findViewById(R.id.get_station_information);

    }

    private void setDataOnViews() {

        refreshAdapterOnStationList();

        listTitle.setText("Select Station");
        listTitle.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        stationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        stationName.setTypeface(AppFonts.getRobotoLight(getActivity()));

        closeListCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeStationlist();
            }
        });

        selectFromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStationList();
            }
        });

        stationList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return parent.isGroupExpanded(groupPosition);
            }
        });

        stationList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                StationListDataHolder cont = (StationListDataHolder) listAdapter.getSelectedItemGroup(groupPosition);

                StationNameCodeBean c = cont.getNameCodeData().get(childPosition);

                inputSrcCode = c.getStationCode();
                inputSrcName = c.getStationName();

                stationName.setText(inputSrcName);
                stationCode.setText(inputSrcCode);

                StationNameCodeBean selectedSrcBean = c;

                if (selectedSrcBean.getStationIcon() == 0) {
                    selectedSrcBean.setStationIcon(R.drawable.shape_logo);
                }

                stationIcon.setImageResource(selectedSrcBean.getStationIcon());
                closeStationlist();

                return false;
            }
        });

        searchView.setFocusable(false);
        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchView.setFocusableInTouchMode(true);
                return false ;
            }
        });
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (listAdapter != null) {
                    listAdapter.filterData(s.toString().toLowerCase());
                    expandAll();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listAdapter != null) {
                    listAdapter.filterData(s.toString().toLowerCase());
                    expandAll();
                }
            }
        });

        getStationInformation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (inputSrcCode == null || inputSrcCode.length() < 1) {
                    Toast.makeText(getActivity(), "Select Station", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*latlng = dbHandler.getLatlngByCode(inputSrcCode);
                if (latlng != null) {
                    comm.respond(new ShowStationInformationFragment(latlng, inputSrcName, inputSrcCode));
                } else {
                    Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
                }*/

                if(InternetChecking.isNetWorkOn(getActivity())) {
                    informationTask = new StationInformationTask(getActivity(), inputSrcCode, StationInformationInputFrag.this) ;
                    informationTask.execute() ;
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });
    }

    private void refreshAdapterOnStationList() {

        data = UtilsMethods.collectStations(getActivity());
        listAdapter = new StationNameCodeAdapter(getActivity(), data);
        stationList.setAdapter(listAdapter);
        expandAll();
    }

    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            stationList.expandGroup(i);
        }
    }

    private void openStationList() {

        MainActivity.toolbar.setVisibility(View.GONE);

        refreshAdapterOnStationList();

        superLayout.setVisibility(View.GONE);
        stationChooserLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        stationChooserLayout.setAnimation(animation);
        stationChooserLayout.animate();
        animation.start();
    }

    private void closeStationlist() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);

        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.search_train));

        stationChooserLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        stationChooserLayout.setAnimation(animation);
        stationChooserLayout.animate();
        animation.start();
    }

    @Override
    public void updateStationInformationUI(StationInformationBean bean) {
        comm.respond(new ShowStationInformationFragment(bean));
    }

    @Override
    public void updateStationInformationException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show() ;
    }

    @Override
    public boolean closeEveryThing() {

        if (stationChooserLayout.getVisibility() == View.VISIBLE) {
            closeStationlist();
            return false;
        }
        return true ;
    }
}
