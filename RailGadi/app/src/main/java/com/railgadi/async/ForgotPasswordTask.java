package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.activities.LoginActivity;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

/**
 * Created by Vijay on 05-11-2015.
 */
public class ForgotPasswordTask extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String email ;
    private Dialog pro ;
    private String success ;
    private String error ;

    public ForgotPasswordTask(Context context, String email) {
        this.context        =   context ;
        this.email          =   email ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pro = UtilsMethods.getStandardProgressDialog(context, ForgotPasswordTask.this) ;
        pro.show() ;
    }


    @Override
    protected Void doInBackground(Void... params) {

        try {

            String response = HttpServicesRailGadi.POST(ApiConstants.FORGOT_PASSWORD, CommonInputJsonMethod.getForgotPasswordJsonInput(context, email)) ;
            if(response != null) {
                JSONObject object = new JSONObject(response) ;
                if(object.has("data")) {
                    success = object.getString("data") ;
                }
                else if(object.has("code") || object.has("message")) {
                    error = object.getString("message") ;
                }
            }
        } catch( Exception e ) {
            error = "Password not reset try again" ;
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        pro.dismiss();

        if(error != null) {
            ((LoginActivity)context).forgotPasswordMessage(error);
        } else {
            ((LoginActivity)context).forgotPasswordMessage(success);
        }
    }
}
