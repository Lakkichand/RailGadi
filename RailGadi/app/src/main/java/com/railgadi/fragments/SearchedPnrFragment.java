package com.railgadi.fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.PnrPassengerListAdapter;
import com.railgadi.async.GetTimeTableTaskNew;
import com.railgadi.async.GetTrainDetailForPnrTask;
import com.railgadi.async.RefreshAndUpdatePnrStatusTask;
import com.railgadi.beans.PnrPreferenceBean;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.beans.RefreshPnrBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IGettingTimeTableComm;
import com.railgadi.interfaces.IRefreshable;
import com.railgadi.preferences.PnrPreferencesHandler;
import com.railgadi.preferences.TimeTablePreferencesHandler;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.DateAndMore;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.Date;
import java.util.Map;

public class SearchedPnrFragment extends Fragment implements IRefreshable, IGettingTimeTableComm {

    private View rootView;

    public TextView possibilityTitle;
    private TextView pnrTitle, fromStationCode, fromStationName, departureDate, departuerTime, toStationName,
            toStationCode, arrivalTime, arrivalDate, trainName, trainNu,
            classType, status, duration, noOfStops;

    public TextView addToTrip, tripOver, tripCanceled;
    public ImageView stamp, timetableIcon, shareIcon, refreshIcon, pnrKiStithi;
    private TextView cross;
    public PnrStatusNewBean pnrStatus;
    private ListView passengerListView;

    public static MainActivity mainActivity;

    private IFragReplaceCommunicator comm;

    private GetTimeTableTaskNew getTimeTableTask;

    private TimeTablePreferencesHandler timeTablePreferencesHandler;

    private PnrPassengerListAdapter adapter;

    private PnrPreferencesHandler preference;

    private RefreshAndUpdatePnrStatusTask refreshAndUpdatePnrStatusTask;
    private GetTrainDetailForPnrTask getTrainDetailForPnrTask;


