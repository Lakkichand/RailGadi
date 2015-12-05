package com.railgadi.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.railgadi.R;
import com.railgadi.activities.AutoLoginActivity;
import com.railgadi.activities.MainActivity;
import com.railgadi.activities.SignUpActivity;
import com.railgadi.adapters.DrawerListAdapter;
import com.railgadi.async.GetNearByStationTask;
import com.railgadi.async.LocateNearByTrainTask;
import com.railgadi.beans.LiveStationNewBean;
import com.railgadi.beans.NavigationChildBean;
import com.railgadi.beans.NavigationGroupBean;
import com.railgadi.beans.NearByStationBean;
import com.railgadi.customUi.AnimatedExpandableListView;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.ICloseEverything;
import com.railgadi.interfaces.ICurrentLocation;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.INearByStations;
import com.railgadi.preferences.PreferenceUtils;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.GPSTracker;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeftDrawerFragment extends Fragment implements ICloseEverything, INearByStations, ICurrentLocation {

    private View rootView;

    private ActionBarDrawerToggle mDrawerToggle;
    public DrawerLayout mDrawerLayout;

    private View containerLayout;
    private AnimatedExpandableListView sideMenuList;
    private LinearLayout navLayout, userDetailLayout ;
    private TextView userName, userEmail, loginButton ;
    private ImageView shareBottom, rateBottom, feedbackBottom;

    private static final int SHARE_POS = -1;
    private static final int RATE_POS = -2;
    private static final int FEED_POS = -3;

    private DrawerListAdapter adapter;

    private IFragReplaceCommunicator comm;

    private List<NavigationGroupBean> allListData;

    private GPSTracker gpsTracker;

    // async tasks
    private GetNearByStationTask getNearByStationTask;
    private LocateNearByTrainTask getNearByTrainTask;

    public static Location currentLocation;


    public LeftDrawerFragment() {

    }

    public void changeLoginViews() {

        if(PreferenceUtils.getCurrentUserName(getActivity()) == null || PreferenceUtils.getCurrentUserName(getActivity()).isEmpty()) {
            userDetailLayout.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        } else {
            userDetailLayout.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
            userName.setText(PreferenceUtils.getCurrentUserName(getActivity()));
            userEmail.setText(PreferenceUtils.getCurrentUserEmail(getActivity()));
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getNearByStationTask != null) {
            getNearByStationTask.cancel(true);
        }
        if (getNearByTrainTask != null) {
            getNearByTrainTask.cancel(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(PreferenceUtils.getCurrentUserName(getActivity()) == null || PreferenceUtils.getCurrentUserName(getActivity()).isEmpty()) {
            userDetailLayout.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        } else {
            userDetailLayout.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
            userName.setText(PreferenceUtils.getCurrentUserName(getActivity()));
            userEmail.setText(PreferenceUtils.getCurrentUserEmail(getActivity()));
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        RouteMapTabsFragment.leftDrawerFragment = LeftDrawerFragment.this;
        FareBreakupFragmentFT.leftDrawerFragment = LeftDrawerFragment.this;
        SearchTrainToNextFrag.leftDrawerFragment = LeftDrawerFragment.this;
        SeatAvlSingleTrainFragment.leftDrawerFragment = LeftDrawerFragment.this;
        MainActivity.closeFragmentObject = LeftDrawerFragment.this;
        MainActivity.leftDrawerFragment = LeftDrawerFragment.this;

        navLayout = (LinearLayout) rootView.findViewById(R.id.navigation_layout);

        comm = (IFragReplaceCommunicator) getActivity();

        gpsTracker = new GPSTracker(getActivity());

        initializeViews();

        return rootView;
    }


    public void initializeViews() {

        allListData = fillData();

        userDetailLayout = (LinearLayout) rootView.findViewById(R.id.user_detail_layout) ;
        userName = (TextView) rootView.findViewById(R.id.current_user_name) ;
        userEmail = (TextView) rootView.findViewById(R.id.current_user_email) ;
        loginButton = (TextView) rootView.findViewById(R.id.drawer_login_button) ;

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(), LoginActivity.class)) ;
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                mDrawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //comm.respond(new LoginFragment());
                        startActivity(new Intent(getActivity(), SignUpActivity.class));
                        getActivity().finish() ;
                    }

                }, 300) ;
            }
        });

        userName.setTypeface(AppFonts.getRobotoLight(getActivity()));
        userEmail.setTypeface(AppFonts.getRobotoLight(getActivity()));
        loginButton.setTypeface(AppFonts.getRobotoLight(getActivity()));

        sideMenuList = (AnimatedExpandableListView) rootView.findViewById(R.id.side_menu_exp_list);
        adapter = new DrawerListAdapter(getActivity(), allListData);
        sideMenuList.setAdapter(adapter);

        sideMenuList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (groupPosition == 0 || groupPosition == 1 || groupPosition == 5 || groupPosition == 11 ||
                        groupPosition == 8 || groupPosition == 9 || groupPosition == 12 ) {

                    closeDrawer(groupPosition, -1);
                    return true;
                }

                if (allListData.get(groupPosition).isSelected()) {
                    allListData.get(groupPosition).setSelected(false);
                    allListData.get(groupPosition).setGroupIndicator(R.drawable.down_arrow) ;
                } else {
                    allListData.get(groupPosition).setSelected(true);
                    allListData.get(groupPosition).setGroupIndicator(R.drawable.up_arrow) ;
                }

                if (allListData.get(groupPosition).isSelected()) {
                    sideMenuList.expandGroupWithAnimation(groupPosition);
                } else {
                    sideMenuList.collapseGroupWithAnimation(groupPosition);
                }

                adapter.notifyDataSetChanged();
                return true;
            }
        });

        sideMenuList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                closeDrawer(groupPosition, childPosition);

                sideMenuList.setSelection(groupPosition);

                return true;
            }
        });

        shareBottom = (ImageView) rootView.findViewById(R.id.share_bottom_icon);
        rateBottom = (ImageView) rootView.findViewById(R.id.rating_bottom_icon);
        feedbackBottom = (ImageView) rootView.findViewById(R.id.feed_back_bottom_icon);

        //shareBottom.setImageDrawable(UtilsMethods.getSvgDrawable(getActivity(), R.drawable.un_share));
        //rateBottom.setImageDrawable(UtilsMethods.getSvgDrawable(getActivity(), R.drawable.un_rate_us));
        feedbackBottom.setImageDrawable(UtilsMethods.getSvgDrawable(getActivity(), R.drawable.un_feedback));

        shareBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer(SHARE_POS, SHARE_POS);
            }
        });

        rateBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer(RATE_POS, RATE_POS);
            }
        });

        feedbackBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer(FEED_POS, FEED_POS);
            }
        });
    }


    public void closeDrawer(final int groupPosition, final int childPosition) {

        mDrawerLayout.closeDrawer(Gravity.LEFT);

        mDrawerLayout.postDelayed(new Runnable() {
            
            @Override
            public void run() {

                if (groupPosition < 0 && childPosition < 0) {
                    if (groupPosition == SHARE_POS && childPosition == SHARE_POS) {
                        UtilsMethods.openShareIntent(getActivity(), Constants.THIS_IS_WONDERFUL+"\n"+Constants.GOOGLE_PLAY_STATIC_URL + getActivity().getPackageName());
                    } else if (groupPosition == RATE_POS && childPosition == RATE_POS) {
                        if (InternetChecking.isNetWorkOn(getActivity())) {
                            startActivity(UtilsMethods.rateUs(getActivity()));
                        } else {
                            InternetChecking.showNoInternetPopup(getActivity());
                        }
                    } else if (groupPosition == FEED_POS && childPosition == FEED_POS) {
                        UtilsMethods.openFeedbackMailIntent(getActivity()) ;
                    }
                    return;
                }

                if (groupPosition >= 0 && childPosition < 0) {

                    NavigationGroupBean gBean = allListData.get(groupPosition);

                    String groupHeading = gBean.getGroupName();

                    if (groupHeading.equals(getActivity().getResources().getString(R.string.home))) {
                        comm.respond(new HomeFragment());
                    } else if (groupHeading.equals(getActivity().getResources().getString(R.string.trips))) {
                        comm.respond(new TripsFragment());
                    } else if (groupHeading.equals(getActivity().getResources().getString(R.string.irctc_booking_cancellation))) {
                        startActivity(new Intent(getActivity(), AutoLoginActivity.class));
                    } else if (groupHeading.equals(getActivity().getResources().getString(R.string.my_live_journey))) {
                        //comm.respond(new MyLiveJourneyFragment());
                    } else if (groupHeading.equals(getActivity().getResources().getString(R.string.ntes_in_mobile_view))) {
                        comm.respond(new NtesInMobileView());
                    } else if (groupHeading.equals(getActivity().getResources().getString(R.string.user_preferences_app_settings))) {
                        comm.respond(new UserPreferencesFragment());
                    } else if (groupHeading.equals(getActivity().getResources().getString(R.string.quit))) {
                        getActivity().finish();
                    } else if (groupHeading.equals(getActivity().getResources().getString(R.string.about_app))) {
                        comm.respond(new AboutAppFragment());
                    }
                    return;
                }

                NavigationGroupBean group = allListData.get(groupPosition);
                NavigationChildBean child = group.getChildData().get(childPosition);

                final String menu = child.getMenu();

                if (menu.equals(getActivity().getResources().getString(R.string.search_train))) {
                    comm.respond(new SearchTrainFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.search_pnr))) {
                    //comm.respond(new PnrStatusFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.seat_availability))) {
                    comm.respond(new SeatAvailabilityFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.train_timetable))) {
                    comm.respond(new TimeTableInputFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.fare_enquiry))) {
                    comm.respond(new FareEnquiryInputFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.seat_map))) {
                    comm.respond(new SeatMapFragment(0));
                } else if (menu.equals(getActivity().getResources().getString(R.string.live_station))) {
                    comm.respond(new LiveStationsFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.curr_book_avl))) {
                    comm.respond(new CurrentBookingAvlFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.coach_composition))) {
                    comm.respond(new CoachCompositionFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.rec_div_can))) {
                    /*OPERATION_FLAG = Constants.RESCHEDULED;
                    if (InternetChecking.isNetWorkOn(getActivity())) {
                        specialTrainTask = new SpecialTrainTask(getActivity(), LeftDrawerFragment.this, Constants.RESCHEDULED);
                        specialTrainTask.execute();
                    } else {
                        InternetChecking.noInterNetToast(getActivity());
                    }*/
                    comm.respond(new ResDivCanFragmentNew());
                } else if (menu.equals(getActivity().getResources().getString(R.string.grp))) {
                    if (InternetChecking.isNetWorkOn(getActivity())) {
                        startActivity(UtilsMethods.grp(getActivity()));
                    } else {
                        InternetChecking.showNoInternetPopup(getActivity());
                    }
                } else if (menu.equals(getActivity().getResources().getString(R.string.live_trains))) {
                    comm.respond(new LiveTrainInputFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.book_tasty_meal))) {
                    //comm.respond(new BookTastyMealFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.station_alarm))) {
                    comm.respond(new AlarmFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.locate_nearby_stations))) {
                    locateNearByStations();
                } else if (menu.equals(getActivity().getResources().getString(R.string.about_app))) {
                    comm.respond(new AboutAppFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.special_trains))) {
                    comm.respond(new SpecialTrainFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.indian_rail_customer_care))) {
                    comm.respond(new CustomerCareFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.railway_menu_price))) {
                    comm.respond(new RailwayMenuAndPriceList());
                } else if (menu.equals(getActivity().getResources().getString(R.string.locate_near_by_trains))) {
                    OPERATION_FLAG = Constants.NEAR_BY_TRAINS;
                    locateNearByTrains();
                } else if (menu.equals(getActivity().getResources().getString(R.string.pnr_conf_predictor))) {
                    comm.respond(new PnrConfPredictorFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.seat_birth_calculator))) {
                    comm.respond(new SeatBirthCalculatorFrag());
                } else if (menu.equals(getActivity().getResources().getString(R.string.track_your_train))) {
                    comm.respond(new TrackYourTrainFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.speedometer))) {
                    comm.respond(new SpeedMeterFragment());
                } else if (menu.equals(getActivity().getResources().getString(R.string.station_information))) {
                    comm.respond(new StationInformationInputFrag());
                }

            }
        }, 300);
    }

    public static String OPERATION_FLAG;


    private void locateNearByStations() {

        Location l = UtilsMethods.checkLocation(getActivity());

        if (l != null) {
            getNearByStationTask = new GetNearByStationTask(getActivity(), new LatLng(l.getLatitude(), l.getLongitude()), Constants.NEAR_BY_STATION_MAX, LeftDrawerFragment.this);
            getNearByStationTask.execute();

        } else {
            locationNotFound();
        }
    }

    @Override
    public void currentLocationFound(Location cLocation) {

        if (currentLocation != null) {
            getNearByStationTask = new GetNearByStationTask(getActivity(), new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), Constants.NEAR_BY_STATION_MAX, LeftDrawerFragment.this);
            getNearByStationTask.execute();
        }
    }


    @Override
    public void locationNotFound() {
        Toast.makeText(getActivity(), "Unable to detect Location", Toast.LENGTH_SHORT).show();
    }


    private void locateNearByTrains() {

        Location l = UtilsMethods.checkLocation(getActivity());

        if (l != null) {
            getNearByTrainTask = new LocateNearByTrainTask(getActivity(), l, LeftDrawerFragment.this);
            getNearByTrainTask.execute();
        } else {
            locationNotFound();
        }
    }


    public void updateNearByTrainsUI(LiveStationNewBean bean, String station, String time) {
        if(bean != null) {
            comm.respond(new LiveStationToNextFrag(bean, station, time, getActivity().getResources().getString(R.string.locate_near_by_trains)));
        }
    }


    @Override
    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show() ;
    }

    @Override
    public void updateUI(Map<String, NearByStationBean> map) {
        comm.respond(new LocateNearByStations(map));
    }


    // collecting data from resources for side menus
    public List<NavigationGroupBean> fillData() {

        String[] groupHeadings = getActivity().getResources().getStringArray(R.array.drawer_group_heading_array);
        int[] headingIcons = {R.drawable.un_home, R.drawable.un_search_pnr, R.drawable.un_search_train, R.drawable.un_live_train,
                R.drawable.un_seat_avl, R.drawable.un_irctc_booking, R.drawable.un_track_your_train,
                R.drawable.un_coach_comp,/* R.drawable.un_my_live_journey, */ R.drawable.un_emblem_india,
                R.drawable.un_user_pref, R.drawable.un_customer_care, R.drawable.un_feedback, R.drawable.app_quit};

        int[] childTextArray = {R.array.drawer_child_text_array_home, R.array.drawer_child_text_array_pnr, R.array.drawer_child_text_array_trains,
                R.array.drawer_child_text_array_live, R.array.drawer_child_text_array_seat_fare, R.array.drawer_child_text_array_irctc,
                R.array.drawer_child_text_array_advance, R.array.drawer_child_text_array_misc, /*R.array.drawer_child_text_array_live_journey,*/
                R.array.drawer_child_text_array_ntes, R.array.drawer_child_text_array_user_p, R.array.drawer_child_text_array_help,
                R.array.drawer_child_text_array_app, R.array.drawer_child_text_array_quit};

        int[] homeChildIconsUn = {R.drawable.un_home};
        int[] pnrChildIconsUn = {R.drawable.un_trips};
        int[] trainsChildIconsUn = {R.drawable.un_search_train, R.drawable.un_time_table, R.drawable.un_special_train, R.drawable.un_rescheduled_train};
        int[] liveChildIconsUn = {R.drawable.un_live_train, R.drawable.un_live_station};
        int[] seatFareChildIconsUn = {R.drawable.un_seat_avl, R.drawable.un_current_avail, R.drawable.un_fare_enquiry};
        int[] irctcChildIconsUn = {R.drawable.un_irctc_booking};
        int[] advanceChildIconsUn = {R.drawable.un_track_your_train, R.drawable.un_locate_near_by_stations, R.drawable.un_locate_near_by_trains, R.drawable.un_alarm_icon, R.drawable.un_speedometer, R.drawable.un_pnr_predictor};
        int[] miscChildIconsUn = {R.drawable.un_coach_comp, /*R.drawable.un_birth_calculator, R.drawable.un_railway_menu_price_list, */R.drawable.station_info, R.drawable.un_seat_map};
        /*int[] myLiveChildIconsUn = {R.drawable.un_my_live_journey};*/
        int[] ntesChildIconsUn = {R.drawable.un_emblem_india};
        int[] prefChildIconsUn = {R.drawable.un_user_pref};
        int[] helpChildIconsUn = {R.drawable.un_customer_care, R.drawable.un_grp};
        int[] appChildIconsUn = {R.drawable.un_about_app, R.drawable.un_help, R.drawable.un_home};
        int[] quitChildIconUn = {R.drawable.app_quit};

        List<int[]> unSelectedIcons = new ArrayList<>();

        unSelectedIcons.add(homeChildIconsUn);
        unSelectedIcons.add(pnrChildIconsUn);
        unSelectedIcons.add(trainsChildIconsUn);
        unSelectedIcons.add(liveChildIconsUn);
        unSelectedIcons.add(seatFareChildIconsUn);
        unSelectedIcons.add(irctcChildIconsUn);
        unSelectedIcons.add(advanceChildIconsUn);
        unSelectedIcons.add(miscChildIconsUn);
        /*unSelectedIcons.add(myLiveChildIconsUn);*/
        unSelectedIcons.add(ntesChildIconsUn);
        unSelectedIcons.add(prefChildIconsUn);
        unSelectedIcons.add(helpChildIconsUn);
        unSelectedIcons.add(appChildIconsUn);
        unSelectedIcons.add(quitChildIconUn);

        List<NavigationGroupBean> nList = new ArrayList<>();

        for (int i = 0; i < groupHeadings.length; i++) {

            String[] childArr = getActivity().getResources().getStringArray(childTextArray[i]);
            int[] un_icons = unSelectedIcons.get(i);

            List<NavigationChildBean> cList = new ArrayList<>();

            for (int j = 0; j < childArr.length; j++) {

                NavigationChildBean childHolder = new NavigationChildBean();

                childHolder.setMenu(childArr[j]);
                childHolder.setIcon(un_icons[j]);

                cList.add(childHolder);
            }

            NavigationGroupBean groupHolder = new NavigationGroupBean();

            groupHolder.setSelected(false);
            groupHolder.setGroupIndicator(R.drawable.down_arrow);
            groupHolder.setGroupName(groupHeadings[i]);
            groupHolder.setIcon(headingIcons[i]);
            groupHolder.setChildData(cList);

            if (i == 0 || i == 1 || i == 5 || i == 8 || i == 11 || i == 9 || i == 12) {
                groupHolder.setExpandable(false);
            } else {
                groupHolder.setExpandable(true);
            }

            nList.add(groupHolder);
        }

        return nList;
    }


    public void setUp(DrawerLayout dLayout, final Toolbar toolbar, int layoutID) {

        containerLayout = getActivity().findViewById(layoutID);

        mDrawerLayout = dLayout;
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

        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return false;
        }
        return true;
    }
}
