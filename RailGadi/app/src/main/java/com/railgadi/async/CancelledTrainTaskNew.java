package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.fragments.ResDivCanFragmentNew;
import com.railgadi.interfaces.IResCanDivMarker;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class CancelledTrainTaskNew extends AsyncTask<Void, Void, Void> {

    private Context context;
    private ResDivCanFragmentNew fragment;

    private String error;
    private Dialog pro;

    private IResCanDivMarker bean;

    public CancelledTrainTaskNew(Context context, ResDivCanFragmentNew fragment) {

        this.context = context;
        this.fragment = fragment;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro = UtilsMethods.getStandardProgressDialog(context, CancelledTrainTaskNew.this);
        pro.show();
    }


    @Override
    protected Void doInBackground(Void... params) {

        String response     =   HttpServicesRailGadi.POST(ApiConstants.CANCELLED_TRAINS, CommonInputJsonMethod.getInputApiKeyOnly(context)) ;
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message");
                return null;
            } else {
                bean = JsonResponseParsing.getCancelledTrains(jsonObject);
            }
            if (bean == null) {
                error = "No Data Found";
            }
        } catch (Exception e) {
            bean = null;
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
            if (bean != null) {
                fragment.updateUI(bean) ;
            } else {
                fragment.updateException("No Data found");
            }
        }
    }
}
