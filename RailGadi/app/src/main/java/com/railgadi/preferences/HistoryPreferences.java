package com.railgadi.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.railgadi.R;
import com.railgadi.beans.FindTrainHistoryContainer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryPreferences {

    private static String KEY ;
    private Activity activity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();
    private Map<Integer, FindTrainHistoryContainer> savedMap;
    private MapHolder mapHolder ;


    public HistoryPreferences(Activity activity) {

        this.activity = activity;
        preferences = this.activity.getPreferences(activity.MODE_PRIVATE);

        KEY =   activity.getResources().getString(R.string.findtrain_history_key) ;
    }


    private class MapHolder {

        private Map<Integer, FindTrainHistoryContainer> map;

        private void setMap(Map<Integer, FindTrainHistoryContainer> map) {
            this.map = map;
        }

        private Map<Integer, FindTrainHistoryContainer> getMap() {
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

    public FindTrainHistoryContainer getHistoryFromPref(String trainNumber) {

        mapHolder = get();
        if (mapHolder != null) {
            return mapHolder.getMap().get(trainNumber) ;
        }

        return null;
    }


    public void saveHistoryToPref(FindTrainHistoryContainer findTrainHistoryContainer) {

        mapHolder = get();

        if (mapHolder == null) {

            mapHolder = new MapHolder();
            savedMap = new HashMap<>();

            savedMap.put(findTrainHistoryContainer.hashCode(), findTrainHistoryContainer);

            mapHolder.setMap(savedMap);

        } else {

            savedMap = mapHolder.getMap();
            savedMap.put(findTrainHistoryContainer.hashCode(), findTrainHistoryContainer);

            mapHolder.setMap(savedMap);
        }

        save(mapHolder);
    }


    public void removeAllSavedHistory() {

        Map<Integer, FindTrainHistoryContainer> map = new HashMap<>() ;
        mapHolder = new MapHolder() ;
        mapHolder.setMap(map);
        save(mapHolder) ;
    }


    public List<FindTrainHistoryContainer> getAllSavedHistory() {

        List<FindTrainHistoryContainer> list = new ArrayList<>() ;

        if(get() == null) {
            return null ;
        }

        Map<Integer, FindTrainHistoryContainer> map = get().getMap() ;

        for(Map.Entry<Integer, FindTrainHistoryContainer> entry : map.entrySet()) {
            list.add(entry.getValue());
        }

        if(list.size() > 0) {
            return list ;
        }

        return null ;
    }
}
