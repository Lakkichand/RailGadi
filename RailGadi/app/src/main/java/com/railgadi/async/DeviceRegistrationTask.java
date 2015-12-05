package com.railgadi.async;

import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.utilities.UtilsMethods;



public class DeviceRegistrationTask extends AsyncTask<Void, Void, Void> {

    private Context context ;

    public DeviceRegistrationTask(Context context) {
        this.context = context ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {

            UtilsMethods.registerDevice(context) ;

        } catch( Exception e ){

        }

        return null;
    }
}
