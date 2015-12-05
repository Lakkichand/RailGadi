package com.railgadi.async;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.preferences.RemovablePnrPreference;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

import java.util.List;

public class DeletePnrTask extends AsyncTask<Void, Void, Void> {


    private Context context ;
    private List<String> pnrList ;
    private Dialog dialog ;
    private boolean loading ;


    public DeletePnrTask(Context context, List<String> pnrList, boolean loading) {
        this.context    =   context ;
        this.pnrList    =   pnrList ;
        this.loading    =   loading ;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute() ;
        if(loading) {
            dialog = UtilsMethods.getStandardProgressDialog(context, DeletePnrTask.this);
            dialog.show();
        }
    }


    @Override
    protected Void doInBackground(Void... params) {

        RemovablePnrPreference pref = new RemovablePnrPreference((Activity) context) ;

        for(String pnr : pnrList) {
            String response = HttpServicesRailGadi.DELETE(ApiConstants.getDeletePnrURL(context, pnr)) ;
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("code") || jsonObject.has("message")) {
                    pref.addToRemovable(pnr);
                }
                else if(jsonObject.has("data")) {
                    if(pref.isExists(pnr)) {
                        pref.removeOne(pnr) ;
                    }
                }
            } catch( Exception e ) {

            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
