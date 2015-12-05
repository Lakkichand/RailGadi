package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.interfaces.IGettingTimeTableComm;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class GetTimeTableTaskNew extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String trainNumber ;
    private IGettingTimeTableComm fragment ;
    private TimeTableNewBean timeTableNewBean ;

    private String error ;

    private Dialog pro ;

    public GetTimeTableTaskNew(Context context, String trainNumber, IGettingTimeTableComm fragment) {

        this.context            =   context ;
        this.trainNumber        =   trainNumber ;
        this.fragment           =   fragment ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pro = UtilsMethods.getStandardProgressDialog(context, GetTimeTableTaskNew.this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String response = HttpServicesRailGadi.POST(ApiConstants.TIME_TABLE, CommonInputJsonMethod.getTimeTableInputJson(context, trainNumber)) ;

        try {

            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message") ;
                return null ;
            } else {
                timeTableNewBean = JsonResponseParsing.getTimeTableData(context, jsonObject);
            }

            if(timeTableNewBean == null) {
                error = "No Data Found" ;
            }

        } catch( Exception e) {
            timeTableNewBean = null ;
        }

        return null;
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();

        if(pro != null){
            pro.dismiss();
        }

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(pro != null){
            pro.dismiss();
        }

        if(error != null) {
            fragment.updateException(error);
        } else {
            fragment.updateTimeTableUI(timeTableNewBean);
        }
    }
}
