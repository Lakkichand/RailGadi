package com.railgadi.fragments;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.FindTrainHistoryAdapter;
import com.railgadi.adapters.QuotaAdapter;
import com.railgadi.adapters.StationNameCodeAdapter;
import com.railgadi.async.SearchTrainTaskNew;
import com.railgadi.beans.FindTrainHistoryContainer;
import com.railgadi.beans.SearchTrainInputBean;
import com.railgadi.beans.StationListDataHolder;
import com.railgadi.beans.StationNameCodeBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.ICloseEverything;
import com.railgadi.interfaces.IFindTrainComm;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IHistoryCommunicator;
import com.railgadi.preferences.HistoryPreferences;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.DateAndMore;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Date;
import java.util.List;

public class RightDrawerFragment extends Fragment implements ICloseEverything {

    private View rootView;

    public static RightDrawerFragment navCurrentObject;

    private SearchTrainTaskNew searchTrainTaskNew ;

    private ActionBarDrawerToggle mDrawerToggle;

    public DrawerLayout mDrawerLayout;

    private View containerLayout;

    private IFragReplaceCommunicator comm;

    private ListView findTrainHistListView;
    private TextView noRecentSearch;
    private ImageView clearRecentSearch;

    private IHistoryCommunicator historyListener;

    private List<FindTrainHistoryContainer> recentList;

    private LinearLayout recentSearchLayout, returnJourneyLayout;

    public static IFindTrainComm trainListFragReference;

    public String selectedStartTime, selectedEndTime, selectedDate;

    private HistoryPreferences historyPreferences ;

    public static boolean recentSearchFlag = false;
    public static boolean returnJourneyFlag = false;

    private List<StationListDataHolder> data;

    // FOR RETURN JOURNEY
    private LinearLayout superLayout, viaLayout, selectDateOfJourneyLayout, quotaLayout, stationChooserLayout, calendarLayout;
    private TextView srcCode, srcName, destCode, destName, viaText, dateText, dayText, littleCalendarText, depArrText, quotaText;
    private CalendarPickerView calendar_view;
    private RangeBar rangebar;
    private EditText searchView;
    private SearchManager searchManager;
    private ExpandableListView allStationList;
    private TextView findTrainButton, selectQuotaTitle;

    private TextView closeQuota;

    private LinearLayout selectQuotaLayoutHidden;

