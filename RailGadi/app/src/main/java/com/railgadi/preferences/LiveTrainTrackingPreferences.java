package com.railgadi.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.railgadi.R;
import com.railgadi.beans.LiveTrainNewBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LiveTrainTrackingPreferences {

    private static String KEY ;
    private Activity activity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();
    private Map<String, LiveTrainNewBean> savedMap;
    private MapHolder mapHolder ;


    public LiveTrainTrackingPreferences(Activity activity) {

        this.activity = activity;
        preferences = this.activity.getPreferences(activity.MODE_PRIVATE);

        KEY =   activity.getResources().getString(R.string.live_train_tracking_key) ;
    }


    private class MapHolder {

        private Map<String, LiveTrainNewBean> map;

        private void setMap(Map<String, LiveTrainNewBean> map) {
            this.map = map;
        }

        private Map<String, LiveTrainNewBean> getMap() {
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

    public void removeTrackingFromPref(String trainNumber) {

        Map<String, LiveTrainNewBean> map = get().getMap() ;

        map.remove(trainNumber) ;
        mapHolder = new MapHolder() ;
        mapHolder.setMap(map);

        save(mapHolder) ;
    }

    public LiveTrainNewBean getLiveTrackingFromPref(String trainNumber) {

        mapHolder = get();

        if (mapHolder != null) {
            return mapHolder.getMap().get(trainNumber) ;
        }

        return null;
    }

    public boolean isAlreadyTracked(String trainNumber) {

        MapHolder holder = get() ;

        if(holder != null) {

            if(holder.getMap() != null) {

                Set<String> keySet = holder.getMap().keySet() ;

                if(keySet.contains(trainNumber)) {
                    return true ;
                }

                return false ;
            }
        }
        return false ;
    }

    public void removeAllSavedTrackingInstances() {

        Map<String, LiveTrainNewBean> map = new HashMap<>() ;
        mapHolder = new MapHolder() ;
        mapHolder.setMap(map);
        save(mapHolder) ;
    }


    public void saveLiveTrackingToPref(LiveTrainNewBean liveTrainNewBean) {

        mapHolder = get();

        if (mapHolder == null) {

            mapHolder = new MapHolder();
            savedMap = new HashMap<>();

            savedMap.put(liveTrainNewBean.getMiniTimeTableBean().getTrainNumber(), liveTrainNewBean);

            mapHolder.setMap(savedMap);

        } else {

            savedMap = mapHolder.getMap();
            savedMap.put(liveTrainNewBean.getMiniTimeTableBean().getTrainNumber(), liveTrainNewBean);

            mapHolder.setMap(savedMap);
        }

        save(mapHolder);
    }

    public List<LiveTrainNewBean> getAllSavedTrackingInstances() {

        List<LiveTrainNewBean> list = new ArrayList<>() ;

        if(get() == null) {
            return null ;
        }

        Map<String, LiveTrainNewBean> map = get().getMap() ;

        for(Map.Entry<String, LiveTrainNewBean> entry : map.entrySet()) {
            list.add(entry.getValue());
        }

        if(list.size() > 0) {
            return list ;
        }

        return null ;
    }
}
