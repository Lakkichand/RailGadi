package com.railgadi.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.PagerAdapter;
import com.railgadi.fonts.AppFonts;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class TripsFragment extends Fragment implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    private View rootView;

    private TabHost mTabHost;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<>();

    private TextView tv ;

    public static UpcomingPnrTab upcomingPnrTab;
    public static CompletedPnrTab completedTrainsTab;
    public static OthersPnrTab othersPnrTab;






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("pnr fragment lifecycle", "onCreate()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.pnr_status).toUpperCase());

        rootView = inflater.inflate(R.layout.pnr_trips_frag, container, false);

        initialiseTabHost(savedInstanceState);
        initialiseViewPager();

        return rootView;
    }

    public void initialiseViewPager() {

        List<android.support.v4.app.Fragment> fragments = new Vector<>();

        if (upcomingPnrTab != null) {
            fragments.add(upcomingPnrTab);
        } else {
            fragments.add(android.support.v4.app.Fragment.instantiate(getActivity(), UpcomingPnrTab.class.getName()));
        }
        if (completedTrainsTab != null) {
            fragments.add(completedTrainsTab);
        } else {
            fragments.add(android.support.v4.app.Fragment.instantiate(getActivity(), CompletedPnrTab.class.getName()));
        }
        if (othersPnrTab != null) {
            fragments.add(othersPnrTab) ;
        } else {
            fragments.add(android.support.v4.app.Fragment.instantiate(getActivity(), OthersPnrTab.class.getName()));
        }

        this.mPagerAdapter = new PagerAdapter(getChildFragmentManager(), fragments);
        this.mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        this.mViewPager.setAdapter(this.mPagerAdapter);
        this.mViewPager.setOnPageChangeListener(this);
    }

    private void initialiseTabHost(Bundle args) {

        mTabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup();

        TabInfo tabInfo = null;
        addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator(Html.fromHtml("Upcoming")), (tabInfo = new TabInfo("Tab1", UpcomingPnrTab.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator(Html.fromHtml("Completed")), (tabInfo = new TabInfo("Tab2", CompletedPnrTab.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator(Html.fromHtml("Others")), (tabInfo = new TabInfo("Tab3", OthersPnrTab.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);

        mTabHost.setOnTabChangedListener(this);

        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTypeface(AppFonts.getRobotoMedium(getActivity()));
            tv.setTextColor(getActivity().getResources().getColor(R.color.white));
            //int size = (int) getActivity().getResources().getDimension(R.dimen.trips_tabs_text_size) ;
            //tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, size-5 );
            tv.setTextSize(getActivity().getResources().getDimension(R.dimen.trips_tabs_text_size));
        }

        mTabHost.getTabWidget().setStripEnabled(true);
        mTabHost.getTabWidget().setRightStripDrawable(R.drawable.color_layer);
        mTabHost.getTabWidget().setLeftStripDrawable(R.drawable.color_layer);
        mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackground(getActivity().getResources().getDrawable(R.drawable.colorfile));

        tv = (TextView) mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).findViewById(android.R.id.title) ;
        tv.setTextColor(getActivity().getResources().getColorStateList(R.color.tab_text_color_list));

    }


    private void addTab(TripsFragment activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
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
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {

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
                //UpcomingTripFragment.upcoming_fragment.refreshAdapterOnUpcomingList();
                break;
            case 1:
                //CompletedTripFragment.complete_fragment.refreshAdapterOnCompleteList();
                break;
            case 2:
                //OthersTripFragment.other_fragment.refreshAdapterOnOthersList();
                break;
        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {


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


    private class TabFactory implements TabHost.TabContentFactory {

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

}
