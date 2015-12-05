package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.adapters.MessageAdapter;
import com.railgadi.async.DeletePnrTask;
import com.railgadi.async.PullToRefreshListTask;
import com.railgadi.beans.PnrPreferenceBean;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.comparators.PnrListComparator;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IPnrDeleteCommunicator;
import com.railgadi.preferences.PnrPreferencesHandler;
import com.railgadi.preferences.RemovablePnrPreference;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UpcomingPnrTab extends Fragment implements IPnrDeleteCommunicator {

    private View rootView;
    private ListView listview;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MessageAdapter adapter;

    public Map<String, PnrStatusNewBean> allUpcomings;

    private PnrPreferencesHandler preferencesHandler;

    private IFragReplaceCommunicator comm;

    private PnrPreferenceBean selectedBean;


    private DeletePnrTask deletePnrTask ;
    private RemovablePnrPreference removablePnrPref ;
    private PullToRefreshListTask pullToRefreshListTask;


    // default constructor
    public UpcomingPnrTab() {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (pullToRefreshListTask != null) {
            pullToRefreshListTask.cancel(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.upcoming_messages_layout, container, false);

        comm = (IFragReplaceCommunicator) getActivity();

        preferencesHandler = new PnrPreferencesHandler(getActivity());

        initializeAllViews();
        refreshUpcomingAdapter();

        return rootView;
    }


    public void initializeAllViews() {
/*

        upcomingLayout = (LinearLayout) rootView.findViewById(R.id.upcoming_layout) ;
        upcomingLayout.setAlpha(Color.TRANSPARENT);
*/

        listview = (ListView) rootView.findViewById(R.id.upcoming_msg_list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.upcoming_swiperefresh);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Map.Entry<String, PnrStatusNewBean>> list = new ArrayList<>(allUpcomings.entrySet());
                Collections.sort(list, new PnrListComparator());
                comm.respond(new ShowExistingPnr(Constants.UPCOMING, list.get(position).getValue()));
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                List<Map.Entry<String, PnrStatusNewBean>> entrySet = new ArrayList<>(allUpcomings.entrySet());
                Collections.sort(entrySet, new PnrListComparator());

                PnrPreferenceBean bean = new PnrPreferenceBean();
                bean.setPnrNumber(entrySet.get(position).getKey());
                bean.setPnrObject(entrySet.get(position).getValue());
                bean.setFlag(Constants.UPCOMING);

                selectedBean = bean;

                //new DeletePnrConfirmDialog(getActivity(), UpcomingPnrTab.this);
                UtilsMethods.confirmDeleteDialog(getActivity(), UpcomingPnrTab.this);

                return true;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(true);

                if (allUpcomings == null) {

                    Toast.makeText(getActivity(), "No Data for Refresh", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                if (InternetChecking.isNetWorkOn(getActivity())) {

                    pullToRefreshListTask = new PullToRefreshListTask(getActivity(), allUpcomings, UpcomingPnrTab.this, swipeRefreshLayout);
                    pullToRefreshListTask.execute();

                } else {
                    InternetChecking.noInterNetToast(getActivity());
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    // method of PnrDeleteCommunicator interface
    @Override
    public void wantToDelete(boolean flag) {

        if (flag) {

            if(InternetChecking.isNetWorkOn(getActivity())) {
                List<String> pnrList = new ArrayList<>(1) ;
                pnrList.add(selectedBean.getPnrNumber());
                deletePnrTask = new DeletePnrTask(getActivity(), pnrList, true) ;
                deletePnrTask.execute() ;
            } else {
                removablePnrPref = new RemovablePnrPreference(getActivity()) ;
                removablePnrPref.addToRemovable(selectedBean.getPnrNumber()) ;
            }

            allUpcomings.remove(selectedBean.getPnrNumber());
            preferencesHandler.removeFromPreferences(selectedBean);
            refreshUpcomingAdapter();
        }
    }

    public void refreshUpcomingAdapter() {

        allUpcomings = preferencesHandler.getAllUpcomingPnr();

        if (allUpcomings == null) {
            listview.setAdapter(null);
        } else {
            if (adapter != null) {
                adapter.changeDataSet(allUpcomings);
                listview.setAdapter(adapter);
            } else {
                adapter = new MessageAdapter(getActivity(), allUpcomings, Constants.UPCOMING);
                listview.setAdapter(adapter);
            }
        }
    }
}