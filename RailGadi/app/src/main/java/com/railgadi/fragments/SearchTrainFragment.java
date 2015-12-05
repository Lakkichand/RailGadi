package com.railgadi.fragments;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.QuotaAdapter;
import com.railgadi.adapters.StationNameCodeAdapter;
import com.railgadi.async.SearchTrainTaskNew;
import com.railgadi.beans.FindTrainHistoryContainer;
import com.railgadi.beans.SearchTrainInputBean;
import com.railgadi.beans.SearchTrainResultBean;
import com.railgadi.beans.StationListDataHolder;
import com.railgadi.beans.StationNameCodeBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.ICloseEverything;
import com.railgadi.interfaces.IFindTrainComm;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IHistoryCommunicator;
import com.railgadi.preferences.HistoryPreferences;
import com.railgadi.preferences.PreferenceUtils;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.DateAndMore;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Date;
import java.util.List;

public class SearchTrainFragment extends Fragment implements IHistoryCommunicator, IFindTrainComm, ICloseEverything {



    private IFragReplaceCommunicator comm;
    private View rootView;

    private HistoryPreferences historyPreferences;

    private LinearLayout selectJourneyDate, selectQuotaLayoutClick;

    // hide layout
    private LinearLayout superLayout;
    public LinearLayout stationChooserLayout, fromStationLayout, toStationLayout, viaLayout;
    public RelativeLayout midLayout ;
    public LinearLayout calendarLayout, selectQuotaLayoutHidden;
    private CalendarPickerView calendar_view;
    private CheckBox includeWeeklySwitch;

    private ExpandableListView stationChooserList;

    private ImageView swapFromTo;
    int viewHeight;
    boolean noSwap = true;
    private static int ANIMATION_DURATION = 500;

    private TextView searchTrain;
    //private SearchView searchView;
    private EditText searchView;

    private SearchManager searchManager;

    private StationNameCodeAdapter listAdapter;

    private TextView dateOfJourney, dayOfJourney, srcStationName, destStationName, srcStationCode,
            destStationCode, littleCalendar, selectedQuota, viaStationCode, viaStationName, depStartTimeText, depEndTimeText;

    private String listIdentifyFlag = "";

    private List<StationListDataHolder> data;

    private String selectedDate;

    private String inputSrcCode, inputSrcName, inputDestCode, inputDestName, inputViaCode, inputViaName, inputDay, inputMonth, selectedStartTime,
            selectedEndTime, inputYear, quotaCode;

    private RangeBar rangebar;

    private ImageView fromIcon, toIcon;

    private TextView showRangeText;
    private TextView stationListTitle, calendarTitle, currentDateMonthTitle, currentDayTitle, selectQuotaTitle;
    private TextView listCloseButton, calendarCloseButton, quotaCloseButton;


    private StationNameCodeBean selectedSrcBean, selectedDestBean;

    public static RightDrawerFragment rightDrawerFragment;

    private MenuItem historyMenu;

    private SearchTrainTaskNew searchTrainTaskNew ;


    public SearchTrainFragment() {


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        rightDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.find_train_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        historyMenu = menu.getItem(0);
        historyMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                openHistory();
                return false;
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.find_train_fragment, container, false);

        RightDrawerFragment.recentSearchFlag = true;
        RightDrawerFragment.returnJourneyFlag = false;

        rightDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
        rightDrawerFragment.showRecentSearch();

