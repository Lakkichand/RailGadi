package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.fragments.SearchedPnrFragment;
import com.railgadi.preferences.PreferenceUtils;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;


public class GetTrainDetailForPnrTask extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private PnrStatusNewBean bean ;
    private SearchedPnrFragment fragment ;

    private String error ;

    private Dialog pro ;

    public GetTrainDetailForPnrTask(Context context, PnrStatusNewBean bean, SearchedPnrFragment fragment) {

        this.context        =   context ;
        this.bean           =   bean ;
        this.fragment       =   fragment ;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro     = UtilsMethods.getStandardProgressDialog(context, GetTrainDetailForPnrTask.this) ;
        pro.show() ;
    }


    @Override
    protected Void doInBackground(Void... params) {

        String response = HttpServicesRailGadi.POST(ApiConstants.TRAIN_DETAIL_FOR_PNR, CommonInputJsonMethod.getTrainDetailsForPnrJson(context, bean.getPnrNumber(), bean.getTrainInfo().trainNumber));

        try {

            if(! PreferenceUtils.isDeviceRegistered(context)) {
                UtilsMethods.registerDevice(context) ;
            }

            JSONObject jsonObject = new JSONObject(response) ;
            if(jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message") ;
                return null ;
            } else {
                bean = JsonResponseParsing.getPnrTrainInfoRouteList(context, jsonObject, bean) ;
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

        if(bean != null) {
            fragment.addPnrToTrips(bean);
        } else if( error != null) {
            fragment.updateException(error);
        } else {
            fragment.updateException("error pnr not added to trips");
        }
    }
}
