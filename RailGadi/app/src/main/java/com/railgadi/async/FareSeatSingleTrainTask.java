package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.FareEnqInput;
import com.railgadi.beans.SeatFareBeanNew;
import com.railgadi.interfaces.ISeatAvlFare;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;



public class FareSeatSingleTrainTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private FareEnqInput fei ;
    private ISeatAvlFare fragment;

    private SeatFareBeanNew seatFareBean ;

    private Dialog pro;

    private String error ;

    public FareSeatSingleTrainTask(Context context, FareEnqInput fei, ISeatAvlFare fragment) {

        this.context    =   context;
        this.fei        =   fei ;
        this.fragment   =   fragment;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro = UtilsMethods.getStandardProgressDialog(context, this);
        pro.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        String response = HttpServicesRailGadi.POST(ApiConstants.SEAT_AVAILABILITY, CommonInputJsonMethod.getFareEnquiryInputJson(context, fei)) ;

        try {

            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message") ;
                return null ;
            } else {
                seatFareBean = JsonResponseParsing.getSeatAvlFareBreakup(jsonObject);
            }

            if(seatFareBean == null) {
                error = "No Data Found" ;
            }

        } catch( Exception e) {
            seatFareBean = null ;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        pro.dismiss();
        if (error != null) {
            fragment.updateException(error);
        } else {
            if(seatFareBean == null) {
                fragment.updateException("No Data Found");
            } else {
                fragment.updateSearchTrainUi(seatFareBean, fei);
            }
        }
    }

}
