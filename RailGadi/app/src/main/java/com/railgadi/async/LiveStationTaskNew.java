package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.LiveStationNewBean;
import com.railgadi.interfaces.ILiveStationInterface;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class LiveStationTaskNew extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String from ;
    private String to ;
    private String time ;
    private ILiveStationInterface fragment ;

    private String error ;
    private LiveStationNewBean bean ;

    private Dialog pro ;

    public LiveStationTaskNew(Context context, String from, String to, String time, ILiveStationInterface fragment) {

        this.context        =   context ;
        this.from           =   from ;
        this.to             =   to ;
        this.time           =   time ;
        this.fragment       =   fragment ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pro = UtilsMethods.getStandardProgressDialog(context, LiveStationTaskNew.this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String response = HttpServicesRailGadi.POST(ApiConstants.LIVE_STATION, CommonInputJsonMethod.getLiveStationInputJson(context, from, to, time)) ;

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

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        pro.dismiss() ;

        if(error != null) {
            fragment.updateException(error);
        } else {
            if(bean != null) {
                fragment.updateLiveStationUI(bean) ;
            } else {
                fragment.updateException("No Data Found");
            }
        }
    }
}
