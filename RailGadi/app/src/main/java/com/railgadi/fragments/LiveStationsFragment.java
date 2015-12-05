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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.StationNameCodeAdapter;
import com.railgadi.async.LiveStationTaskNew;
import com.railgadi.beans.LiveStationNewBean;
import com.railgadi.beans.StationListDataHolder;
import com.railgadi.beans.StationNameCodeBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.ICloseEverything;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.ILiveStationInterface;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.List;

public class LiveStationsFragment extends Fragment implements ICloseEverything, ILiveStationInterface {

    private static String FROM = "FROM" ;
    private static String TO = "to" ;
    private String listFlag ;

    private View rootView ;

    private IFragReplaceCommunicator comm ;

    private StationNameCodeAdapter listAdapter ;
    private List<StationListDataHolder> data ;

    private LiveStationTaskNew liveStationTask ;

    // views
    private TextView viaStationCode, toStationCode, viaStationName, toStationName, trainInNext ;
    private TextView getTrainsButton , clearButton ;
    private RadioGroup radioGroup ;
    private RadioButton twoHR, fourHR ;

    private LinearLayout superLayout, viaClickLayout, toClickLayout, stationChooserLayout ;
    private ExpandableListView allStationListView ;
    private EditText searchView ;
    private TextView stationListTitle ;
    private TextView closeStation ;
    private ImageView viaIcon, toIcon ;






