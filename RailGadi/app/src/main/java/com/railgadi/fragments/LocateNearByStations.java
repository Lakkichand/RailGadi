package com.railgadi.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.NearByStationAdapter;
import com.railgadi.beans.NearByStationBean;
import com.railgadi.comparators.NearByComparator;
import com.railgadi.utilities.AppLocationService;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LocateNearByStations extends Fragment implements LocationListener {

    private View rootView;

    private ListView listView;
    private TextView selectRange, km, impStationText;
    private SeekBar seekBar;
    private CheckBox impStationSwitch;

    private Location currentLocation;
    private AppLocationService appLocationService;
    private LocationManager locationManager;
    private String bestProvider;

    private NearByStationAdapter adapter;

    private List<Map.Entry<String, NearByStationBean>> beanList;
    private List<Map.Entry<String, NearByStationBean>> updatedBeanList;
    private List<Map.Entry<String, NearByStationBean>> impStationList;

    private Map<String, NearByStationBean> runningMap;

    private String[] impStnCodes;

    public LocateNearByStations(Map<String, NearByStationBean> map) {

        this.runningMap = map;
        updatedBeanList = new ArrayList<>();

        beanList = new ArrayList<>(map.entrySet());

        runningMap = new HashMap<>();

        for (Map.Entry<String, NearByStationBean> entry : map.entrySet()) {
            if (entry.getValue().getDistance() <= (Constants.NEAR_BY_STATION_MIN)) {
                updatedBeanList.add(entry);
                runningMap.put(entry.getKey(), entry.getValue());
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        currentLocation = UtilsMethods.checkLocation(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        locationManager.removeUpdates(this);
        locationManager = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.near_by_station_layout, container, false);

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.locate_nearby_stations).toUpperCase());

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.MIN_LOCATION_UPDATE_TIME, 0, this);

        impStnCodes = getActivity().getResources().getStringArray(R.array.important_stations);
        importantSetup();

        initializeAllViews();
        setAdapterOnList(updatedBeanList);

        return rootView;
    }


    private void initializeAllViews() {

        listView = (ListView) rootView.findViewById(R.id.near_station_list);

        selectRange = (TextView) rootView.findViewById(R.id.select_range);

        seekBar = (SeekBar) rootView.findViewById(R.id.distance_seekbar);

        km = (TextView) rootView.findViewById(R.id.km);

        impStationText = (TextView) rootView.findViewById(R.id.important_station_text);

        impStationSwitch = (CheckBox) rootView.findViewById(R.id.important_station_switch);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                km.setText(progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int d = seekBar.getProgress();

                updatedBeanList = new ArrayList<>();
                runningMap = new HashMap<>();

                for (Map.Entry<String, NearByStationBean> entry : beanList) {
                    if (entry.getValue().getDistance() <= d) {
                        updatedBeanList.add(entry);
                        runningMap.put(entry.getKey(), entry.getValue());
                    }
                }

                if (impStationSwitch.isChecked()) {
                    importantSetup();
                    setAdapterOnList(impStationList);
                } else {
                    setAdapterOnList(updatedBeanList);
                }
            }
        });

        impStationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    importantSetup();
                    if (impStationList.size() < 0) {
                        Toast.makeText(getActivity(), "No important Station", Toast.LENGTH_SHORT).show();
                        impStationSwitch.setChecked(false);
                    } else {
                        setAdapterOnList(impStationList);
                    }
                } else if (!isChecked) {
                    setAdapterOnList(updatedBeanList);
                }
            }
        });
    }

    private void importantSetup() {

        impStationList = new ArrayList<>();

        for (String code : impStnCodes) {

            if (runningMap.keySet().contains(code)) {

                Map<String, NearByStationBean> m = new HashMap<>();
                m.put(code, runningMap.get(code));
                impStationList.addAll(m.entrySet());
            }
        }
    }

    private void setAdapterOnList(List<Map.Entry<String, NearByStationBean>> updatedBeanList) {

        Collections.sort(updatedBeanList, new NearByComparator());

        if (adapter != null) {
            adapter.changeDataSet(updatedBeanList);
            listView.setAdapter(adapter);
        } else {
            adapter = new NearByStationAdapter(getActivity(), updatedBeanList);
            listView.setAdapter(adapter);
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        if (updatedBeanList != null) {

            LatLng c = new LatLng(location.getLatitude(), location.getLongitude());

            for (Map.Entry<String, NearByStationBean> entry : updatedBeanList) {
                LatLng stationLatLng = new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude());

                double dis = LatLngTool.distance(c, stationLatLng, LengthUnit.KILOMETER);

                entry.getValue().setDistance(dis);
            }
            setAdapterOnList(updatedBeanList);
            impStationSwitch.setChecked(false);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

        currentLocation = UtilsMethods.checkLocation(getActivity());
    }

    @Override
    public void onProviderDisabled(String provider) {
        currentLocation = UtilsMethods.checkLocation(getActivity());
    }
}
