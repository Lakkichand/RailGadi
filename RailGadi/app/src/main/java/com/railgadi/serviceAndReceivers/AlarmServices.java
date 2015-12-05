package com.railgadi.serviceAndReceivers;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.railgadi.activities.MainActivity;
import com.railgadi.beans.AlarmDataBean;
import com.railgadi.preferences.AlarmPreference;

public class AlarmServices extends Service implements LocationListener {

    protected LocationManager locationManager;
    public static boolean playAlarm = false;
    boolean flag = false;
    protected LatLng currentLatlng;
    double updatedDist;
    int i = 0;

    public static MainActivity mainActivity;
    public static AlarmPreference preference ;

    private static AlarmDataBean bean ;


    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(getApplicationContext(), "AlarmService called",Toast.LENGTH_LONG).show() ;

        preference = new AlarmPreference(getApplicationContext()) ;
        bean = preference.getSavedAlarm().get(0) ;

        try {

            locationManager = (LocationManager) getApplicationContext().getSystemService(this.LOCATION_SERVICE);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.MIN_LOCATION_UPDATE_TIME, 0, this);

            getProvider();

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void changeLatLng() {
        bean = preference.getSavedAlarm().get(0) ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new AlarmPreference(getApplicationContext()).deleteSavedAlarm();
        stopSelf();
    }

    public void alarm() {

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        KeyguardManager km = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardLock kl = km.newKeyguardLock("INFO");
        WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "INFO");
        wl.acquire(); // wake up the screen
        kl.disableKeyguard();// dismiss the keyguard

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class) ;
        intent.setAction("playalarmreceiver") ;
        sendBroadcast(intent) ;
    }


    public void onLocationChanged(Location location) {

        try {

            if (flag == false) {
                Toast.makeText(getApplicationContext(), "Location Detected", Toast.LENGTH_SHORT).show() ;
                flag = true;
            }

            currentLatlng = new LatLng(location.getLatitude(), location.getLongitude());

            LatLng latlng = new LatLng(bean.getLatitude(), bean.getLongitude());
            updatedDist = LatLngTool.distance(latlng, currentLatlng, LengthUnit.KILOMETER);

            if (updatedDist <= bean.getDistance() && (!playAlarm)) {

                playAlarm = true;
                alarm();
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        i++;

    }

    private boolean isGPSEnabled ;
    private boolean isNetworkEnabled ;
    private Location location ;

    public void getProvider()
    {
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location!=null)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                onLocationChanged(location);
            }

        }
        else if(isGPSEnabled){
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        else {

        }
    }

    public void onProviderDisabled(String arg0) {

        //String msg = "It seems GPS is not working\nfor keep alarm working Please on GPS" ;
        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show() ;
        getProvider();

    }

    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
