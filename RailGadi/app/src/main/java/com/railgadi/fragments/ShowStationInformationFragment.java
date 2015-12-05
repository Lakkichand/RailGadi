package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.async.AmenitiesTask;
import com.railgadi.beans.NearByAmenitiesBean;
import com.railgadi.beans.StationInformationBean;
import com.railgadi.interfaces.IAmenitiesConnector;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.InternetChecking;

import java.util.List;

public class ShowStationInformationFragment extends Fragment implements IAmenitiesConnector {

    private View rootView ;

    private IFragReplaceCommunicator comm ;

    private AmenitiesTask amenitiesTask ;

    private MapView mapView ;

    private GoogleMap googleMap ;

    private StationInformationBean stationDetailBean ;

    private TextView stationCode, stationName, getAmenitiesButton, track, noOfPlatform, elevation, address, nearByLocation ;


    public ShowStationInformationFragment(StationInformationBean bean) {
        this.stationDetailBean = bean ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView        =   inflater.inflate(R.layout.station_info_with_map, container, false) ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.station_information).toUpperCase());

        comm            =   (IFragReplaceCommunicator) getActivity() ;

        initializeAllViews() ;

        return rootView ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        mapView = (MapView) rootView.findViewById(R.id.halfmapview);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mapView.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        String [] latlng = {stationDetailBean.getStationLatitude(), stationDetailBean.getStationLongitude()} ;

        if(latlng != null && latlng.length == 2) {
            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]))));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]))).zoom(Constants.MAP_DEFAULT_ZOOM).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            Toast.makeText(getActivity(), "Location Not Found", Toast.LENGTH_SHORT).show() ;
        }
    }


    public void initializeAllViews() {

        stationName         =   (TextView) rootView.findViewById(R.id.name) ;
        stationCode         =   (TextView) rootView.findViewById(R.id.code) ;
        track               =   (TextView) rootView.findViewById(R.id.track) ;
        elevation           =   (TextView) rootView.findViewById(R.id.elevation) ;
        address             =   (TextView) rootView.findViewById(R.id.address) ;
        nearByLocation      =   (TextView) rootView.findViewById(R.id.near_by_location) ;
        noOfPlatform        =   (TextView) rootView.findViewById(R.id.no_of_platform) ;

        getAmenitiesButton  =   (TextView) rootView.findViewById(R.id.get_near_by_amenities) ;

        String name         =   stationDetailBean.getStationName() ;
        String nameInHindi  =   stationDetailBean.getStationNameHindi() ;
        String code         =   stationDetailBean.getStationCode() ;
        String sTrack       =   stationDetailBean.getTrack() ;
        String noPlat       =   stationDetailBean.getNoOfPlatforms() ;
        String elev         =   stationDetailBean.getElevation() ;
        String add          =   stationDetailBean.getAddress() ;

        if((name != null && (!name.contains("null"))) && (nameInHindi != null && (! nameInHindi.contains("null"))) ) {
            stationName.setText(stationDetailBean.getStationName() + "\n" + stationDetailBean.getStationNameHindi());
        } else if(name != null && (! name.contains("null"))) {
            stationName.setText(name);
        } else if(nameInHindi != null && (! nameInHindi.contains("null"))) {
            stationName.setText(nameInHindi);
        }

        if(code != null && (! code.contains("null"))) {
            stationCode.setText(stationDetailBean.getStationCode());
        }

        if(sTrack!= null && (! sTrack.contains("null"))) {
            track.setText("Track : " + sTrack);
        }

        if(noPlat != null && (! noPlat.contains("null"))) {
            noOfPlatform.setText("No of Platform : " + noPlat);
        }

        if(elev != null && (! elev.contains("null"))) {
            elevation.setText("Elevation  : " + elev);
        }

        if(add != null && (! add.contains("null"))) {
            address.setText(stationDetailBean.getAddress());
        }

        StationInformationBean.NearByLocation nbl = stationDetailBean.getNearByLocation() ;

        if(nbl != null) {
            if( (nbl.location != null && (! nbl.location.contains("null"))) && (nbl.distance != null && nbl.distance.contains("null")) ) {
                nearByLocation.setText(nbl.location + " : " + nbl.distance);
            }
        }

        getAmenitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stationDetailBean.getStationLongitude() != null && stationDetailBean.getStationLatitude() != null) {
                    if (InternetChecking.isNetWorkOn(getActivity())) {
                        amenitiesTask = new AmenitiesTask(getActivity(), Constants.ATM, stationDetailBean, ShowStationInformationFragment.this);
                        amenitiesTask.execute();
                    } else {
                        InternetChecking.noInterNetToast(getActivity());
                    }
                } else {
                    Toast.makeText(getActivity(), "Location Not Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        if(amenitiesTask != null) {
            amenitiesTask.cancel(true) ;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateAmenities(List<NearByAmenitiesBean> amenitiesList) {

        comm.respond(new AmenitiesMainTabFragment(amenitiesList, stationDetailBean));
    }
}
