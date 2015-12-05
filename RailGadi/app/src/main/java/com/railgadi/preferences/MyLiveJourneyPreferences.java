package com.railgadi.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.railgadi.R;
import com.railgadi.beans.PnrStatusNewBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyLiveJourneyPreferences {

    private static String KEY ;
    private Activity activity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();
    private Map<String, PnrStatusNewBean> savedMap;
    private MapHolder mapHolder ;


    public MyLiveJourneyPreferences(Activity activity) {

        this.activity = activity;
        preferences = this.activity.getPreferences(activity.MODE_PRIVATE);

        KEY =   activity.getResources().getString(R.string.my_live_journey_key) ;
    }


    private class MapHolder {

        private Map<String, PnrStatusNewBean> map;

        private void setMap(Map<String, PnrStatusNewBean> map) {
            this.map = map;
        }

        private Map<String, PnrStatusNewBean> getMap() {
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


    public PnrStatusNewBean getLiveJourneyFromPref() {

        mapHolder = get();

        if (mapHolder != null) {

            ArrayList<Map.Entry<String, PnrStatusNewBean>> entryList = new ArrayList<>(mapHolder.getMap().entrySet()) ;

            if(entryList != null && entryList.size() > 0) {
                return entryList.get(0).getValue();
            } else {
                return null ;
            }
        }

        return null;
    }

    public void saveLiveJourneyToPref(PnrStatusNewBean pnrStatusNewBean) {

        mapHolder = get();

        if (mapHolder == null) {

            mapHolder = new MapHolder();
            savedMap = new HashMap<>();

            savedMap.put(pnrStatusNewBean.getTrainInfo().trainNumber, pnrStatusNewBean);

            mapHolder.setMap(savedMap);

        } else {

            savedMap = mapHolder.getMap();
            savedMap.put(pnrStatusNewBean.getTrainInfo().trainNumber, pnrStatusNewBean);

            mapHolder.setMap(savedMap);
        }

        save(mapHolder);
    }


    public void removeLiveJourneyData() {

        Map<String, PnrStatusNewBean> map = new HashMap<>() ;
        mapHolder = new MapHolder() ;
        mapHolder.setMap(map);
        save(mapHolder) ;
    }


}
