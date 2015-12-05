package com.railgadi.utilities;

import android.content.Context;

import com.railgadi.R;
import com.railgadi.preferences.PreferenceUtils;

public final class ApiConstants {

    public static int COUNT                             =   0 ;

    public static final String TERMS_AND_CONDITION      =   "http://www.railgadiapp.in/privacy.html" ;

    public static final String RAIL_GADI_G_PLUS         =   "https://plus.google.com/+RailgadiappIn" ;

    public static final String RAIL_BASE_URL            =   "http://railgadiapp.in/";

    public static final String NTES_IN_MOBILE_VIEW      =   "http://enquiry.indianrail.gov.in/mntes/";

    public static final String AMENITIES_BASE_URL       =   "https://maps.googleapis.com/maps/api/place/textsearch/json?";

    // new APIs

    public static final String LOGIN                    =   RAIL_BASE_URL.concat("user_validation") ;

    public static final String REGISTRATION             =   RAIL_BASE_URL.concat("user_signup") ;

    public static final String FORGOT_PASSWORD          =   RAIL_BASE_URL.concat("user_reset_password") ;

    public static final String DELETE_PNR               =   RAIL_BASE_URL.concat("delete_pnr") ;

    public static final String DEVICE_REGISTRATION      =   RAIL_BASE_URL.concat("device_reg") ;

    public static final String PNR                      =   RAIL_BASE_URL.concat("pnr");

    public static final String REFRESH_PNR              =   RAIL_BASE_URL.concat("pnr_refresh");

    public static final String PNR_SMS                  =   RAIL_BASE_URL.concat("pnr_sms");

    public static final String TRAIN_DETAIL_FOR_PNR     =   RAIL_BASE_URL.concat("traindetails_for_pnr");

    public static final String CURRENT_BOOKING_AVL      =   RAIL_BASE_URL.concat("current_booking");

    public static final String STATION_INFORMATION      =   RAIL_BASE_URL.concat("station_details");

    public static final String COACH_COMPOSITION        =   RAIL_BASE_URL.concat("coach_compositions");

    public static final String AUTO_SUGGESTION          =   RAIL_BASE_URL.concat("trains");

    public static final String TIME_TABLE               =   RAIL_BASE_URL.concat("train_info");

    public static final String MINI_TIME_TABLE          =   RAIL_BASE_URL.concat("train_info_input");

    public static final String FARE_ENQUIRY             =   RAIL_BASE_URL.concat("fare_enq");

    public static final String SEAT_AVAILABILITY        =   RAIL_BASE_URL.concat("seatAvl_fare");

    public static final String LIVE_STATION             =   RAIL_BASE_URL.concat("live_station");

    public static final String SEARCH_TRAIN             =   RAIL_BASE_URL.concat("search_trains");

    public static final String SPOT_LIVE_TRAIN          =   RAIL_BASE_URL.concat("spot_train");

    public static final String SPECIAL_TRAINS           =   RAIL_BASE_URL.concat("special_trains") ;

    public static final String RESCHEDULED_TRAINS       =   RAIL_BASE_URL.concat("rescheduled_trains") ;

    public static final String CANCELLED_TRAINS         =   RAIL_BASE_URL.concat("cancelled_trains") ;

    public static final String DIVERTED_TRAINS          =   RAIL_BASE_URL.concat("diverted_trains") ;


    public static String getDeletePnrURL(Context context, String pnrNumber) {
        String deletePnrUrl = RAIL_BASE_URL.concat("delete_pnr?api_key="+UtilsMethods.getRailApiKey(context)+"&pnr_no="+pnrNumber+"&dev_id="+PreferenceUtils.getDeviceID(context)) ;
        return deletePnrUrl ;
    }

    public static String getIrctcAutoLoginURL(String userName, String passWord) {
        String url = "javascript:document.getElementById('j_username').value = '" + userName + "';" + "var frms = document.getElementsByName('LoginForm');" + "frms[0].submit(); }; ";
        return url;
    }

    public static String getNearAtmURL(Context context, String lat, String lng) {
        String url = AMENITIES_BASE_URL.concat("location=" + lat + "," + lng + "&radius=" + Constants.AMENITIES_DISTANCE + "&sensor=false&query=atm&key=" + context.getResources().getString(R.string.amenities_key));
        return url;
    }

    public static String getNearRestaurantURL(Context context, String lat, String lng) {
        String url = AMENITIES_BASE_URL.concat("location=" + lat + "," + lng + "&radius=" + Constants.AMENITIES_DISTANCE + "&sensor=false&query=restaurant&key=" + context.getResources().getString(R.string.amenities_key));
        return url;
    }

    public static String getNearHotelURL(Context context, String lat, String lng) {
        String url = AMENITIES_BASE_URL.concat("location=" + lat + "," + lng + "&radius=" + Constants.AMENITIES_DISTANCE + "&sensor=false&query=hotel&key=" + context.getResources().getString(R.string.amenities_key));
        return url;
    }

    public static String getNearHospitalURL(Context context, String lat, String lng) {
        String url = AMENITIES_BASE_URL.concat("location=" + lat + "," + lng + "&radius=" + Constants.AMENITIES_DISTANCE + "&sensor=false&query=hospital&key=" + context.getResources().getString(R.string.amenities_key));
        return url;
    }
}
