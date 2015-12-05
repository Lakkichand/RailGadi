package com.railgadi.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.serviceAndReceivers.AlarmServices;


public class PlayAlarmActivity extends Activity {

    private TextView stopAlarm;
    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_alarm_layout);

        stopAlarm = (TextView) findViewById(R.id.stop_alarm_button);

        try {
            Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }

        } catch (Exception e) {

        }

        stopAlarm.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                try {
                    mMediaPlayer.stop();
                    stopService(new Intent(getApplicationContext(), AlarmServices.class)) ;
                    PlayAlarmActivity.this.finish();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            return false ;
        }
        if(keyCode == KeyEvent.KEYCODE_HOME) {
            return false ;
        }
        if(keyCode == KeyEvent.KEYCODE_MENU) {
            return false ;
        }

        return true ;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
