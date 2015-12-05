package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.StationInformationBean;
import com.railgadi.interfaces.IStationInformationInterface;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class StationInformationTask extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String stationCode ;
    private IStationInformationInterface fragment ;
    private Dialog pro ;
    private StationInformationBean bean ;
    private String error ;

    public StationInformationTask(Context context, String stationCode, IStationInformationInterface fragment) {

        this.context        =   context ;
        this.stationCode    =   stationCode ;
        this.fragment       =   fragment ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro     = UtilsMethods.getStandardProgressDialog(context, StationInformationTask.this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String response     =   HttpServicesRailGadi.POST(ApiConstants.STATION_INFORMATION, CommonInputJsonMethod.getStationInformationInputJson(context, stationCode)) ;

        try {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message");
                return null;
            } else {
                bean = JsonResponseParsing.getStationInformation(context, jsonObject) ;
            }

            if(bean == null) {
                error = "No Data Found" ;
            }
        } catch( Exception e ) {
            bean = null ;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        pro.dismiss();

        if(error != null) {
            fragment.updateStationInformationException(error); ;
        } else {
            if(bean != null) {
                fragment.updateStationInformationUI(bean);
            } else {
                fragment.updateStationInformationException("No Data Found");
            }
        }
    }
}
