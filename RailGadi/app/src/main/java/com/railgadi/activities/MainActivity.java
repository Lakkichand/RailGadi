package com.railgadi.activities;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.railgadi.R;
import com.railgadi.async.DeletePnrTask;
import com.railgadi.async.DeviceRegistrationTask;
import com.railgadi.async.RefreshInBackGroundTask;
import com.railgadi.dbhandlers.StationDBcreate;
import com.railgadi.dbhandlers.TrainDBcreate;
import com.railgadi.fragments.FareBreakupFragmentFT;
import com.railgadi.fragments.HomeFragment;
import com.railgadi.fragments.LeftDrawerFragment;
import com.railgadi.fragments.RightDrawerFragment;
import com.railgadi.fragments.RouteMapTabsFragment;
import com.railgadi.fragments.SearchedPnrFragment;
import com.railgadi.fragments.ShowExistingPnr;
import com.railgadi.interfaces.ICloseEverything;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.preferences.PnrPreferencesHandler;
import com.railgadi.preferences.PreferenceUtils;
import com.railgadi.preferences.RemovablePnrPreference;
import com.railgadi.serviceAndReceivers.AlarmServices;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.MyApplication;
import com.railgadi.utilities.UtilsMethods;

import java.util.List;

public class MainActivity extends ActionBarActivity implements IFragReplaceCommunicator {



    public static Toolbar toolbar;

    public FragmentManager fragManager;

    private FragmentTransaction fragTrans;

    private StationDBcreate stationDbCreator;
    private TrainDBcreate trainDbCreator ;

    public PnrPreferencesHandler preferencesHandler;

    private RefreshInBackGroundTask refreshInBackGroundTask;
    private DeletePnrTask deletePnrTask ;
    private DeviceRegistrationTask drt ;

    public static ICloseEverything closeFragmentObject;
    public static LeftDrawerFragment leftDrawerFragment;
    public static RightDrawerFragment rightDrawerFragment;

    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.getInstance().trackScreenView("Home Screen");

        if(InternetChecking.isNetWorkOn(this)) {
            if(PreferenceUtils.getGcmId(this) == null || PreferenceUtils.getGcmId(this).equals("")) {
                UtilsMethods.getGcmID(this) ;
            }
            if(! PreferenceUtils.isDeviceRegistered(this)) {
                drt = new DeviceRegistrationTask(this) ;
                drt.execute() ;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stationDbCreator = new StationDBcreate(this);
        trainDbCreator = new TrainDBcreate(this) ;

        preferencesHandler = new PnrPreferencesHandler(this);

        // thread for replacing upcoming to completed
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    preferencesHandler.changeUp2CompIfExists();

                } catch (Exception e) {

                }
            }
        }).start();

        if(InternetChecking.isNetWorkOn(this)) {

            refreshInBackGroundTask = new RefreshInBackGroundTask(this);
            refreshInBackGroundTask.execute();

            RemovablePnrPreference removablePref = new RemovablePnrPreference(this) ;
            List<String> removablePnr = removablePref.getAllRemovablePnr() ;
            if(removablePnr != null && removablePnr.size() > 0) {
                deletePnrTask = new DeletePnrTask(this, removablePnr, false) ;
                deletePnrTask.execute() ;
            }
        }

        try {
            stationDbCreator.createDataBase();
            stationDbCreator.openDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            trainDbCreator.createDataBase();
            trainDbCreator.openDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //newDBHander = new NewDBHandler(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // initializing static instances in fragment
        RouteMapTabsFragment.mainActivity = MainActivity.this;
        SearchedPnrFragment.mainActivity = MainActivity.this;
        ShowExistingPnr.mainActivity = MainActivity.this;
        FareBreakupFragmentFT.mainActivity = MainActivity.this;
        AlarmServices.mainActivity = MainActivity.this;

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.burger_icon);
        toolbar.setTitle(getResources().getString(R.string.home).toUpperCase());

        fragManager = getSupportFragmentManager();
        fragTrans = fragManager.beginTransaction();

        respond(new HomeFragment());

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        LeftDrawerFragment drawerFragmentLeft = (LeftDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_drawer_fragment_left);
        drawerFragmentLeft.setUp(drawerLayout, toolbar, R.id.nav_drawer_fragment_left);


        RightDrawerFragment drawerFragmentRight = (RightDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_drawer_fragment_right);
        drawerFragmentRight.setUp(drawerLayout, toolbar, R.id.nav_drawer_fragment_right);

    }

    private String CURRENT;

    @Override
    public void respond(Fragment fragment) {

        try {

            fragTrans = fragManager.beginTransaction();

            String TAG = fragment.getClass().getName();
            if (fragManager.getBackStackEntryCount() > 0) {
                CURRENT = getSupportFragmentManager().getBackStackEntryAt(fragManager.getBackStackEntryCount() - 1).getName();
            }

            if (CURRENT != null) {
                if (CURRENT.equals(TAG)) {
                    return;
                }
            }

            fragTrans.replace(R.id.container_fragment, fragment, TAG).addToBackStack(TAG).commit();
            CURRENT = "";

        } catch (Exception e) {

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (refreshInBackGroundTask != null) {
            refreshInBackGroundTask.cancel(true);
        }
        if(deletePnrTask != null) {
            deletePnrTask.cancel(true) ;
        }
        if(drt != null) {
            drt.cancel(true) ;
        }
    }

    @Override
    public void onBackPressed() {

        if (leftDrawerFragment != null) {
            if (!leftDrawerFragment.closeEveryThing()) {
                return;
            }
        }
        if (rightDrawerFragment != null) {
            if (!rightDrawerFragment.closeEveryThing()) {
                return;
            }
        }

        if (fragManager.getBackStackEntryCount() > 0) {
            CURRENT = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        }
        if (CURRENT != null && CURRENT.equals(HomeFragment.class.getName())) {
            finish();
            return;
        }

        if (closeFragmentObject != null) {
            if (!closeFragmentObject.closeEveryThing()) {
                return;
            }
        }

        fragManager.popBackStack(CURRENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        CURRENT = "" ;
    }
}