        MainActivity.toolbar.setVisibility(View.VISIBLE);

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.search_train).toUpperCase());

        MainActivity.closeFragmentObject = SearchTrainFragment.this;

        rightDrawerFragment.setHistoryListener(this);
        // default initializations

        selectedDate = DateAndMore.formatDateToString(new Date(), DateAndMore.DATE_WITH_SLASH);
        quotaCode = null ;
        inputSrcName = null;
        inputDestName = null;
        inputSrcCode = null;
        inputDestCode = null;
        selectedStartTime = "00" ;
        selectedEndTime = "24" ;

        comm = (IFragReplaceCommunicator) getActivity();

        initializeAllViews();

        rightDrawerFragment.setAdapterHistoryList();
        return rootView;
    }


    private void openHistory() {

        boolean flag = rightDrawerFragment.setAdapterHistoryList();
        rightDrawerFragment.mDrawerLayout.openDrawer(Gravity.RIGHT);
    }


    // method of historyCommunicator interface
    @Override
    public void chooseFromHistory(FindTrainHistoryContainer hc) {

        inputSrcCode = hc.getSrcCode();
        inputDestCode = hc.getDestCode();
        inputSrcName = hc.getSrcName();
        inputDestName = hc.getDestName();
        quotaCode = null ;

        selectedDate = DateAndMore.formatDateToString(new Date(), DateAndMore.DATE_WITH_SLASH);

        String[] nowDate = DateAndMore.formatDateToString(new Date(), DateAndMore.DAY_DATE_MONTH).split(",");
        dateOfJourney.setText(nowDate[1].trim());
        dayOfJourney.setText(nowDate[0].trim());

        srcStationCode.setText(hc.getSrcCode());
        srcStationName.setText(hc.getSrcName());
        destStationCode.setText(hc.getDestCode());
        destStationName.setText(hc.getDestName());

        selectedSrcBean = new StationNameCodeBean(hc.getSrcCode(), hc.getSrcName(), hc.getSrcIcon());
        selectedDestBean = new StationNameCodeBean(hc.getDestCode(), hc.getDestName(), hc.getDestIcon());

        fromIcon.setImageResource(selectedSrcBean.getStationIcon());
        toIcon.setImageResource(selectedDestBean.getStationIcon());
    }


    private void refreshAdapterOnStationList() {

        data = UtilsMethods.collectStations(getActivity());
        listAdapter = new StationNameCodeAdapter(getActivity(), data);
        stationChooserList.setAdapter(listAdapter);
        expandAll();
    }


    private void buildStationChooserList() {

        refreshAdapterOnStationList();

        stationChooserList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return parent.isGroupExpanded(groupPosition);
            }
        });


        stationChooserList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                StationListDataHolder cont = (StationListDataHolder) listAdapter.getSelectedItemGroup(groupPosition);

                StationNameCodeBean c = cont.getNameCodeData().get(childPosition);

                switch (listIdentifyFlag) {

                    case Constants.FROM_LIST: {

                        inputSrcCode = c.getStationCode();
                        inputSrcName = c.getStationName();

                        srcStationName.setText(inputSrcName);
                        srcStationCode.setText(inputSrcCode);

                        selectedSrcBean = c;

                        if (selectedSrcBean.getStationIcon() == 0) {
                            selectedSrcBean.setStationIcon(R.drawable.shape_logo);
                        }
                        fromIcon.setImageResource(selectedSrcBean.getStationIcon());

                        break;
                    }

                    case Constants.TO_LIST: {

                        inputDestCode = c.getStationCode();
                        inputDestName = c.getStationName();

                        destStationName.setText(inputDestName);
                        destStationCode.setText(inputDestCode);

                        selectedDestBean = c;

                        if (selectedDestBean.getStationIcon() == 0) {
                            selectedDestBean.setStationIcon(R.drawable.shape_logo);
                        }

                        toIcon.setImageResource(selectedDestBean.getStationIcon());

                        break;
                    }

                    case Constants.VIA_LIST: {

                        inputViaCode = c.getStationCode();
                        inputViaName = c.getStationName();

                        viaStationCode.setText(inputViaCode);
                        viaStationName.setText(inputViaName);

                        break;
                    }
                }

                searchView.setText("");
                listAdapter.setUpdatedNull() ;
                closeStationlist();
                return false;
            }
        });
    }


    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            stationChooserList.expandGroup(i);
        }
    }


    // new method old one is commented
    private void initializeAllViews() {

        // linear layouts
        superLayout = (LinearLayout) rootView.findViewById(R.id.super_layout);
        calendarLayout = (LinearLayout) rootView.findViewById(R.id.calendar_layout);
        stationChooserLayout = (LinearLayout) rootView.findViewById(R.id.station_chooser_layout); // station list layout
        fromStationLayout = (LinearLayout) rootView.findViewById(R.id.select_from_station_layout);
        midLayout = (RelativeLayout) rootView.findViewById(R.id.mid_layout);
        toStationLayout = (LinearLayout) rootView.findViewById(R.id.select_to_station_layout);
        selectQuotaLayoutClick = (LinearLayout) rootView.findViewById(R.id.open_select_quota);
        selectQuotaLayoutHidden = (LinearLayout) rootView.findViewById(R.id.select_quota_layout);
        viaLayout = (LinearLayout) rootView.findViewById(R.id.via_layout);

        fromStationLayout.setOnClickListener(new ClickOnStationLayout());
        toStationLayout.setOnClickListener(new ClickOnStationLayout());
        selectQuotaLayoutClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQuotaLayout();
            }
        });

        viaLayout.setOnClickListener(new ClickOnStationLayout());

        // switch
        includeWeeklySwitch = (CheckBox) rootView.findViewById(R.id.include_weekly_train_switch);

        // intializing textviews
        littleCalendar = (TextView) rootView.findViewById(R.id.little_calendar);
        littleCalendar.setText(DateAndMore.formatDateToString(new Date(), DateAndMore.DATE_WITH_SLASH).substring(0, 2));

        currentDateMonthTitle = (TextView) rootView.findViewById(R.id.current_daymonth_titlebar);
        currentDayTitle = (TextView) rootView.findViewById(R.id.current_day_titlebar);

        showRangeText = (TextView) rootView.findViewById(R.id.range_show_txt);
        dateOfJourney = (TextView) rootView.findViewById(R.id.journey_date_textview);
        dateOfJourney.setTypeface(AppFonts.getRobotoLight(getActivity()));
        dayOfJourney = (TextView) rootView.findViewById(R.id.journey_day_textview);
        dayOfJourney.setTypeface(AppFonts.getRobotoLight(getActivity()));

        String[] nowDate = DateAndMore.formatDateToString(new Date(), DateAndMore.DAY_DATE_MONTH).split(",");

        dateOfJourney.setText(nowDate[1].trim());
        dayOfJourney.setText(nowDate[0].trim());

        fromIcon = (ImageView) rootView.findViewById(R.id.from_icon);
        toIcon = (ImageView) rootView.findViewById(R.id.to_icon);

        srcStationName = (TextView) rootView.findViewById(R.id.from_station_name_text);
        srcStationCode = (TextView) rootView.findViewById(R.id.from_station_code_text);
        destStationName = (TextView) rootView.findViewById(R.id.to_station_name_text);
        destStationCode = (TextView) rootView.findViewById(R.id.to_station_code_text);
        viaStationCode = (TextView) rootView.findViewById(R.id.via_text_code);
        viaStationName = (TextView) rootView.findViewById(R.id.via_textview_name);


        selectQuotaTitle = (TextView) rootView.findViewById(R.id.select_quota_title);

        stationListTitle = (TextView) rootView.findViewById(R.id.station_list_title);
        calendarTitle = (TextView) rootView.findViewById(R.id.calendar_title);
        calendarTitle.setText(Html.fromHtml("<small>Select Date</small>"));

        String[] sdate = DateAndMore.formatDateToString(new Date(), DateAndMore.FULL_DAY_WITHOUT_TIME).split(",");
        currentDateMonthTitle.setText(sdate[1].trim());
        currentDayTitle.setText(sdate[0]);

        selectedQuota = (TextView) rootView.findViewById(R.id.selected_quota_textview);
        selectedQuota.setTypeface(AppFonts.getRobotoLight(getActivity()));

        // setting fonts
        srcStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        srcStationName.setTypeface(AppFonts.getRobotoLight(getActivity()));
        destStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        destStationName.setTypeface(AppFonts.getRobotoLight(getActivity()));
        viaStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        viaStationName.setTypeface(AppFonts.getRobotoLight(getActivity()));

        // imageviews

        swapFromTo = (ImageView) rootView.findViewById(R.id.swap_from_to_imageview);
        swapFromTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (inputSrcCode == null || inputDestCode == null || inputSrcName == null || inputDestName == null) {
                    return;
                }
                swapSrcDest();*/

            }
        });


        if(PreferenceUtils.getFromLayoutHeight(getActivity()) == 0 || PreferenceUtils.getMidLayoutHeight(getActivity()) == 0) {

            ViewTreeObserver viewTreeObserver = fromStationLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        fromStationLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
                        midLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
                        viewHeight = fromStationLayout.getHeight() + midLayout.getHeight() ;
                        fromStationLayout.getLayoutParams();
                    }
                });
            }
        } else {
            viewHeight = PreferenceUtils.getFromLayoutHeight(getActivity()) + PreferenceUtils.getMidLayoutHeight(getActivity()) ;
        }

        listCloseButton = (TextView) rootView.findViewById(R.id.close_station_list_cross);
        listCloseButton.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        listCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeStationlist();
            }
        });

        calendarCloseButton = (TextView) rootView.findViewById(R.id.close_calendar_cross);
        calendarCloseButton.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        calendarCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeCalendar();
            }
        });

        quotaCloseButton = (TextView) rootView.findViewById(R.id.select_quota_close);
        quotaCloseButton.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        quotaCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeQuotaLayout();
            }
        });

        // all station list
        stationChooserList = (ExpandableListView) rootView.findViewById(R.id.station_chooser_list);
        stationChooserList.setBackgroundColor(getActivity().getResources().getColor(R.color.calendar_background));
        buildStationChooserList();
        expandAll();

        quotaListView = (ListView) rootView.findViewById(R.id.all_quotas_listview);

        // searchView
        searchView = (EditText) rootView.findViewById(R.id.search_train_searchview);
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
                if(listAdapter != null) {
                    listAdapter.filterData(s.toString().toLowerCase());
                    expandAll();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(listAdapter != null) {
                    listAdapter.filterData(s.toString().toLowerCase());
                    expandAll();
                }
            }
        });

        // find train button
        searchTrain = (TextView) rootView.findViewById(R.id.find_train_button);
        searchTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (inputSrcCode == null || inputSrcCode.equals("")) {
                    Toast.makeText(getActivity(), "Select Source", Toast.LENGTH_SHORT).show();
                    return;
                } else if (inputDestCode == null || inputDestCode.equals("")) {
                    Toast.makeText(getActivity(), "Select Destination", Toast.LENGTH_SHORT).show();
                    return;
                } else if (selectedDate == null || selectedDate.equals("")) {
                    Toast.makeText(getActivity(), "Select Date of Journey", Toast.LENGTH_SHORT).show();
                    return;
                } else if (inputSrcCode.equals(inputDestCode)) {
                    Toast.makeText(getActivity(), "Source and Destination could not be same", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (includeWeeklySwitch.isChecked()) {

                    inputDay = "";
                    inputMonth = "";
                    inputYear = "";
                } else {
                    inputDay = selectedDate.substring(0, 2);
                    inputMonth = selectedDate.substring(3, 5);
                    inputYear = selectedDate.substring(6, selectedDate.length());
                }

                SearchTrainInputBean input = new SearchTrainInputBean() ;
                input.setFromStationCode(inputSrcCode) ;
                input.setFromStationName(inputSrcName) ;
                input.setToStationCode(inputDestCode) ;
                input.setToStationName(inputDestName) ;
                input.setStartRange(selectedStartTime);
                input.setEndRange(selectedEndTime);
                input.setDay(inputDay) ;
                input.setMonth(inputMonth) ;
                input.setYear(inputYear) ;
                input.setQuota(quotaCode) ;

                if(InternetChecking.isNetWorkOn(getActivity())) {
                    searchTrainTaskNew = new SearchTrainTaskNew(getActivity(), input, SearchTrainFragment.this);
                    searchTrainTaskNew.execute();
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });

        // initializing range bar
        rangebar = (RangeBar) rootView.findViewById(R.id.departure_time_rangebar);
        rangebar.setPinRadius(25.0f);
        rangebar.setSelectorColor(getResources().getColor(R.color.colorPrimary));
        rangebar.setTickColor(getResources().getColor(R.color.colorPrimary));
        rangebar.setPinTextColor(getResources().getColor(R.color.white));
        rangebar.setBarColor(getResources().getColor(R.color.gray));
        rangebar.setConnectingLineColor(getResources().getColor(R.color.colorPrimary));
        rangebar.setPinColor(getResources().getColor(R.color.colorPrimary));

        rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {

            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                selectedStartTime = leftPinValue;
                selectedEndTime = rightPinValue;

                if(selectedStartTime.length() == 1) {
                    selectedStartTime = "0"+selectedStartTime ;
                }
                if(selectedEndTime.length() == 1) {
                    selectedEndTime = "0"+selectedEndTime ;
                }

                String l = leftPinValue + ":00";
                String r = rightPinValue + ":00";

                String rangeText = l + " to " + r;

                showRangeText.setText(rangeText);
            }
        });

        // date of journey layout
        selectJourneyDate = (LinearLayout) rootView.findViewById(R.id.select_jorney_date_dialog);
        selectJourneyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeCalendar();
                openCalendar();
            }
        });
    }


    public void swapSrcDest() {

        /*if (inputSrcCode == null || inputDestCode == null || inputSrcName == null || inputDestName == null) {
            return;
        }

        StringBuffer sc = new StringBuffer(inputSrcCode);
        StringBuffer dc = new StringBuffer(inputDestCode);
        StringBuffer sn = new StringBuffer(inputSrcName);
        StringBuffer dn = new StringBuffer(inputDestName);

        srcStationCode.setText(dc.toString());
        destStationCode.setText(sc.toString());
        srcStationName.setText(dn.toString());
        destStationName.setText(sn.toString());

        inputSrcCode = dc.toString();
        inputDestCode = sc.toString();
        inputSrcName = dn.toString();
        inputDestName = sn.toString();

        int icon = selectedDestBean.getStationIcon() ;
        selectedDestBean.setStationIcon(selectedSrcBean.getStationIcon());
        selectedSrcBean.setStationIcon(icon);

        fromIcon.setImageResource(selectedSrcBean.getStationIcon());
        toIcon.setImageResource(selectedDestBean.getStationIcon());*/

        StringBuffer sc = new StringBuffer(inputSrcCode);
        StringBuffer dc = new StringBuffer(inputDestCode);
        StringBuffer sn = new StringBuffer(inputSrcName);
        StringBuffer dn = new StringBuffer(inputDestName);

        inputSrcCode = dc.toString();
        inputDestCode = sc.toString();
        inputSrcName = dn.toString();
        inputDestName = sn.toString();

        if (noSwap) {
            TranslateAnimation ta1 = new TranslateAnimation(0, 0, 0, viewHeight);
            ta1.setDuration(ANIMATION_DURATION);
            ta1.setFillAfter(true);
            fromStationLayout.startAnimation(ta1);
            fromStationLayout.bringToFront();

            TranslateAnimation ta2 = new TranslateAnimation(0, 0, 0, -viewHeight);
            ta2.setDuration(ANIMATION_DURATION);
            ta2.setFillAfter(true);
            toStationLayout.startAnimation(ta2);
            toStationLayout.bringToFront();

            noSwap = false;

        } else {
            TranslateAnimation ta1 = new TranslateAnimation(0, 0, viewHeight, 0);
            ta1.setDuration(ANIMATION_DURATION);
            ta1.setFillAfter(true);
            fromStationLayout.startAnimation(ta1);
            fromStationLayout.bringToFront();

            TranslateAnimation ta2 = new TranslateAnimation(0, 0, -viewHeight, 0);
            ta2.setDuration(ANIMATION_DURATION);
            ta2.setFillAfter(true);
            toStationLayout.startAnimation(ta2);
            toStationLayout.bringToFront();

            noSwap = true;
        }
    }


    @Override
    public void updateSearchTrainListUI(SearchTrainResultBean searchTrainResult, SearchTrainInputBean input) {

        FindTrainHistoryContainer history = new FindTrainHistoryContainer();

        history.setDestCode(selectedDestBean.getStationCode());
        history.setDestName(selectedDestBean.getStationName());
        history.setDestIcon(selectedDestBean.getStationIcon());
        history.setSrcCode(selectedSrcBean.getStationCode());
        history.setSrcName(selectedSrcBean.getStationName());
        history.setSrcIcon(selectedSrcBean.getStationIcon());

        historyPreferences = new HistoryPreferences(getActivity());
        historyPreferences.saveHistoryToPref(history);

        Toast.makeText(getActivity(), searchTrainResult.getSearchTrainList().size() + " Trains Found", Toast.LENGTH_SHORT).show();

        comm.respond(new SearchTrainToNextFrag(searchTrainResult, input)) ;
    }


    @Override
    public void updateException(String exceptionType) {
        Toast.makeText(getActivity(), exceptionType, Toast.LENGTH_SHORT).show();
    }


    private class ClickOnStationLayout implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            int id = v.getId();

            if (id == R.id.select_from_station_layout) {
                listIdentifyFlag = Constants.FROM_LIST;
                searchView.setText("");
                listAdapter.filterData("");
                stationListTitle.setText(getActivity().getResources().getString(R.string.select_station));
            } else if (id == R.id.select_to_station_layout) {
                listIdentifyFlag = Constants.TO_LIST;
                searchView.setText("");
                listAdapter.filterData("");
                stationListTitle.setText(getActivity().getResources().getString(R.string.select_station));
            } else if (id == R.id.via_layout) {
                listIdentifyFlag = Constants.VIA_LIST;
                searchView.setText("");
                listAdapter.filterData("");
                stationListTitle.setText(getActivity().getResources().getString(R.string.select_station));
            }

            openStationList();
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


    private void initializeCalendar() {

        calendar_view = (CalendarPickerView) rootView.findViewById(R.id.calendar_view);
        calendar_view.init(new Date(), DateAndMore.get120DaysAfterDate())
                .inMode(CalendarPickerView.SelectionMode.SINGLE)
                .withSelectedDate(new Date());
        calendar_view.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        calendar_view.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {

                closeCalendar();

                String[] dateArr = DateAndMore.formatDateToString(date, DateAndMore.DAY_DATE_MONTH).split(",");
                dateOfJourney.setText(dateArr[1].trim());
                dayOfJourney.setText(dateArr[0].trim());

                MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.search_train).toUpperCase());

                selectedDate = DateAndMore.formatDateToString(date, DateAndMore.DATE_WITH_SLASH);
                littleCalendar.setText(selectedDate.substring(0, 2));
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }

    private void openCalendar() {

        MainActivity.toolbar.setVisibility(View.GONE);

        superLayout.setVisibility(View.GONE);
        calendarLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        calendarLayout.setAnimation(animation);
        calendarLayout.animate();
        animation.start();
    }

    private ListView quotaListView;

    private void openQuotaLayout() {

        final String[] all_quota_codes;
        final String[] all_quota_names;

        selectQuotaTitle.setText(Html.fromHtml("<small>Select Quota</small>"));

        all_quota_names = getActivity().getResources().getStringArray(R.array.quota_names_array);
        all_quota_codes = getActivity().getResources().getStringArray(R.array.quota_codes_array);
        QuotaAdapter qAdapter = new QuotaAdapter(getActivity(), all_quota_codes, all_quota_names);
        quotaListView.setAdapter(qAdapter);

        quotaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                quotaCode = all_quota_codes[position];
                selectedQuota.setText(all_quota_codes[position]);
                closeQuotaLayout();
            }
        });

        visibleQuotaLayout();
    }


    private void visibleQuotaLayout() {

        MainActivity.toolbar.setVisibility(View.GONE);

        superLayout.setVisibility(View.GONE);
        selectQuotaLayoutHidden.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        selectQuotaLayoutHidden.setAnimation(animation);
        selectQuotaLayoutHidden.animate();
        animation.start();
    }


    public void closeQuotaLayout() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);
        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.search_train).toUpperCase());

        selectQuotaLayoutHidden.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        selectQuotaLayoutHidden.setAnimation(animation);
        selectQuotaLayoutHidden.animate();
        animation.start();
    }


    public void closeCalendar() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);
        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.search_train).toUpperCase());

        calendarLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        calendarLayout.setAnimation(animation);
        calendarLayout.animate();
        animation.start();
    }


    public void closeStationlist() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);

        superLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.search_train).toUpperCase());

        stationChooserLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        stationChooserLayout.setAnimation(animation);
        stationChooserLayout.animate();
        animation.start();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean closeEveryThing() {

        if (stationChooserLayout.getVisibility() == View.VISIBLE) {
            closeStationlist();
            return false;
        } else if (calendarLayout.getVisibility() == View.VISIBLE) {
            closeCalendar();
            return false;
        } else if (selectQuotaLayoutHidden.getVisibility() == View.VISIBLE) {
            closeQuotaLayout();
            return false;
        }

        return true;
    }
}