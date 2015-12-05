package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.javadocmd.simplelatlng.Geohasher;
import com.javadocmd.simplelatlng.LatLng;
import com.railgadi.beans.NearByStationBean;
import com.railgadi.dbhandlers.StationsDBHandler;
import com.railgadi.interfaces.INearByStations;
import com.railgadi.utilities.UtilsMethods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vijay on 05-08-2015.
 */
public class GetNearByStationTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private Dialog pro;
    private LatLng currentLatlng;
    private double distance;
    private INearByStations fragment;


    private String errMsg;

    private Map<String, NearByStationBean> nearByMap;

    public GetNearByStationTask(Context context, com.google.android.gms.maps.model.LatLng currentLatLng, double distance, INearByStations fragment) {

        this.context = context;
        this.nearByMap = new HashMap<>();
        this.distance = distance;
        this.fragment = fragment;

        this.currentLatlng = new LatLng(currentLatLng.latitude, currentLatLng.longitude);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro = UtilsMethods.getStandardProgressDialog(context, this) ;
        pro.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {

            String myGeo = Geohasher.hash(currentLatlng);

            StationsDBHandler dbHandler = new StationsDBHandler(context);

            List<NearByStationBean> allStations = dbHandler.getAllStationNearByMe(myGeo);

            for (NearByStationBean bean : allStations) {

                if (bean.getDistance() <= distance) {
                    nearByMap.put(bean.getStationCode(), bean);
                }
            }

        } catch (Exception e) {

            errMsg = e.getMessage();
        }

        if (isCancelled()) {
            if (pro.isShowing()) {
                pro.dismiss();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        pro.dismiss();

        if (errMsg != null) {
            fragment.updateException(errMsg);
        } else if (nearByMap.size() == 0) {
            fragment.updateException("No Station found in given range");
        } else {
            fragment.updateUI(nearByMap);
        }
    }
}
