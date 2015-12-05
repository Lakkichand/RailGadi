package com.railgadi.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.railgadi.R;
import com.railgadi.beans.TimeTableNewBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TimeTablePreferencesHandler {


    private static String KEY ;
    private Activity activity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();
    private Map<String, TimeTableNewBean> savedMap;
    private MapHolder mapHolder;


    public TimeTablePreferencesHandler(Activity activity) {

        this.activity = activity;
        preferences = this.activity.getPreferences(activity.MODE_PRIVATE);

        this.KEY = this.activity.getResources().getString(R.string.timetable_pref_key) ;
    }

    private class MapHolder {

        private Map<String, TimeTableNewBean> map;

        private void setMap(Map<String, TimeTableNewBean> map) {
            this.map = map;
        }

        private Map<String, TimeTableNewBean> getMap() {
            return this.map;
        }
    }

    private void save(MapHolder mapHolder) {

        try {

            editor = preferences.edit();
            String json = gson.toJson(mapHolder);
            editor.putString(KEY, json);
            editor.commit();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private MapHolder get() {

        Gson gson = new Gson();
        String json = preferences.getString(KEY, "");

        MapHolder obj = null ;
        try {

            Type holderType = new TypeToken<MapHolder>() {}.getType();
            obj = gson.fromJson(json, holderType);

        } catch(Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show() ;
        }

        return obj;
    }

    public boolean isAlreadyExists(String trainNumber) {

        mapHolder = get();
        if (mapHolder != null) {
            savedMap = mapHolder.getMap() ;
            if(savedMap.keySet().contains(trainNumber)) {
                return true ;
            } else {
                return false ;
            }
        }
        return false ;
    }



    public TimeTableNewBean getTimeTableFromPref(String trainNumber) {

        mapHolder = get();
        if (mapHolder != null) {
            return mapHolder.getMap().get(trainNumber) ;
        }

        return null;
    }

    public void saveTimeTableToPref(TimeTableNewBean pnrPreferenceBean) {

        mapHolder = get();

        if (mapHolder == null) {

            mapHolder = new MapHolder();
            savedMap = new HashMap<>();

            savedMap.put(pnrPreferenceBean.getTrainNumber(), pnrPreferenceBean);

            mapHolder.setMap(savedMap);

        } else {

            savedMap = mapHolder.getMap();
            savedMap.put(pnrPreferenceBean.getTrainNumber(), pnrPreferenceBean);

            mapHolder.setMap(savedMap);
        }

        save(mapHolder);
    }

    public void removeAllSavedTimetable() {

        Map<String, TimeTableNewBean> map = new HashMap<>() ;
        mapHolder = new MapHolder() ;
        mapHolder.setMap(map);
        save(mapHolder) ;
    }

    public void removeFromPreferences(String trainNumber) {

        Map<String, TimeTableNewBean> map = get().getMap() ;

        map.remove(trainNumber) ;
        mapHolder = new MapHolder() ;
        mapHolder.setMap(map);

        save(mapHolder) ;
    }

    public List<TimeTableNewBean> getAllSavedTimeTable() {

        List<TimeTableNewBean> list = new ArrayList<>() ;

        if(get() == null) {
            return null ;
        }

        Map<String, TimeTableNewBean> map = get().getMap() ;

        for(Map.Entry<String, TimeTableNewBean> entry : map.entrySet()) {
            list.add(entry.getValue());
        }

        if(list.size() > 0) {
            return list ;
        }

        return null ;
    }
}
