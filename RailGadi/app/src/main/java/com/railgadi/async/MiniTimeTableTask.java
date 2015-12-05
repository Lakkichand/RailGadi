package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.MiniTimeTableBean;
import com.railgadi.interfaces.IStationsViaTrainNumber;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class MiniTimeTableTask extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String trainNumber ;
    private IStationsViaTrainNumber fragment ;

    private String error ;
    private MiniTimeTableBean bean ;

    private Dialog pro ;

    public MiniTimeTableTask(Context context, String trainNumber, IStationsViaTrainNumber fragment) {

        this.context            =   context ;
        this.trainNumber        =   trainNumber ;
        this.fragment           =   fragment ;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro = UtilsMethods.getStandardProgressDialog(context, MiniTimeTableTask.this) ;
        pro.show() ;
    }


    @Override
    protected Void doInBackground(Void... params) {

        String response = HttpServicesRailGadi.POST(ApiConstants.MINI_TIME_TABLE, CommonInputJsonMethod.getTimeTableInputJson(context, trainNumber)) ;

        try {

            if(response == null) {
                error = "No Data Found" ;
                return null ;
            }

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message");
                return null;
            } else {
                bean = JsonResponseParsing.getMiniTimetable(jsonObject);
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

        if(bean != null) {
            fragment.updateMiniTimetable(bean);
        } else if( error != null) {
            fragment.updateException(error);
        }
        else {
            fragment.updateException("No Composition found") ;
        }
    }
}
