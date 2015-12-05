package com.railgadi.serviceAndReceivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.railgadi.utilities.GPSTracker;


public class GPStStatusReceiver extends BroadcastReceiver {

    private GPSTracker gps;

    private double latitude;

    private double longitude;

    @Override
    public void onReceive( Context context, Intent intent )
    {
        final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            //do something
        //    Toast.makeText(context, "true", Toast.LENGTH_SHORT).show();

            gps = new GPSTracker(context);


        }
        else
        {
           // Toast.makeText(context, "false", Toast.LENGTH_SHORT).show();
        }
    }
}
