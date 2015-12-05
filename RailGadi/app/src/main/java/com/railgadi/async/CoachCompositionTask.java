package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.CoachCompositionBean;
import com.railgadi.fragments.CoachCompositionFragment;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class CoachCompositionTask extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String trainNumber ;
    private CoachCompositionBean bean ;
    private CoachCompositionFragment fragment ;

    private String error ;

    private Dialog pro ;

    public CoachCompositionTask(Context context, String trainNumber, CoachCompositionFragment fragment) {

        this.context        =   context ;
        this.trainNumber    =   trainNumber ;
        this.fragment       =   fragment ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro     = UtilsMethods.getStandardProgressDialog(context, this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String response = HttpServicesRailGadi.POST(ApiConstants.COACH_COMPOSITION, CommonInputJsonMethod.getCoachCompositionInputJson(context, trainNumber)) ;

        try {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message");
                return null;
            } else {
                bean = JsonResponseParsing.getCoachComposition(jsonObject, context);
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

        if(bean != null) {
            if(bean.getCoachBean() != null && bean.getCoachBean().size() > 0) {
                fragment.updateComposition(bean);
            } else {
                fragment.updateException("No coaches found");
            }
        } else if( error != null) {
            fragment.updateException(error);
        }
        else {
            fragment.updateException("No Composition found") ;
        }
    }
}
