package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.RefreshPnrBean;
import com.railgadi.interfaces.IRefreshable;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class RefreshAndUpdatePnrStatusTask extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String pnrNumber ;
    private IRefreshable fragment ;

    private RefreshPnrBean bean ;

    private String error ;

    private Dialog pro ;

    public RefreshAndUpdatePnrStatusTask(Context context, String pnrNumber, IRefreshable fragment) {
        this.context        =   context ;
        this.pnrNumber      =   pnrNumber ;
        this.fragment       =   fragment ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pro = UtilsMethods.getStandardProgressDialog(context, RefreshAndUpdatePnrStatusTask.this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String response = HttpServicesRailGadi.POST(ApiConstants.REFRESH_PNR, CommonInputJsonMethod.getPnrInputJson(context, pnrNumber)) ;

        try {

            JSONObject jsonObject = new JSONObject(response) ;
            if(jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message") ;
                return null ;
            } else {
                bean = JsonResponseParsing.getRefreshPnr(jsonObject) ;
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
            fragment.updateRefreshPnrUI(bean);
        }
    }

}
