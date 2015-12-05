package com.railgadi.fragments;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.AlarmStationListAdapter;
import com.railgadi.async.AutoSuggestionTask;
import com.railgadi.async.GetTimeTableTaskNew;
import com.railgadi.beans.AlarmDataBean;
import com.railgadi.beans.RouteChildBean;
import com.railgadi.beans.RouteGroupBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.beans.TrainDataBean;
import com.railgadi.customUi.CustomAutoCompleteView;
import com.railgadi.dbhandlers.TrainDBHandler;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IAutoSuggestion;
import com.railgadi.interfaces.ICloseEverything;
import com.railgadi.interfaces.IGettingTimeTableComm;
import com.railgadi.preferences.AlarmPreference;
import com.railgadi.serviceAndReceivers.AlarmServices;
import com.railgadi.serviceAndReceivers.StartAlarmReceiver;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddAlarmFragment extends Fragment implements ICloseEverything, IGettingTimeTableComm, IAutoSuggestion {

    private View rootView;

    private MenuItem saveAlarm;

    private AlarmStationListAdapter adapter;

    private AutoSuggestionTask autoSuggestionTask ;
    private GetTimeTableTaskNew getTimeTableTaskNew ;

    // views
    private TextView selectCityText, code, name, beforeTime, selectRangeText, km, listTitle;
    private TextView alarmNote, aboutAlarm;
    private ImageView cityIcon;
    private TextView listClose  ;
    private LinearLayout autoSearchLayout ;
    private SeekBar distanceSeekbar;
    private LinearLayout stationLayout, superLayout, cityClickLayout;
    private CustomAutoCompleteView enterTrainNumberEditText;
    private ListView stationListview;

    private String selectedStationCode, selectedStationName ;

    private RouteChildBean selectedStationEntry;
    private double alarmDistance;

    private List<RouteChildBean> mapEntryBeanList;


    private String trainNumber;
    private String [] suggestionArray = {"please search..."} ;
    private List<TrainDataBean> allSuggestionList ;
    private TrainDBHandler trainDBHandler ;
    private ArrayAdapter autoSuggestionAdapter ;


    public AddAlarmFragment(String trainNumber, List<RouteChildBean> allStationList) {
        this.trainNumber = trainNumber;
        this.mapEntryBeanList = allStationList;
    }


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(autoSuggestionTask != null) {
            autoSuggestionTask.cancel(true) ;
        }
        if(getTimeTableTaskNew != null) {
            getTimeTableTaskNew.cancel(true) ;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.add_alarm_fragment, container, false);

        MainActivity.closeFragmentObject = AddAlarmFragment.this;

        MainActivity.toolbar.setTitle(Html.fromHtml("Add Alarm".toUpperCase()));

        initializeAllViews();
        setDataOnViews();
        setTypeface();

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.route_map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        saveAlarm = menu.getItem(0);
        saveAlarm.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (selectedStationEntry != null) {
                    saveAlarm(selectedStationEntry, alarmDistance);
                } else {
                    Toast.makeText(getActivity(), "Select Station", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void saveAlarm(RouteChildBean selectedStationEntry, double alarmDistance) {

        AlarmPreference ap = new AlarmPreference(getActivity());

        AlarmDataBean adb = new AlarmDataBean();

        adb.setTrainNumber(trainNumber);
        adb.setStationCode(selectedStationCode);
        adb.setStationName(selectedStationName);
/*

        Date instanceDate = new Date() ;

        switch(selectedStationEntry.getDay()) {

            case "2" : {
                instanceDate    =   UtilsMethods.addRemoveDayFromDate(instanceDate, 1) ;
                break ;
            }
            case "3" : {
                instanceDate    =   UtilsMethods.addRemoveDayFromDate(instanceDate, 2) ;
                break ;
            }
            case "4" : {
                instanceDate    =   UtilsMethods.addRemoveDayFromDate(instanceDate, 3) ;
                break ;
            }
            case "5" : {
                instanceDate    =   UtilsMethods.addRemoveDayFromDate(instanceDate, 4) ;
                break ;
            }
        }
*/

        Date instanceDate = UtilsMethods.addRemoveDayFromDate(new Date(), (Integer.parseInt(selectedStationEntry.getDay())-1)) ;

        Calendar cal = Calendar.getInstance();
        cal.setTime(instanceDate);
        cal.add(Calendar.HOUR, -1);
        instanceDate = cal.getTime();

        adb.setArrivalDateTime(instanceDate);
        adb.setLatitude(Double.parseDouble(selectedStationEntry.getLatitude()));
        adb.setLongitude(Double.parseDouble(selectedStationEntry.getLongitude()));
        adb.setCompleted(false);
        if (alarmDistance == 0.0) {
            adb.setDistance(0.500);
        } else {
            adb.setDistance(alarmDistance);
        }

        LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertToOnGPS();
        } else {

            boolean isSaved = ap.saveAlarm(adb) ;
            if(isSaved) {
                if(! isMyServiceRunning(AlarmServices.class)) {

                    AlarmServices.playAlarm = false ;
                    getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(), AlarmServices.class)) ;

                    if(enterTrainNumberEditText.isEnabled()) {
                        Toast.makeText(getActivity().getApplicationContext(), "Alarm Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Alarm Updated", Toast.LENGTH_SHORT).show();
                    }

                    //getActivity().getApplicationContext().startService(new Intent(getActivity().getApplicationContext(), AlarmServices.class));

                    startAlarm(adb) ;

                } else {
                    AlarmServices.changeLatLng();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Alarm not saved .. Try again", Toast.LENGTH_SHORT).show() ;
            }
        }
    }



    private void startAlarm(AlarmDataBean adb) {

        try {

            Intent alarmIntent = new Intent(getActivity(), StartAlarmReceiver.class) ;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, 0) ;

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(adb.getArrivalDateTime());

            //SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm") ;
            //Date date = format.parse("04-11-2015 10:50") ;
            calendar.setTime(adb.getArrivalDateTime());
            //calendar.setTime(date);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        } catch( Exception e ){

        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void alertToOnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void initializeAllViews() {

        selectCityText = (TextView) rootView.findViewById(R.id.select_city_text);
        code = (TextView) rootView.findViewById(R.id.station_code);
        name = (TextView) rootView.findViewById(R.id.station_name);
        beforeTime = (TextView) rootView.findViewById(R.id.before_time);
        selectRangeText = (TextView) rootView.findViewById(R.id.select_range);
        km = (TextView) rootView.findViewById(R.id.km);
        alarmNote = (TextView) rootView.findViewById(R.id.about_alarm_note);
        aboutAlarm = (TextView) rootView.findViewById(R.id.about_alarm);

        listTitle = (TextView) rootView.findViewById(R.id.station_list_title);

        distanceSeekbar = (SeekBar) rootView.findViewById(R.id.distance_seekbar);

        cityIcon = (ImageView) rootView.findViewById(R.id.city_icon);
        listClose = (TextView) rootView.findViewById(R.id.close_station);
        listClose.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        superLayout = (LinearLayout) rootView.findViewById(R.id.super_layout);
        stationLayout = (LinearLayout) rootView.findViewById(R.id.station_chooser_layout);
        cityClickLayout = (LinearLayout) rootView.findViewById(R.id.city_click_layout);

        stationListview = (ListView) rootView.findViewById(R.id.station_list_view);

        enterTrainNumberEditText = (CustomAutoCompleteView) rootView.findViewById(R.id.enter_train_number_edittext);

        autoSearchLayout = (LinearLayout) rootView.findViewById(R.id.auto_search_layout) ;
        autoSearchLayout.setVisibility(View.GONE);
        autoSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchTerm = enterTrainNumberEditText.getText().toString() ;
                if(searchTerm == null || searchTerm.length() < 1){
                    Toast.makeText(getActivity(), "Please enter text", Toast.LENGTH_LONG).show() ;
                    return ;
                }

                if(autoSuggestionTask != null) {
                    autoSuggestionTask.cancel(true) ;
                    autoSuggestionTask = null ;
                }

                autoSuggestionTask = new AutoSuggestionTask(getActivity(), searchTerm, AddAlarmFragment.this);
                autoSuggestionTask.execute();
            }
        });
    }


    private void setDataOnViews() {

        km.setText("10 km");

        listTitle.setText(getActivity().getResources().getString(R.string.select_station));
        listTitle.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        cityClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mapEntryBeanList != null) {
                    openStationList();
                } else {
                    Toast.makeText(getActivity(), "Enter Train Number First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeStationList();
            }
        });

        distanceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alarmDistance = progress;

                if (progress == 0) {
                    km.setText("On Station");
                    beforeTime.setText("On Station");
                } else {
                    km.setText(progress + " km");
                    beforeTime.setText("Before: " + progress + " km");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        stationListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedStationEntry = mapEntryBeanList.get(position);

                selectedStationCode = selectedStationEntry.getStationCode();
                selectedStationName = selectedStationEntry.getStationName();

                code.setText(selectedStationCode);
                name.setText(selectedStationName);

                closeStationList();
            }
        });

        if (trainNumber != null && mapEntryBeanList != null) {

            enterTrainNumberEditText.setEnabled(false);
            enterTrainNumberEditText.setText(trainNumber);

            selectedStationEntry = mapEntryBeanList.get(0);
            selectedStationCode = selectedStationEntry.getStationCode();
            selectedStationName = selectedStationEntry.getStationName();

            setAdapterOnList(mapEntryBeanList);
        } else {
            addTextWatcher();
        }
    }

    private void addTextWatcher() {

        enterTrainNumberEditText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        enterTrainNumberEditText.setFocusable(false);
        enterTrainNumberEditText.setAdapter(null);
        enterTrainNumberEditText.setThreshold(1);

        autoSuggestionAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, suggestionArray);
        enterTrainNumberEditText.setAdapter(autoSuggestionAdapter);

        enterTrainNumberEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enterTrainNumberEditText.setText("");
                trainNumber = null ;
                return true ;
            }
        });

        enterTrainNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                enterTrainNumberEditText.setFocusableInTouchMode(true);
                return false;
            }
        });

        enterTrainNumberEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                trainNumber = allSuggestionList.get(position).getTrainNumber();
                autoSearchLayout.setVisibility(View.GONE);

                if (trainNumber != null && trainNumber.length() == 5) {

                    if(InternetChecking.isNetWorkOn(getActivity())) {
                        getTimeTableTaskNew =   new GetTimeTableTaskNew(getActivity(), trainNumber, AddAlarmFragment.this) ;
                        getTimeTableTaskNew.execute() ;
                    }
                }
            }
        });

        enterTrainNumberEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {


                getAutoSuggestion(userInput.toString());

            }
        });


        /*enterTrainNumberEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTrain = suggestionEntries.get(position).getKey();
                if(InternetChecking.isNetWorkOn(getActivity())) {
                    getTimeTableTaskNew =   new GetTimeTableTaskNew(getActivity(), selectedTrain, AddAlarmFragment.this) ;
                    getTimeTableTaskNew.execute() ;
                }
            }
        });
        enterTrainNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                enterTrainNumberEditText.setFocusableInTouchMode(true);
                return false;
            }
        });
        enterTrainNumberEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (autoSuggestionTask != null) {
                    autoSuggestionTask.cancel(true);
                    autoSuggestionTask = null;
                }

                autoSuggestionTask = new AutoSuggestionTask(getActivity(), s.toString(), AddAlarmFragment.this);
                autoSuggestionTask.execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }


    private void getAutoSuggestion(String userInput) {

        if(userInput.toString().length() < 1) {
            autoSearchLayout.setVisibility(View.GONE);
            trainNumber = null ;
            return ;
        }

        if(trainDBHandler == null) {
            trainDBHandler = new TrainDBHandler(getActivity()) ;
        }

        List<TrainDataBean> suggestionList = trainDBHandler.getSearchedItemList(userInput.toString()) ;
        updateAutoSuggestion(suggestionList) ;
    }



    private void setAdapterOnList(List<RouteChildBean> data) {

        if(adapter != null) {
            adapter.changeDataSet(data) ;
        } else {
            adapter = new AlarmStationListAdapter(getActivity(), data);
            stationListview.setAdapter(adapter);
        }
    }


    @Override
    public void updateAutoSuggestion(List<TrainDataBean> suggestionEntries) {

        try {

            if(suggestionEntries == null || suggestionEntries.size() < 1) {
                enterTrainNumberEditText.setAdapter(null) ;
                autoSearchLayout.setVisibility(View.VISIBLE);
                enterTrainNumberEditText.dismissDropDown();
                return ;
            }

            autoSearchLayout.setVisibility(View.GONE);

            allSuggestionList = suggestionEntries;
            suggestionArray = new String[allSuggestionList.size()];

            for (int i = 0; i < allSuggestionList.size(); i++) {
                TrainDataBean bean = allSuggestionList.get(i);
                suggestionArray[i] = bean.getTrainNumber() + " - " + bean.getTrainName();
            }

            // update the adapater
            autoSuggestionAdapter.notifyDataSetChanged();
            autoSuggestionAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, suggestionArray);
            enterTrainNumberEditText.setAdapter(autoSuggestionAdapter);
            autoSuggestionAdapter.notifyDataSetChanged();
            enterTrainNumberEditText.showDropDown();

        } catch( Exception e ) {

        }

    }


    @Override
    public void updateAutoSuggestionException(String exception) {
        enterTrainNumberEditText.setAdapter(null);
        autoSearchLayout.setVisibility(View.GONE);
    }


    private void setTypeface() {

        selectCityText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        code.setTypeface(AppFonts.getRobotoLight(getActivity()));
        name.setTypeface(AppFonts.getRobotoLight(getActivity()));
        beforeTime.setTypeface(AppFonts.getRobotoThin(getActivity()));
        selectRangeText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        km.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        enterTrainNumberEditText.setTypeface(AppFonts.getRobotoThin(getActivity()));
        listTitle.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        aboutAlarm.setTypeface(AppFonts.getRobotoLight(getActivity()));
        alarmNote.setTypeface(AppFonts.getRobotoReguler(getActivity()));
    }

    private void openStationList() {

        MainActivity.toolbar.setVisibility(View.GONE);

        superLayout.setVisibility(View.GONE);
        stationLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(100);
        stationLayout.setAnimation(animation);
        stationLayout.animate();
        animation.start();
    }

    private void closeStationList() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);

        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.station_alarm).toUpperCase());

        stationLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(100);
        stationLayout.setAnimation(animation);
        stationLayout.animate();
        animation.start();
    }


    @Override
    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void updateTimeTableUI(TimeTableNewBean bean) {

        List<RouteChildBean> childList       =   new ArrayList<>() ;
        for(RouteGroupBean rgb : bean.getGroupBeans()) {
            for(RouteChildBean cb : rgb.getChildList()) {
                childList.add(cb);
            }
        }

        mapEntryBeanList = childList;

        selectedStationEntry = childList.get(0);

        selectedStationCode = childList.get(0).getStationCode();
        selectedStationName = childList.get(0).getStationName();

        code.setText(selectedStationCode);
        name.setText(selectedStationName);

        setAdapterOnList(childList);
    }


    @Override
    public boolean closeEveryThing() {

        if (stationLayout.getVisibility() == View.VISIBLE) {
            closeStationList();
            return false;
        }
        return true;
    }
}
