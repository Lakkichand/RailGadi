package com.railgadi.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.SixDaysTrainListAdapter;
import com.railgadi.beans.SearchTrainInputBean;
import com.railgadi.beans.SearchTrainResultBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IFindTrainComm;
import com.railgadi.utilities.Constants;

import java.util.Collections;
import java.util.List;


public class SearchTrainToNextFrag extends Fragment implements IFindTrainComm {

    private View rootView;

    private ListView stationsList;
    private SixDaysTrainListAdapter adapter;

    private int NEW_COLOR, OLD_COLOR;
    private static final int LIGHT_ALPHA = 80;
    private static final int DARK_ALPHA = 255;

    private LinearLayout depSortButton, arrSortButton, durSortButton;
    private View depSortView, arrSortView, durSortView;
    private ImageView depImageView, arrImageView, durImageView;
    private TextView depSortText, arrSortText, durSortText;


    private SearchTrainResultBean resultBean;
    private SearchTrainInputBean input;

    public static RightDrawerFragment rightDrawerFragment;
    public static LeftDrawerFragment leftDrawerFragment;

    private MenuItem returnJourneyMenu;

    private List<SearchTrainResultBean.SearchTrain> searchTrainList ;

    public SearchTrainToNextFrag(SearchTrainResultBean resultBean, SearchTrainInputBean input) {

        this.resultBean = resultBean;
        this.input = input;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.return_journey_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        returnJourneyMenu = menu.getItem(0);
        returnJourneyMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                clickOnReturnJourney();
                return false;
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.all_train_list_findtrainfrag, container, false);

        RightDrawerFragment.recentSearchFlag = false;
        RightDrawerFragment.returnJourneyFlag = true;
        RightDrawerFragment.trainListFragReference = SearchTrainToNextFrag.this;

