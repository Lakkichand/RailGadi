package com.railgadi.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.LiveTrainRecentSearchAdapter;
import com.railgadi.adapters.LiveTrainStationAdapter;
import com.railgadi.async.AutoSuggestionTask;
import com.railgadi.async.LiveTrainNewTask;
import com.railgadi.async.MiniTimeTableTask;
import com.railgadi.beans.LiveTrainNewBean;
import com.railgadi.beans.MiniTimeTableBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.beans.TrainDataBean;
import com.railgadi.customUi.CustomAutoCompleteView;
import com.railgadi.dbhandlers.TrainDBHandler;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IAutoSuggestion;
import com.railgadi.interfaces.ICloseEverything;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.ILiveTrainInterface;
import com.railgadi.interfaces.IPnrDeleteCommunicator;
import com.railgadi.interfaces.IStationsViaTrainNumber;
import com.railgadi.preferences.LiveTrainHistoryPreference;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LiveTrainInputFragment extends Fragment implements IPnrDeleteCommunicator, ILiveTrainInterface, ICloseEverything, IAutoSuggestion, IStationsViaTrainNumber {

    private View rootView;

    private IFragReplaceCommunicator comm;

    private MiniTimeTableBean miniTimeTableBean;


    private MiniTimeTableTask miniTimeTableTask;
    private AutoSuggestionTask autoSuggestionTask;
    private LiveTrainNewTask liveTrainNewTask;

    private LiveTrainStationAdapter stationAdapter ;
    private LiveTrainRecentSearchAdapter recentAdapter ;

    private LiveTrainHistoryPreference historyPreference ;

    // views
    private CustomAutoCompleteView enterTrainNumberEditText;
    private LinearLayout superLayout, stationClickLayout, dateClickLayout, hiddenStationLayout;
    private ListView stationList;
    private TextView stationTitle, stationSelectedTextView;
    private TextView stationCross;
    private LinearLayout autoSearchLayout ;
    private TextView showStatusButton, today, yesterday, tomorrow;

    private ArrayAdapter<String> autoAdapter;

    private String selectedStationCode;
    private String selectedStationName;

    private TextView recentSearchTextView ;
    private ListView recentSearchListView ;
    private LinearLayout recentSearchLayout ;

    //private int selectedDay, selectedMonth, selectedYear ;
    private Date selectedDate;

    private Map<String, Date> runningMap;

    private int longClickedPosition  ;

    private Map<String, String> trainNameNumberMap ;

    private String trainNumber;
    private String [] suggestionArray = {"please search..."} ;
    private List<TrainDataBean> allSuggestionList ;
    private TrainDBHandler trainDBHandler ;
    private ArrayAdapter autoSuggestionAdapter ;



    public LiveTrainInputFragment() {

    }

    public LiveTrainInputFragment(Map<String, String> trainNumberName) {

        this.trainNameNumberMap =   trainNumberName ;
    }


    @Override
    public void onPause() {
        super.onPause();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        if (autoSuggestionTask != null) {
            autoSuggestionTask.cancel(true);
        }
        if (miniTimeTableTask != null) {
            miniTimeTableTask.cancel(true);
        }
        if (liveTrainNewTask != null) {
            liveTrainNewTask.cancel(true);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.live_trains_input_layout, container, false);

        comm = (IFragReplaceCommunicator) getActivity();

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.live_trains).toUpperCase());

        MainActivity.closeFragmentObject = LiveTrainInputFragment.this;

        historyPreference = new LiveTrainHistoryPreference(getActivity()) ;

        initializeAllViews();
        setDataOnViews();
        setTypeFace();

        if(trainNameNumberMap != null && trainNameNumberMap.size() > 0) {
            startPreGettingStations() ;
        }

        return rootView ;
    }


    private void initializeAllViews() {


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

                autoSuggestionTask = new AutoSuggestionTask(getActivity(), searchTerm, LiveTrainInputFragment.this);
                autoSuggestionTask.execute();
            }
        });

        enterTrainNumberEditText = (CustomAutoCompleteView) rootView.findViewById(R.id.enter_train_number_edittext);
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

                    if (InternetChecking.isNetWorkOn(getActivity())) {

                        miniTimeTableTask = new MiniTimeTableTask(getActivity(), trainNumber, LiveTrainInputFragment.this);
                        miniTimeTableTask.execute();
                    } else {

                        InternetChecking.noInterNetToast(getActivity());
                    }
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(enterTrainNumberEditText.getWindowToken(), 0);
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



        /*enterTrainNumberEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enterTrainNumberEditText.setText("");
                return true ;
            }
        });
        enterTrainNumberEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String trainNumber = autoAdapter.getItem(position).toString();
                trainNumber = trainNumber.substring(0, 5);

                if (trainNumber.length() == 5) {

                    if (InternetChecking.isNetWorkOn(getActivity())) {

                        miniTimeTableTask = new MiniTimeTableTask(getActivity(), trainNumber, LiveTrainInputFragment.this);
                        miniTimeTableTask.execute();
                    } else {

                        InternetChecking.noInterNetToast(getActivity());
                    }
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(enterTrainNumberEditText.getWindowToken(), 0);
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
                autoSuggestionTask = new AutoSuggestionTask(getActivity(), s.toString(), LiveTrainInputFragment.this);
                autoSuggestionTask.execute();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        superLayout = (LinearLayout) rootView.findViewById(R.id.super_layout);
        stationClickLayout = (LinearLayout) rootView.findViewById(R.id.station_click_layout);
        dateClickLayout = (LinearLayout) rootView.findViewById(R.id.date_click_layout);
        hiddenStationLayout = (LinearLayout) rootView.findViewById(R.id.hidden_station_layout);

        stationList = (ListView) rootView.findViewById(R.id.station_list);

        stationCross = (TextView) rootView.findViewById(R.id.station_cross);
        stationCross.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        stationSelectedTextView = (TextView) rootView.findViewById(R.id.station_selected);
        stationTitle = (TextView) rootView.findViewById(R.id.station_list_title);

        today = (TextView) rootView.findViewById(R.id.today);
        yesterday = (TextView) rootView.findViewById(R.id.yesterday);
        tomorrow = (TextView) rootView.findViewById(R.id.tomorrow);

        showStatusButton = (TextView) rootView.findViewById(R.id.show_live_train_status_button);

        recentSearchLayout = (LinearLayout) rootView.findViewById(R.id.recent_search_layout) ;
        recentSearchListView = (ListView) rootView.findViewById(R.id.recent_search_live_train_list) ;
        recentSearchTextView = (TextView) rootView.findViewById(R.id.recent_search_textview) ;

        recentSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MiniTimeTableBean bean = (MiniTimeTableBean) recentAdapter.getItem(position);
                trainNumber = bean.getTrainNumber().trim() ;
                enterTrainNumberEditText.setText(bean.getTrainNumber()+"-"+bean.getTrainName()) ;
                autoSearchLayout.setVisibility(View.GONE);
                updateMiniTimetable(bean) ;
            }
        });

        recentSearchListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                longClickedPosition = position ;
                UtilsMethods.confirmDeleteDialog(getActivity(), LiveTrainInputFragment.this);
                return true ;
            }
        });

        setAdapterOnRecent() ;
    }


    public void startPreGettingStations() {

        for(Map.Entry<String, String> entry : trainNameNumberMap.entrySet()) {

            trainNumber = entry.getKey().trim() ;

            if (trainNumber.length() == 5) {

                enterTrainNumberEditText.setText(trainNumber+"-"+entry.getValue());

                if (InternetChecking.isNetWorkOn(getActivity())) {

                    miniTimeTableTask = new MiniTimeTableTask(getActivity(), trainNumber, LiveTrainInputFragment.this);
                    miniTimeTableTask.execute();
                } else {

                    InternetChecking.noInterNetToast(getActivity());
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(enterTrainNumberEditText.getWindowToken(), 0);
            }

            break ;
        }
    }


    private void getAutoSuggestion(String userInput) {

        if(userInput.toString().length() < 1) {
            autoSearchLayout.setVisibility(View.GONE);
            return ;
        }

        if(trainDBHandler == null) {
            trainDBHandler = new TrainDBHandler(getActivity()) ;
        }

        List<TrainDataBean> suggestionList = trainDBHandler.getSearchedItemList(userInput.toString()) ;
        updateAutoSuggestion(suggestionList);
    }



    @Override
    public void wantToDelete(boolean flag) {

        if(flag) {

            MiniTimeTableBean bean = (MiniTimeTableBean) recentAdapter.getItem(longClickedPosition);
            historyPreference.removeFromPreferences(bean.getTrainNumber());
            setAdapterOnRecent();
        }
    }


    private void setAdapterOnRecent() {

        List<MiniTimeTableBean> allSavedQueries = historyPreference.getAllSavedTimeTable() ;

        if(allSavedQueries != null && allSavedQueries.size() > 0) {

            recentAdapter       =   new LiveTrainRecentSearchAdapter(getActivity(), allSavedQueries) ;
            recentSearchListView.setAdapter(recentAdapter) ;
            recentSearchLayout.setVisibility(View.VISIBLE);
        }
        else {
            recentSearchLayout.setVisibility(View.GONE);
        }

    }


    private void setDataOnViews() {

        stationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MiniTimeTableBean.TrainRoute route = (MiniTimeTableBean.TrainRoute) stationAdapter.getItem(position);

                selectedStationCode = route.stationCode;

                stationSelectedTextView.setText(route.stationCode + " - " + route.stationName);

                runningMap = UtilsMethods.getRunningInstances(route.day, miniTimeTableBean.getRunningDays());

                updateRunningInstanceUI(runningMap);

                closeStationLayout();
            }
        });

        stationClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(trainNumber == null || trainNumber.isEmpty()) {
                    Toast.makeText(getActivity(), "Please Select Train", Toast.LENGTH_LONG).show() ;
                    return ;
                }
                openStationLayout();
            }
        });

        stationCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeStationLayout();
            }
        });

        stationTitle.setText(Html.fromHtml("<small>Stations</small>"));

        showStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedStationCode == null) {
                    Toast.makeText(getActivity(), "Please select station", Toast.LENGTH_SHORT).show() ;
                    return ;
                }
                if(selectedDate == null) {
                    return ;
                }

                if (InternetChecking.isNetWorkOn(getActivity())) {

                    if(trainNumber != null && trainNumber.length() == 5) {
                        liveTrainNewTask = new LiveTrainNewTask(getActivity(), trainNumber, selectedStationCode, selectedDate, miniTimeTableBean, LiveTrainInputFragment.this);
                        liveTrainNewTask.execute();
                    } else {
                        Toast.makeText(getActivity(), "Train Number is Not correct", Toast.LENGTH_SHORT).show() ;
                    }
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                yesterday.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                tomorrow.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                yesterday.setTextColor(getActivity().getResources().getColor(R.color.black));
                tomorrow.setTextColor(getActivity().getResources().getColor(R.color.black));
                today.setTextColor(getActivity().getResources().getColor(R.color.white));
                today.setBackground(getActivity().getResources().getDrawable(R.drawable.find_train_button_selector));

                selectedDate = runningMap.get(Constants.TODAY);
            }
        });

        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                today.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                tomorrow.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                today.setTextColor(getActivity().getResources().getColor(R.color.black));
                tomorrow.setTextColor(getActivity().getResources().getColor(R.color.black));
                yesterday.setTextColor(getActivity().getResources().getColor(R.color.white));
                yesterday.setBackground(getActivity().getResources().getDrawable(R.drawable.find_train_button_selector));

                selectedDate = runningMap.get(Constants.YESTERDAY);

            }
        });

        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                today.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                yesterday.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                today.setTextColor(getActivity().getResources().getColor(R.color.black));
                yesterday.setTextColor(getActivity().getResources().getColor(R.color.black));
                tomorrow.setTextColor(getActivity().getResources().getColor(R.color.white));
                tomorrow.setBackground(getActivity().getResources().getDrawable(R.drawable.find_train_button_selector));

                selectedDate = runningMap.get(Constants.TOMORROW);
            }
        });
    }


    private void setTypeFace() {

        enterTrainNumberEditText.setTypeface(AppFonts.getRobotoThin(getActivity()));
        stationSelectedTextView.setTypeface(AppFonts.getRobotoLight(getActivity()));
    }


    public void updateRunningInstanceUI(Map<String, Date> runningInstanceMap) {

        if (runningInstanceMap == null || runningInstanceMap.size() == 0) {
            Toast.makeText(getActivity(), "No running instances", Toast.LENGTH_SHORT).show();
            selectedDate = null ;
            dateClickLayout.setVisibility(View.GONE);
            today.setVisibility(View.GONE);
            tomorrow.setVisibility(View.GONE);
            yesterday.setVisibility(View.GONE);
            return;
        }

        dateClickLayout.setVisibility(View.VISIBLE);

        Set<String> keySet = runningInstanceMap.keySet();
        if (keySet.contains(Constants.TODAY)) {
            today.setText(Constants.TODAY);
            today.setVisibility(View.VISIBLE);
        }
        if (keySet.contains(Constants.TOMORROW)) {
            tomorrow.setText(Constants.TOMORROW);
            tomorrow.setVisibility(View.VISIBLE);
        }
        if (keySet.contains(Constants.YESTERDAY)) {
            yesterday.setText(Constants.YESTERDAY);
            yesterday.setVisibility(View.VISIBLE);
        }

        selectedDate = runningInstanceMap.get(Constants.TODAY);
    }


    private void openStationLayout() {

        MainActivity.toolbar.setVisibility(View.GONE);

        superLayout.setVisibility(View.GONE);
        hiddenStationLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        hiddenStationLayout.setAnimation(animation);
        hiddenStationLayout.animate();
        animation.start();
    }


    private void closeStationLayout() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);

        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.live_trains).toUpperCase());

        hiddenStationLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        hiddenStationLayout.setAnimation(animation);
        hiddenStationLayout.animate();
        animation.start();
    }


    private void updateStationList(List<MiniTimeTableBean.TrainRoute> routeList) {

        // clicking true
        stationClickLayout.setClickable(true);
        dateClickLayout.setClickable(true);

        stationAdapter = new LiveTrainStationAdapter(getActivity(), routeList);
        stationList.setAdapter(stationAdapter);

        /*selectedStationCode = routeList.get(0).stationCode;

        selectedStationName = routeList.get(0).stationName;

        stationSelectedTextView.setText(selectedStationName);*/

    }


    /*private void updateDateViews(String selectedStationCode) {

        allDates    =   trainData.getDates(selectedStationCode) ;

        today.setVisibility(View.GONE);
        yesterday.setVisibility(View.GONE);
        tomorrow.setVisibility(View.GONE);
        TextView [] arr = {today, yesterday, tomorrow} ;

        for(int i=0 ; i<allDates.size() ; i++) {
            String date = DateAndMore.formatDateToString(allDates.get(i), DateAndMore.DAY_DATE) ;
            arr[i].setText(date);
            arr[i].setVisibility(View.VISIBLE);
            if(i==0) {
                arr[i].setBackground(getActivity().getResources().getDrawable(R.drawable.find_train_button_selector));
                selectedDatePosition = 0 ;
            }
        }
    }*/


    public void updateLiveTrainData(LiveTrainNewBean liveTrainNewBean, TimeTableNewBean ttb) {

        if(liveTrainNewBean.getLastStation() == null) {
            updateException("Train is not yet started");
        } else {
            enterTrainNumberEditText.setText("");

            historyPreference.saveTimeTableToPref(this.miniTimeTableBean) ;

            if(trainNameNumberMap != null) {
                trainNameNumberMap.clear();
            }

            selectedStationCode = null ;

            comm.respond(new LiveTrainTabsFragment(liveTrainNewBean, ttb));
        }
    }


    @Override
    public void updateException(String errMsg) {
        Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void updateMiniTimetable(MiniTimeTableBean bean) {

        List<MiniTimeTableBean.TrainRoute> routeList = bean.getTrainRouteList();
        Collections.sort(routeList);
        updateStationList(routeList);
        this.miniTimeTableBean = bean;



        /*MiniTimeTableBean.TrainRoute route = (MiniTimeTableBean.TrainRoute) stationAdapter.getItem(0);

        selectedStationCode = route.stationCode;

        stationSelectedTextView.setText(route.stationCode + " - " + route.stationName);

        runningMap = UtilsMethods.getRunningInstances(route.day, miniTimeTableBean.getRunningDays());

        updateRunningInstanceUI(runningMap);*/

        closeStationLayout();
    }


    @Override
    public void updateAutoSuggestion(List<TrainDataBean> suggestionList) {

        try {

            if(suggestionList == null || suggestionList.size() < 1) {
                enterTrainNumberEditText.setAdapter(null) ;
                autoSearchLayout.setVisibility(View.VISIBLE);
                enterTrainNumberEditText.dismissDropDown();
                return ;
            }

            autoSearchLayout.setVisibility(View.GONE);

            allSuggestionList = suggestionList ;

            suggestionArray = new String[allSuggestionList.size()] ;

            for(int i=0 ; i<allSuggestionList.size() ; i++) {
                TrainDataBean bean = allSuggestionList.get(i) ;
                suggestionArray[i] =  bean.getTrainNumber()+" - "+bean.getTrainName() ;
            }

            // update the adapater
            autoSuggestionAdapter.notifyDataSetChanged();
            autoSuggestionAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, suggestionArray);
            enterTrainNumberEditText.setAdapter(autoSuggestionAdapter);
            autoSuggestionAdapter.notifyDataSetChanged();
            enterTrainNumberEditText.showDropDown();

        } catch (Exception e) {

        }
    }


    @Override
    public void updateAutoSuggestionException(String exception) {
        enterTrainNumberEditText.setAdapter(null);
        autoSearchLayout.setVisibility(View.GONE);
    }


    @Override
    public boolean closeEveryThing() {

        if (hiddenStationLayout.getVisibility() == View.VISIBLE) {
            closeStationLayout();
            return false;
        }

        return true;
    }
}
