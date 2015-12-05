package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.SpecialTrainNewBean;
import com.railgadi.fragments.SpecialTrainFragment;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class SpecialTrainTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private SpecialTrainFragment fragment;

    private SpecialTrainNewBean bean ;
    private String error;

    private Dialog pro;

    public SpecialTrainTask(Context context, SpecialTrainFragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pro = UtilsMethods.getStandardProgressDialog(context, this);
        pro.show();
    }


    @Override
    protected Void doInBackground(Void... params) {

        String response     =   HttpServicesRailGadi.POST(ApiConstants.SPECIAL_TRAINS, CommonInputJsonMethod.getInputApiKeyOnly(context)) ;

        try {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message");
                return null;
            } else {
                bean = JsonResponseParsing.getSpecialTrains(jsonObject) ;
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
            fragment.updateSpecialTrainException(error) ;
        } else {
            if(bean != null) {
                fragment.updateSpecialTrainUI(bean);
            } else {
                fragment.updateException("No Data Found");
            }
        }
    }

}
