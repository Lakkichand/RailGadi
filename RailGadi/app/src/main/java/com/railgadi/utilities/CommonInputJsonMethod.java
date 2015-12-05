package com.railgadi.utilities;

import android.content.Context;

import com.railgadi.beans.FareEnqInput;
import com.railgadi.beans.SearchTrainInputBean;
import com.railgadi.beans.SignUpRequestBean;
import com.railgadi.preferences.PreferenceUtils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class CommonInputJsonMethod {


    public static JSONObject getPnrInputJson(Context context, String pnrNumber) {

        JSONObject json = null ;

        try {

            json = new JSONObject();
            json.accumulate("pnr_no", pnrNumber) ;
            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;
            if(PreferenceUtils.getDeviceID(context) != null ) {
                json.accumulate("dev_id", PreferenceUtils.getDeviceID(context)) ;
            }

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }



    public static JSONObject getInputApiKeyOnly(Context context) {

        JSONObject json = null ;
        try {

            json = new JSONObject();
            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }



    public static JSONObject getLoginJsonInput(Context context, String id, String pwd) {

        JSONObject json = null ;
        try {

            json = new JSONObject();

            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;
            json.accumulate("email", id) ;
            if(pwd != null && (! pwd.isEmpty())) {
                json.accumulate("password", pwd);
            }

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }



    public static JSONObject getForgotPasswordJsonInput(Context context, String email) {
        JSONObject json = null ;
        try {

            json = new JSONObject();

            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;
            json.accumulate("email", email) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }



    public static JSONObject getRegistrationInputJson(Context context, SignUpRequestBean bean) {

        JSONObject json = null ;
        try {

            json = new JSONObject();

            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;
            json.accumulate("username", bean.getFullName()) ;
            json.accumulate("email", bean.getEmail()) ;
            json.accumulate("dev_id", bean.getDevId()) ;
            json.accumulate("dev_type", bean.getDevType()) ;
            json.accumulate("gender", bean.getGender()) ;
            if(bean.getPassword() != null && (! bean.getPassword().isEmpty())) {
                json.accumulate("password", bean.getPassword());
            }

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }


    public static JSONObject getDeviceRegInputJson(Context context) {

        JSONObject json = null ;
        try {

            json = new JSONObject();

            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;
            json.accumulate("dev_id", PreferenceUtils.getDeviceID(context)) ;
            json.accumulate("dev_type", "android") ;
            json.accumulate("reg_tok", PreferenceUtils.getGcmId(context)) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }


    public static JSONObject getTrainDetailsForPnrJson(Context context, String pnrNo, String trainNumber) {

        JSONObject json = null ;
        try {

            json = new JSONObject();
            json.accumulate("pnr_no", pnrNo ) ;
            json.accumulate("train_no", trainNumber) ;
            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;
            if(PreferenceUtils.getDeviceID(context) != null) {
                json.accumulate("dev_id", PreferenceUtils.getDeviceID(context)) ;
            }

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }


    public static JSONObject getCoachCompositionInputJson(Context context, String trainNumber) {

        JSONObject json = null ;
        try {

            json = new JSONObject();
            json.accumulate("train_no", trainNumber) ;
            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }


    public static JSONObject getDeletePnrInputJson(Context context, String pnrNumber) {

        JSONObject json = null ;
        try {

            json = new JSONObject();
            json.accumulate("dev_id", PreferenceUtils.getDeviceID(context)) ;
            json.accumulate("pnr_no", pnrNumber) ;
            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }


    public static JSONObject getTimeTableInputJson(Context context, String trainNumber) {

        JSONObject json = null ;
        try {

            json = new JSONObject();
            json.accumulate("train_no", trainNumber) ;
            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }


    public static JSONObject getCurrentBookingInputJson(Context context, String stationCode) {

        JSONObject json = null ;
        try {

            json = new JSONObject();
            json.accumulate("stn_code", stationCode) ;
            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }


    public static JSONObject getStationInformationInputJson(Context context, String station) {

        JSONObject json = null ;
        try {

            json = new JSONObject();
            json.accumulate("station", station) ;
            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }


    public static JSONObject getAutoSuggestionInputJson(Context context, String inputQuery) {

        JSONObject json = null ;
        try {

            json = new JSONObject();
            json.accumulate("train_name", inputQuery) ;
            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }

    //http://railgadiapp.in/fare_enq ===> train_no,src,dest,class,day,month,(age, concession_code, quota)
    public static JSONObject getFareEnquiryInputJson(Context context, FareEnqInput fei) {

        JSONObject json = null ;
        try {

            json = new JSONObject();
            json.accumulate("train_no", fei.getTrainNo()) ;
            json.accumulate("src", fei.getSrcCode()) ;
            json.accumulate("dest", fei.getDestCode()) ;
            json.accumulate("class", fei.getClassCode()) ;
            json.accumulate("day", fei.getDay()) ;
            json.accumulate("month", fei.getMonth()) ;
            if(fei.getAge() != null && (! fei.getAge().isEmpty())) {
                json.accumulate("age", fei.getAge()) ;
            }
            if(fei.getConcCode() != null && (! fei.getConcCode().isEmpty())) {
                json.accumulate("concession_code", fei.getConcCode()) ;
            }
            if(fei.getQuota() != null && (! fei.getQuota().isEmpty())) {
                json.accumulate("quota", fei.getQuota()) ;
            }
            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }


    public static JSONObject getLiveStationInputJson(Context context, String from, String to, String time) {

        JSONObject json = null ;
        try {

            json = new JSONObject();
            json.accumulate("stn_code", from) ;
            if(to != null && (! to.isEmpty())) {
                json.accumulate("to_stn", to) ;
            }
            if(time != null && (! time.isEmpty())) {
                json.accumulate("nxthrs", time) ;
            }
            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }



    public static JSONObject getLiveTrainInputJson(Context context, String trainNo, String stnCode, Date date) {

        JSONObject json = null ;

        try {

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH)+1 ;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            json = new JSONObject();
            json.accumulate("train_no", trainNo) ;
            json.accumulate("stnCode", stnCode) ;
            json.accumulate("day", day) ;
            json.accumulate("month", month) ;
            json.accumulate("year", year) ;
            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }



    public static JSONObject getSearchTrainInputJson(Context context, SearchTrainInputBean bean) {

        JSONObject json = null ;

        try {

            json = new JSONObject();
            json.accumulate("src", bean.getFromStationCode()) ;
            json.accumulate("dstn", bean.getToStationCode()) ;
            json.accumulate("deptTimeFrm", bean.getStartRange()) ;
            json.accumulate("deptTimeTo", bean.getEndRange()) ;
            if(bean.getDay() != null && (! bean.getDay().isEmpty())) {
                json.accumulate("day", bean.getDay()) ;
            }
            if(bean.getMonth() != null && (! bean.getMonth().isEmpty())) {
                json.accumulate("month", bean.getMonth()) ;
            }
            if(bean.getYear() != null && (! bean.getYear().isEmpty())) {
                json.accumulate("year", bean.getYear()) ;
            }

            /*
            if(bean.getDay() != null && (! bean.getDay().isEmpty())) {
                json.accumulate("to_stn", bean.getDay()) ;
            }
            if(bean.getDay() != null && (! bean.getDay().isEmpty())) {
                json.accumulate("to_stn", bean.getDay()) ;
            }
            if(bean.getDay() != null && (! bean.getDay().isEmpty())) {
                json.accumulate("to_stn", bean.getDay()) ;
            }
            if(bean.getDay() != null && (! bean.getDay().isEmpty())) {
                json.accumulate("to_stn", bean.getDay()) ;
            }*/

            json.accumulate("api_key", UtilsMethods.getRailApiKey(context)) ;

        } catch( Exception e ) {
            json = null ;
        }
        return json ;
    }
}
