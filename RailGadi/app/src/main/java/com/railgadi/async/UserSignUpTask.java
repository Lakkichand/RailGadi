package com.railgadi.async;

import android.app.Dialog;
import android.os.AsyncTask;

import com.railgadi.activities.SignUpActivity;
import com.railgadi.beans.SignUpRequestBean;
import com.railgadi.preferences.PreferenceUtils;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class UserSignUpTask extends AsyncTask<Void, Void, Void> {

    private SignUpActivity activity ;
    private SignUpRequestBean rBean ;

    private boolean regRes ;

    private Dialog pro ;


    public UserSignUpTask(SignUpActivity activity, SignUpRequestBean rBean) {

        this.activity           =   activity ;
        this.rBean              =   rBean  ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro = UtilsMethods.getStandardProgressDialog(activity, UserSignUpTask.this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {

            if (!PreferenceUtils.isDeviceRegistered(activity)) {
                UtilsMethods.registerDevice(activity);
            }

            String response = HttpServicesRailGadi.POST(ApiConstants.REGISTRATION, CommonInputJsonMethod.getRegistrationInputJson(activity, rBean));

            JSONObject json = new JSONObject(response) ;

            if(json.has("code")) {
                regRes = false ;
                PreferenceUtils.setLoginSession(activity, false) ;
            } else {
                regRes = true ;
                PreferenceUtils.setLoginSession(activity, true) ;
            }

        } catch( Exception e ) {

        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        pro.dismiss() ;
        activity.regResponse(regRes) ;
    }
}
