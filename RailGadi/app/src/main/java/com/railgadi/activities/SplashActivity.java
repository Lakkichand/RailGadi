package com.railgadi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.widget.ProgressBar;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.railgadi.R;
import com.railgadi.utilities.MyApplication;
import com.railgadi.utilities.UtilsMethods;

import java.security.MessageDigest;


public class SplashActivity extends ActionBarActivity {

    private String KEY;

    private ProgressBar progBar;
    private Handler mHandler;
    private int mProgressStatus = 0;


    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.getInstance().trackScreenView("Splash Screen");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ShimmerFrameLayout layout = (ShimmerFrameLayout) findViewById(R.id.shimmerlayout);
        layout.startShimmerAnimation();

        KEY = this.getResources().getString(R.string.tutorial_lock);

        String key = "" ;

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.railgadi",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = Base64.encodeToString(md.digest(), Base64.DEFAULT) ;
            }
        } catch (Exception e) {

        }


        progBar = (ProgressBar) findViewById(R.id.progBar1);
        mHandler = new Handler();

        fillProgressBar();

        if (UtilsMethods.checkPlayService(this)) {

            UtilsMethods.getDeviceID(this);
            UtilsMethods.getGcmID(this);
        }
    }


    public void fillProgressBar() {

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                progBar.setMax(2000);
                while (mProgressStatus < 2000) {
                    mProgressStatus += 1;
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            progBar.setProgress(mProgressStatus);
                        }
                    });
                    try {
                        //Display progress slowly
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                updateUI();
            }
        }).start();
    }

    private void updateUI() {

        if (UtilsMethods.checkPlayService(this)) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            Boolean yourLocked = prefs.getBoolean(KEY, false);

            if (yourLocked == false) {
                prefs.edit().putBoolean(KEY, true).commit();
                startActivity(new Intent(SplashActivity.this, TutorialActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }
    }
}
