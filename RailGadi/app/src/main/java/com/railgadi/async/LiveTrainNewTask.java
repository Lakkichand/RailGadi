package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.LiveTrainNewBean;
import com.railgadi.beans.MiniTimeTableBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.interfaces.ILiveTrainInterface;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

import java.util.Date;

public class LiveTrainNewTask extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String trainNumber ;
    private LiveTrainNewBean bean ;
    private String stnCode ;
    private Date date ;
    private MiniTimeTableBean miniTimeTableBean;
    private ILiveTrainInterface fragment ;
    private TimeTableNewBean timeTableNewBean ;
    private String error ;

    private Dialog pro ;


    public LiveTrainNewTask(Context context, String trainNumber, String stnCode, Date date, MiniTimeTableBean miniTimeTableBean, ILiveTrainInterface fragment) {

        this.context            =   context ;
        this.trainNumber        =   trainNumber ;
        this.stnCode            =   stnCode ;
        this.date               =   date ;
        this.miniTimeTableBean  =   miniTimeTableBean ;
        this.fragment           =   fragment ;
    }


    public LiveTrainNewTask(Context context, String trainNumber, LiveTrainNewBean liveTrainNewBean, ILiveTrainInterface fragment) {

        this.context            =   context ;
        this.trainNumber        =   trainNumber ;
        this.stnCode            =   liveTrainNewBean.getJourneyStation() ;
        this.date               =   liveTrainNewBean.getInputDateInstance() ;
        this.miniTimeTableBean  =   liveTrainNewBean.getMiniTimeTableBean() ;
        this.fragment           =   fragment ;

    }


    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        pro     = UtilsMethods.getStandardProgressDialog(context, this) ;
        pro.show() ;
    }


    @Override
    protected Void doInBackground(Void... params) {

        String response = HttpServicesRailGadi.POST(ApiConstants.SPOT_LIVE_TRAIN, CommonInputJsonMethod.getLiveTrainInputJson(context, trainNumber, stnCode, date)) ;

        try {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message");
                return null;
            } else {
                bean = JsonResponseParsing.getLiveTrainData(miniTimeTableBean, jsonObject) ;
                bean.setInputDateInstance(date);
            }

            if(bean == null) {
                error = "No Data Found" ;
            }
        } catch( Exception e ) {
            bean = null ;
        }

        String timeTableResponse = HttpServicesRailGadi.POST(ApiConstants.TIME_TABLE, CommonInputJsonMethod.getTimeTableInputJson(context, trainNumber)) ;

        try {

            JSONObject jsonObject = new JSONObject(timeTableResponse);

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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        pro.dismiss();

        if(bean != null && timeTableNewBean != null) {
            fragment.updateLiveTrainData(bean, timeTableNewBean) ;
        }
        else if( error != null) {
            fragment.updateException(error);
        }
        else {
            fragment.updateException("No Data found") ;
        }
    }
}