        rightDrawerFragment.showReturnJourney();
        rightDrawerFragment.setData(input);
        rightDrawerFragment.setFonts();
        rightDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);

        leftDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);

        toolbarTitle = input.getFromStationCode() + " to " + input.getToStationCode();

        MainActivity.toolbar.setTitle(toolbarTitle.toUpperCase());

        initializeAllViews();
        setDataOnViews(resultBean, input);

        return rootView;
    }

    //handling menu event
    public void clickOnReturnJourney() {
        rightDrawerFragment.mDrawerLayout.openDrawer(Gravity.RIGHT);
    }

    public class MySorting {
        private String type;
        private boolean flag;

        public MySorting(String type, boolean flag) {
            this.type = type;
            this.flag = flag;
        }

        public String getType() {
            return this.type;
        }

        public boolean getFlag() {
            return this.flag;
        }
    }

    public boolean depFlag = false;
    public boolean arrFlag = false;
    public boolean durFlag = false;
    public boolean depClickFlag = true ;
    public boolean arrClickFlag = false ;
    public boolean durClickFlag = false ;

    public class ClickOnSortButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.departure_sort_button: {

                    depSortText.setTextColor(OLD_COLOR);
                    depImageView.setImageAlpha(DARK_ALPHA);
                    arrSortText.setTextColor(NEW_COLOR);
                    arrImageView.setImageAlpha(LIGHT_ALPHA);
                    durSortText.setTextColor(NEW_COLOR);
                    durImageView.setImageAlpha(LIGHT_ALPHA);

                    depSortView.setVisibility(View.VISIBLE);
                    arrSortView.setVisibility(View.GONE);
                    durSortView.setVisibility(View.GONE);

                    arrClickFlag = false ;
                    durClickFlag = false ;

                    if(! depClickFlag) {
                        MySorting mySorting = new MySorting(Constants.DEP_TYPE, depFlag);
                        SearchTrainResultBean.SearchTrain.mySort = mySorting;
                        adapter.sortDataSet();
                        depClickFlag = true ;
                        break ;
                    }

                    depFlag = !depFlag;
                    if (depFlag) {
                        depImageView.setImageResource(R.drawable.polygon_up);
                    } else {
                        depImageView.setImageResource(R.drawable.polygon_down);
                    }

                    MySorting mySorting = new MySorting(Constants.DEP_TYPE, depFlag);
                    SearchTrainResultBean.SearchTrain.mySort = mySorting;
                    adapter.sortDataSet();
                    break;
                }
                case R.id.arrival_sort_button: {

                    depSortText.setTextColor(NEW_COLOR);
                    depImageView.setImageAlpha(LIGHT_ALPHA);
                    arrSortText.setTextColor(OLD_COLOR);
                    arrImageView.setImageAlpha(DARK_ALPHA);
                    durSortText.setTextColor(NEW_COLOR);
                    durImageView.setImageAlpha(LIGHT_ALPHA);

                    arrSortView.setVisibility(View.VISIBLE);
                    durSortView.setVisibility(View.GONE);
                    depSortView.setVisibility(View.GONE);

                    depClickFlag = false ;
                    durClickFlag = false ;

                    if(! arrClickFlag) {
                        MySorting mySorting = new MySorting(Constants.ARR_TYPE, arrFlag);
                        SearchTrainResultBean.SearchTrain.mySort = mySorting;
                        adapter.sortDataSet();
                        arrClickFlag = true ;
                        break ;
                    }

                    arrFlag = !arrFlag;
                    if (arrFlag) {
                        arrImageView.setImageResource(R.drawable.polygon_up);
                    } else {
                        arrImageView.setImageResource(R.drawable.polygon_down);
                    }

                    MySorting mySorting = new MySorting(Constants.ARR_TYPE, arrFlag);
                    SearchTrainResultBean.SearchTrain.mySort = mySorting;
                    adapter.sortDataSet();
                    break;
                }
                case R.id.duration_sort_button: {

                    depSortText.setTextColor(NEW_COLOR);
                    depImageView.setImageAlpha(LIGHT_ALPHA);
                    arrSortText.setTextColor(NEW_COLOR);
                    arrImageView.setImageAlpha(LIGHT_ALPHA);
                    durSortText.setTextColor(OLD_COLOR);
                    durImageView.setImageAlpha(DARK_ALPHA);

                    durSortView.setVisibility(View.VISIBLE);
                    arrSortView.setVisibility(View.GONE);
                    depSortView.setVisibility(View.GONE);

                    depClickFlag = false ;
                    arrClickFlag = false ;

                    if(! durClickFlag) {
                        MySorting mySorting = new MySorting(Constants.DUR_TYPE, durFlag);
                        SearchTrainResultBean.SearchTrain.mySort = mySorting;
                        adapter.sortDataSet();
                        durClickFlag = true ;
                        break ;
                    }

                    durFlag = !durFlag;
                    if (durFlag) {
                        durImageView.setImageResource(R.drawable.polygon_up);
                    } else {
                        durImageView.setImageResource(R.drawable.polygon_down);
                    }

                    MySorting mySorting = new MySorting(Constants.DUR_TYPE, durFlag);
                    SearchTrainResultBean.SearchTrain.mySort = mySorting;
                    adapter.sortDataSet();
                    break;
                }
            }
        }
    }


    public void initializeAllViews() {

        // sorting buttons (linearLayouts)
        depSortButton = (LinearLayout) rootView.findViewById(R.id.departure_sort_button);
        depSortButton.setOnClickListener(new ClickOnSortButton());
        arrSortButton = (LinearLayout) rootView.findViewById(R.id.arrival_sort_button);
        arrSortButton.setOnClickListener(new ClickOnSortButton());
        durSortButton = (LinearLayout) rootView.findViewById(R.id.duration_sort_button);
        durSortButton.setOnClickListener(new ClickOnSortButton());

        // sorting button triangle images
        depImageView = (ImageView) rootView.findViewById(R.id.departure_image_view);
        arrImageView = (ImageView) rootView.findViewById(R.id.arrival_image_view);
        durImageView = (ImageView) rootView.findViewById(R.id.duration_image_view);

        // sorting button bottom views
        depSortView = rootView.findViewById(R.id.departure_view);
        arrSortView = rootView.findViewById(R.id.arrival_view);
        durSortView = rootView.findViewById(R.id.duration_view);

        // sorting textviews
        depSortText = (TextView) rootView.findViewById(R.id.dep_sort_text);
        depSortText.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        arrSortText = (TextView) rootView.findViewById(R.id.arr_sort_text);
        arrSortText.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        durSortText = (TextView) rootView.findViewById(R.id.dur_sort_text);
        durSortText.setTypeface(AppFonts.getRobotoMedium(getActivity()));

        OLD_COLOR = depSortText.getCurrentTextColor();
        NEW_COLOR = Color.argb(80, Color.red(OLD_COLOR), Color.green(OLD_COLOR), Color.blue(OLD_COLOR));

        arrSortText.setTextColor(NEW_COLOR);
        arrImageView.setAlpha(LIGHT_ALPHA);
        durSortText.setTextColor(NEW_COLOR);
        durImageView.setAlpha(LIGHT_ALPHA);

        stationsList = (ListView) rootView.findViewById(R.id.available_train_list);

    }

    private String toolbarTitle;

    public void setDataOnViews(SearchTrainResultBean resultBean, SearchTrainInputBean input) {

        this.resultBean = resultBean;
        this.input = input;

        toolbarTitle = this.input.getFromStationCode() + " to " + this.input.getToStationCode();
        setAdapterOnStationList(resultBean, input);

        MainActivity.toolbar.setTitle(toolbarTitle.toUpperCase());

        //rightDrawerFragment.mDrawerLayout.closeDrawer(Gravity.RIGHT);
    }


    public void setAdapterOnStationList(SearchTrainResultBean resultBean, SearchTrainInputBean input) {

        if(searchTrainList == null) {
            searchTrainList = resultBean.getSearchTrainList();
        }

        Collections.sort(searchTrainList);
        adapter = new SixDaysTrainListAdapter(getActivity(), input, searchTrainList);
        stationsList.setAdapter(adapter);

        rightDrawerFragment.setData(input);
    }

    @Override
    public void updateSearchTrainListUI(SearchTrainResultBean searchTrainResult, SearchTrainInputBean input) {

        setDataOnViews(searchTrainResult, input);
    }

    @Override
    public void updateException(String exceptionType) {
        Toast.makeText(getActivity(), exceptionType, Toast.LENGTH_SHORT).show();
    }

}
