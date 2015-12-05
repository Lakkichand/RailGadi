package com.railgadi.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.railgadi.R;
import com.railgadi.beans.RouteChildBean;
import com.railgadi.beans.RouteGroupBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.routedirections.Route;
import com.railgadi.routedirections.Routing;
import com.railgadi.routedirections.RoutingListener;
import com.railgadi.utilities.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeTableMapView extends Fragment implements RoutingListener {

    private View rootView;

    private GoogleMap googleMap;
    private MapView mapView;

    private double srcLat, srcLong;
    private LatLng start, end;

    public static TimeTableNewBean bean;

    private List<RouteChildBean> childList;

    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.time_table_map_view, container, false);

        activity = getActivity();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
            mapView.onLowMemory();
    }


    private List<Marker> markerList = new ArrayList<>();
    private Map<Marker, Integer> markerHmap = new HashMap<>();
    public static int pos = -1;
    private boolean markerClick = false;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        mapView = (MapView) rootView.findViewById(R.id.fullmapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();



        childList = new ArrayList<>();
        for (RouteGroupBean rgb : bean.getGroupBeans()) {
            for (RouteChildBean cb : rgb.getChildList()) {
                childList.add(cb);
            }
        }

        try {
            MapsInitializer.initialize(activity.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mapView.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        for (int i = 0; i < childList.size(); i++) {

            RouteChildBean entry = childList.get(i);

            if (i == 0) {
                srcLat = Double.valueOf(entry.getLatitude());
                srcLong = Double.valueOf(entry.getLongitude());
            }

            final View smallMarker = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
            TextView onlyCode = (TextView) smallMarker.findViewById(R.id.only_code);
            onlyCode.setText(entry.getStationCode());

            String l = entry.getLatitude();
            String lo = entry.getLongitude();

            if (l != null && lo != null) {

                double lat = Double.valueOf(entry.getLatitude());
                double lng = Double.valueOf(entry.getLongitude());

                Marker customMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), smallMarker))));

                markerList.add(customMarker);
                markerHmap.put(customMarker, i);
            }
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {

                mapClick();
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {

                if (markerClick == false) {
                    pos = markerHmap.get(arg0);
                    markerClick(pos);
                } else {
                    mapClick();
                    pos = markerHmap.get(arg0);
                    markerClick(pos);
                }

                return false;
            }
        });

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                // TODO Auto-generated method stub
                View v = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
                if (markerList != null) {

                    TextView code = (TextView) v.findViewById(R.id.code);
                    TextView stName = (TextView) v.findViewById(R.id.name);
                    TextView arrDep = (TextView) v.findViewById(R.id.arr_dep);
                    TextView hault = (TextView) v.findViewById(R.id.hault);
                    TextView distance = (TextView) v.findViewById(R.id.dist_from_src);

                    RouteChildBean station = childList.get(pos);

                    code.setText(station.getStationCode());
                    stName.setText(station.getStationName());
                    arrDep.setText("A: " + station.getArrival() + "  D:" + station.getDeparture());
                    hault.setText("Halt : " + station.getHaltTime());
                    distance.setText(station.getDistance() + " Km from Source");

                    if (pos == 0 || pos == childList.size() - 1) {
                        hault.setVisibility(View.GONE);
                    } else {
                        hault.setVisibility(View.VISIBLE);
                    }

                    code.setTypeface(AppFonts.getRobotoReguler(getActivity()));
                    stName.setTypeface(AppFonts.getRobotoReguler(getActivity()));
                    arrDep.setTypeface(AppFonts.getRobotoReguler(getActivity()));
                    hault.setTypeface(AppFonts.getRobotoReguler(getActivity()));
                    distance.setTypeface(AppFonts.getRobotoReguler(getActivity()));
                }
                return v;
            }

            @Override
            public View getInfoContents(Marker arg0) {

                final View infoview = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
                return infoview;
            }
        });


        PolylineOptions line = new PolylineOptions();

        for (RouteChildBean bean : childList) {

            if (bean.getLatitude() != null && bean.getLongitude() != null) {
                LatLng ll = new LatLng(Double.parseDouble(bean.getLatitude()), Double.parseDouble(bean.getLongitude()));
                line.add(ll);
            }
        }

        line.width(5);
        line.color(Color.BLACK);
        line.geodesic(true);

        RouteChildBean child = childList.get(0);
        start = new LatLng(Double.parseDouble(child.getLatitude()), Double.parseDouble(child.getLongitude()));
        child = childList.get(childList.size() - 1);
        end = new LatLng(Double.parseDouble(child.getLatitude()), Double.parseDouble(child.getLongitude()));

        for (int i = 0; i < childList.size() - 1; i++) {

            Routing routing = new Routing(Routing.TravelMode.TRANSIT);
            routing.registerListener(TimeTableMapView.this);
            routing.execute(start, end);

        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(srcLat, srcLong)).zoom(Constants.MAP_DEFAULT_ZOOM).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        markerClick(pos);
    }


    public void mapClick() {
        markerClick = false;
        final View v = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        if (pos >= 0 && pos < markerList.size()) {
            double lat = Double.valueOf(childList.get(pos).getLatitude());
            double lng = Double.valueOf(childList.get(pos).getLongitude());

            LatLng latLag = new LatLng(lat, lng);
            markerList.get(pos).remove();

            TextView onlyCode = (TextView) v.findViewById(R.id.only_code);
            onlyCode.setTypeface(AppFonts.getRobotoReguler(getActivity()));
            onlyCode.setText(childList.get(pos).getStationCode());

            MarkerOptions mMarkerOptions = new MarkerOptions();
            mMarkerOptions.position(latLag);

            Marker customMarker = googleMap.addMarker(mMarkerOptions
                    .position(latLag)
                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), v))));

            markerHmap.put(customMarker, pos);
            markerList.set(pos, customMarker);
        }
    }


    public void markerClick(int position) {

        try {

            String latitude = childList.get(position).getLatitude();
            String longitude = childList.get(position).getLongitude();

            if (latitude != null && longitude != null) {

                double lat = Double.valueOf(latitude);
                double lng = Double.valueOf(longitude);

                LatLng latLag = new LatLng(lat, lng);

                markerList.get(position).remove();

                MarkerOptions mMarkerOptions = new MarkerOptions();
                mMarkerOptions.position(latLag);

                Marker customMarker = googleMap.addMarker(mMarkerOptions
                        .position(latLag)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

                markerHmap.put(customMarker, position);
                markerList.set(position, customMarker);
                markerList.get(position).showInfoWindow();
            }
        } catch (Exception e) {
            //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        markerClick = true;
    }


    @Override
    public void onRoutingFailure() {
        // The Routing request failed
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(final PolylineOptions mPolyOptions, Route route) {

        PolylineOptions polyOptions = new PolylineOptions();

        if (activity != null) {

            polyOptions.color(activity.getResources().getColor(R.color.colorPrimary));
            polyOptions.width(10);
            polyOptions.addAll(mPolyOptions.getPoints());
            googleMap.addPolyline(polyOptions);
        }
    }


    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
}
