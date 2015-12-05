package com.railgadi.fragments;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.AlarmListAdapter;
import com.railgadi.async.GetTimeTableTaskNew;
import com.railgadi.beans.AlarmDataBean;
import com.railgadi.beans.RouteChildBean;
import com.railgadi.beans.RouteGroupBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IGettingTimeTableComm;
import com.railgadi.preferences.AlarmPreference;
import com.railgadi.serviceAndReceivers.AlarmReceiver;
import com.railgadi.serviceAndReceivers.AlarmServices;
import com.railgadi.utilities.InternetChecking;

import java.util.ArrayList;
import java.util.List;

public class AlarmFragment extends Fragment implements IGettingTimeTableComm {

    private View rootView;

    private AlarmListAdapter adapter;

    private GetTimeTableTaskNew getTimeTableTask;

    private MenuItem addAlarm;

    private IFragReplaceCommunicator comm;

    private AlarmPreference alarmPreference;

    private List<AlarmDataBean> savedAlarms;

    // views
    private LinearLayout superLayout;
    private ListView allAlarmList;
    private TextView noSavedAlarm;
    private TextView savedAlarm;


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

        if(getTimeTableTask != null) {
            getTimeTableTask.cancel(true) ;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.alarm_frag_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);


        addAlarm = menu.getItem(0);
        addAlarm.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (savedAlarms == null) {

                    comm = (IFragReplaceCommunicator) getActivity();

                    comm.respond(new AddAlarmFragment(null, null));
                } else {
                    Toast.makeText(getActivity(), "You can add only one alarm at a time\nyou can edit existing", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.alarm_main_layout, container, false);

        comm = (IFragReplaceCommunicator) getActivity();

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.station_alarm).toUpperCase() );

        alarmPreference = new AlarmPreference(getActivity());

        initializeAllViews();

        return rootView;
    }


    private void initializeAllViews() {

        allAlarmList = (ListView) rootView.findViewById(R.id.all_alarm_list_view);
        allAlarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                openEditDeleteDialog();
                return true;
            }
        });

        superLayout = (LinearLayout) rootView.findViewById(R.id.super_layout);

        savedAlarm = (TextView) rootView.findViewById(R.id.saved_alarm);
        savedAlarm.setTypeface(AppFonts.getRobotoLight(getActivity()));


        noSavedAlarm = (TextView) rootView.findViewById(R.id.no_saved_alarm);
        noSavedAlarm.setTypeface(AppFonts.getRobotoLight(getActivity()));

        setAdapterOnList();
    }

    private void setAdapterOnList() {

        savedAlarms = alarmPreference.getSavedAlarm();

        if (savedAlarms != null) {
            superLayout.setVisibility(View.VISIBLE);
            noSavedAlarm.setVisibility(View.GONE);

            if(adapter != null) {
                adapter.changeDataSet(savedAlarms) ;
                allAlarmList.setAdapter(adapter );
            } else {
                adapter = new AlarmListAdapter(getActivity(), savedAlarms);
                allAlarmList.setAdapter(adapter);
            }

        } else {
            superLayout.setVisibility(View.GONE);
            noSavedAlarm.setVisibility(View.VISIBLE);
            allAlarmList.setAdapter(null);
        }
    }

    @Override
    public void updateTimeTableUI(TimeTableNewBean bean) {
        List<RouteChildBean> childList       =   new ArrayList<>() ;
        for(RouteGroupBean rgb : bean.getGroupBeans()) {
            for(RouteChildBean cb : rgb.getChildList()) {
                childList.add(cb);
            }
        }
        comm.respond(new AddAlarmFragment(bean.getTrainNumber(), childList));
    }

    @Override
    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show();
    }

    private void openEditDeleteDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_delete_alarm);
        dialog.setCancelable(true);

        TextView edit = (TextView) dialog.findViewById(R.id.edit);
        TextView delete = (TextView) dialog.findViewById(R.id.delete);

        edit.setTypeface(AppFonts.getRobotoLight(getActivity()));
        delete.setTypeface(AppFonts.getRobotoLight(getActivity()));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(InternetChecking.isNetWorkOn(getActivity())) {
                    getTimeTableTask = new GetTimeTableTaskNew(getActivity(), savedAlarms.get(0).getTrainNumber(), AlarmFragment.this);
                    getTimeTableTask.execute();
                } else {
                    InternetChecking.noInterNetToast(getActivity()) ;
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                alarmPreference.deleteSavedAlarm();
                setAdapterOnList();
                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                if (isMyServiceRunning(AlarmReceiver.class)) {
                    AlarmServices.playAlarm = false;
                    getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(), AlarmServices.class));
                }
            }
        });

        dialog.show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
