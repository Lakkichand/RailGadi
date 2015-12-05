package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.javadocmd.simplelatlng.LatLng;
import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.AmenitiesAdapter;
import com.railgadi.async.AmenitiesTask;
import com.railgadi.beans.NearByAmenitiesBean;
import com.railgadi.beans.StationInformationBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IAmenitiesConnector;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.List;


public class AmenitiesMainTabFragment extends Fragment implements IAmenitiesConnector {

    private View rootView ;

    private TextView atm, restaurant, hotel, hospital ;
    private LinearLayout atmLayout, restaurentLayout, hotelLayout, hospitalLayout ;
    private View atmView, restaurantView, hotelView, hospitalView ;
    private ListView amenitiesListView ;

    private List<NearByAmenitiesBean> atmList ;
    private List<NearByAmenitiesBean> restList ;
    private List<NearByAmenitiesBean> hotelList ;
    private List<NearByAmenitiesBean> hospitalList ;

    private AmenitiesAdapter adapter ;

    private AmenitiesTask amenitiesTask ;

    private String selectedType ;

    private StationInformationBean stationDetailBean ;
    private String lat, lng ;
    private LatLng source, destination ;

    public AmenitiesMainTabFragment(List<NearByAmenitiesBean> atmAmenitiesList, StationInformationBean bean) {

        this.atmList            =   atmAmenitiesList ;
        this.stationDetailBean  =   bean ;
        this.lat                =   bean.getStationLatitude() ;
        this.lng                =   bean.getStationLongitude() ;
        this.source             =   new LatLng(Double.valueOf(this.lat), Double.valueOf(this.lng)) ;
        this.selectedType       =   Constants.ATM ;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(amenitiesTask != null) {
            amenitiesTask.cancel(true) ;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView        =   inflater.inflate(R.layout.near_by_amenities_fragment, container, false) ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.station_information).toUpperCase());

        initializeAllView() ;
        setDataOnViews() ;
        setAmenitiesAdapter(atmList);

        return rootView ;
    }


    private void initializeAllView() {

        atm                 =   (TextView) rootView.findViewById(R.id.atm_text) ;
        restaurant          =   (TextView) rootView.findViewById(R.id.restaurant_text) ;
        hotel               =   (TextView) rootView.findViewById(R.id.hotel_text) ;
        hospital            =   (TextView) rootView.findViewById(R.id.hospital_text) ;

        atm.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        restaurant.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        hotel.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        hospital.setTypeface(AppFonts.getRobotoMedium(getActivity()));

        atmLayout           =   (LinearLayout) rootView.findViewById(R.id.atm_layout) ;
        restaurentLayout    =   (LinearLayout) rootView.findViewById(R.id.restaurant_layout) ;
        hotelLayout         =   (LinearLayout) rootView.findViewById(R.id.hotel_layout) ;
        hospitalLayout      =   (LinearLayout) rootView.findViewById(R.id.hospital_layout) ;

        atmView             =   rootView.findViewById(R.id.atm_view) ;
        restaurantView      =   rootView.findViewById(R.id.restaurant_view) ;
        hotelView           =   rootView.findViewById(R.id.hotel_view) ;
        hospitalView        =   rootView.findViewById(R.id.hospital_view) ;

        amenitiesListView   =   (ListView) rootView.findViewById(R.id.amenities_list) ;
    }

    private void setDataOnViews() {

        amenitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NearByAmenitiesBean bean = (NearByAmenitiesBean) adapter.getItem(position) ;
                destination = new LatLng(Double.valueOf(bean.getLatitude()), Double.valueOf(bean.getLongitude())) ;
                startActivity(UtilsMethods.getMapRouteIntent(source, destination));
            }
        });

        atmLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                selectedType = Constants.ATM ;

                if(atmList != null) {
                    setAmenitiesAdapter(atmList);
                    return ;
                }

                if(InternetChecking.isNetWorkOn(getActivity())) {
                    amenitiesTask = new AmenitiesTask(getActivity(), selectedType, stationDetailBean, AmenitiesMainTabFragment.this);
                    amenitiesTask.execute();
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });

        restaurentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedType = Constants.RESTAURANT ;

                if(restList != null) {
                    setAmenitiesAdapter(restList);
                    return ;
                }
                if(InternetChecking.isNetWorkOn(getActivity())) {
                    amenitiesTask = new AmenitiesTask(getActivity(), selectedType, stationDetailBean, AmenitiesMainTabFragment.this);
                    amenitiesTask.execute();
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });

        hotelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = Constants.HOTEL ;

                if(hotelList != null) {
                    setAmenitiesAdapter(hotelList) ;
                    return ;
                }
                if(InternetChecking.isNetWorkOn(getActivity())) {
                    amenitiesTask = new AmenitiesTask(getActivity(), selectedType, stationDetailBean, AmenitiesMainTabFragment.this);
                    amenitiesTask.execute();
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });

        hospitalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedType = Constants.HOSPITAL ;

                if(hospitalList!= null) {
                    setAmenitiesAdapter(hospitalList);
                    return ;
                }
                if(InternetChecking.isNetWorkOn(getActivity())) {
                    amenitiesTask = new AmenitiesTask(getActivity(), selectedType, stationDetailBean, AmenitiesMainTabFragment.this);
                    amenitiesTask.execute();
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });
    }


    public void setAmenitiesAdapter(List<NearByAmenitiesBean> amenitiesList) {

        if(adapter != null) {
            adapter.changeDataSet(amenitiesList) ;
            amenitiesListView.setAdapter(adapter);
        } else {
            adapter =   new AmenitiesAdapter(getActivity(), amenitiesList, source) ;
            amenitiesListView.setAdapter(adapter);
        }
        updateViews(amenitiesList);
    }


    @Override
    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show() ;
    }


    @Override
    public void updateAmenities(List<NearByAmenitiesBean> amenitiesList) {
        try {
            setAmenitiesAdapter(amenitiesList);
        } catch(Exception e) {

        }
    }


    private void updateViews(List<NearByAmenitiesBean> amenitiesList) {

        switch(selectedType) {

            case Constants.ATM : {

                atmList =   amenitiesList ;

                atmView.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                hotelView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                hospitalView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                restaurantView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));

                atm.setTextColor(getActivity().getResources().getColor(R.color.white));
                restaurant.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                hotel.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                hospital.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));

                break ;
            }

            case Constants.RESTAURANT : {

                restList =   amenitiesList ;

                atmView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                hotelView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                hospitalView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                restaurantView.setBackgroundColor(getActivity().getResources().getColor(R.color.white));

                atm.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                restaurant.setTextColor(getActivity().getResources().getColor(R.color.white));
                hotel.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                hospital.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));

                break ;
            }

            case Constants.HOTEL : {

                hotelList =   amenitiesList ;

                atmView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                hotelView.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                hospitalView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                restaurantView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));

                atm.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                restaurant.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                hotel.setTextColor(getActivity().getResources().getColor(R.color.white));
                hospital.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));

                break ;
            }

            case Constants.HOSPITAL : {

                hospitalList =   amenitiesList ;

                atmView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                hotelView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                hospitalView.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                restaurantView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));

                atm.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                restaurant.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                hotel.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                hospital.setTextColor(getActivity().getResources().getColor(R.color.white));

                break ;
            }
        }
    }
}
