package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.interfaces.IPnrStatusComm;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class PnrStatusTaskNew extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String pnrNumber ;
    private IPnrStatusComm fragment ;

    private PnrStatusNewBean bean ;

    private String error ;

    private Dialog pro ;

    public PnrStatusTaskNew(Context context, String pnrNumber, IPnrStatusComm fragment) {
        this.context        =   context ;
        this.pnrNumber      =   pnrNumber ;
        this.fragment       =   fragment ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pro = UtilsMethods.getStandardProgressDialog(context, PnrStatusTaskNew.this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        //String response = HttpServicesRailGadi.GET(ApiConstants.getPnrStatusURL(context, pnrNumber)) ;

        String response = HttpServicesRailGadi.POST(ApiConstants.PNR, CommonInputJsonMethod.getPnrInputJson(context, pnrNumber)) ;

        try {

            JSONObject jsonObject = new JSONObject(response) ;
            if(jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message") ;
                return null ;
            } else {
                bean = JsonResponseParsing.getPnrStatus(context, jsonObject) ;
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
            fragment.updatePnrUI(bean);
        } else if( error != null) {
            fragment.updateException(error);
        } else {
            fragment.updateException("error");
        }
    }
}
