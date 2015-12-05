package com.railgadi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.adapters.PagerAdapter;
import com.railgadi.async.DeviceRegistrationTask;
import com.railgadi.fragments.TutorialFive;
import com.railgadi.fragments.TutorialFour;
import com.railgadi.fragments.TutorialOne;
import com.railgadi.fragments.TutorialThree;
import com.railgadi.fragments.TutorialTwo;
import com.railgadi.preferences.PreferenceUtils;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.List;
import java.util.Vector;

public class TutorialActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {

    private ViewPager tutorialPager;

    private PagerAdapter mPagerAdapter ;

    private TextView getStarted, one, two, three, four, five ;

    private DeviceRegistrationTask deviceRegistrationTask ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tutorialPager   =   (ViewPager) findViewById(R.id.tutorial_pager);

        getStarted      =   (TextView) findViewById(R.id.get_started) ;

        one             =   (TextView) findViewById(R.id.one) ;
        two             =   (TextView) findViewById(R.id.two) ;
        three           =   (TextView) findViewById(R.id.three) ;
        four            =   (TextView) findViewById(R.id.four) ;
        five            =   (TextView) findViewById(R.id.five) ;

        initialiseViewPager();

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialActivity.this, SignUpActivity.class));
                finish();
            }
        });

        deviceRegistrationTask  =   new DeviceRegistrationTask(this) ;
        deviceRegistrationTask.execute() ;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(InternetChecking.isNetWorkOn(this)) {
            if(PreferenceUtils.getGcmId(this) == null || PreferenceUtils.getGcmId(this).equals("")) {
                UtilsMethods.getGcmID(this) ;
            }
        }
    }

    public void initialiseViewPager() {

        List<Fragment> fragments = new Vector<>();

        fragments.add(android.support.v4.app.Fragment.instantiate(this, TutorialOne.class.getName()));
        fragments.add(android.support.v4.app.Fragment.instantiate(this, TutorialTwo.class.getName()));
        fragments.add(android.support.v4.app.Fragment.instantiate(this, TutorialThree.class.getName()));
        fragments.add(android.support.v4.app.Fragment.instantiate(this, TutorialFour.class.getName()));
        fragments.add(android.support.v4.app.Fragment.instantiate(this, TutorialFive.class.getName()));

        this.mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        this.tutorialPager.setAdapter(this.mPagerAdapter);
        this.tutorialPager.setOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                one.setBackground(getResources().getDrawable(R.drawable.round_white_selected));
                two.setBackground(getResources().getDrawable(R.drawable.round));
                three.setBackground(getResources().getDrawable(R.drawable.round));
                four.setBackground(getResources().getDrawable(R.drawable.round));
                five.setBackground(getResources().getDrawable(R.drawable.round));
                break;
            case 1:
                two.setBackground(getResources().getDrawable(R.drawable.round_white_selected));
                one.setBackground(getResources().getDrawable(R.drawable.round));
                three.setBackground(getResources().getDrawable(R.drawable.round));
                four.setBackground(getResources().getDrawable(R.drawable.round));
                five.setBackground(getResources().getDrawable(R.drawable.round));
                break;
            case 2:
                three.setBackground(getResources().getDrawable(R.drawable.round_white_selected));
                two.setBackground(getResources().getDrawable(R.drawable.round));
                one.setBackground(getResources().getDrawable(R.drawable.round));
                four.setBackground(getResources().getDrawable(R.drawable.round));
                five.setBackground(getResources().getDrawable(R.drawable.round));
                break;
            case 3 :
                four.setBackground(getResources().getDrawable(R.drawable.round_white_selected));
                two.setBackground(getResources().getDrawable(R.drawable.round));
                three.setBackground(getResources().getDrawable(R.drawable.round));
                one.setBackground(getResources().getDrawable(R.drawable.round));
                five.setBackground(getResources().getDrawable(R.drawable.round));
                break ;
            case 4 :
                five.setBackground(getResources().getDrawable(R.drawable.round_white_selected));
                two.setBackground(getResources().getDrawable(R.drawable.round));
                three.setBackground(getResources().getDrawable(R.drawable.round));
                four.setBackground(getResources().getDrawable(R.drawable.round));
                one.setBackground(getResources().getDrawable(R.drawable.round));
                break ;
            case 5 :

                break ;
        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {


    }
}
