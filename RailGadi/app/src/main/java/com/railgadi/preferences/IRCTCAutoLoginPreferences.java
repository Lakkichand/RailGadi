package com.railgadi.preferences;

import android.app.Activity;
import android.content.SharedPreferences;

public class IRCTCAutoLoginPreferences {


    private static final String USER_NAME_KEY = "AUTOLOGINUSERNAME";
    private static final String PASSWORD_KEY = "AUTOLOGINPASSWORD";
    private Activity activity;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public IRCTCAutoLoginPreferences(Activity activity) {

        this.activity = activity;
        preferences = this.activity.getPreferences(activity.MODE_PRIVATE);
        editor  =   preferences.edit() ;
    }

    public void saveIrctcUsername(String userName) {

        editor.putString(USER_NAME_KEY, userName);
        editor.commit();
    }

    public void saveIrctcPassword(String password) {

        editor.putString(PASSWORD_KEY, password);
        editor.commit();
    }

    public String getUserName() {
        String username = preferences.getString(USER_NAME_KEY, null);
        return username ;
    }

    public String getPassword() {
        String password = preferences.getString(PASSWORD_KEY, null);
        return password ;
    }
}

