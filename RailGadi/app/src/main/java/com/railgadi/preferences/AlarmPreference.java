package com.railgadi.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.railgadi.R;
import com.railgadi.beans.AlarmDataBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class AlarmPreference {

    private static String KEY ;
    private static String NAME ;
    private Context context ;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();

    public AlarmPreference(Context context) {

        this.context= context;
        this.KEY = this.context.getResources().getString(R.string.alarm_pref_key) ;
        this.NAME = this.context.getResources().getString(R.string.alarm_pref_name) ;
        preferences = this.context.getSharedPreferences(NAME, context.MODE_PRIVATE) ;
    }

    private boolean save(AlarmDataBean bean) {
        try {

            editor = preferences.edit();
            String json = gson.toJson(bean);
            editor.putString(KEY, json);
            editor.commit();
            return true ;
        } catch(Exception e) {
            return false ;
        }
    }

    private AlarmDataBean get() {

        Gson gson = new Gson();
        String json = preferences.getString(KEY, "");
        AlarmDataBean obj = null ;
        try {
            Type holderType = new TypeToken<AlarmDataBean>() {}.getType();
            obj = gson.fromJson(json, holderType);
        } catch(Exception e) {
            return null ;
        }
        return obj;
    }

    public List<AlarmDataBean> getSavedAlarm() {

        List<AlarmDataBean> beans = new ArrayList<>() ;

        if(get() == null) {
            return null ;
        }
        beans.add(get()) ;
        return beans ;
    }

    public void deleteSavedAlarm() {

        save(null) ;
    }

    public boolean saveAlarm(AlarmDataBean bean) {

        return save(bean);
    }
}