    public RightDrawerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navCurrentObject = RightDrawerFragment.this;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (searchTrainTaskNew != null) {
            searchTrainTaskNew.cancel(true) ;
        }
    }

    public void setHistoryListener(IHistoryCommunicator historyListener) {

        this.historyListener = historyListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.right_navigation_layout, container, false);

        SearchTrainFragment.rightDrawerFragment = RightDrawerFragment.this;
        SearchTrainToNextFrag.rightDrawerFragment = RightDrawerFragment.this;
        RouteMapTabsFragment.rightDrawerFragment = RightDrawerFragment.this;
        FareBreakupFragmentFT.rightDrawerFragment = RightDrawerFragment.this;
        HomeFragment.rightDrawerFragment = RightDrawerFragment.this;
        SeatAvlSingleTrainFragment.rightDrawerFragment = RightDrawerFragment.this;
        MainActivity.closeFragmentObject = RightDrawerFragment.this;
        MainActivity.rightDrawerFragment = RightDrawerFragment.this ;

        comm = (IFragReplaceCommunicator) getActivity();

        historyPreferences = new HistoryPreferences(getActivity()) ;

        // default initialization
        selectedDate = DateAndMore.formatDateToString(new Date(), DateAndMore.DATE_WITH_SLASH);

        initializeAllViews();

        return rootView;
    }

    public boolean setAdapterHistoryList() {

        recentList = historyPreferences.getAllSavedHistory() ;

        if (recentList == null) {
            noRecentSearch.setVisibility(View.VISIBLE);
            findTrainHistListView.setVisibility(View.GONE);
            return false;
        }

        findTrainHistListView.setAdapter(new FindTrainHistoryAdapter(getActivity(), recentList));
        clearRecentSearch.setClickable(true);
        noRecentSearch.setVisibility(View.GONE);
        findTrainHistListView.setVisibility(View.VISIBLE);
        return true;
    }


    private StationNameCodeAdapter listAdapter;


    public void initializeAllViews() {

        //layouts of history
        recentSearchLayout = (LinearLayout) rootView.findViewById(R.id.recent_search_layout);
        returnJourneyLayout = (LinearLayout) rootView.findViewById(R.id.return_journey_layout);

        findTrainHistListView = (ListView) rootView.findViewById(R.id.find_train_history_listview);
        findTrainHistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                historyListener.chooseFromHistory(recentList.get(position));
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });

        // no recent
        noRecentSearch = (TextView) rootView.findViewById(R.id.no_recent_search);

        // clear recent search
        clearRecentSearch = (ImageView) rootView.findViewById(R.id.clear_recent_search);
        clearRecentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (historyPreferences.getAllSavedHistory() == null) {
                    Toast.makeText(getActivity(), "No Recent Searches", Toast.LENGTH_SHORT).show();
                    return;
                }

                historyPreferences.removeAllSavedHistory();

                mDrawerLayout.closeDrawer(Gravity.RIGHT);
                Toast.makeText(getActivity(), "Cleared Successfully", Toast.LENGTH_SHORT).show();
                recentList = null;

                clearRecentSearch.setClickable(false);
                setAdapterHistoryList();
            }
        });

        // layout views of return journey

        superLayout = (LinearLayout) rootView.findViewById(R.id.super_layout);

        viaLayout = (LinearLayout) rootView.findViewById(R.id.via_layout);
        viaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openViaStationChoose();
            }
        });

        quotaListView = (ListView) rootView.findViewById(R.id.all_quotas_listview);

        quotaLayout = (LinearLayout) rootView.findViewById(R.id.open_select_quota);
        quotaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQuotaDialog();
            }
        });

        selectQuotaLayoutHidden = (LinearLayout) rootView.findViewById(R.id.select_quota_layout);

        selectDateOfJourneyLayout = (LinearLayout) rootView.findViewById(R.id.select_jorney_date_dialog);
        selectDateOfJourneyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openCalendar();
            }
        });

        stationChooserLayout = (LinearLayout) rootView.findViewById(R.id.station_chooser_layout);

        calendarLayout = (LinearLayout) rootView.findViewById(R.id.calendar_layout);
        calendarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar();
            }
        });


        srcCode = (TextView) rootView.findViewById(R.id.from_station_code_text);
        srcName = (TextView) rootView.findViewById(R.id.from_station_name_text);
        destCode = (TextView) rootView.findViewById(R.id.to_station_code_text);
        destName = (TextView) rootView.findViewById(R.id.to_station_name_text);
        viaText = (TextView) rootView.findViewById(R.id.via_text_code);
        dateText = (TextView) rootView.findViewById(R.id.journey_date_textview);
        dayText = (TextView) rootView.findViewById(R.id.journey_day_textview);
        littleCalendarText = (TextView) rootView.findViewById(R.id.little_calendar);
        depArrText = (TextView) rootView.findViewById(R.id.range_show_txt);
        quotaText = (TextView) rootView.findViewById(R.id.selected_quota_textview);

        selectQuotaTitle = (TextView) rootView.findViewById(R.id.select_quota_title);

        // imageView
        closeQuota = (TextView) rootView.findViewById(R.id.select_quota_close);
        closeQuota.setTypeface(AppFonts.getRobotoReguler(getActivity())) ;
        closeQuota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeQuotaLayout();
            }
        });

        littleCalendarText.setText(DateAndMore.formatDateToString(new Date(), DateAndMore.DATE_WITH_SLASH).substring(0, 2));


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

                String l = leftPinValue + ":00";
                String r = rightPinValue + ":00";

                String rangeText = l + " to " + r;

                depArrText.setText(rangeText);
            }
        });

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
                listAdapter.filterData(s.toString().toLowerCase());
                expandAll();
            }

            @Override
            public void afterTextChanged(Editable s) {
                listAdapter.filterData(s.toString().toLowerCase());
                expandAll();
            }
        });

        findTrainButton = (TextView) rootView.findViewById(R.id.find_train_button);
        findTrainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedDate == null || selectedDate.equals("")) {
                    Toast.makeText(getActivity(), "Select Date of Journey", Toast.LENGTH_SHORT).show();
                    return;
                }

                returnDay = selectedDate.substring(0, 2);
                returnMonth = selectedDate.substring(3, 5);
                returnYear = selectedDate.substring(6, selectedDate.length());

                if(InternetChecking.isNetWorkOn(getActivity())) {

                    SearchTrainInputBean input = new SearchTrainInputBean() ;
                    input.setFromStationCode(returnSrcCode) ;
                    input.setFromStationName(returnSrcName) ;
                    input.setToStationCode(returnDestCode) ;
                    input.setToStationName(returnDestName) ;
                    input.setDay(returnDay) ;
                    input.setMonth(returnMonth) ;
                    input.setYear(returnYear) ;
                    input.setQuota(returnConsCode) ;


                    searchTrainTaskNew  = new SearchTrainTaskNew(getActivity(), input, trainListFragReference) ;
                    searchTrainTaskNew.execute() ;

                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                }
                else {
                    //InternetChecking.noInterNetToast(getActivity());
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });

        allStationList = (ExpandableListView) rootView.findViewById(R.id.station_chooser_list);
        buildStationChooserList();
    }

    public void refreshAdapterOnStationList() {

        data = UtilsMethods.collectStations(getActivity()) ;
        listAdapter = new StationNameCodeAdapter(getActivity(), data);
        allStationList.setAdapter(listAdapter);
        expandAll();
    }




    public void openViaStationChoose() {

        refreshAdapterOnStationList();
        returnJourneyLayout.setVisibility(View.GONE);
        stationChooserLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        stationChooserLayout.setAnimation(animation);
        stationChooserLayout.animate();
        animation.start();
    }

    public void closeViaStationChoose() {

        stationChooserLayout.setVisibility(View.GONE);
        returnJourneyLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        stationChooserLayout.setAnimation(animation);
        stationChooserLayout.animate();
        animation.start();
    }

    public void initializeCalendar() {

        calendar_view = (CalendarPickerView) rootView.findViewById(R.id.calendar_view);
        calendar_view.setTypeface(AppFonts.getRobotoLight(getActivity()));
        calendar_view.init(new Date(), DateAndMore.get120DaysAfterDate())
                .inMode(CalendarPickerView.SelectionMode.SINGLE)
                .withSelectedDate(new Date());

        calendar_view.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {

                closeCalendar();

                String[] dateArr = DateAndMore.formatDateToString(date, DateAndMore.DAY_DATE_MONTH).split(",");
                dayText.setText(dateArr[0].trim());
                dateText.setText(dateArr[1].trim());

                selectedDate = DateAndMore.formatDateToString(date, DateAndMore.DATE_WITH_SLASH);
                littleCalendarText.setText(selectedDate.substring(0, 2));
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }


    public void closeCalendar() {

        returnJourneyLayout.setVisibility(View.VISIBLE);
        calendarLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        calendarLayout.setAnimation(animation);
        calendarLayout.animate();
        animation.start();
    }

    public void openCalendar() {

        initializeCalendar();

        returnJourneyLayout.setVisibility(View.GONE);
        calendarLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        calendarLayout.setAnimation(animation);
        calendarLayout.animate();
        animation.start();
    }

    public void setFonts() {

        noRecentSearch.setTypeface(AppFonts.getRobotoLight(getActivity()));
        srcCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        srcName.setTypeface(AppFonts.getRobotoLight(getActivity()));
        destCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        destName.setTypeface(AppFonts.getRobotoLight(getActivity()));
        viaText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        dateText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        dayText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        littleCalendarText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        depArrText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        quotaText.setTypeface(AppFonts.getRobotoLight(getActivity()));

    }

    private ListView quotaListView;

    private String[] all_quota_codes;
    private String[] all_quota_names;

    public void openQuotaDialog() {

        selectQuotaTitle.setText(Html.fromHtml("<small>Select Quota</small>"));

        all_quota_names = getActivity().getResources().getStringArray(R.array.quota_names_array);
        all_quota_codes = getActivity().getResources().getStringArray(R.array.quota_codes_array);
        QuotaAdapter qAdapter = new QuotaAdapter(getActivity(), all_quota_codes, all_quota_names);
        quotaListView.setAdapter(qAdapter);

        quotaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                returnConsCode = all_quota_codes[position];
                quotaText.setText(all_quota_codes[position]);
                closeQuotaLayout();
            }
        });

        visibleQuotaLayout();
    }

    public void visibleQuotaLayout() {

        returnJourneyLayout.setVisibility(View.GONE);
        selectQuotaLayoutHidden.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        selectQuotaLayoutHidden.setAnimation(animation);
        selectQuotaLayoutHidden.animate();
        animation.start();
    }


    public void closeQuotaLayout() {

        returnJourneyLayout.setVisibility(View.VISIBLE);
        selectQuotaLayoutHidden.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        selectQuotaLayoutHidden.setAnimation(animation);
        selectQuotaLayoutHidden.animate();
        animation.start();
    }


    private String returnSrcCode, returnSrcName, returnDestCode, returnDestName, returnDay, returnMonth, returnYear, returnConsCode;


    public void setData(SearchTrainInputBean inputData) {

        returnSrcCode = inputData.getToStationCode() ;
        returnSrcName = inputData.getToStationName() ;
        returnDestCode = inputData.getFromStationCode() ;
        returnDestName = inputData.getFromStationName() ;
        returnConsCode = inputData.getQuota() ;

        String[] nowDate = DateAndMore.formatDateToString(new Date(), DateAndMore.DAY_DATE_MONTH).split(",");
        dateText.setText(nowDate[1].trim());
        dayText.setText(nowDate[0].trim());

        srcCode.setText(returnSrcCode);
        srcName.setText(returnSrcName);
        destCode.setText(returnDestCode);
        destName.setText(returnDestName);
    }


    public void buildStationChooserList() {

        refreshAdapterOnStationList();

        allStationList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return parent.isGroupExpanded(groupPosition);
            }
        });


        allStationList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                StationListDataHolder cont = (StationListDataHolder) listAdapter.getSelectedItemGroup(groupPosition);

                StationNameCodeBean c = cont.getNameCodeData().get(childPosition);

                closeViaStationChoose();
                return false;
            }
        });
    }

    public void closeStationlist() {

        MainActivity.toolbar.setVisibility(View.VISIBLE);

        returnJourneyLayout.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(Html.fromHtml("<small>Find Train</small>"));

        stationChooserLayout.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        animation.setDuration(Constants.STATION_ANIM_DURATION);
        stationChooserLayout.setAnimation(animation);
        stationChooserLayout.animate();
        animation.start();
    }

    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            allStationList.expandGroup(i);
        }
    }


    private class SearchViewListener implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

        @Override
        public boolean onClose() {
            listAdapter.filterData("");
            expandAll();
            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            listAdapter.filterData(query);
            expandAll();
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            listAdapter.filterData(query);
            expandAll();
            return false;
        }
    }

    public void showRecentSearch() {
        recentSearchLayout.setVisibility(View.VISIBLE);
        returnJourneyLayout.setVisibility(View.GONE);
    }

    public void showReturnJourney() {
        recentSearchLayout.setVisibility(View.GONE);
        returnJourneyLayout.setVisibility(View.VISIBLE);
    }

    private String title;

    public void setUp(DrawerLayout dLayout, final Toolbar toolbar, int layoutID) {

        containerLayout = getActivity().findViewById(layoutID);

        mDrawerLayout = dLayout;

        //mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), dLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean closeEveryThing() {

        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
            return false;
        }
        return true;
    }
}
