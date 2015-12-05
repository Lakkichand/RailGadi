package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.LoginResponseBean;
import com.railgadi.interfaces.ILogin;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class LoginTask extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String id, password ;
    private ILogin activity ;

    private LoginResponseBean bean ;

    private Dialog pro ;

    public LoginTask(Context context, String id, String password, ILogin activity) {

        this.context        =   context ;
        this.id             =   id ;
        this.password       =   password ;
        this.activity       =   activity ;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro     = UtilsMethods.getStandardProgressDialog(context, LoginTask.this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String response = HttpServicesRailGadi.POST(ApiConstants.LOGIN, CommonInputJsonMethod.getLoginJsonInput(context, id, password)) ;

        if(response != null) {

            try {

                JSONObject json = new JSONObject(response) ;

                if(json.has("code") || json.has("message")) {
                    bean = new LoginResponseBean() ;
                    bean.setException(json.getString("message"));
                }
                else if (json.has("data")) {

                    bean = JsonResponseParsing.getSuccessLoginData(json) ;
                }

            } catch( Exception e ) {
                bean = null ;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        pro.dismiss() ;
        this.cancel(true) ;

        activity.updateLoginUI( bean );
    }
}
