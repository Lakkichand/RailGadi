package com.railgadi.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.railgadi.R;
import com.railgadi.beans.SessionHolder;

import java.lang.reflect.Type;

public class SessionPreferenceHandler {

    private static String KEY ;
    private Activity activity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();

    public SessionPreferenceHandler(Activity activity) {

        this.activity = activity;
        preferences = this.activity.getPreferences(activity.MODE_PRIVATE);
        this.KEY = this.activity.getResources().getString(R.string.session_pref_key) ;
    }

    private void save(SessionHolder sHolder) {
        try {

            editor = preferences.edit();
            String json = gson.toJson(sHolder);
            editor.putString(KEY, json);
            editor.commit();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private SessionHolder get() {

        Gson gson = new Gson();
        String json = preferences.getString(KEY, "");
        SessionHolder obj = null ;
        try {
            Type holderType = new TypeToken<SessionHolder>() {}.getType();
            obj = gson.fromJson(json, holderType);
        } catch(Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show() ;
        }
        return obj;
    }

    public SessionHolder getSession() {
        return get() ;
    }

    public void saveSession(SessionHolder sHolder) {
        save(sHolder);
    }
}
