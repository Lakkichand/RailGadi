package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.javadocmd.simplelatlng.LatLng;
import com.railgadi.beans.NearByAmenitiesBean;
import com.railgadi.beans.StationInformationBean;
import com.railgadi.interfaces.IAmenitiesConnector;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import java.util.List;

public class AmenitiesTask extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String placeType ;
    private LatLng source ;
    private IAmenitiesConnector fragment ;

    private String response ;

    private List<NearByAmenitiesBean> amenitiesList ;

    private Dialog pro ;



    public AmenitiesTask(Context context, String placeType, StationInformationBean bean, IAmenitiesConnector fragment) {

        this.context        =   context ;
        this.placeType      =   placeType ;
        this.source         =   new LatLng(Double.parseDouble(bean.getStationLatitude()), Double.parseDouble(bean.getStationLongitude())) ;
        this.fragment       =   fragment ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro     =   UtilsMethods.getStandardProgressDialog(context, this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String url = null ;

        if(Constants.ATM.equals(placeType)) {
            url  =   ApiConstants.getNearAtmURL(context, source.getLatitude() + "", source.getLongitude() + "") ;
            response    =   HttpServicesRailGadi.GET(url) ;
        }
        else if(Constants.RESTAURANT.equals(placeType)) {
            url  =   ApiConstants.getNearRestaurantURL(context, source.getLatitude() + "", source.getLongitude() + "") ;
            response     =   HttpServicesRailGadi.GET(url) ;
        }
        else if(Constants.HOTEL.equals(placeType)) {
            url  =   ApiConstants.getNearHotelURL(context, source.getLatitude() + "", source.getLongitude() + "") ;
            response     =   HttpServicesRailGadi.GET(url) ;
        }
        else if(Constants.HOSPITAL.equals(placeType)) {
            url  =   ApiConstants.getNearHospitalURL(context, source.getLatitude() + "", source.getLongitude() + "") ;
            response     =   HttpServicesRailGadi.GET(url) ;
        }

        amenitiesList       =   JsonResponseParsing.getNearByAmenities(response, new LatLng(source.getLatitude(), source.getLongitude())) ;

        if(isCancelled() && pro.isShowing()) {
            pro.dismiss();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        pro.dismiss();

        if(amenitiesList != null) {
            fragment.updateAmenities(amenitiesList);
        } else {
            fragment.updateException("No near by found");
        }
    }
}
