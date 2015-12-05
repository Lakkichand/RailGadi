package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.javadocmd.simplelatlng.Geohasher;
import com.javadocmd.simplelatlng.LatLng;
import com.railgadi.beans.LiveStationNewBean;
import com.railgadi.beans.NearByStationBean;
import com.railgadi.dbhandlers.StationsDBHandler;
import com.railgadi.fragments.LeftDrawerFragment;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

public class LocateNearByTrainTask extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private LeftDrawerFragment fragment ;
    private LatLng currentLatlng ;
    private Location myLocation ;
    private static final String DEFAULT_TIME = "2";
    private LiveStationNewBean bean ;
    private NearByStationBean nearest ;
    private String error ;
    private Dialog pro ;


    public LocateNearByTrainTask(Context context, Location cLocation, LeftDrawerFragment fragment) {

        this.context    =   context ;
        this.myLocation =   cLocation ;
        this.fragment   =   fragment ;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro = UtilsMethods.getStandardProgressDialog(context, this) ;
        pro.show() ;
    }


    @Override
    protected Void doInBackground(Void... params) {

        try {

            currentLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude()) ;

            String myGeo = Geohasher.hash(currentLatlng);

            StationsDBHandler dbHandler = new StationsDBHandler(context);

            List<NearByStationBean> allStations = dbHandler.getAllStationNearByMe(myGeo);

            nearest = Collections.min(allStations) ;

            String response = HttpServicesRailGadi.POST(ApiConstants.LIVE_STATION, CommonInputJsonMethod.getLiveStationInputJson(context, nearest.getStationCode(), null, DEFAULT_TIME)) ;

            try {

                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.has("code") && jsonObject.has("message")) {
                    error = jsonObject.getString("message");
                    return null;
                } else {
                    bean = JsonResponseParsing.getLiveStation(jsonObject);
                }

                if(bean == null) {
                    error = "No Data Found" ;
                }
            } catch( Exception e ) {
                bean = null ;
            }

        } catch( Exception e ) {

        }

        if(isCancelled()) {
            if(pro.isShowing()) {
                pro.dismiss();
            }
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        pro.dismiss();

        if(error != null) {
            fragment.updateException(error);
        } else {
            if(bean != null) {
                fragment.updateNearByTrainsUI(bean, nearest.getStationName(), DEFAULT_TIME) ;
            } else {
                fragment.updateException("No Data Found");
            }
        }
    }
}
