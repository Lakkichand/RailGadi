package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.FareEnqInput;
import com.railgadi.beans.FareEnquiryNewBean;
import com.railgadi.interfaces.IFareEnqWithClassQuota;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class FareEnqNewTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private FareEnqInput fei ;
    private IFareEnqWithClassQuota fragment ;

    private FareEnquiryNewBean bean  ;

    private String error ;

    private Dialog pro ;

    public FareEnqNewTask(Context context, FareEnqInput fei, IFareEnqWithClassQuota fragment) {

        this.context        =   context ;
        this.fei            =   fei ;
        this.fragment       =   fragment ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro     = UtilsMethods.getStandardProgressDialog(context, FareEnqNewTask.this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String response = HttpServicesRailGadi.POST(ApiConstants.FARE_ENQUIRY, CommonInputJsonMethod.getFareEnquiryInputJson(context, fei)) ;

        try {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message");
                return null;
            } else {
                bean = JsonResponseParsing.getFareEnquiry(jsonObject);
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
            fragment.updateFareOnClassAndQuota(bean, fei); ;
        } else if( error != null) {
            fragment.updateException(error);
        }
        else {
            fragment.updateException("No Data found") ;
        }
    }
}
