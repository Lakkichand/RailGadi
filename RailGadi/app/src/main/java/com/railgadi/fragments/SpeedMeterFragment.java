package com.railgadi.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.customUi.SpeedometerView;
import com.railgadi.interfaces.IBaseGpsListener;
import com.railgadi.utilities.CLocation;

import java.util.Formatter;
import java.util.Locale;

public class SpeedMeterFragment extends Fragment implements IBaseGpsListener {

    private static Handler handler ;

    private boolean flag = false ;

    private long delay = 1000 ;

    private SpeedometerView mView ;

    private int count = 0 ;

    private String speedValueStr ;

    private Thread threadMainMeter ;

    private View rootView ;

    private TextView speedText ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //mView    =   new SpeedometerView(getActivity()) ;

        rootView    =   inflater.inflate(R.layout.speed_meter_layout, container, false) ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.speedometer).toUpperCase());

        LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertToOnGPS();
        } else {
            setUp();
        }

        mView       =   (SpeedometerView) rootView.findViewById(R.id.speedometer_view) ;

        speedText   =   (TextView) rootView.findViewById(R.id.speed_textview) ;

        return rootView ;
    }


    private void setUp() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }


    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        float nCurrentSpeed = 0;

        if(location != null)
        {
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');
        double speed=Double.parseDouble(strCurrentSpeed);
        speed=speed*1.60934;
        String strUnits = "miles/hour";
        count=(int) speed;
        String s=String.valueOf(count);

        //Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show() ;

        speedText.setText((int)speed+" kmph");

        moveNeedle();
    }


    private void moveNeedle() {

        handler = new Handler() {

            public void handleMessage(android.os.Message msg) {

                Bundle b = msg.getData();
                int key = b.getInt("angle_in_degrees", 0);

                if (key == 0) {

                } else {
                    mView.calculateAngleOfDeviation(key);
                }

            };
        };

        handler.postDelayed(null, delay);

        threadMainMeter = new Thread(new Runnable() {

            public void run() {

                generateValue();

                if (Integer.parseInt(speedValueStr) > 240) {
                    speedValueStr = "240";
                }
                if (count > Integer.parseInt(speedValueStr)) {
                    for (int i = count; i >= Integer
                            .parseInt(speedValueStr); i = i - 1) {

                        try {
                            Thread.sleep(15);
                            Message msg = new Message();
                            Bundle b = new Bundle();
                            b.putInt("angle_in_degrees", i);
                            msg.setData(b);
                            handler.sendMessage(msg);

                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }

                } else {
                    for (int i = count; i <= Integer
                            .parseInt(speedValueStr); i = i + 1) {

                        try {
                            Thread.sleep(15);
                            Message msg = new Message();
                            Bundle b = new Bundle();
                            b.putInt("angle_in_degrees", i);
                            msg.setData(b);
                            handler.sendMessage(msg);

                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        threadMainMeter.start();

    }

    private void generateValue() {

        speedValueStr = String.valueOf(count);

    }


    private void alertToOnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onLocationChanged(Location location) {

        if(flag == false) {
            //Toast.makeText(getActivity(), "Location Detected", Toast.LENGTH_SHORT).show() ;
            flag = true ;
        }

        if(location != null) {
            CLocation myLocation = new CLocation(location) ;
            updateSpeed(myLocation);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }
}
