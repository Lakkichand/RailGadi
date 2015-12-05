package com.railgadi.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.PagerAdapter;
import com.railgadi.async.GetTimeTableTaskNew;
import com.railgadi.async.ParseStationsTask;
import com.railgadi.beans.RouteChildBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IGettingTimeTableComm;
import com.railgadi.interfaces.IUpdateStationBean;
import com.railgadi.preferences.TimeTablePreferencesHandler;
import com.railgadi.utilities.InternetChecking;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class RouteMapTabsFragment extends Fragment implements IUpdateStationBean, TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener, IGettingTimeTableComm {

    private View rootView;

    private TimeTablePreferencesHandler timeTablePreferencesHandler ;

    private TimeTableNewBean bean ;

    public static MainActivity mainActivity;

    private TabHost mTabHost;
    private ViewPager mViewPager;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<>();
    private PagerAdapter mPagerAdapter;

    public static TimeTableListView timeTableListView ;
    public static RightDrawerFragment rightDrawerFragment;
    public static LeftDrawerFragment leftDrawerFragment;

    private GetTimeTableTaskNew getTimeTableTask ;

    private static TextView tv ;

    private ParseStationsTask parseStationsTask ;


    public RouteMapTabsFragment(TimeTableNewBean bean) {

        this.bean                           =   bean ;

        TimeTableListView.bean              =   bean ;
        TimeTableMapView.bean               =   bean ;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.route_map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem saveMenu = menu.getItem(0);
        MenuItem refreshMenu = menu.getItem(1) ;
        saveMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                saveTimeTable();
                return false;
            }
        });

        refreshMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                refreshTimeTable() ;
                return false;
            }
        }) ;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.train_route_map_fragment, container, false);

        MainActivity.toolbar.setVisibility(View.VISIBLE);
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.train_timetable).toUpperCase());

        rightDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        leftDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);

        timeTablePreferencesHandler = new TimeTablePreferencesHandler(getActivity()) ;

        initialiseTabHost(savedInstanceState);
        initialiseViewPager();

        return rootView;
    }


    private void saveTimeTable() {

        if(timeTablePreferencesHandler.isAlreadyExists(bean.getTrainNumber()) ) {

            TimeTableNewBean saved = timeTablePreferencesHandler.getTimeTableFromPref(bean.getTrainNumber()) ;
            if(! saved.isVisible()) {
                saved.setVisible(true);
                timeTablePreferencesHandler.saveTimeTableToPref(saved);
            } else {
                Toast.makeText(getActivity(), "Already Added to Favourites", Toast.LENGTH_SHORT).show();
            }
        } else {
            bean.setVisible(true);
            timeTablePreferencesHandler.saveTimeTableToPref(bean);
            Toast.makeText(getActivity(), "Saved as Favourites", Toast.LENGTH_SHORT).show() ;
        }
    }


    public void refreshTimeTable() {

        if(InternetChecking.isNetWorkOn(getActivity())) {
            String trainNumber = TimeTableListView.bean.getTrainNumber().trim();
            getTimeTableTask = new GetTimeTableTaskNew(getActivity(), trainNumber, RouteMapTabsFragment.this);
            getTimeTableTask.execute();
        } else {
            InternetChecking.noInterNetToast(getActivity());
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity.toolbar.setVisibility(View.VISIBLE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getTimeTableTask != null) {
            getTimeTableTask.cancel(true) ;
        }
        if(parseStationsTask != null){
            parseStationsTask.cancel(true) ;
        }
    }


    @Override
    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show() ;
    }


    @Override
    public void updateTimeTableUI(TimeTableNewBean bean) {

        this.bean                           =   bean ;

        if(InternetChecking.isNetWorkOn(getActivity())) {
            parseStationsTask = new ParseStationsTask(getActivity(), bean, RouteMapTabsFragment.this);
            parseStationsTask.execute();
        } else {
            InternetChecking.noInterNetToast(getActivity());
        }

        if(timeTablePreferencesHandler.isAlreadyExists(bean.getTrainNumber())) {

            timeTablePreferencesHandler.saveTimeTableToPref(bean);

            Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show() ;

        } else {
            Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show() ;
        }
    }


    private void initialiseTabHost(Bundle args) {

        mTabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup();

        TabInfo tabInfo = null;
        addTabs(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator(Html.fromHtml("List View")), (tabInfo = new TabInfo("Tab1", TimeTableListView.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        addTabs(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator(Html.fromHtml("Map View")), (tabInfo = new TabInfo("Tab2", TimeTableMapView.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);

        mTabHost.setOnTabChangedListener(this);

        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {

            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTypeface(AppFonts.getRobotoMedium(getActivity()));
            tv.setTextColor(getActivity().getResources().getColor(R.color.white));
        }

        mTabHost.getTabWidget().setStripEnabled(true);
        mTabHost.getTabWidget().setRightStripDrawable(R.drawable.color_layer);
        mTabHost.getTabWidget().setLeftStripDrawable(R.drawable.color_layer);
        mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackground(getActivity().getResources().getDrawable(R.drawable.colorfile));

        tv = (TextView) mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).findViewById(android.R.id.title) ;
        tv.setTextColor(getActivity().getResources().getColorStateList(R.color.tab_text_color_list));
    }


    public void initialiseViewPager() {

        List<android.support.v4.app.Fragment> fragments = new Vector<>();

        fragments.add(android.support.v4.app.Fragment.instantiate(getActivity(), TimeTableListView.class.getName()));
        fragments.add(android.support.v4.app.Fragment.instantiate(getActivity(), TimeTableMapView.class.getName()));

        this.mPagerAdapter = new PagerAdapter(getChildFragmentManager(), fragments);
        this.mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        this.mViewPager.setAdapter(this.mPagerAdapter);
        this.mViewPager.setOnPageChangeListener(this);
    }


    private class TabInfo {

        private String tag;
        private Class<?> clss;
        private Bundle args;
        private android.support.v4.app.Fragment fragment;

        TabInfo(String tag, Class<?> clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }

    }

    class TabFactory implements TabHost.TabContentFactory {

        private final Context mContext;

        public TabFactory(Context context) {
            mContext = context;
        }

        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    private void addTabs(RouteMapTabsFragment activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        tabSpec.setContent(activity.new TabFactory(getActivity()));
        tabHost.addTab(tabSpec);
    }


    public void onTabChanged(String tag) {
        int pos = this.mTabHost.getCurrentTab();

        this.mViewPager.setCurrentItem(pos);
        mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.colorfile);

        tv = (TextView) mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).findViewById(android.R.id.title);
        tv.setTextColor(this.getResources().getColorStateList(R.color.tab_text_color_list));
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        int pos = this.mViewPager.getCurrentItem();

        this.mTabHost.setCurrentTab(pos);
        mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.colorfile);

        tv = (TextView) mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).findViewById(android.R.id.title);
        tv.setTextColor(this.getResources().getColorStateList(R.color.tab_text_color_list));
    }


    @Override
    public void onPageSelected(int position) {
        this.mTabHost.setCurrentTab(position);
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {


    }


    @Override
    public void updateStationBeans(List<RouteChildBean> beans) {
        timeTableListView.setDataOnViews(beans);
    }

}