    public SearchedPnrFragment(PnrStatusNewBean pnrStatus) {

        this.pnrStatus = pnrStatus;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (refreshAndUpdatePnrStatusTask != null) {
            refreshAndUpdatePnrStatusTask.cancel(true);
        }

        if (getTimeTableTask != null) {
            getTimeTableTask.cancel(true);
        }

        if (getTrainDetailForPnrTask != null) {
            getTrainDetailForPnrTask.cancel(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        MainActivity.toolbar.setVisibility(View.VISIBLE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.search_pnr_layout, container, false);

        comm = (IFragReplaceCommunicator) getActivity();

        preference = new PnrPreferencesHandler(getActivity());

        timeTablePreferencesHandler = new TimeTablePreferencesHandler(getActivity());

        initializeViews();
        setDataOnViews(pnrStatus);

        return rootView;
    }


    public void initializeViews() {

        // textviews
        pnrTitle = (TextView) rootView.findViewById(R.id.pnr_number_title);
        fromStationName = (TextView) rootView.findViewById(R.id.from_station_name_textview);
        fromStationCode = (TextView) rootView.findViewById(R.id.from_station_code_textview);
        fromStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        departureDate = (TextView) rootView.findViewById(R.id.departure_date_textview);
        departuerTime = (TextView) rootView.findViewById(R.id.departure_time_textview);
        toStationName = (TextView) rootView.findViewById(R.id.to_station_name_textview);
        toStationCode = (TextView) rootView.findViewById(R.id.to_station_code_textview);
        toStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        arrivalDate = (TextView) rootView.findViewById(R.id.arrival_date_textview);
        arrivalTime = (TextView) rootView.findViewById(R.id.arrival_time_textview);
        trainName = (TextView) rootView.findViewById(R.id.train_name_textview);
        trainNu = (TextView) rootView.findViewById(R.id.train_number_textview);
        classType = (TextView) rootView.findViewById(R.id.class_type_textview);
        status = (TextView) rootView.findViewById(R.id.status_textview);
        duration = (TextView) rootView.findViewById(R.id.duration_textview);
        noOfStops = (TextView) rootView.findViewById(R.id.nu_of_stop_textview);
        possibilityTitle = (TextView) rootView.findViewById(R.id.possibility_title);

        addToTrip = (TextView) rootView.findViewById(R.id.add_trip_text_button); // button
        addToTrip.setOnClickListener(new ClickOnAddTrip());
        tripOver = (TextView) rootView.findViewById(R.id.trip_over_button);
        tripCanceled = (TextView) rootView.findViewById(R.id.trip_canceled_button);


        // listviews
        passengerListView = (ListView) rootView.findViewById(R.id.pnr_psgr_list);

        // imageviews
        pnrKiStithi = (ImageView) rootView.findViewById(R.id.pnr_ki_stithi_top);
        cross = (TextView) rootView.findViewById(R.id.pnr_close_cross);
        cross.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        stamp = (ImageView) rootView.findViewById(R.id.status_image);
        timetableIcon = (ImageView) rootView.findViewById(R.id.train_timetable_icon);
        shareIcon = (ImageView) rootView.findViewById(R.id.share_button);
        refreshIcon = (ImageView) rootView.findViewById(R.id.refresh_button);

    }

    public Drawable getSvgDrawable(int imageId) {
        SVG svg = SVGParser.getSVGFromResource(getActivity().getResources(), imageId);
        return svg.createPictureDrawable();
    }

    public void setDataOnViews(final PnrStatusNewBean pnrStatus) {

        try {

            final PnrStatusNewBean.TrainInfo pnrTrainInfo = pnrStatus.getTrainInfo();

            pnrKiStithi.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pnr_ki_stithi));

            pnrTitle.setText(new StringBuffer(pnrStatus.getPnrNumber()).insert(3, "-").insert(7, "-").toString());

            fromStationCode.setText(pnrStatus.getBoardFromCode());
            fromStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
            fromStationName.setText(pnrStatus.getBoardFromName());
            departureDate.setText(DateAndMore.formatDateToString(pnrTrainInfo.srcDepDate, DateAndMore.DAY_DATE_MONTH));  // array
            departuerTime.setText(pnrTrainInfo.srcDepTime);

            toStationCode.setText(pnrStatus.getBoardToCode());
            toStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
            toStationName.setText(pnrStatus.getBoardToName());
            arrivalDate.setText(DateAndMore.formatDateToString(pnrTrainInfo.destArrDate, DateAndMore.DAY_DATE_MONTH));  // array
            arrivalTime.setText(pnrTrainInfo.destArrTime);

            trainName.setText(pnrTrainInfo.trainName);
            trainName.setTypeface(AppFonts.getRobotoLight(getActivity()));
            trainNu.setText(pnrTrainInfo.trainNumber);
            classType.setText(pnrStatus.getClassType());
            duration.setText(pnrTrainInfo.duration);

            noOfStops.setText(pnrTrainInfo.totalHalts);

            if (pnrStatus.getChartingStatus().equalsIgnoreCase("charting done") || pnrStatus.getChartingStatus().equalsIgnoreCase("chart prepared")) {
                possibilityTitle.setText("COACH");
            }

            Date pnrDate = pnrTrainInfo.destArrDate ;

            if (pnrDate.compareTo(new Date()) == -1) {
                addToTrip.setVisibility(View.GONE);
                tripOver.setVisibility(View.VISIBLE);
            }

            String statusString = pnrStatus.getMainStatus();
            status.setText(statusString);

            if (statusString.contains("W/L")) {
                status.setText("WL");
                status.setTextColor(getActivity().getResources().getColor(R.color.white));
                status.setBackground(getActivity().getResources().getDrawable(R.drawable.light_red));
            } else if (statusString.contains("CNF") || statusString.contains("Conf")) {
                status.setText("CONFIRM");
                status.setBackground(getActivity().getResources().getDrawable(R.drawable.green));
            } else if (statusString.contains("RAC")) {
                status.setText("RAC");
                status.setBackground(getActivity().getResources().getDrawable(R.drawable.yellow));
            } else if (statusString.contains("CAN") || statusString.contains("Can/Mod")) {
                status.setText("CANCELED");
                status.setTextColor(getActivity().getResources().getColor(R.color.white));
                status.setBackground(getActivity().getResources().getDrawable(R.drawable.black));

                addToTrip.setVisibility(View.GONE);
                tripOver.setVisibility(View.GONE);
                tripCanceled.setVisibility(View.VISIBLE);
            } else {
                status.setTextColor(getActivity().getResources().getColor(R.color.black));
            }

            /*if (pnrStatus.getChartingStatus().equalsIgnoreCase("charting done") || pnrStatus.getChartingStatus().equalsIgnoreCase("chart prepared")) {
                adapter = new PnrPassengerListAdapter(getActivity(), pnrStatus.getPassengerList(), true, SearchedPnrFragment.this);
                addToTrip.setClickable(false);
                addToTrip.setText(pnrStatus.getChartingStatus());
            } else {
                adapter = new PnrPassengerListAdapter(getActivity(), pnrStatus.getPassengerList(), false, SearchedPnrFragment.this);
            }*/

            if (pnrTrainInfo.destArrDate.compareTo(new Date()) <= 0) {
                adapter = new PnrPassengerListAdapter(getActivity(), pnrStatus.getPassengerList(), true, SearchedPnrFragment.this);
                addToTrip.setClickable(false);
                addToTrip.setText(pnrStatus.getChartingStatus());
            } else {
                adapter = new PnrPassengerListAdapter(getActivity(), pnrStatus.getPassengerList(), false, SearchedPnrFragment.this);
            }


/*

            if (pnrStatus.getChartingStatus().equalsIgnoreCase("charting done") || pnrStatus.getChartingStatus().equalsIgnoreCase("chart prepared")) {

                int size = pnrStatus.getTrainDetailRoute().getAllStationsList().size() ;
                TimeTableNewBean.AllStations endStation = pnrStatus.getTrainDetailRoute().getAllStationsList().get(size-1);
                Date arrDate = UtilsMethods.addRemoveDayFromDate(DateAndMore.formatStringToDate(pnrStatus.getTravelDate(), DateAndMore.DATE_WITH_DASH_TWO), Integer.parseInt(endStation.day)) ;

                Date actArrDateTime = UtilsMethods.getDateWithTime(DateAndMore.formatDateToString(arrDate, DateAndMore.DATE_WITH_DASH_TWO), endStation.arrTime) ;

                if(actArrDateTime.compareTo(new Date()) == -1) {
                    addToTrip.setClickable(false);
                    addToTrip.setText("Trip Over");
                }
                else {
                    adapter = new PnrPassengerListAdapter(getActivity(), pnrStatus.getPassengerList(), true, SearchedPnrFragment.this);
                    addToTrip.setClickable(false);
                    addToTrip.setText(pnrStatus.getChartingStatus());
                }
            } else {
                adapter = new PnrPassengerListAdapter(getActivity(), pnrStatus.getPassengerList(), false, SearchedPnrFragment.this);
            }
*/

            passengerListView.setAdapter(adapter);

            cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.fragManager.popBackStack();
                }
            });

            refreshIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (InternetChecking.isNetWorkOn(getActivity())) {
                        refreshAndUpdatePnrStatusTask = new RefreshAndUpdatePnrStatusTask(getActivity(), pnrStatus.getPnrNumber(), SearchedPnrFragment.this);
                        refreshAndUpdatePnrStatusTask.execute();
                    } else {
                        InternetChecking.noInterNetToast(getActivity());
                    }
                }
            });

            shareIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String forShare = pnrStatus.getPnrNumber()+"\n"+pnrStatus.getMainStatus() ;
                    UtilsMethods.openShareIntent(getActivity(), forShare);
                }
            });

            timetableIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String trainNumber = pnrTrainInfo.trainNumber;

                    TimeTableNewBean savedTimeTable = timeTablePreferencesHandler.getTimeTableFromPref(trainNumber);

                    if (savedTimeTable != null) {
                        updateTimeTableUI(savedTimeTable);
                    } else {
                        if (InternetChecking.isNetWorkOn(getActivity())) {

                            getTimeTableTask = new GetTimeTableTaskNew(getActivity(), trainNumber, SearchedPnrFragment.this);
                            getTimeTableTask.execute();

                        } else {
                            InternetChecking.noInterNetToast(getActivity());
                        }
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public class ClickOnAddTrip implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            try {

                int totalSavedUpcoming = 0;

                Map<String, PnrStatusNewBean> map = preference.getAllUpcomingPnr();
                if (map != null && map.size() > 0) {
                    totalSavedUpcoming = map.size();
                }

                if (totalSavedUpcoming >= 20) {
                    Toast.makeText(getActivity(), "Unable to add more then 20 trips", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(InternetChecking.isNetWorkOn(getActivity())) {
                    getTrainDetailForPnrTask = new GetTrainDetailForPnrTask(getActivity(), pnrStatus, SearchedPnrFragment.this);
                    getTrainDetailForPnrTask.execute();
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }

            } catch (Exception e) {

                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void addPnrToTrips(PnrStatusNewBean bean) {

        PnrPreferenceBean prefHolder = new PnrPreferenceBean();
        prefHolder.setFlag(Constants.UPCOMING);
        prefHolder.setPnrNumber(bean.getPnrNumber());
        prefHolder.setPnrObject(bean);

        preference.saveToPreferences(prefHolder);

        timeTablePreferencesHandler.saveTimeTableToPref(bean.getTrainDetailRoute());

        mainActivity.fragManager.popBackStack();
    }


    @Override
    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateTimeTableUI(TimeTableNewBean bean) {
        comm.respond(new RouteMapTabsFragment(bean));
    }

    @Override
    public void updateRefreshPnrUI(RefreshPnrBean refreshStatus) {

        this.pnrStatus.setPnrNumber(refreshStatus.getPnrNumber());
        this.pnrStatus.setChartingStatus(refreshStatus.getCharting());
        this.pnrStatus.setPassengerList(refreshStatus.getPassengerList());
        this.pnrStatus.setLastTimeCheck(refreshStatus.getLastTimeCheck());

        setDataOnViews(pnrStatus);
        Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
    }
}
