package com.railgadi.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.HomeLiveTrackingAdapter;
import com.railgadi.adapters.HomeTicketAdapter;
import com.railgadi.async.PnrStatusTaskNew;
import com.railgadi.async.PnrValidationTask;
import com.railgadi.beans.LiveTrainNewBean;
import com.railgadi.beans.PnrPreferenceBean;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.customUi.HorizontalListViewUI;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IPnrStatusComm;
import com.railgadi.preferences.LiveTrainTrackingPreferences;
import com.railgadi.preferences.MyLiveJourneyPreferences;
import com.railgadi.preferences.PnrPreferencesHandler;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.DateAndMore;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment implements IPnrStatusComm {

    private AdView mAdView;
    private AdRequest adRequest;

    private static boolean STARTUP = true;

    private View rootView;

    public static RightDrawerFragment rightDrawerFragment;

    private IFragReplaceCommunicator comm;

    private HorizontalListViewUI horizontalListViewUI;
    private EditText enterPnrEdit;
    private ImageView searchVoiceButton;

    private HomeTicketAdapter horizontalAdapter;
    private HomeLiveTrackingAdapter trackingAdapter;

    private LiveTrainTrackingPreferences liveTrackPref;
    private PnrPreferencesHandler pnrPrefHandler;

    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;

    private LinearLayout listLayout, emptyLayout;
    private ListView trackingListView;
    private TextView welcomeToRailGadi, youCanSeePnr;


    private Dialog dialog;


    // async tasks
    private PnrStatusTaskNew getPnrStatusTask;
    private PnrValidationTask pnrValidationTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        MainActivity.toolbar.setVisibility(View.VISIBLE);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem playStore = menu.getItem(0);
        MenuItem googlePlus = menu.getItem(1);
        MenuItem liveJourney = menu.getItem(2);
        MenuItem overFlow = menu.getItem(3);


        playStore.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                startActivity(UtilsMethods.rateUs(getActivity()));
                return true;
            }
        });

        googlePlus.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(ApiConstants.RAIL_GADI_G_PLUS));
                startActivity(i);

                return true;
            }
        });

        overFlow.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                View v = getActivity().findViewById(R.id.cont_menu);
                PopupMenu popup = new PopupMenu(getActivity(), v);
                popup.getMenuInflater().inflate(R.menu.home_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        String title = item.getTitle().toString();

                        if (title.equals(getActivity().getResources().getString(R.string.share))) {
                            UtilsMethods.openShareIntent(getActivity(), Constants.THIS_IS_WONDERFUL + "\n" + Constants.GOOGLE_PLAY_STATIC_URL + getActivity().getPackageName());
                        } else if (title.equals(getActivity().getResources().getString(R.string.rating))) {
                            if (InternetChecking.isNetWorkOn(getActivity())) {
                                startActivity(UtilsMethods.rateUs(getActivity()));
                            } else {
                                InternetChecking.showNoInternetPopup(getActivity());
                            }
                        } else if (title.equals(getActivity().getResources().getString(R.string.feed_back))) {
                            UtilsMethods.openFeedbackMailIntent(getActivity());
                        }

                        return true;
                    }
                });
                popup.show();

                return true;
            }
        });

        liveJourney.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                openLiveJourney();

                return true;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.demo_home, container, false);

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.home).toUpperCase());
        MainActivity.toolbar.setNavigationIcon(R.drawable.burger_icon);
        MainActivity.toolbar.setVisibility(View.VISIBLE);

        // voice search
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
        speechRecognizer.setRecognitionListener(new SpeechListener());

        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, true);

        mAdView = (AdView) rootView.findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //String devId = PreferenceUtils.getDeviceID(getActivity()) ;
        //String gcmId = PreferenceUtils.getGcmId(getActivity()) ;

        rightDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);

        pnrPrefHandler = new PnrPreferencesHandler(getActivity());
        liveTrackPref = new LiveTrainTrackingPreferences(getActivity());

        comm = (IFragReplaceCommunicator) getActivity();

        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

        initializeAllViews();

        if (STARTUP) {
            List<String> pnrFromMobile = getFromMessages();
            processPnrFromMobile(pnrFromMobile);
            STARTUP = false;
        }

        setUpLiveJourney();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    public void initializeAllViews() {

        listLayout = (LinearLayout) rootView.findViewById(R.id.list_layout);
        emptyLayout = (LinearLayout) rootView.findViewById(R.id.empty_layout);
        trackingListView = (ListView) rootView.findViewById(R.id.home_live_tracking_list);
        setAdapterOnTrackingList();

        welcomeToRailGadi = (TextView) rootView.findViewById(R.id.welcome_to_railgadi);
        youCanSeePnr = (TextView) rootView.findViewById(R.id.you_can_see_your_pnr);
        welcomeToRailGadi.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        youCanSeePnr.setTypeface(AppFonts.getRobotoLight(getActivity()));

        horizontalListViewUI = (HorizontalListViewUI) rootView.findViewById(R.id.horizontal_listview);
        setHorizontalAdapter();

        // edittext
        enterPnrEdit = (EditText) rootView.findViewById(R.id.enter_pnr_edittext);
        enterPnrEdit.setFocusable(false);
        enterPnrEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                enterPnrEdit.setFocusableInTouchMode(true);
                return false;
            }
        });

        enterPnrEdit.setTypeface(AppFonts.getRobotoLight(getActivity()));
        enterPnrEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pnr = s.toString();
                if (pnr.length() == 10) {

                    PnrPreferenceBean bean = pnrPrefHandler.getFromPreferences(pnr);
                    if (bean != null) {

                        comm.respond(new ShowExistingPnr(bean.getFlag(), bean.getPnrObject()));
                        enterPnrEdit.setText("");
                    } else {

                        if (InternetChecking.isNetWorkOn(getActivity())) {
                            getPnrStatusTask = new PnrStatusTaskNew(getActivity(), pnr, HomeFragment.this);
                            getPnrStatusTask.execute();
                        } else {
                            InternetChecking.noInterNetToast(getActivity());
                        }
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(enterPnrEdit.getWindowToken(), 0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchVoiceButton = (ImageView) rootView.findViewById(R.id.search_voice_btn);
        searchVoiceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = UtilsMethods.openRecording(getActivity(), speechRecognizer);
                dialog.show();
                speechRecognizer.startListening(speechIntent);
            }
        });
    }


    public void setHorizontalAdapter() {

        Map<String, PnrStatusNewBean> allUpcomingPnr = pnrPrefHandler.getAllUpcomingPnr();

        if (allUpcomingPnr != null) {

            for (Map.Entry<String, PnrStatusNewBean> entry : allUpcomingPnr.entrySet()) {
                entry.getValue().setFirstVisible(true);
            }

            horizontalAdapter = new HomeTicketAdapter(getActivity(), allUpcomingPnr, HomeFragment.this);
            horizontalListViewUI.setAdapter(horizontalAdapter);

            emptyLayout.setVisibility(View.GONE);
            listLayout.setVisibility(View.VISIBLE);

        } else {

            emptyLayout.setVisibility(View.VISIBLE);
            listLayout.setVisibility(View.GONE);
        }
    }


    private void setUpLiveJourney() {

        MyLiveJourneyPreferences livePreferences = new MyLiveJourneyPreferences(getActivity());

        PnrStatusNewBean livePnrBean = livePreferences.getLiveJourneyFromPref();

        if (livePnrBean != null) {

            /*String travelDate = livePnrBean.getTravelDate() ;
            String duration = livePnrBean.getTrainInfo().duration ;
            String srcDepTime = livePnrBean.getTrainInfo().srcDepTime ;
            Date destinationDate = UtilsMethods.getDestinationDate(travelDate, srcDepTime, duration) ;*/
            try {
                String d = DateAndMore.formatDateToString(livePnrBean.getTrainInfo().destArrDate, DateAndMore.DATE_WITH_DASH_TWO) + " " + livePnrBean.getTrainInfo().destArrTime;
                Date destinationDate = DateAndMore.formatStringToDate(d, DateAndMore.DATA_HH_MM);
                if (new Date().compareTo(destinationDate) >= 0) {
                    livePreferences.removeLiveJourneyData();
                }
            } catch (Exception e) {

            }
        }
    }


    private void openLiveJourney() {

        MyLiveJourneyPreferences livePreferences = new MyLiveJourneyPreferences(getActivity());

        PnrStatusNewBean livePnrBean = livePreferences.getLiveJourneyFromPref();

        if (livePnrBean != null) {
            comm.respond(new MyLiveJourneyFragment(livePnrBean));
        } else {
            Toast.makeText(getActivity(), "No live journey found", Toast.LENGTH_SHORT).show();
        }
    }


    public void setAdapterOnTrackingList() {

        List<LiveTrainNewBean> trackInstanceList = liveTrackPref.getAllSavedTrackingInstances();

        if (trackInstanceList != null && trackInstanceList.size() > 0) {
            trackingAdapter = new HomeLiveTrackingAdapter(getActivity(), trackInstanceList);
            trackingListView.setAdapter(trackingAdapter);
        } else {
            trackingListView.setAdapter(null);
        }
    }


    public void processPnrFromMobile(List<String> pnrList) {

        if (pnrList != null && pnrList.size() > 0) {

            if (InternetChecking.isNetWorkOn(getActivity())) {
                pnrValidationTask = new PnrValidationTask(getActivity(), pnrList, this);
                pnrValidationTask.execute();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (pnrValidationTask != null) {
            pnrValidationTask.cancel(true);
        }

        if (getPnrStatusTask != null) {
            getPnrStatusTask.cancel(true);
        }

        if (trackingAdapter != null) {
            trackingAdapter.cancelTask();
        }

        if (trackingAdapter != null) {
            trackingAdapter.cancelTask();
        }
    }


    public List<String> getFromMessages() {

        Set<String> mSet = new HashSet<>();

        Uri message = Uri.parse("content://sms/");

        Cursor c = getActivity().getContentResolver().query(message, null, null, null, null);
        getActivity().startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {

            for (int i = 0; i < totalSMS; i++) {

                boolean inboxFlag = false;

                String id = c.getString(c.getColumnIndexOrThrow("_id"));
                String address = c.getString(c.getColumnIndexOrThrow("address"));
                String msg = c.getString(c.getColumnIndexOrThrow("body")).trim();

                boolean folderFlag = c.getString(c.getColumnIndexOrThrow("type")).contains("1");

                if (msg.startsWith("PNR") && msg.contains("DOJ") && msg.contains("TRAIN") &&
                        (msg.contains("Dep") || msg.contains("TIME"))) {

                    if (folderFlag) {
                        inboxFlag = true;
                    } else {
                        inboxFlag = false;
                    }

                    if (inboxFlag) {

                        String pnr = DateAndMore.getPnrFromMsg(msg);

                        if (!pnrPrefHandler.isExixts(pnr)) {
                            mSet.add(pnr);
                        }
                    }
                }
                c.moveToNext();
            }
        }

        return new ArrayList<>(mSet);
    }


    public void updatePnrUI(PnrStatusNewBean pnrStatus) {

        enterPnrEdit.setText("");
        comm.respond(new SearchedPnrFragment(pnrStatus));
    }

    public void updateException(String exception) {

        enterPnrEdit.setText("");
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show();
    }

    private class SpeechListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {
            searchVoiceButton.setEnabled(false);
        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            dialog.dismiss();
            searchVoiceButton.setEnabled(true);
        }

        @Override
        public void onError(int error) {
            dialog.dismiss();
            searchVoiceButton.setEnabled(true);
            String errorText = UtilsMethods.getErrorText(error);
            Toast.makeText(getActivity(), errorText, Toast.LENGTH_SHORT).show();
        }

        public void onResults(Bundle results) {

            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            String text1 = matches.get(0);
            String text = "";
            for (int i = 0; i < text1.length(); i++) {
                if (text1.charAt(i) != ' ') {
                    text = text + text1.charAt(i);
                }
            }

            String regex = "[0-9]+";
            if (text.length() > 10) {
                text = text.substring(0, 10);
            }

            try {
                for (int k = 0; k < text.length(); k++) {
                    if (Character.isDigit(text.charAt(k))) {
                        if (k == text.length() - 1) {
                            // Toast.makeText(getApplicationContext(),"Speaking over",Toast.LENGTH_LONG).show();
                            enterPnrEdit.setText(text);
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Please speak only numeric value", Toast.LENGTH_LONG).show();
                        break;
                    }

                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }

}
