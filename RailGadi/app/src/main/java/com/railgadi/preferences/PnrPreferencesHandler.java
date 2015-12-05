package com.railgadi.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.railgadi.R;
import com.railgadi.beans.PnrPreferenceBean;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.utilities.Constants;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PnrPreferencesHandler {


    private static String KEY ;

    private Activity activity;

    private SharedPreferences preferences;
    private Editor editor;

    private Gson gson = new Gson();
    private Map<String, PnrPreferenceBean> savedMap;
    private MapHolder mapHolder;

    public PnrPreferencesHandler(Activity activity) {

        this.activity = activity;
        preferences = this.activity.getPreferences(activity.MODE_PRIVATE);

        this.KEY    =   this.activity.getResources().getString(R.string.pnr_pref_handler_key) ;
    }

    private class MapHolder {

        private Map<String, PnrPreferenceBean> map;

        private void setMap(Map<String, PnrPreferenceBean> map) {
            this.map = map;
        }

        private Map<String, PnrPreferenceBean> getMap() {
            return this.map;
        }
    }

    private void save(MapHolder mapHolder) {

        editor = preferences.edit();
        String json = gson.toJson(mapHolder);
        editor.putString(KEY, json);
        editor.commit();
    }

    private MapHolder get() {

        Gson gson = new Gson();
        String json = preferences.getString(KEY, "");

        //MapHolder obj = new MapHolder() ;
        MapHolder obj = null ;
        try {

            Type holderType = new TypeToken<MapHolder>() {}.getType();

            obj = gson.fromJson(json, holderType);

        } catch(Exception e) {

            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show() ;
        }

        return obj;
    }

    public PnrPreferenceBean getFromPreferences(String pnrNumber) {

        mapHolder = get();
        if (mapHolder != null) {
            return mapHolder.getMap().get(pnrNumber);
        }

        return null;
    }

    public boolean isExixts(String pnr) {
        if(get() != null) {
            if(get().getMap() != null) {
                return get().getMap().keySet().contains(pnr) ;
            }
        }
        return false ;
    }

    public void saveToPreferences(PnrPreferenceBean pnrPreferenceBean) {

        /*if(UpcomingTripFragment.allUpcomings != null) {
            UpcomingTripFragment.allUpcomings.add(pnrPreferenceBean.getPnrObject());
        }*/
        mapHolder = get();

        if (mapHolder == null) {

            mapHolder = new MapHolder();
            savedMap = new HashMap<>();

            savedMap.put(pnrPreferenceBean.getPnrNumber(), pnrPreferenceBean);

            mapHolder.setMap(savedMap);

        } else {

            savedMap = mapHolder.getMap();
            savedMap.put(pnrPreferenceBean.getPnrNumber(), pnrPreferenceBean);

            mapHolder.setMap(savedMap);
        }

        save(mapHolder);
    }


    public void removeFromPreferences(PnrPreferenceBean preferenceBean) {

        Map<String, PnrPreferenceBean> map = get().getMap() ;

        map.remove(preferenceBean.getPnrNumber()) ;
        mapHolder = new MapHolder() ;
        mapHolder.setMap(map);

        save(mapHolder) ;
    }

    public void removeFromPreferences(String pnrNumber) {

        Map<String, PnrPreferenceBean> map = get().getMap() ;

        map.remove(pnrNumber) ;
        mapHolder = new MapHolder() ;
        mapHolder.setMap(map);

        save(mapHolder) ;
    }

    public void changeUp2CompIfExists() throws Exception {

        MapHolder getAllMap = get() ;

        if(getAllMap == null) return ;

        for(Map.Entry<String, PnrPreferenceBean> entry : getAllMap.getMap().entrySet()) {

            PnrPreferenceBean bean = entry.getValue() ;

            PnrStatusNewBean ps = bean.getPnrObject() ;

            Date pnrDate = ps.getTrainInfo().destArrDate ;

            if(pnrDate.compareTo(new Date()) == -1) {
                bean.setFlag(Constants.COMPLETED);
            }
        }

        save(getAllMap);
    }


    public Map<String,PnrStatusNewBean> getAllUpcomingPnr() {

        Map<String, PnrStatusNewBean> upcomingMap = new HashMap<>() ;

        if(get() == null) {
            return null ;
        }

        Map<String, PnrPreferenceBean> map = get().getMap() ;

        for(Map.Entry<String, PnrPreferenceBean> entry : map.entrySet()) {

            PnrPreferenceBean bean = entry.getValue() ;
            if(bean.getFlag().equals(Constants.UPCOMING)) {
                upcomingMap.put(bean.getPnrNumber(), bean.getPnrObject()) ;
            }
        }

        if(upcomingMap.size() == 0) {
            return null ;
        } else {
            return upcomingMap ;
        }
    }

    public Map<String,PnrStatusNewBean> getAllCompletedPnr() {

        Map<String, PnrStatusNewBean> completedMap = new HashMap<>() ;

        if(get() == null) {
            return null ;
        }

        Map<String, PnrPreferenceBean> map = get().getMap() ;

        for(Map.Entry<String, PnrPreferenceBean> entry : map.entrySet()) {

            PnrPreferenceBean bean = entry.getValue() ;
            if(bean.getFlag().equals(Constants.COMPLETED)) {
                completedMap.put(bean.getPnrNumber(), bean.getPnrObject()) ;
            }
        }

        if(completedMap.size() == 0) {
            return null ;
        } else {
            return completedMap ;
        }
    }

    public Map<String,PnrStatusNewBean> getAllOthersPnr() {

        Map<String, PnrStatusNewBean> otherMap = new HashMap<>() ;

        if(get() == null) {
            return null ;
        }

        Map<String, PnrPreferenceBean> map = get().getMap() ;

        for(Map.Entry<String, PnrPreferenceBean> entry : map.entrySet()) {

            PnrPreferenceBean bean = entry.getValue() ;
            if(bean.getFlag().equals(Constants.OTHERS)) {
                otherMap.put(bean.getPnrNumber(), bean.getPnrObject()) ;
            }
        }

        if(map.size() == 0) {
            return null ;
        } else {
            return otherMap ;
        }
    }
}
