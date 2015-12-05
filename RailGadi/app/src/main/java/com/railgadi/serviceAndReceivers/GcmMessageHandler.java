package com.railgadi.serviceAndReceivers;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.preferences.PreferenceUtils;
import com.railgadi.utilities.ApiConstants;

public class GcmMessageHandler extends IntentService {

    private String mes;
    private Handler handler;
    private static int mNotificationId = 0;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        try {

            mes = extras.getString("message");

            if (! PreferenceUtils.isMuteNotificationFlag(getApplicationContext())) {

                showNotification(extras);

                Log.i("GCM", "Received : (" + messageType + ") " + extras.getString("message"));

                Log.i("GCM EXTRAS : ", extras.toString());

                Log.i("GCM", "Received : (" + messageType + ") " + extras.getString("message"));

                GcmReceiver.completeWakefulIntent(intent);
            }

        } catch (Exception e) {

        }
    }

    public void showNotification(Bundle extras) {

        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.rail_icon)
                .setContentTitle("Railgadi")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mes))
                .setContentText(mes).setAutoCancel(true);

        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder.setSound(uri);

        //LED
        mBuilder.setLights(Color.RED, 3000, 3000);

        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        mNotificationId += 1;
        ApiConstants.COUNT += 1;
        mBuilder.setContentIntent(pending);
        mBuilder.setContentText(mes).setNumber(mNotificationId);
        notifyManager.notify(0, mBuilder.build());

    }

    public void showToast() {
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG).show();
            }
        });
    }
}

