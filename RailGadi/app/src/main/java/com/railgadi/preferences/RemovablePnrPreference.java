package com.railgadi.preferences;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.railgadi.R;
import com.railgadi.beans.FindTrainHistoryContainer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vijay on 05-11-2015.
 */
public class RemovablePnrPreference {

    private static String KEY ;
    private Activity activity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();
    private Map<Integer, FindTrainHistoryContainer> savedMap;
    private PnrListHolder pnrListHolder;


    public RemovablePnrPreference(Activity activity) {

        this.activity = activity;
        preferences = this.activity.getPreferences(activity.MODE_PRIVATE);

        KEY =   activity.getResources().getString(R.string.removable_pnr_pref_key) ;
    }


    private class PnrListHolder {

        private List<String> pnrList ;

        private void setList(List<String> list ) {
            this.pnrList = list ;
        }

        private List<String> getList() {
            return this.pnrList ;
        }
    }

    private void save(PnrListHolder pnrListHolder) {

        try {

            editor = preferences.edit();
            String json = gson.toJson(pnrListHolder);
            editor.putString(KEY, json);
            editor.commit();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private PnrListHolder get() {

        Gson gson = new Gson();
        String json = preferences.getString(KEY, "");

        PnrListHolder obj = null ;
        try {

            Type holderType = new TypeToken<PnrListHolder>() {}.getType();
            obj = gson.fromJson(json, holderType);

        } catch(Exception e) {

        }

        return obj;
    }


    public void addToRemovable(String pnr) {

        List<String> list = null ;

        PnrListHolder holder = get() ;

        if(holder != null) {

            list = holder.getList() ;
            if(list != null) {
                list.add(pnr);
                holder.setList(list);
                save(holder);
            } else {
                list = new ArrayList<>() ;
                list.add(pnr);
                holder.setList(list);
                save(holder);
            }
        }
        else {

            holder = new PnrListHolder() ;

            list = new ArrayList<>() ;
            list.add(pnr);

            holder.setList(list);
            save(holder) ;
        }
    }


    public boolean isExists(String pnr) {

        if(get() != null) {
            if(get().getList() != null && get().getList().size() > 0) {
                if(get().getList().contains(pnr)) {
                    return true ;
                }
            } else {
                return false ;
            }
        }
        return false ;
    }


    public void removeOne(String pnr) {
        PnrListHolder holder = get() ;
        if(holder != null) {
            List<String> allPnr = holder.getList() ;
            if(allPnr != null && allPnr.size() > 0) {
                if(allPnr.contains(pnr)) {
                    for(int i=0 ; i<allPnr.size() ; i++) {
                        if(allPnr.get(i).equals(pnr)) {
                            allPnr.remove(i) ;
                            holder.setList(allPnr);
                            save(holder);
                            break ;
                        }
                    }
                }
            }
        }
    }


    public List<String> getAllRemovablePnr() {
        if(get() != null) {
            if(get().getList() != null && get().getList().size() > 0) {
                get().getList() ;
            } else {
                return null ;
            }
        }
        return null ;
    }
}
