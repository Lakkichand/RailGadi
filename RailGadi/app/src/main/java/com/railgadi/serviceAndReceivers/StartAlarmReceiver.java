package com.railgadi.serviceAndReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Vijay on 03-11-2015.
 */
public class StartAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        context.startService(new Intent(context, AlarmServices.class));
    }
}
