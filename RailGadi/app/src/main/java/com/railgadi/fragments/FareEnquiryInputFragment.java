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
import com.railgadi.adapters.AgeAndConcessionAdapter;
import com.railgadi.adapters.LiveTrainRecentSearchAdapter;
import com.railgadi.adapters.SeatAvlStationAdapter;
import com.railgadi.async.AutoSuggestionTask;
import com.railgadi.async.FareEnqNewTask;
import com.railgadi.async.MiniTimeTableTask;
import com.railgadi.beans.FareEnqInput;
import com.railgadi.beans.FareEnquiryNewBean;
import com.railgadi.beans.MiniTimeTableBean;
import com.railgadi.beans.TrainDataBean;
import com.railgadi.dbhandlers.TrainDBHandler;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IAutoSuggestion;
import com.railgadi.interfaces.ICloseEverything;
import com.railgadi.interfaces.IFareEnqWithClassQuota;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IPnrDeleteCommunicator;
import com.railgadi.interfaces.IStationsViaTrainNumber;
import com.railgadi.preferences.FareEnquiryPreferences;
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

public class FareEnquiryInputFragment extends Fragment implements IPnrDeleteCommunicator, IAutoSuggestion, IStationsViaTrainNumber, ICloseEverything, IFareEnqWithClassQuota {

    private View rootView;
    private IFragReplaceCommunicator comm;

    private FareEnquiryPreferences fareEnqPref;

    private LiveTrainRecentSearchAdapter historyAdapter ;

    private FareEnqNewTask fareEnquiryNewTask;
    private MiniTimeTableTask miniTimeTableTask ;
    private AutoSuggestionTask autoSuggestionTask;

    // visible views
    private TextView getFareButton;
    private AutoCompleteTextView enterTrainNumberEditText;
    private TextView fromCode, fromName, toCode, toName, dateOfJourney, dayOfJourney, littleCalendar, concessionText, ageText, recentSearchTextview;
    private LinearLayout superLayout, fromStationLayout, toStationLayout, calendarlayout, concLayout, ageLayout;


    // hidden views
    private LinearLayout stationListLayoutHidden, calendarLayoutHidden, historyHiddenLayout, concessionLayoutHidden, ageLayoutHidden;
    private ListView allStationListView, ageListView, concessionListView, allRecentListView ;
    private CalendarPickerView calendarView;
    private LinearLayout autoSearchLayout ;
    private TextView closeStationImage, closeHistoryImage, closeCalendarImage, closeConcessionImage, closeAgeImage;
    private TextView stationListTitle, calendarTitle, historyTitle, calendarDayMonthTitlebar, calendarDayTitlebar, concessionTitle, ageTitle;


    // selected variables
    private static final String FROM = "from_list";
    private static final String TO = "to_list";
    private static boolean isFromClick = false;
    private static boolean isToClick = false;

    private int longClickedPosition ;

    private static boolean fromListflag = false;

    private String selectedDate;
    private String jDay;
    private String jMonth;
    private String defaultQuota;
    private String selectedConcessionCode;
    private String selectedAgeCode;

    private SeatAvlStationAdapter stationAdapter;

    String selectedFromCode, selectedFromName, selectedToCode, selectedToName;

    private List<MiniTimeTableBean.TrainRoute> savedList;
    private List<MiniTimeTableBean.TrainRoute> fromStationList, toStationList;
    private MiniTimeTableBean savedMiniTimetable ;
    private List<MiniTimeTableBean> allSavedHistory ;
    private MiniTimeTableBean longClickHistoryBean ;

    private List<Map.Entry<String, String>> suggestionEntries;

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

        if (autoSuggestionTask != null)
            autoSuggestionTask.cancel(true);

        if (fareEnquiryNewTask != null)
            fareEnquiryNewTask.cancel(true);

