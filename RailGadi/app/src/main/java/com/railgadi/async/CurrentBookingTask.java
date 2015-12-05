package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.CurrentBookingAvailabilityBean;
import com.railgadi.fragments.CurrentBookingAvlFragment;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class CurrentBookingTask extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String stationCode ;
    private CurrentBookingAvlFragment fragment ;

    private Dialog pro ;

    private CurrentBookingAvailabilityBean bean ;
    private String error ;

    public CurrentBookingTask(Context context, String stationCode, CurrentBookingAvlFragment fragment) {

        this.context        =   context ;
        this.stationCode    =   stationCode ;
        this.fragment       =   fragment ;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro = UtilsMethods.getStandardProgressDialog(context, this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String response = HttpServicesRailGadi.POST(ApiConstants.CURRENT_BOOKING_AVL, CommonInputJsonMethod.getCurrentBookingInputJson(context, stationCode)) ;

        try {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message");
                return null;
            } else {
                bean = JsonResponseParsing.getCurrentBookingAvailability(jsonObject);
            }

            if(bean == null) {
                error = "No Availability Found" ;
            }

        } catch( Exception e ) {
            bean = null ;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        pro.dismiss();

        if(error != null) {
            fragment.updateException(error);
        } else {
            if(bean == null) {
                fragment.updateException("No Availability Found");
            } else {
                fragment.updateUI(bean);
            }
        }
    }
}
