package com.railgadi.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.OnlySeatAvlInputAdapter;
import com.railgadi.adapters.QuotaAdapter;
import com.railgadi.adapters.SeatAvlRecentAdapter;
import com.railgadi.async.AutoSuggestionTask;
import com.railgadi.async.FareSeatSingleTrainTask;
import com.railgadi.async.GetTimeTableTaskNew;
import com.railgadi.async.MiniTimeTableTask;
import com.railgadi.beans.FareEnqInput;
import com.railgadi.beans.SeatFareBeanNew;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.beans.TrainDataBean;
import com.railgadi.dbhandlers.TrainDBHandler;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IAutoSuggestion;
import com.railgadi.interfaces.ICloseEverything;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IGettingTimeTableComm;
import com.railgadi.interfaces.IPnrDeleteCommunicator;
import com.railgadi.interfaces.ISeatAvlFare;
import com.railgadi.preferences.SeatAvlPreferences;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.DateAndMore;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SeatAvailabilityFragment extends Fragment implements IPnrDeleteCommunicator, IAutoSuggestion, ISeatAvlFare, ICloseEverything, IGettingTimeTableComm {

    private View rootView;

    private IFragReplaceCommunicator comm;

    private FareSeatSingleTrainTask fareSeatSingleTrainTask;
    private AutoSuggestionTask autoSuggestionTask;
    private MiniTimeTableTask miniTimeTableTask;
    private SeatAvlPreferences seatAvlPreferences;

    private OnlySeatAvlInputAdapter stationAdapter;
    private SeatAvlRecentAdapter historyAdapter;

    // visibile views
    private AutoCompleteTextView enterTrainNumberEditText;
    private TextView srcCodeText, srcNameText, destCodeText, destNameText, dateText, dayText, littleCalendarText, quotaText;
    private TextView getAvailabilityButton, recentSearchedButton;
    private LinearLayout superLayout, selectFromLayout, selectToLayout, selectCalendarLayout, selectQuotaLayout, stationListLayoutHide, quotaLayoutHide;


    // hidden views
    private CalendarPickerView calendar_view;
    private TextView closeStationImageView, closeCalendarImageView, closeQuotaImageView, closeHistoryImageView;
    private ListView stopStationListView, quotaListView, historyListView;
    private TextView selectQuotaTitle, selectHistoryTitle;
    private LinearLayout hiddenStationLayout, hiddenHistoryLayout, hiddenCalendarLayout, hiddenQuotaLayout;
    private TextView currentDateMonthTitle, currentDateTitle ;
    private LinearLayout autoSearchLayout ;


    // variables
    private static final String FROM = "from_list";
    private static final String TO = "to_list";
    private static boolean isFromClick = false;
    private static boolean isToClick = false;

    private int longClickedPosition ;

    private static boolean fromListflag = false;

    private String[] all_quota_names;
    private String[] all_quota_codes;

    private String selectedQuota;
    private String selectedDate;
    private String selectedDay;
    private String selectedMonth;
    private String selectedYear;
    private String selectedFromCode;
    private String selectedFromName;
    private String selectedToCode;
    private String selectedToName;

    private List<TimeTableNewBean.AllStations> savedList;
    private List<TimeTableNewBean.AllStations> fromStationList, toStationList;
    private TimeTableNewBean savedTimeTable;
    private List<TimeTableNewBean> allSavedHistory;

    private List<Map.Entry<String, String>> suggestionEntries;
    private TimeTableNewBean longClickHistoryBean ;

    private String trainNumber;
    private String [] suggestionArray = {"please search..."} ;
    private List<TrainDataBean> allSuggestionList ;
    private TrainDBHandler trainDBHandler ;
    private ArrayAdapter autoSuggestionAdapter ;



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
        if (autoSuggestionTask != null) {
            autoSuggestionTask.cancel(true);
        }
        if (miniTimeTableTask != null) {
            miniTimeTableTask.cancel(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.seat_avl_fragment, container, false);

        comm = (IFragReplaceCommunicator) getActivity();

        MainActivity.closeFragmentObject = SeatAvailabilityFragment.this;

        fromListflag = false;

        seatAvlPreferences = new SeatAvlPreferences(getActivity());

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.seat_availability).toUpperCase());

        initializeAllViews();
        initializeCalendar();
        setDataOnViews();
        setTypeFaces();
        setHistoryAdapter();


        // default initializations
        selectedDate = DateAndMore.formatDateToString(new Date(), DateAndMore.DATE_WITH_SLASH);
        selectedQuota = null;

        return rootView;
    }


    public void setHistoryAdapter() {

        allSavedHistory = seatAvlPreferences.getAllSavedTimeTable();
        if (allSavedHistory != null && allSavedHistory.size() > 0) {

            historyAdapter = new SeatAvlRecentAdapter(getActivity(), allSavedHistory);
            historyListView.setAdapter(historyAdapter);
        } else {
            historyListView.setAdapter(null);
        }
    }


    public void initializeAllViews() {

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

                autoSuggestionTask = new AutoSuggestionTask(getActivity(), searchTerm, SeatAvailabilityFragment.this);
                autoSuggestionTask.execute();
            }
        });

        // visible views
        enterTrainNumberEditText = (AutoCompleteTextView) rootView.findViewById(R.id.enter_train_number_edittext);
        enterTrainNumberEditText.setText("");
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
                        GetTimeTableTaskNew getTimeTableTaskNew = new GetTimeTableTaskNew(getActivity(), trainNumber, SeatAvailabilityFragment.this);
                        getTimeTableTaskNew.execute();
                    } else {
                        enterTrainNumberEditText.setText("");
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
                selectedTrain = suggestionEntries.get(position).getKey();
                if (selectedTrain.toString().length() == 5) {

                    trainNumber = selectedTrain.toString();

                    if (InternetChecking.isNetWorkOn(getActivity())) {
                        *//*miniTimeTableTask   =   new MiniTimeTableTask(getActivity(), trainNumber, SeatAvailabilityFragment.this) ;
                        miniTimeTableTask.execute() ;*//*
                        GetTimeTableTaskNew getTimeTableTaskNew = new GetTimeTableTaskNew(getActivity(), trainNumber, SeatAvailabilityFragment.this);
                        getTimeTableTaskNew.execute();
                    } else {
                        enterTrainNumberEditText.setText("");
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

                autoSuggestionTask = new AutoSuggestionTask(getActivity(), s.toString(), SeatAvailabilityFragment.this);
                autoSuggestionTask.execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/


        srcCodeText = (TextView) rootView.findViewById(R.id.from_station_code_text);
        srcNameText = (TextView) rootView.findViewById(R.id.from_station_name_text);
        destCodeText = (TextView) rootView.findViewById(R.id.to_station_code_text);
        destNameText = (TextView) rootView.findViewById(R.id.to_station_name_text);
        dateText = (TextView) rootView.findViewById(R.id.journey_date_textview);
        dayText = (TextView) rootView.findViewById(R.id.journey_day_textview);
        littleCalendarText = (TextView) rootView.findViewById(R.id.little_calendar);
        quotaText = (TextView) rootView.findViewById(R.id.selected_quota_textview);
        selectQuotaTitle = (TextView) rootView.findViewById(R.id.select_quota_title);

        superLayout = (LinearLayout) rootView.findViewById(R.id.super_layout);
        selectFromLayout = (LinearLayout) rootView.findViewById(R.id.select_from_station_layout);
        selectToLayout = (LinearLayout) rootView.findViewById(R.id.select_to_station_layout);
        selectCalendarLayout = (LinearLayout) rootView.findViewById(R.id.select_calendar_layout);
        selectQuotaLayout = (LinearLayout) rootView.findViewById(R.id.open_select_quota_layout);

        getAvailabilityButton = (TextView) rootView.findViewById(R.id.find_train_button);
        recentSearchedButton = (TextView) rootView.findViewById(R.id.recent_history_textview);

        // hidden views
        hiddenStationLayout = (LinearLayout) rootView.findViewById(R.id.station_list_layout_hidden);
        hiddenCalendarLayout = (LinearLayout) rootView.findViewById(R.id.calendar_layout_hidden);
        hiddenQuotaLayout = (LinearLayout) rootView.findViewById(R.id.quota_layout_hidden);
        hiddenHistoryLayout = (LinearLayout) rootView.findViewById(R.id.history_hidden_layout);

        closeStationImageView = (TextView) rootView.findViewById(R.id.close_station_imageview);
        closeCalendarImageView = (TextView) rootView.findViewById(R.id.close_calendar_imageview);
        closeHistoryImageView = (TextView) rootView.findViewById(R.id.close_history_imageview);
        closeQuotaImageView = (TextView) rootView.findViewById(R.id.close_quota_imageview);
        closeStationImageView.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        closeCalendarImageView.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        closeQuotaImageView.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        stopStationListView = (ListView) rootView.findViewById(R.id.all_stop_station_list);
        quotaListView = (ListView) rootView.findViewById(R.id.all_quotas_listview);
        historyListView = (ListView) rootView.findViewById(R.id.all_recent_listview);

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


    public void setDataOnViews() {

        stopStationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isFromClick) {

                    TimeTableNewBean.AllStations ss = fromStationList.get(position);

                    selectedFromCode = ss.stnCode;
                    selectedFromName = ss.stnName;
                    srcCodeText.setText(selectedFromCode);
                    srcNameText.setText(selectedFromName);

                    toStationList = new ArrayList<>(savedList);

                    Collections.sort(toStationList);

                    List<TimeTableNewBean.AllStations> dummy = new ArrayList<>();

                    for (int i = (position + 1); i <= toStationList.size() - 1; i++) {
                        dummy.add(toStationList.get(i));
                    }

                    toStationList = dummy;
                    refreshToAdapter(toStationList);

                } else {

                    TimeTableNewBean.AllStations ss = toStationList.get(position);

                    selectedToCode = ss.stnCode;
                    selectedToName = ss.stnName;
                    destCodeText.setText(selectedToCode);
                    destNameText.setText(selectedToName);
                }

                closeStationList();
            }
        });

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimeTableNewBean saved = (TimeTableNewBean) historyAdapter.getItem(position);
                enterTrainNumberEditText.setText(saved.getTrainNumber() + " - " + saved.getTrainName());
                trainNumber = saved.getTrainNumber();
                autoSearchLayout.setVisibility(View.GONE);
                updateTimeTableUI(saved);
                closeRecentSearchedLayout();
            }
        });

        historyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                longClickHistoryBean = (TimeTableNewBean) historyAdapter.getItem(position) ;
                longClickedPosition = position ;
                UtilsMethods.confirmDeleteDialog(getActivity(), SeatAvailabilityFragment.this);
                return true ;
            }
        });

        quotaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedQuota = all_quota_codes[position];
                quotaText.setText(selectedQuota);
            }
        });

        getAvailabilityButton.setOnClickListener(new ClickOnGetAvailability());

        String[] nowDate = DateAndMore.formatDateToString(new Date(), DateAndMore.DAY_DATE_MONTH).split(",");
        dateText.setText(nowDate[1].trim());
        dayText.setText(nowDate[0].trim());
        littleCalendarText.setText(DateAndMore.formatDateToString(new Date(), DateAndMore.DATE_WITH_SLASH).substring(0, 2));

        SpannableString content = new SpannableString(getResources().getString(R.string.recent_searched));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        recentSearchedButton.setText(content);
        recentSearchedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (allSavedHistory != null && allSavedHistory.size() > 0) {
                    openRecentSearchedLayout();
                } else {
                    Toast.makeText(getActivity(), "No recent search found", Toast.LENGTH_LONG).show();
                }
            }
        });



        selectFromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStationList(FROM);
            }
        });

        selectToLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStationList(TO);
            }
        });

        selectQuotaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQuota();
            }
        });

        selectCalendarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar();
            }
        });


        closeStationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeStationList();
            }
        });

        closeHistoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeRecentSearchedLayout();
            }
        });

        closeQuotaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeQuota();
            }
        });

        closeCalendarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeCalendar();
            }
        });
    }


    private class ClickOnGetAvailability implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (selectedFromCode == null || selectedFromCode.equals("")) {
                Toast.makeText(getActivity(), "Select Source", Toast.LENGTH_SHORT).show();
                return;
            } else if (selectedToCode == null || selectedToCode.equals("")) {
                Toast.makeText(getActivity(), "Select Destination", Toast.LENGTH_SHORT).show();
                return;
            } else if (selectedDate == null || selectedDate.equals("")) {
                Toast.makeText(getActivity(), "Select Date of Journey", Toast.LENGTH_SHORT).show();
                return;
            } else if (selectedFromCode.equals(selectedToCode)) {
                Toast.makeText(getActivity(), "Source and Destination could not be same", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedDay = selectedDate.substring(0, 2);
            selectedMonth = selectedDate.substring(3, 5);
            selectedYear = selectedDate.substring(6, selectedDate.length());

            if (InternetChecking.isNetWorkOn(getActivity())) {

                FareEnqInput fei = new FareEnqInput();

                fei.setTrainNo(trainNumber);
                fei.setDay(selectedDay);
                fei.setMonth(selectedMonth);
                fei.setSrcCode(selectedFromCode);
                fei.setDestCode(selectedToCode);
                fei.setClassCode(savedTimeTable.getAvlClasses().get(savedTimeTable.getAvlClasses().size() - 1));
                fei.setQuota(selectedQuota);

                fareSeatSingleTrainTask = new FareSeatSingleTrainTask(getActivity(), fei, SeatAvailabilityFragment.this);
                fareSeatSingleTrainTask.execute();

            } else {
                enterTrainNumberEditText.setText("");
                //comm.respond(new NoInternetConnection());
                //InternetChecking.showNoInternetPopup(getActivity());
                InternetChecking.noInterNetToast(getActivity());
            }

        }
    }

    public void setTypeFaces() {

        enterTrainNumberEditText.setTypeface(AppFonts.getRobotoLight(getActivity()));

        srcCodeText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        srcNameText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        destCodeText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        destNameText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        dateText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        dayText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        quotaText.setTypeface(AppFonts.getRobotoLight(getActivity()));
    }


    public void openQuota() {

        selectQuotaTitle.setText(Html.fromHtml("<small>Select Quota</small>"));

        all_quota_names = getActivity().getResources().getStringArray(R.array.quota_names_array);
        all_quota_codes = getActivity().getResources().getStringArray(R.array.quota_codes_array);
        QuotaAdapter qAdapter = new QuotaAdapter(getActivity(), all_quota_codes, all_quota_names);
        quotaListView.setAdapter(qAdapter);

        quotaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedQuota = all_quota_codes[position];
                quotaText.setText(all_quota_codes[position]);
                closeQuota();
            }
        });

        // showing
        MainActivity.toolbar.setVisibility(View.GONE);

        superLayout.setVisibility(View.GONE);
        hiddenQuotaLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        hiddenQuotaLayout.setAnimation(animation);
        hiddenQuotaLayout.animate();
        animation.start();

    }

    public void closeQuota() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);
        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.search_train).toUpperCase());

        hiddenQuotaLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        hiddenQuotaLayout.setAnimation(animation);
        hiddenQuotaLayout.animate();
        animation.start();
    }


    private void openRecentSearchedLayout() {

        MainActivity.toolbar.setVisibility(View.GONE);

        superLayout.setVisibility(View.GONE);
        hiddenHistoryLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        hiddenHistoryLayout.setAnimation(animation);
        hiddenHistoryLayout.animate();
        animation.start();
    }

    private void closeRecentSearchedLayout() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);
        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.seat_availability).toUpperCase());

        hiddenHistoryLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        hiddenHistoryLayout.setAnimation(animation);
        hiddenHistoryLayout.animate();
        animation.start();
    }


    public void openStationList(String flag) {

        if (flag.equals(FROM)) {

            if (fromStationList != null) {

                fromListflag = true;
                isFromClick = true;
                isToClick = false;

                refreshFromAdapter(fromStationList);

                MainActivity.toolbar.setVisibility(View.GONE);

                superLayout.setVisibility(View.GONE);
                hiddenStationLayout.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
                animation.setDuration(Constants.STATION_ANIM_DURATION);
                hiddenStationLayout.setAnimation(animation);
                hiddenStationLayout.animate();
                animation.start();
            }
        } else if (flag.equals(TO)) {

            if (toStationList != null) {

                if (fromListflag) {

                    isToClick = true;
                    isFromClick = false;

                    refreshToAdapter(toStationList);

                    MainActivity.toolbar.setVisibility(View.GONE);

                    superLayout.setVisibility(View.GONE);
                    hiddenStationLayout.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
                    animation.setDuration(Constants.STATION_ANIM_DURATION);
                    hiddenStationLayout.setAnimation(animation);
                    hiddenStationLayout.animate();
                    animation.start();
                } else {
                    Toast.makeText(getActivity(), "Choose Source First", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void closeStationList() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);

        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.search_train).toUpperCase());

        hiddenStationLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        hiddenStationLayout.setAnimation(animation);
        hiddenStationLayout.animate();
        animation.start();
    }

    public void openCalendar() {

        MainActivity.toolbar.setVisibility(View.GONE);

        superLayout.setVisibility(View.GONE);
        hiddenCalendarLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        hiddenCalendarLayout.setAnimation(animation);
        hiddenCalendarLayout.animate();
        animation.start();
    }

    public void closeCalendar() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);
        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.seat_availability).toUpperCase());

        hiddenCalendarLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        hiddenCalendarLayout.setAnimation(animation);
        hiddenCalendarLayout.animate();
        animation.start();
    }

    public void initializeCalendar() {

        calendar_view = (CalendarPickerView) rootView.findViewById(R.id.calendar_view);
        currentDateMonthTitle = (TextView) rootView.findViewById(R.id.current_daymonth_titlebar);
        currentDateTitle = (TextView) rootView.findViewById(R.id.current_day_titlebar);

        Date currentDate = new Date();
        Date dateAf120 = DateAndMore.get120DaysAfterDate();

        calendar_view.init(currentDate, dateAf120)
                .inMode(CalendarPickerView.SelectionMode.SINGLE)
                .withSelectedDate(currentDate);

        calendar_view.setTypeface(AppFonts.getRobotoLight(getActivity()));

        String[] sdate = DateAndMore.formatDateToString(new Date(), DateAndMore.FULL_DAY_WITHOUT_TIME).split(",");
        currentDateMonthTitle.setText(sdate[1].trim());
        currentDateTitle.setText(sdate[0]);

        calendar_view.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {

                closeCalendar();

                String[] dateArr = DateAndMore.formatDateToString(date, DateAndMore.DAY_DATE_MONTH).split(",");
                dateText.setText(dateArr[1].trim());
                dayText.setText(dateArr[0].trim());

                MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.search_train).toUpperCase());

                selectedDate = DateAndMore.formatDateToString(date, DateAndMore.DATE_WITH_SLASH);
                littleCalendarText.setText(selectedDate.substring(0, 2));
                //Toast.makeText(getActivity(), selectedDate, Toast.LENGTH_SHORT).show() ;
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }


    private void refreshFromAdapter(List<TimeTableNewBean.AllStations> list) {

        stationAdapter = new OnlySeatAvlInputAdapter(getActivity(), list);
        stopStationListView.setAdapter(stationAdapter);
    }

    private void refreshToAdapter(List<TimeTableNewBean.AllStations> list) {

        stationAdapter = new OnlySeatAvlInputAdapter(getActivity(), list);
        stopStationListView.setAdapter(stationAdapter);
    }


    @Override
    public void updateTimeTableUI(TimeTableNewBean bean) {

        savedTimeTable = bean;
        savedList = bean.getAllStationsList();

        fromStationList = new ArrayList<>(bean.getAllStationsList());
        toStationList = new ArrayList<>(bean.getAllStationsList());

        //Collections.sort(fromStationList, new StationDistanceComparator());
        //Collections.sort(toStationList, new StationDistanceComparator());
        Collections.sort(fromStationList);
        Collections.sort(toStationList);


        TimeTableNewBean.AllStations start = fromStationList.get(0);
        TimeTableNewBean.AllStations end = fromStationList.get(fromStationList.size() - 1);

        selectedFromCode = start.stnCode;
        selectedFromName = start.stnName;
        selectedToCode = end.stnCode;
        selectedToName = end.stnName;

        srcCodeText.setText(selectedFromCode);
        srcNameText.setText(selectedFromName);
        destCodeText.setText(selectedToCode);
        destNameText.setText(selectedToName);

    }

    @Override
    public void updateException(String exceptionType) {

        Toast.makeText(getActivity(), exceptionType, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void updateSearchTrainUi(SeatFareBeanNew seatFareBeanNew, FareEnqInput fei) {

        enterTrainNumberEditText.setText("");

        selectedFromCode = null;
        selectedToCode = null;
        selectedDate = null;
        selectedDay = null;
        selectedMonth = null;
        selectedYear = null;
        fromStationList = null;
        toStationList = null;

        seatAvlPreferences.saveTimeTableToPref(savedTimeTable) ;
        comm.respond(new SeatAvlSingleTrainFragment(seatFareBeanNew, savedTimeTable, fei));
    }


    @Override
    public void wantToDelete(boolean flag) {
        if(flag) {
            historyAdapter.removeItem(longClickedPosition) ;
            seatAvlPreferences.removeFromPreferences(longClickHistoryBean.getTrainNumber()) ;
            closeRecentSearchedLayout();
        }
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

        if (hiddenCalendarLayout.getVisibility() == View.VISIBLE) {

            closeCalendar();
            return false;

        } else if (hiddenQuotaLayout.getVisibility() == View.VISIBLE) {

            closeQuota();
            return false;

        } else if (hiddenStationLayout.getVisibility() == View.VISIBLE) {

            closeStationList();
            return false;

        } else if (hiddenHistoryLayout.getVisibility() == View.VISIBLE) {

            closeRecentSearchedLayout();
            return false;

        }

        return true;
    }

}
