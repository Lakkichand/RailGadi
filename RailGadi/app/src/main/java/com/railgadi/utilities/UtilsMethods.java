package com.railgadi.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.speech.SpeechRecognizer;
import android.util.Base64;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.javadocmd.simplelatlng.LatLng;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.railgadi.R;
import com.railgadi.beans.CoachCompositionBean;
import com.railgadi.beans.FindTrainHistoryContainer;
import com.railgadi.beans.RouteChildBean;
import com.railgadi.beans.RouteGroupBean;
import com.railgadi.beans.StationListDataHolder;
import com.railgadi.beans.StationNameCodeBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.dbhandlers.StationsDBHandler;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IPnrDeleteCommunicator;
import com.railgadi.preferences.HistoryPreferences;
import com.railgadi.preferences.PreferenceUtils;
import com.railgadi.preferences.TimeTablePreferencesHandler;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class UtilsMethods {

    private static Dialog dialog;

    public static String getDelayFromCurrent(String depDate, String pnrStationDay, String actArrTime, String dayOfArrival) throws Exception {

        int dayToBeReduce = (Integer.parseInt(dayOfArrival)) - Integer.parseInt(pnrStationDay);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        depDate = depDate.concat(" " + actArrTime);
        Date d = sdf.parse(depDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_MONTH, dayToBeReduce);
        Date newDate = cal.getTime();
        long diff = new Date().getTime() - newDate.getTime();
        int hours = (int) ((diff / 1000) / 60) / 60;
        int minutes = (int) ((diff / 1000) / 60) % 60;
        String timeDiff = hours + "h " + minutes + "m";
        return timeDiff;

    }


    public static boolean isStartWithDigit(String text) {

        if(text != null && text.length() > 0) {

            char ch = text.charAt(0) ;
            switch(ch) {
                case '0' : return true ;
                case '1' : return true ;
                case '2' : return true ;
                case '3' : return true ;
                case '4' : return true ;
                case '5' : return true ;
                case '6' : return true ;
                case '7' : return true ;
                case '8' : return true ;
                case '9' : return true ;
                default : return false ;
            }
        }
        return false ;
    }


    public static String addFacebookURL(String id) {

        return id.concat("@facebook.com");
    }


    public static void registerDevice(Context context) throws Exception {

        String devId = PreferenceUtils.getDeviceID(context);
        if (devId == null || devId.isEmpty()) {
            UtilsMethods.getDeviceID(context);
        }

        String gcmId = PreferenceUtils.getGcmId(context);
        if (gcmId == null || gcmId.isEmpty()) {
            UtilsMethods.getGcmID(context);
        }

        String response = HttpServicesRailGadi.POST(ApiConstants.DEVICE_REGISTRATION, CommonInputJsonMethod.getDeviceRegInputJson(context));

        JSONObject object = new JSONObject(response);

        if (object.has("code") || object.has("Code")) {
            PreferenceUtils.setDeviceRegistrationFlag(context, false);
        } else {
            PreferenceUtils.setDeviceRegistrationFlag(context, true);
        }
    }


    public static boolean fieldRequired(String text) {

        if (text == null || text.isEmpty()) {
            return false;
        }
        return true;
    }


    public static boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        //String regExpn = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }


    public static void getDeviceID(Context context) {

        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        PreferenceUtils.setDeviceID(context, android_id);
    }


    public static void getGcmID(final Context context) {

        new AsyncTask<Void, Void, String>() {

            GoogleCloudMessaging gcm;
            String gcmId;

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    gcmId = gcm.register(context.getResources().getString(R.string.gcm_api_key));
                    msg = "Device registered, registration ID : " + gcmId;

                    if (gcmId != null || gcmId != "") {
                        PreferenceUtils.setGcmId(context, gcmId);
                    }
                } catch (IOException e) {
                    msg = "Error : " + e.getMessage();
                }
                return msg;
            }

        }.execute(null, null, null);
    }


    public static boolean checkPlayService(final Context context) {

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            dialog.setCancelable(true);
            dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity) context, requestCode);
            dialog.show();
            return false;
        }
        return true;
    }


    public static Date getDateWithTime(String date, String time) throws Exception {

        String fullDate = date + " " + time;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        return sdf.parse(fullDate);
    }


    public static Date addRemoveDayFromDate(Date date, int day) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        date = cal.getTime();
        return date;
    }


    public static String[] getNextRunningDay(boolean[] runningDays) {

        String[] hmy = new String[3];
        Calendar cal = Calendar.getInstance(Configuration.IND_TIMEZONE);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 6;
        } else {
            day -= 2;
        }
        if (runningDays[day]) {
            hmy[2] = cal.get(Calendar.YEAR) + "";
            hmy[1] = String.format("%02d", cal.get(Calendar.MONTH) + 1);
            hmy[0] = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
            return hmy;
        }
        int i = 0;
        int idx = day;
        for (i = 0; i < 7; i++) {
            idx = (i + day) % 7;
            if (runningDays[idx]) {
                break;
            }
        }
        if (i < 7) {
            cal.add(Calendar.DATE, i);
            hmy[2] = cal.get(Calendar.YEAR) + "";
            hmy[1] = String.format("%02d", cal.get(Calendar.MONTH) + 1);
            hmy[0] = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
            return hmy;
        }
        return null;
    }


    public static boolean[] getRunningDays(String day, boolean[] runningDays) {

        int stationDay = Integer.parseInt(day);

        boolean[] newRunningDays = runningDays;
        if (stationDay > 1) {
            stationDay--;
            //boolean temp=runningDays[runningDays.length];
            newRunningDays = new boolean[runningDays.length];
            for (int i = 0; i < 7; i++) {
                boolean runningDay = runningDays[i];
                int idx = (i + stationDay) % 7;
                newRunningDays[idx] = runningDay;
            }
        }
        return newRunningDays;
    }


    public static Map<String, Date> getRunningInstances(String stationDay, boolean[] runningDays) {

        Map<String, Date> dateMap = new HashMap<>();
        boolean[] newRunning = getRunningDays(stationDay, runningDays);
        Calendar cal = Calendar.getInstance(Configuration.IND_TIMEZONE);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            day = 6;
        } else {
            day -= 2;
        }
        if (newRunning[day]) {
            dateMap.put(Constants.TODAY, cal.getTime());
        }
        if ((day == 0) && newRunning[6] || (day > 0) && newRunning[day - 1]) {
            Calendar calendar = Calendar.getInstance(Configuration.IND_TIMEZONE);
            calendar.add(Calendar.DATE, -1);
            dateMap.put(Constants.YESTERDAY, calendar.getTime());
        }
        if ((day == 6) && newRunning[0] || (day < 6) && newRunning[day + 1]) {
            Calendar calendar = Calendar.getInstance(Configuration.IND_TIMEZONE);
            calendar.add(Calendar.DATE, 1);
            dateMap.put(Constants.TOMORROW, calendar.getTime());
        }

        return dateMap;
    }


    public static String[] extractHoursMinutes(String time) {

        String[] hh_mm = time.split(" ");
        hh_mm[0] = hh_mm[0].replaceAll("\\D+", "");
        hh_mm[1] = hh_mm[1].replaceAll("\\D+", "");

        return hh_mm;
    }


    public static Date getDestinationDate(String travelDate, String stationDepTime, String timeDuration) {

        try {

            travelDate = travelDate + " " + stationDepTime;
            Date tDate = DateAndMore.formatStringToDate(travelDate, DateAndMore.DATA_HH_MM);
            tDate = addDuration(tDate, extractHoursMinutes(timeDuration));
            return tDate;

        } catch (Exception e) {

        }
        return null;
    }


    public static Date addDuration(Date date, String[] duration) {

        Calendar cal = Calendar.getInstance(Configuration.IND_TIMEZONE);
        cal.setTime(date);
        int day = Integer.parseInt(duration[0]) / 24;
        int hours = Integer.parseInt(duration[0]) % 24;
        cal.add(Calendar.DATE, day);
        cal.add(Calendar.HOUR, hours);
        cal.add(Calendar.MINUTE, Integer.parseInt(duration[1]));
        return cal.getTime();
    }


    public static void openShareIntent(Context context, String text) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }


    public static Intent rateUs(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()));
    }


    public static String getRailApiKey(Context context) {

        byte[] keyArray = new byte[24];
        byte[] temporaryKey = null;
        String key = context.getResources().getString(R.string.rail_api_lock_key);
        byte[] sharedVector = {0x01, 0x02, 0x03, 0x05, 0x07, 0x0B, 0x0D, 0x11};
        String api = "";
        String cipherText = context.getResources().getString(R.string.rail_api_key);
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            temporaryKey = m.digest(key.getBytes("UTF-8"));
            if (temporaryKey.length < 24) {
                int index = 0;
                for (int i = temporaryKey.length; i < 24; i++) {
                    keyArray[i] = temporaryKey[index];
                }
            }
            Cipher c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyArray, "DESede"), new IvParameterSpec(sharedVector));
            byte[] decrypted = c.doFinal(Base64.decode(cipherText, 1));
            api = new String(decrypted, "UTF-8");
        } catch (Exception e) {
        }
        return api;
    }


    public static void confirmDeleteDialog(Context context, final IPnrDeleteCommunicator fragment) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm");
        builder.setMessage("Do you want to delete ?");

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        fragment.wantToDelete(true);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        fragment.wantToDelete(false);
                        break;
                }
            }
        };

        builder.setPositiveButton("Yes", dialogClickListener);
        builder.setNegativeButton("No", dialogClickListener);

        builder.show();
    }


    public static Dialog openRecording(Context context, final SpeechRecognizer speechRecognizer) {

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pnr_voice_search_layout);
        dialog.setCancelable(true);

        TextView listening = (TextView) dialog.findViewById(R.id.listening);
        listening.setTypeface(AppFonts.getRobotoLight(context));

        ImageView mic = (ImageView) dialog.findViewById(R.id.voice_search_mic_image);
        mic.setImageDrawable(getSvgDrawable(context, R.drawable.voice_search_mic));

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                speechRecognizer.stopListening();
            }
        });

        return dialog;
    }


    public static Drawable getSvgDrawable(Context context, int imageId) {

        SVG svg = SVGParser.getSVGFromResource(context.getResources(), imageId);
        return svg.createPictureDrawable();
    }


    public static Intent grp(Context context) {

        Intent i;
        PackageManager manager = context.getPackageManager();
        try {
            i = manager.getLaunchIntentForPackage("com.info.railwayapp");
            if (i == null) {
                throw new PackageManager.NameNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            return i;
        } catch (PackageManager.NameNotFoundException e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GOOGLE_PLAY_STATIC_URL + Constants.GRP_PACKAGE));
        }
    }


    public static boolean[] getPantries(String input) {

        char[] pan = input.toCharArray();
        boolean[] avlPantry = new boolean[pan.length];
        for (int i = 0; i < pan.length; i++) {
            char c = pan[i];
            switch (c) {
                case '1':
                    avlPantry[i] = true;
                    break;
                case '0':
                    avlPantry[i] = false;
                    break;
            }
        }
        return avlPantry;
    }

    public static boolean[] getRunningDays(String input) {

        char[] rd = input.toCharArray();
        boolean[] runningDays = new boolean[rd.length];
        for (int i = 0; i < rd.length; i++) {
            char c = rd[i];
            switch (c) {
                case '1':
                    runningDays[i] = true;
                    break;
                case '0':
                    runningDays[i] = false;
                    break;
            }
        }
        return runningDays;
    }


    public static List<String> getAvailableClasses(String input) {

        List<String> avlClasses = new ArrayList<>(input.length());
        String[] allClasses = {"1A", "2A", "3A", "3E", "CC", "FC", "SL", "2S"};
        char[] classes = input.toCharArray();

        for (int i = 0; i < classes.length; i++) {
            char c = classes[i];
            switch (c) {
                case '1':
                    avlClasses.add(allClasses[i]);
            }
        }
        return avlClasses;
    }


    public static Dialog getStandardProgressDialog(Context context, final AsyncTask asyncTask) {

        Dialog pro = new Dialog(context);
        pro.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pro.setContentView(R.layout.progress_layout);
        pro.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pro.setCancelable(true);

        pro.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (asyncTask != null) {
                    asyncTask.cancel(true);
                }
            }
        });

        ImageView bg = (ImageView) pro.findViewById(R.id.progress_bar_image);
        Animation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(1200);
        bg.startAnimation(anim);

        return pro;
    }


    public static Location checkLocation(Context context) {

        AppLocationService appLocationService = AppLocationService.getInstance(context);

        Location currentLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);

        if (currentLocation == null) {

            currentLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (currentLocation != null) {

            return currentLocation;

        } else {
            InternetChecking.showLocationAlert(context);
        }
        return null;
    }


    public static void saveTimeTable(Context context, TimeTableNewBean bean, JSONArray array) throws Exception {

        TimeTablePreferencesHandler ttp = new TimeTablePreferencesHandler((Activity) context);
        if (ttp.isAlreadyExists(bean.getTrainNumber())) {
            return;
        }

        bean.setGroupBeans(parseRoute(context, array, false));

        TimeTablePreferencesHandler pref = new TimeTablePreferencesHandler((Activity) context);
        pref.saveTimeTableToPref(bean);

    }


    public static List<RouteGroupBean> parseRoute(Context context, JSONArray routeArray, boolean isInterMediateConsider) throws Exception {

        StationsDBHandler dbHandler = new StationsDBHandler(context);

        int totalDays = Integer.parseInt(routeArray.getJSONObject(routeArray.length() - 1).getString("Day"));

        List<RouteGroupBean> rgbList = new ArrayList<>();

        int rl = 0;
        String day = null;

        for (int i = 1; i <= totalDays; i++) {

            RouteGroupBean rgb = new RouteGroupBean();

            rgb.setDayGroup(i + "");

            List<RouteChildBean> childList = new ArrayList<>();

            for (int j = rl; ; j++) {

                if (j == routeArray.length()) {
                    rgb.setChildList(childList);
                    rgbList.add(rgb);
                    break;
                }

                JSONObject route = routeArray.getJSONObject(j);

                if (j > rl && Integer.parseInt(route.getString("Day")) > Integer.parseInt(day)) {
                    rl = j;
                    rgb.setChildList(childList);
                    rgbList.add(rgb);
                    break;
                }

                if (rl == j) {
                    day = route.getString("Day");
                }

                RouteChildBean rcb = new RouteChildBean();

                String adt = route.getString("adt");
                if ((!adt.isEmpty()) && (adt.charAt(0) == '+')) {
                    rcb.setAdtColor(context.getResources().getColor(R.color.red));
                } else if ((!adt.isEmpty()) && (adt.charAt(0) == '-')) {
                    rcb.setAdtColor(context.getResources().getColor(R.color.green));
                } else {
                    rcb.setAdtColor(context.getResources().getColor(R.color.black));
                }
                rcb.setAdt(adt);
                rcb.setDistance(route.getString("Distance"));

                rcb.setDay(route.getString("Day"));

                String arr = route.getString("ArrTime");
                if (arr == null || arr.isEmpty()) {
                    rcb.setArrival("Src");
                    rcb.setArrivalSelected(true);
                } else {
                    rcb.setArrival(arr);
                    rcb.setArrivalSelected(false);
                }

                String dep = route.getString("DeptTime");
                if (dep == null || dep.isEmpty()) {
                    rcb.setDeparture("Dest");
                    rcb.setDepartureSelected(true);
                } else {
                    rcb.setDeparture(dep);
                    rcb.setDepartureSelected(false);
                }

                boolean hasMain = false;
                if (route.has("MainOrSubStn")) {
                    hasMain = true;
                }

                /*if (route.has("sNo")) {
                    rcb.setSerialNumber(route.getString("sNo"));
                } else {
                    rcb.setSerialNumber((i + 1) + "");
                }

                if (route.has("MainOrSubStn")) {
                    rcb.setMain(true);
                } else {
                    rcb.setMain(false);
                }*/

                rcb.setHaltTime(route.getString("HaltTime"));
                rcb.setPlatform(route.getString("Platform"));
                rcb.setStationCode(route.getString("StnCode"));
                rcb.setStationName(route.getString("StnName"));

                String[] latlng = dbHandler.getLatlngByCode(rcb.getStationCode());
                rcb.setLatitude(latlng[0]);
                rcb.setLongitude(latlng[1]);

                if (latlng != null) {

                    if (hasMain) {

                        if (route.getString("MainOrSubStn").equals("Main")) {
                            if (route.has("sNo")) {
                                rcb.setSerialNumber(route.getString("sNo"));
                            } else {
                                rcb.setSerialNumber((i + 1) + "");
                            }

                            rcb.setMain(true);
                            childList.add(rcb);
                        } else if (route.getString("MainOrSubStn").equals("Intermediate") && isInterMediateConsider) {

                            if (route.has("sNo")) {
                                rcb.setSerialNumber(route.getString("sNo"));
                            } else {
                                rcb.setSerialNumber((i + 1) + "");
                            }

                            rcb.setMain(false);
                            childList.add(rcb);
                        }

                    } else {
                        childList.add(rcb);
                    }
                }
            }
        }

        return rgbList;
    }


    public static List<CoachCompositionBean.CoachBean> extractCoachComposition(Context context, JSONArray array) throws Exception {

        List<CoachCompositionBean.CoachBean> beanList = new ArrayList<>(array.length());

        if (array != null && array.length() > 0) {

            for (int i = 0; i < array.length(); i++) {

                JSONObject json = array.getJSONObject(i);

                CoachCompositionBean.CoachBean cb = new CoachCompositionBean.CoachBean();

                cb.setSerialNo(json.getString("SNo"));
                cb.setCode(json.getString("Code").toLowerCase());
                cb.setDescription(json.getString("Desc").toLowerCase());
                cb.setTextColor(context.getResources().getColor(R.color.black));
                cb.setBackground(0);

                if (i == 0) {

                    if (cb.getDescription().endsWith("-d")) {
                        cb.setDescription("Diesel");
                    } else if (cb.getDescription().endsWith("-de") || cb.getDescription().endsWith("-ed")) {
                        cb.setDescription("Diesel + Electric");
                    } else if (cb.getDescription().endsWith("-e")) {
                        cb.setDescription("Electric");
                    } else {
                        cb.setDescription("Diesel");
                    }
                    cb.setBackground(R.drawable.boogie_diesel);
                    cb.setTextColor(context.getResources().getColor(R.color.white));

                } else {

                    if (cb.getDescription().equals("sl") && cb.getCode().startsWith("s")) {
                        cb.setDescription("Sleeper");
                        cb.setBackground(R.drawable.boogie_sl);
                        cb.setTextColor(context.getResources().getColor(R.color.white));
                    } else if (cb.getDescription().equals("a1")) {

                        if (cb.getCode().startsWith("hb")) {
                            cb.setDescription("AC 1 + AC 3");
                            cb.setBackground(R.drawable.boogie_1a_3a);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                        } else if (cb.getCode().startsWith("ha")) {
                            cb.setDescription("AC 1 + AC 2");
                            cb.setBackground(R.drawable.boogie_1a_2a);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                        } else if (cb.getCode().startsWith("h")) {
                            cb.setDescription("AC 1");
                            cb.setBackground(R.drawable.boogie_ac1);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                        } else if (cb.getCode().startsWith("h")) {
                            cb.setDescription("AC 1");
                            cb.setBackground(R.drawable.boogie_ac1);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                        } else if (cb.getCode().startsWith("hb")) {
                            cb.setDescription("AC 1 + AC 3");
                            cb.setBackground(R.drawable.boogie_1a_3a);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                        }
                    } else if (cb.getDescription().equals("a2")) {

                        if (cb.getCode().startsWith("a")) {
                            cb.setDescription("AC 2");
                            cb.setBackground(R.drawable.boogie_2a);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                        } else if (cb.getCode().startsWith("ab")) {
                            cb.setDescription("AC 2 + AC 3");
                            cb.setBackground(R.drawable.boogie_2a_3a);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                        }
                    } else if (cb.getDescription().equals("a3")) {

                        if (cb.getCode().startsWith("be")) {
                            cb.setBackground(R.drawable.boogie_3a);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                            cb.setDescription(cb.getCode());
                            cb.setCode("A3");
                        } else if (cb.getCode().startsWith("b")) {
                            cb.setDescription("AC 3");
                            cb.setBackground(R.drawable.boogie_3a);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                        } else if (cb.getCode().startsWith("gd")) {
                            cb.setDescription("Luggage + Handicapped");
                            cb.setBackground(R.drawable.boogie_3a);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                        } else if (cb.getCode().startsWith("g")) {
                            cb.setDescription("AC 3");
                            cb.setBackground(R.drawable.boogie_3e);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                        }
                    } else if (cb.getDescription().equals("gen")) {

                        if (cb.getCode().startsWith("gs")) {
                            cb.setDescription("General");
                            cb.setBackground(R.drawable.boogie_gen);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                        }

                        switch (cb.getCode()) {

                            case "lds":
                                cb.setDescription("Ladies");
                                cb.setBackground(R.drawable.boogie_hcp);
                                cb.setTextColor(context.getResources().getColor(R.color.white));
                                break;

                            case "hcp":
                                cb.setDescription("Handicapped boogie");
                                cb.setBackground(R.drawable.boogie_hcp);
                                cb.setTextColor(context.getResources().getColor(R.color.white));
                                break;

                            case "rms":
                                cb.setDescription("Mail Box");
                                cb.setBackground(R.drawable.boogie_mailbox);
                                cb.setTextColor(context.getResources().getColor(R.color.white));
                                break;

                            case "ur":
                                cb.setDescription("General");
                                cb.setBackground(R.drawable.boogie_gen);
                                cb.setTextColor(context.getResources().getColor(R.color.white));
                                break;

                            case "gs":
                                cb.setDescription("General");
                                cb.setBackground(R.drawable.boogie_gen);
                                cb.setTextColor(context.getResources().getColor(R.color.white));
                                break;

                            case "gen":
                                cb.setDescription("General");
                                cb.setBackground(R.drawable.boogie_gen);
                                cb.setTextColor(context.getResources().getColor(R.color.white));
                                break;

                            case "gn":
                                cb.setDescription("General");
                                cb.setBackground(R.drawable.boogie_gen);
                                cb.setTextColor(context.getResources().getColor(R.color.white));
                                break;
                        }
                    } else if (cb.getDescription().equals("pc") && cb.getCode().startsWith("pc")) {

                        cb.setDescription("Pantry Car");
                        cb.setBackground(R.drawable.boogie_pantry);
                        cb.setTextColor(context.getResources().getColor(R.color.white));
                    } else if (cb.getDescription().equals("eog")) {

                        cb.setDescription("End of Generator");
                        cb.setBackground(R.drawable.boogie_slr);
                        cb.setTextColor(context.getResources().getColor(R.color.white));
                    } else if (cb.getDescription().equals("fc") && cb.getCode().startsWith("fc")) {

                        cb.setDescription("First Class");
                        cb.setBackground(R.drawable.boogie_fc);
                        cb.setTextColor(context.getResources().getColor(R.color.white));
                    } else if (cb.getDescription().equals("2s") && cb.getCode().startsWith("d")) {

                        cb.setCode("2S");
                        cb.setDescription("Second Sitting");
                        cb.setBackground(R.drawable.boogie_2s);
                        cb.setTextColor(context.getResources().getColor(R.color.white));
                    } else if (cb.getDescription().equals("cc") && cb.getCode().startsWith("c")) {

                        if (cb.getCode().startsWith("c") || cb.getCode().startsWith("j")) {
                            cb.setDescription("Chair Car");
                            cb.setBackground(R.drawable.boogie_cc);
                            cb.setTextColor(context.getResources().getColor(R.color.white));
                        }
                    } else if (cb.getDescription().equals("ex")) {

                        cb.setDescription("Executive");
                        cb.setBackground(R.drawable.boogie_ac1);
                        cb.setTextColor(context.getResources().getColor(R.color.white));
                    }
                }
                beanList.add(cb);
            }

            return beanList;
        }
        return null;
    }


    private static List<StationNameCodeBean> getPopularStations() {

        List<StationNameCodeBean> listBean = new ArrayList<>();

        listBean.add(new StationNameCodeBean("NDLS", "NEW DELHI", R.drawable.city_icon_delhi));
        listBean.add(new StationNameCodeBean("AGA", "AGRA CITY", R.drawable.city_icon_agra));
        listBean.add(new StationNameCodeBean("ADI", "AHMEDABAD JN", R.drawable.city_icon_ahmedabad));
        listBean.add(new StationNameCodeBean("SBC", "BANGALORE CITY JN", R.drawable.city_icon_banglore));
        listBean.add(new StationNameCodeBean("JP", "JAIPUR", R.drawable.city_icon_jaipur));
        listBean.add(new StationNameCodeBean("MAS", "CHENNAI CENTRAL", R.drawable.city_icon_chennai));
        listBean.add(new StationNameCodeBean("HYB", "HYDERABAD DECCAN", R.drawable.city_icon_hyderabad));
        listBean.add(new StationNameCodeBean("CNB", "KANPUR CENTRAL", R.drawable.city_icon_kanpur));
        listBean.add(new StationNameCodeBean("CHTS", "COCHIN", R.drawable.city_icon_kochi));
        listBean.add(new StationNameCodeBean("HWH", "KOLKATA", R.drawable.city_icon_kolkata));
        listBean.add(new StationNameCodeBean("LKO", "LUCKNOW", R.drawable.city_icon_lakhnow));
        listBean.add(new StationNameCodeBean("BCT", "MUMBAI CENTRAL", R.drawable.city_icon_mumbai));
        listBean.add(new StationNameCodeBean("PUNE", "PUNE JN", R.drawable.city_icon_pune));
        listBean.add(new StationNameCodeBean("BSB", "VARANASI JN", R.drawable.city_icon_varanshi));

        return listBean;
    }


    public static List<StationListDataHolder> collectStations(Activity activity) {

        List<StationListDataHolder> list = new ArrayList<>();

        StationListDataHolder sh = null;

        HistoryPreferences hp = new HistoryPreferences(activity);
        List<FindTrainHistoryContainer> recentList = hp.getAllSavedHistory();

        Set<StationNameCodeBean> set = new HashSet<>();

        if (recentList != null) {
            for (FindTrainHistoryContainer f : recentList) {

                StationNameCodeBean s = new StationNameCodeBean(f.getSrcCode(), f.getSrcName(), f.getSrcIcon());
                set.add(s);
                s = new StationNameCodeBean(f.getDestCode(), f.getDestName(), f.getDestIcon());
                set.add(s);
            }

            sh = new StationListDataHolder();
            sh.setName(activity.getResources().getString(R.string.recent_searched));
            sh.setNameCodeData(new ArrayList<>(set));

            list.add(sh);
        }

        sh = new StationListDataHolder();
        sh.setName(activity.getResources().getString(R.string.popular_stations));
        sh.setNameCodeData(getPopularStations());

        list.add(sh);

        return list;
    }


    private static void alertToOnGPS(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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


    public static Map<String, Integer> getKeysMap(Set<Integer> k) {

        Set<Integer> keys = new HashSet<>(k);

        Map<String, Integer> keysMap = new HashMap<>();

        if (keys.size() > 3) {
            for (int i = 0; i < k.size(); i++) {
                if (keys.contains(0)) {
                    keysMap.put(Constants.TODAY, 0);
                    keys.remove(0);
                } else if (keys.contains(-1)) {
                    keysMap.put(Constants.YESTERDAY, -1);
                    keys.remove(-1);
                } else if (keys.contains(1)) {
                    keysMap.put(Constants.TOMORROW, 1);
                    keys.remove(1);
                } else if (keys.contains(-2)) {
                    keysMap.put(Constants.DAY_BEFORE_YESTERDAY, -2);
                    keys.remove(-2);
                } else if (keys.contains(2)) {
                    keysMap.put(Constants.DAY_AFTER_TOMORROW, 2);
                    keys.remove(2);
                }

                if (keysMap.size() == 3) {
                    break;
                }
            }
        } else {
            for (int i = 0; i < k.size(); i++) {
                if (keys.contains(0)) {
                    keysMap.put(Constants.TODAY, 0);
                    keys.remove(0);
                } else if (keys.contains(-1)) {
                    keysMap.put(Constants.YESTERDAY, -1);
                    keys.remove(-1);
                } else if (keys.contains(1)) {
                    keysMap.put(Constants.TOMORROW, 1);
                    keys.remove(1);
                } else if (keys.contains(-2)) {
                    keysMap.put(Constants.DAY_BEFORE_YESTERDAY, -2);
                    keys.remove(-2);
                } else if (keys.contains(2)) {
                    keysMap.put(Constants.DAY_AFTER_TOMORROW, 2);
                    keys.remove(2);
                }
            }
        }

        return keysMap;
    }


    // uses in voice search
    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }


    public static Intent getMapRouteIntent(LatLng source, LatLng destination) {

        String uri = "http://maps.google.com/maps?saddr=" + source.getLatitude() + "," + source.getLongitude() + "&daddr=" + destination.getLatitude() + "," + destination.getLongitude();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        return intent;
    }


    public static void openFeedbackMailIntent(Context context) {

        StringBuffer msg = new StringBuffer("\n\n\n");
        try {
            msg.append("\n\n\n" + "Build Version : " + getBuildVersion(context) + "\n");
        } catch (PackageManager.NameNotFoundException e) {

        }
        msg.append("Device : " + getDeviceModel() + "\n");
        msg.append("OS : " + getOSVersion());

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.FEEDBACK_RECEIVER_MAIL});
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, Constants.RAILGADI_COMMENTS_SUBJECT);
        intent.putExtra(Intent.EXTRA_TEXT, msg.toString());
        Intent mailer = Intent.createChooser(intent, null);
        context.startActivity(mailer);

    }


    public static String getDeviceModel() {

        String phoneModel = Build.BRAND + " " + Build.MODEL;
        return phoneModel;
    }

    public static String getBuildVersion(Context context) throws PackageManager.NameNotFoundException {

        PackageManager manager = context.getPackageManager();
        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
        String version = info.versionName;
        return version;
    }


    public static String getOSVersion() {

        StringBuilder builder = new StringBuilder();
        builder.append("android : ").append(Build.VERSION.RELEASE);

        return builder.toString();
    }



    public static String serializeObjectToString(Object object) {

        try {
            String serializedObject = "" ;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(object);
            so.flush();
            serializedObject = bo.toString();
            return serializedObject ;
        } catch (Exception e) {
            return "" ;
        }
    }

    public static Object deSerializeStringToObject(String text) {

        try {
            byte b[] = text.getBytes();
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            Object obj = (Object) si.readObject();
            return obj ;
        } catch( Exception e ){
            return null ;
        }
    }
}
