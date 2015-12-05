package com.railgadi.serviceAndReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.railgadi.activities.PlayAlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent alarmScreen = new Intent(context, PlayAlarmActivity.class) ;
        alarmScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
        context.startActivity(alarmScreen);
    }
}