    private String selectedViaName ;
    private String from , to , time ;


    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(liveStationTask != null) {
            liveStationTask.cancel(true) ;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView        =   inflater.inflate(R.layout.live_stations_fragment, container, false ) ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.live_station).toUpperCase());

        comm            =   (IFragReplaceCommunicator) getActivity() ;

        MainActivity.closeFragmentObject = LiveStationsFragment.this ;

        time = "2" ;

        initializeAllViews() ;
        setDataOnViews() ;
        setTypeface() ;


        return rootView ;
    }

    private void initializeAllViews() {

        viaStationCode              =   (TextView) rootView.findViewById(R.id.via_station_code) ;
        toStationCode               =   (TextView) rootView.findViewById(R.id.to_station_code) ;
        viaStationName              =   (TextView) rootView.findViewById(R.id.via_station_name) ;
        toStationName               =   (TextView) rootView.findViewById(R.id.to_station_name) ;
        trainInNext                 =   (TextView) rootView.findViewById(R.id.train_in_next) ;
        getTrainsButton             =   (TextView) rootView.findViewById(R.id.get_train_button) ;
        clearButton                 =   (TextView) rootView.findViewById(R.id.clear_button) ;
        stationListTitle            =   (TextView) rootView.findViewById(R.id.station_list_title) ;

        viaIcon                     =   (ImageView) rootView.findViewById(R.id.via_icon) ;
        toIcon                      =   (ImageView) rootView.findViewById(R.id.to_icon) ;

        radioGroup                  =   (RadioGroup) rootView.findViewById(R.id.radio_group) ;

        twoHR                       =   (RadioButton) rootView.findViewById(R.id.two_hr_check);
        fourHR                      =   (RadioButton) rootView.findViewById(R.id.four_hr_check);

        closeStation                =   (TextView) rootView.findViewById(R.id.close_station) ;
        closeStation.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        searchView = (EditText) rootView.findViewById(R.id.search_train_searchview);
        searchView.setFocusable(false);
        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchView.setFocusableInTouchMode(true);
                return false;
            }
        });

        allStationListView          =   (ExpandableListView) rootView.findViewById(R.id.all_station_list) ;

        superLayout                 =   (LinearLayout) rootView.findViewById(R.id.super_layout) ;
        viaClickLayout              =   (LinearLayout) rootView.findViewById(R.id.via_click_layout) ;
        toClickLayout               =   (LinearLayout) rootView.findViewById(R.id.to_click_layout) ;
        stationChooserLayout        =   (LinearLayout) rootView.findViewById(R.id.station_chooser_layout) ;
    }


    private void setDataOnViews() {

        toStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        viaStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        toStationName.setTypeface(AppFonts.getRobotoLight(getActivity()));
        viaStationName.setTypeface(AppFonts.getRobotoLight(getActivity()));

        allStationListView.setBackgroundColor(getActivity().getResources().getColor(R.color.calendar_background));
        refreshAdapterOnStationList();
        expandAll();

        allStationListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return parent.isGroupExpanded(groupPosition);
            }
        });


        allStationListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {

                StationListDataHolder cont = (StationListDataHolder) listAdapter.getSelectedItemGroup(groupPosition);

                StationNameCodeBean c = cont.getNameCodeData().get(childPosition);

                if(FROM.equals(listFlag)) {

                    selectedViaName = c.getStationName();
                    from = c.getStationCode();
                    viaIcon.setImageResource(c.getStationIcon());
                    viaStationCode.setText(c.getStationCode());
                    viaStationName.setText(c.getStationName());
                }
                else if(TO.equals(listFlag)) {

                    to= c.getStationCode();
                    toIcon.setImageResource(c.getStationIcon());
                    toStationCode.setText(c.getStationCode());
                    toStationName.setText(c.getStationName());
                }

                closeStationlist();
                return false;
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAdapter.filterData(s.toString().toLowerCase());
                expandAll();
            }

            @Override
            public void afterTextChanged(Editable s) {
                listAdapter.filterData(s.toString().toLowerCase());
                expandAll();
            }
        });

        getTrainsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(from == null) {
                    Toast.makeText(getActivity(), "Select from is mandatory", Toast.LENGTH_SHORT).show() ;
                    return ;
                }

                if(from.equals(to)) {
                    Toast.makeText(getActivity(), "Source and Destination could not be same", Toast.LENGTH_SHORT).show() ;
                    return ;
                }

                if(InternetChecking.isNetWorkOn(getActivity())) {

                    /*liveStationTask     =   new LiveStationTask(getActivity(), from , to, time, LiveStationsFragment.this) ;
                    liveStationTask.execute() ;*/

                    liveStationTask = new LiveStationTaskNew(getActivity(), from, to, time, LiveStationsFragment.this) ;
                    liveStationTask.execute() ;

                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = (RadioButton) group.findViewById(checkedId) ;
                time = button.getText().toString().charAt(0)+"" ;
            }
        });


        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                from = null ;
                to = null ;
                time = "2";
                twoHR.setChecked(true);
                viaStationCode.setText("Via");
                toStationCode.setText("To");
                viaStationName.setText("");
                toStationName.setText("");
                toIcon.setImageResource(R.drawable.shape_logo);
                viaIcon.setImageResource(R.drawable.shape_logo);
            }
        });


        viaClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listFlag = FROM ;
                openStationList();
            }
        });


        toClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from == null) {
                    Toast.makeText(getActivity(), "Select from first", Toast.LENGTH_SHORT).show() ;
                    return ;
                }
                listFlag = TO ;
                openStationList();
            }
        });


        closeStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeStationlist();
            }
        });
    }


    private void setTypeface() {

        viaStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        toStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        trainInNext.setTypeface(AppFonts.getRobotoLight(getActivity()));

        twoHR.setTypeface(AppFonts.getRobotoLight(getActivity()));
        fourHR.setTypeface(AppFonts.getRobotoLight(getActivity()));

        getTrainsButton.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        clearButton.setTypeface(AppFonts.getRobotoReguler(getActivity()));
    }

    private void refreshAdapterOnStationList() {

        data = UtilsMethods.collectStations(getActivity());
        listAdapter = new StationNameCodeAdapter(getActivity(), data);
        allStationListView.setAdapter(listAdapter);
        expandAll();
    }

    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            allStationListView.expandGroup(i);
        }
    }

    private void openStationList() {

        MainActivity.toolbar.setVisibility(View.GONE);

        stationListTitle.setText("Select Station");
        stationListTitle.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        searchView.setText("");
        listAdapter.filterData("");

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
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.live_station).toUpperCase());

        stationChooserLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        stationChooserLayout.setAnimation(animation);
        stationChooserLayout.animate();
        animation.start();
    }


    @Override
    public void updateException(String exception) {

        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show() ;
    }


    @Override
    public void updateLiveStationUI(LiveStationNewBean bean) {
        comm.respond(new LiveStationToNextFrag(bean, selectedViaName, time, getActivity().getResources().getString(R.string.live_station)));
    }

    @Override
    public boolean closeEveryThing() {
        if (stationChooserLayout.getVisibility() == View.VISIBLE) {
            closeStationlist();
            return false;
        }

        return true;
    }
}