        if(miniTimeTableTask != null)
            miniTimeTableTask.cancel(true) ;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fare_enquiry_input, container, false);

        comm = (IFragReplaceCommunicator) getActivity();

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.fare_enquiry).toUpperCase());

        MainActivity.closeFragmentObject = FareEnquiryInputFragment.this;

        fareEnqPref = new FareEnquiryPreferences(getActivity()) ;

        initializeAllViews();
        initializeCalendar();
        setDataOnViews();
        setTypefaces();
        setHistoryAdapter() ;

        // default initializations
        //selectedConcessionCode = "ZZZZZZ";
        //selectedAgeCode = "30";
        selectedDate = DateAndMore.formatDateToString(new Date(), DateAndMore.DATE_WITH_SLASH);
        selectedFromCode = null;
        selectedToCode = null;
        jDay = selectedDate.substring(0, 2);
        jMonth = selectedDate.substring(3, 5);
        //defaultQuota = "GN";

        return rootView;
    }


    public void setHistoryAdapter() {

        allSavedHistory = fareEnqPref.getAllSavedTimeTable() ;
        if(allSavedHistory != null && allSavedHistory.size() > 0) {

            historyAdapter = new LiveTrainRecentSearchAdapter(getActivity(), allSavedHistory) ;
            allRecentListView.setAdapter(historyAdapter);
        }
        else{
            allRecentListView.setAdapter(null) ;
        }
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

                autoSuggestionTask = new AutoSuggestionTask(getActivity(), searchTerm, FareEnquiryInputFragment.this);
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

                if (trainNumber.toString().length() == 5) {

                    if (InternetChecking.isNetWorkOn(getActivity())) {
                        miniTimeTableTask   =   new MiniTimeTableTask(getActivity(), trainNumber, FareEnquiryInputFragment.this) ;
                        miniTimeTableTask.execute() ;
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
                        miniTimeTableTask   =   new MiniTimeTableTask(getActivity(), trainNumber, FareEnquiryInputFragment.this) ;
                        miniTimeTableTask.execute() ;
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

                autoSuggestionTask = new AutoSuggestionTask(getActivity(), s.toString(), FareEnquiryInputFragment.this);
                autoSuggestionTask.execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/



        fromCode = (TextView) rootView.findViewById(R.id.from_station_code_text);
        fromName = (TextView) rootView.findViewById(R.id.from_station_name_text);
        toCode = (TextView) rootView.findViewById(R.id.to_station_code_text);
        toName = (TextView) rootView.findViewById(R.id.to_station_name_text);
        dateOfJourney = (TextView) rootView.findViewById(R.id.journey_date_textview);
        dayOfJourney = (TextView) rootView.findViewById(R.id.journey_day_textview);
        littleCalendar = (TextView) rootView.findViewById(R.id.little_calendar);
        concessionText = (TextView) rootView.findViewById(R.id.selected_conc_textview);
        ageText = (TextView) rootView.findViewById(R.id.select_age_textview);
        getFareButton = (TextView) rootView.findViewById(R.id.get_fare_button);
        recentSearchTextview = (TextView) rootView.findViewById(R.id.recent_history_textview) ;

        superLayout = (LinearLayout) rootView.findViewById(R.id.super_layout);
        fromStationLayout = (LinearLayout) rootView.findViewById(R.id.select_from_station_layout);
        toStationLayout = (LinearLayout) rootView.findViewById(R.id.select_to_station_layout);
        concLayout = (LinearLayout) rootView.findViewById(R.id.open_select_conc_layout);
        ageLayout = (LinearLayout) rootView.findViewById(R.id.open_age_dialog);
        calendarlayout = (LinearLayout) rootView.findViewById(R.id.select_calendar_layout);

        // hidden views
        stationListLayoutHidden = (LinearLayout) rootView.findViewById(R.id.station_list_layout_hidden);
        calendarLayoutHidden = (LinearLayout) rootView.findViewById(R.id.calendar_layout_hidden);
        concessionLayoutHidden = (LinearLayout) rootView.findViewById(R.id.concession_hidden_layout);
        historyHiddenLayout = (LinearLayout) rootView.findViewById(R.id.history_hidden_layout);
        ageLayoutHidden = (LinearLayout) rootView.findViewById(R.id.age_layout_hidden);

        allStationListView = (ListView) rootView.findViewById(R.id.all_stop_station_list);
        ageListView = (ListView) rootView.findViewById(R.id.all_age_listview);
        allRecentListView = (ListView) rootView.findViewById(R.id.all_recent_listview);
        concessionListView = (ListView) rootView.findViewById(R.id.all_conc_listview);

        closeStationImage = (TextView) rootView.findViewById(R.id.close_station_imageview);
        closeCalendarImage = (TextView) rootView.findViewById(R.id.close_calendar_imageview);
        closeConcessionImage = (TextView) rootView.findViewById(R.id.close_concession_imageview);
        closeAgeImage = (TextView) rootView.findViewById(R.id.close_age_imageview);
        closeHistoryImage = (TextView) rootView.findViewById(R.id.close_history_imageview);

        closeStationImage.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        closeCalendarImage.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        closeAgeImage.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        closeConcessionImage.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        closeHistoryImage.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        stationListTitle = (TextView) rootView.findViewById(R.id.station_list_title);
        calendarTitle = (TextView) rootView.findViewById(R.id.calendar_title);
        calendarDayMonthTitlebar = (TextView) rootView.findViewById(R.id.current_daymonth_titlebar);
        calendarDayTitlebar = (TextView) rootView.findViewById(R.id.current_day_titlebar);
        concessionTitle = (TextView) rootView.findViewById(R.id.select_concession_title);
        ageTitle = (TextView) rootView.findViewById(R.id.select_age_title);
        historyTitle = (TextView) rootView.findViewById(R.id.select_history_title);
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
        updateAutoSuggestion(suggestionList);

    }



    private void setDataOnViews() {

        allStationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isFromClick) {

                    MiniTimeTableBean.TrainRoute ss = fromStationList.get(position) ;

                    selectedFromCode = ss.stationCode ;
                    selectedFromName = ss.stationName ;
                    fromCode.setText(selectedFromCode);
                    fromName.setText(selectedFromName);

                    toStationList = new ArrayList<>(savedList);
                    Collections.sort(toStationList);

                    List<MiniTimeTableBean.TrainRoute> dummy = new ArrayList<>();

                    for (int i = (position + 1); i <= toStationList.size() - 1; i++) {
                        dummy.add(toStationList.get(i));
                    }

                    toStationList = dummy;
                    refreshToAdapter(toStationList);

                } else {

                    MiniTimeTableBean.TrainRoute ss = toStationList.get(position) ;

                    selectedToCode = ss.stationCode ;
                    selectedToName = ss.stationName ;
                    toCode.setText(selectedToCode);
                    toName.setText(selectedToName);
                }

                closeStationList();
            }
        });


        allRecentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MiniTimeTableBean saved = (MiniTimeTableBean) historyAdapter.getItem(position) ;
                enterTrainNumberEditText.setText(saved.getTrainNumber()+" - "+saved.getTrainName());
                trainNumber = saved.getTrainNumber() ;
                autoSearchLayout.setVisibility(View.GONE);
                updateMiniTimetable(saved) ;
                closeRecentSearchedLayout();
            }
        });

        allRecentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                longClickHistoryBean = (MiniTimeTableBean) historyAdapter.getItem(position) ;
                longClickedPosition = position ;
                UtilsMethods.confirmDeleteDialog(getActivity(), FareEnquiryInputFragment.this);
                return true ;
            }
        });


        getFareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (trainNumber == null || trainNumber.equals("")) {
                    Toast.makeText(getActivity(), "Enter Train Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedFromCode == null || selectedToCode == null) {
                    Toast.makeText(getActivity(), "Select Station", Toast.LENGTH_SHORT).show();
                    return;
                }

                FareEnqInput fei = new FareEnqInput();
                fei.setTrainNo(trainNumber);
                fei.setDay(jDay);
                fei.setMonth(jMonth);
                fei.setClassCode(savedMiniTimetable.getAvlClasses().get(savedMiniTimetable.getAvlClasses().size()-1)) ;
                fei.setAge(selectedAgeCode);
                fei.setSrcCode(selectedFromCode);
                fei.setDestCode(selectedToCode);
                fei.setConcCode(selectedConcessionCode);

                //fareEnquiryTask = new FareEnquiryTask(getActivity(), fei, FareEnquiryInputFragment.this, true);
                //fareEnquiryTask.execute();

                if(InternetChecking.isNetWorkOn(getActivity())) {
                    fareEnquiryNewTask = new FareEnqNewTask(getActivity(), fei, FareEnquiryInputFragment.this);
                    fareEnquiryNewTask.execute();
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });

        String[] nowDate = DateAndMore.formatDateToString(new Date(), DateAndMore.DAY_DATE_MONTH).split(",");
        dateOfJourney.setText(nowDate[1].trim());
        dayOfJourney.setText(nowDate[0].trim());
        littleCalendar.setText(DateAndMore.formatDateToString(new Date(), DateAndMore.DATE_WITH_SLASH).substring(0, 2));

        SpannableString content = new SpannableString(getResources().getString(R.string.recent_searched));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        recentSearchTextview.setText(content);
        recentSearchTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(allSavedHistory != null && allSavedHistory.size() > 0) {
                    openRecentSearchedLayout();
                } else {
                    Toast.makeText(getActivity(), "No recent search found", Toast.LENGTH_LONG).show() ;
                }
            }
        });

        fromStationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStationList(FROM);
            }
        });

        toStationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStationList(TO);
            }
        });

        calendarlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar();
            }
        });

        concLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConcessionLayout();
            }
        });

        ageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAgeLayout();
            }
        });

        closeAgeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAgeLayout();
            }
        });

        closeConcessionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeConcessionLayout();
            }
        });

        closeStationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeStationList();
            }
        });

        closeCalendarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeCalendar();
            }
        });

        closeHistoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeRecentSearchedLayout();
            }
        });

        concessionTitle.setText(getActivity().getResources().getString(R.string.concession));
        ageTitle.setText(getActivity().getResources().getString(R.string.select_age));
    }


    private void setTypefaces() {

        enterTrainNumberEditText.setTypeface(AppFonts.getRobotoThin(getActivity()));

        fromName.setTypeface(AppFonts.getRobotoLight(getActivity()));
        fromCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        toName.setTypeface(AppFonts.getRobotoLight(getActivity()));
        toCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        littleCalendar.setTypeface(AppFonts.getRobotoThin(getActivity()));
        dateOfJourney.setTypeface(AppFonts.getRobotoLight(getActivity()));
        dayOfJourney.setTypeface(AppFonts.getRobotoThin(getActivity()));
        concessionText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        ageText.setTypeface(AppFonts.getRobotoLight(getActivity()));

        calendarTitle.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        stationListTitle.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        historyTitle.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        concessionTitle.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        ageTitle.setTypeface(AppFonts.getRobotoMedium(getActivity()));
    }


    public void openStationList(String flag) {

        if (flag.equals(FROM)) {

            if (fromStationList != null) {

                fromListflag = true;
                isFromClick = true;
                isToClick = false;

                stationListTitle.setText(getActivity().getResources().getString(R.string.select_station));

                refreshFromAdapter(fromStationList);

                MainActivity.toolbar.setVisibility(View.GONE);

                superLayout.setVisibility(View.GONE);
                stationListLayoutHidden.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
                animation.setDuration(Constants.STATION_ANIM_DURATION);
                stationListLayoutHidden.setAnimation(animation);
                stationListLayoutHidden.animate();
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
                    stationListLayoutHidden.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
                    animation.setDuration(Constants.STATION_ANIM_DURATION);
                    stationListLayoutHidden.setAnimation(animation);
                    stationListLayoutHidden.animate();
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
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.fare_enquiry).toUpperCase());

        stationListLayoutHidden.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        stationListLayoutHidden.setAnimation(animation);
        stationListLayoutHidden.animate();
        animation.start();
    }


    public void openCalendar() {

        MainActivity.toolbar.setVisibility(View.GONE);

        superLayout.setVisibility(View.GONE);
        calendarLayoutHidden.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        calendarLayoutHidden.setAnimation(animation);
        calendarLayoutHidden.animate();
        animation.start();
    }

    public void closeCalendar() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);
        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.fare_enquiry).toUpperCase());

        calendarLayoutHidden.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        calendarLayoutHidden.setAnimation(animation);
        calendarLayoutHidden.animate();
        animation.start();
    }


    private void setupConcession() {

        final String[] all_conc_code;
        final String[] all_conc_name;

        concessionTitle.setText(Html.fromHtml("<small>Concession</small>"));

        all_conc_code = getActivity().getResources().getStringArray(R.array.conc_codes);
        all_conc_name = getActivity().getResources().getStringArray(R.array.conc_code_description);
        AgeAndConcessionAdapter adapter = new AgeAndConcessionAdapter(getActivity(), all_conc_name);
        concessionListView.setAdapter(adapter);

        concessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedConcessionCode = all_conc_code[position];
                concessionText.setText(all_conc_name[position]);
                closeConcessionLayout();
            }
        });
    }

    private void openConcessionLayout() {

        setupConcession();

        MainActivity.toolbar.setVisibility(View.GONE);

        superLayout.setVisibility(View.GONE);
        concessionLayoutHidden.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        concessionLayoutHidden.setAnimation(animation);
        concessionLayoutHidden.animate();
        animation.start();
    }


    private void openRecentSearchedLayout() {

        MainActivity.toolbar.setVisibility(View.GONE);

        superLayout.setVisibility(View.GONE);
        historyHiddenLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        historyHiddenLayout.setAnimation(animation);
        historyHiddenLayout.animate();
        animation.start();
    }

    private void closeRecentSearchedLayout() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);
        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.fare_enquiry).toUpperCase());

        historyHiddenLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        historyHiddenLayout.setAnimation(animation);
        historyHiddenLayout.animate();
        animation.start();
    }


    private void closeConcessionLayout() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);
        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.fare_enquiry).toUpperCase());

        concessionLayoutHidden.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        concessionLayoutHidden.setAnimation(animation);
        concessionLayoutHidden.animate();
        animation.start();
    }

    private void setupAge() {

        final String[] all_age_values;
        final String[] all_age_desc;

        concessionTitle.setText(Html.fromHtml("<small>Concession</small>"));

        all_age_values = getActivity().getResources().getStringArray(R.array.age_code);
        all_age_desc = getActivity().getResources().getStringArray(R.array.age_code_description);
        AgeAndConcessionAdapter adapter = new AgeAndConcessionAdapter(getActivity(), all_age_desc);
        ageListView.setAdapter(adapter);

        ageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAgeCode = all_age_values[position];
                ageText.setText(all_age_desc[position]);
                closeConcessionLayout();
            }
        });
    }

    @Override
    public void updateMiniTimetable(MiniTimeTableBean bean) {

        savedMiniTimetable  =   bean ;
        savedList           =   bean.getTrainRouteList() ;

        fromStationList = new ArrayList<>(bean.getTrainRouteList());
        toStationList = new ArrayList<>(bean.getTrainRouteList());

        //Collections.sort(fromStationList, new StationDistanceComparator());
        //Collections.sort(toStationList, new StationDistanceComparator());
        Collections.sort(fromStationList);
        Collections.sort(toStationList);


        MiniTimeTableBean.TrainRoute start = fromStationList.get(0) ;
        MiniTimeTableBean.TrainRoute end = fromStationList.get(fromStationList.size() - 1) ;

        selectedFromCode = start.stationCode ;
        selectedFromName = start.stationName ;
        selectedToCode = end.stationCode ;
        selectedToName = end.stationName ;

        fromCode.setText(selectedFromCode);
        fromName.setText(selectedFromName);
        toCode.setText(selectedToCode);
        toName.setText(selectedToName);

    }


    private void openAgeLayout() {

        setupAge();

        MainActivity.toolbar.setVisibility(View.GONE);

        superLayout.setVisibility(View.GONE);
        ageLayoutHidden.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        ageLayoutHidden.setAnimation(animation);
        ageLayoutHidden.animate();
        animation.start();
    }

    private void closeAgeLayout() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);
        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.fare_enquiry).toUpperCase());

        ageLayoutHidden.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        ageLayoutHidden.setAnimation(animation);
        ageLayoutHidden.animate();
        animation.start();
    }

    private void initializeCalendar() {

        calendarView = (CalendarPickerView) rootView.findViewById(R.id.calendar_view);
        calendarView.setTypeface(AppFonts.getRobotoLight(getActivity()));
        calendarDayMonthTitlebar = (TextView) rootView.findViewById(R.id.current_daymonth_titlebar);
        calendarDayTitlebar = (TextView) rootView.findViewById(R.id.current_day_titlebar);

        calendarView.init(new Date(), DateAndMore.get120DaysAfterDate())
                .inMode(CalendarPickerView.SelectionMode.SINGLE)
                .withSelectedDate(new Date());

        String[] sdate = DateAndMore.formatDateToString(new Date(), DateAndMore.FULL_DAY_WITHOUT_TIME).split(",");
        calendarDayMonthTitlebar.setText(sdate[1].trim());
        calendarDayTitlebar.setText(sdate[0]);

        calendarView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {

                closeCalendar();

                String[] dateArr = DateAndMore.formatDateToString(date, DateAndMore.DAY_DATE_MONTH).split(",");
                dateOfJourney.setText(dateArr[1].trim());
                dayOfJourney.setText(dateArr[0].trim());

                MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.fare_enquiry).toUpperCase());

                selectedDate = DateAndMore.formatDateToString(date, DateAndMore.DATE_WITH_SLASH);
                jDay = selectedDate.substring(0, 2);
                jMonth = selectedDate.substring(3, 5);

                littleCalendar.setText(selectedDate.substring(0, 2));
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }

    private void refreshFromAdapter(List<MiniTimeTableBean.TrainRoute> list) {

        stationAdapter = new SeatAvlStationAdapter(getActivity(), list);
        allStationListView.setAdapter(stationAdapter);
    }

    private void refreshToAdapter(List<MiniTimeTableBean.TrainRoute> list) {

        stationAdapter = new SeatAvlStationAdapter(getActivity(), list);
        allStationListView.setAdapter(stationAdapter);
    }

    @Override
    public void updateFareOnClassAndQuota(FareEnquiryNewBean bean, FareEnqInput fei) {

        enterTrainNumberEditText.setText("");

        fareEnqPref.saveTimeTableToPref(savedMiniTimetable);
        comm.respond(new FareEnquiryToNextFrag(bean, savedMiniTimetable, fei));
    }

    @Override
    public void updateException(String exceptionType) {

        Toast.makeText(getActivity(), exceptionType, Toast.LENGTH_SHORT).show();

        fromCode.setText("From");
        fromName.setText("");
        toCode.setText("To");
        toName.setText("");
        enterTrainNumberEditText.setText("");
        fromStationList = null;
        toStationList = null;
        selectedFromCode = null;
        selectedToCode = null;

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
    public void wantToDelete(boolean flag) {
        if(flag) {
            historyAdapter.removeItem(longClickedPosition);
            fareEnqPref.removeFromPreferences(longClickHistoryBean.getTrainNumber()) ;
            closeRecentSearchedLayout();
        }
    }

    @Override
    public boolean closeEveryThing() {

        if (calendarLayoutHidden.getVisibility() == View.VISIBLE) {

            closeCalendar();
            return false;

        } else if (ageLayoutHidden.getVisibility() == View.VISIBLE) {

            closeAgeLayout();
            return false;

        } else if (concessionLayoutHidden.getVisibility() == View.VISIBLE) {

            closeConcessionLayout();
            return false;

        } else if (stationListLayoutHidden.getVisibility() == View.VISIBLE) {

            closeStationList();
            return false;

        } else if (historyHiddenLayout.getVisibility() == View.VISIBLE) {

            closeRecentSearchedLayout();
            return false;
        }

        return true;

    }
}
