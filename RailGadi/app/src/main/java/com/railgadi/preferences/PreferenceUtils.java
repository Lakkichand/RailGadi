package com.railgadi.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {

    public static final String PREFERNCE_NAME       =   "com.railgadi" ;

    public static final String DEVICE_ID            =   "device_id" ;

    public static final String GCM_ID               =   "google_cloud_msg_id" ;

    public static final String LOGIN_SESSION        =   "login_session" ;

    public static final String DEVICE_REG_STATUS    =   "device_registration_status" ;

    public static final String NOTIFICATION_KEY     =   "mute_notification_key" ;

    public static final String USERNAME_KEY         =   "current_login_user" ;

    public static final String USEREMAIL_KEY        =   "current_user_email" ;

    public static final String FARE_INPUT_KEY       =   "fare_enq_input_key" ;

    public static final String FROM_LAYOUT_HEIGHT   =   "from_layout_height_key" ;

    public static final String MID_LAYOUT_HEIGHT    =   "mid_layout_height_key" ;



    public static int getFromLayoutHeight(Context context) {
        try {
            SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
            int height = Integer.parseInt(mPrefs.getString(FROM_LAYOUT_HEIGHT, ""));
            return height ;
        } catch(Exception e){
            return 0 ;
        }
    }
    public static void saveFromLayoutHeight(Context context, int height) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putString(FROM_LAYOUT_HEIGHT, height+"").commit();
    }

    public static int getMidLayoutHeight(Context context) {
        try {
            SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
            int height = Integer.parseInt(mPrefs.getString(MID_LAYOUT_HEIGHT, ""));
            return height;
        } catch( Exception e ) {
            return 0 ;
        }
    }
    public static void saveMidLayoutHeight(Context context, int height) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putString(MID_LAYOUT_HEIGHT, height+"").commit();
    }

    public static String retrieveFareEnqInput(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        return mPrefs.getString(FARE_INPUT_KEY, "");
    }

    public static void saveFareEnqInput(Context context, String serializedObject) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putString(FARE_INPUT_KEY, serializedObject).commit();
    }


    public static String getDeviceID(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        return mPrefs.getString(DEVICE_ID, "");
    }
    public static void setDeviceID(Context context, String id) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putString(DEVICE_ID, id).commit();
    }


    public static String getGcmId(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        return mPrefs.getString(GCM_ID, "");
    }
    public static void setGcmId(Context context, String id) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putString(GCM_ID, id).commit();
    }


    public static boolean isLogin(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        return mPrefs.getBoolean(LOGIN_SESSION, false);
    }
    public static void setLoginSession(Context context, boolean flag) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putBoolean(LOGIN_SESSION, flag).commit();
    }


    public static boolean isDeviceRegistered(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        return mPrefs.getBoolean(DEVICE_REG_STATUS, false);
    }
    public static void setDeviceRegistrationFlag(Context context, boolean flag) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putBoolean(DEVICE_REG_STATUS, flag).commit();
    }


    public static boolean isMuteNotificationFlag(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        return mPrefs.getBoolean(NOTIFICATION_KEY, false) ;
    }
    public static void setMuteNotificationFlag(Context context, boolean flag) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putBoolean(NOTIFICATION_KEY, flag).commit() ;
    }


    public static String getCurrentUserName(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        return mPrefs.getString(USERNAME_KEY, "");
    }
    public static void setCurrentUserName(Context context, String userName) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putString(USERNAME_KEY, userName).commit() ;
    }


    public static String getCurrentUserEmail(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        return mPrefs.getString(USEREMAIL_KEY, "");
    }
    public static void setCurrentUserEmail(Context context, String userEmail) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putString(USEREMAIL_KEY, userEmail).commit() ;
    }


    public static void clearAllPreference(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().clear().commit();
    }


}
