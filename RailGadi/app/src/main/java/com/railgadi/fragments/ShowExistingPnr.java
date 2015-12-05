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
import com.railgadi.adapters.ShowPnrPsgListAdapter;
import com.railgadi.async.GetTimeTableTaskNew;
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

public class ShowExistingPnr extends Fragment implements IRefreshable, IGettingTimeTableComm {

    private View rootView ;

    private GetTimeTableTaskNew getTimeTableTask ;
    private TimeTablePreferencesHandler timeTablePreferencesHandler ;
    private IFragReplaceCommunicator comm ;

    public  TextView psgrListPoss ;

    private TextView psgrListTitle, psgrListPsgr, psgrListStatus ;
    private TextView pnrTitle, fromStationCode, fromStationName, departureDate, departuerTime, toStationName, toStationCode,
            arrivalTime, arrivalDate, trainName, trainNu, classType, status, duration, noOfStops ;

    public TextView pnrHeader, tripOver, lastUpdate, tripCanceled ;
    public ImageView  stamp, timetableIcon, shareIcon, refreshIcon ;
    public TextView cross ;
    private ListView passengerListView ;
    private ImageView pnrKiStithi ;

    private ShowPnrPsgListAdapter adapter ;

    private boolean chartingFlag = false ;

    private String flag ;
    public PnrStatusNewBean pnrStatus ;

    private PnrPreferencesHandler preferencesHandler ;

    public static MainActivity mainActivity ;

    private RefreshAndUpdatePnrStatusTask refreshAndUpdatePnrStatusTask ;




    public ShowExistingPnr(String flag, PnrStatusNewBean pnrStatus) {

        this.flag = flag ;
        this.pnrStatus = pnrStatus ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(refreshAndUpdatePnrStatusTask != null) {
            refreshAndUpdatePnrStatusTask.cancel(true) ;
        }

        if(getTimeTableTask != null) {
            getTimeTableTask.cancel(true) ;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView        =   inflater.inflate(R.layout.show_pnr_status_from_list_layout, container, false) ;

        MainActivity.toolbar.setVisibility(View.GONE);

        timeTablePreferencesHandler =   new TimeTablePreferencesHandler(getActivity()) ;

        comm    =   (IFragReplaceCommunicator) getActivity() ;

        preferencesHandler = new PnrPreferencesHandler(getActivity()) ;

        initializeViews();
        setDataOnViews(pnrStatus);

        return rootView ;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity.toolbar.setVisibility(View.VISIBLE);
    }


    private void initializeViews() {

        // textviews
        pnrTitle            =   (TextView) rootView.findViewById(R.id.pnr_number_title) ;
        pnrHeader           =   (TextView) rootView.findViewById(R.id.pnr_status_header) ;
        fromStationName     =   (TextView) rootView.findViewById(R.id.from_station_name_textview) ;
        fromStationCode     =   (TextView) rootView.findViewById(R.id.from_station_code_textview) ;
        departureDate       =   (TextView) rootView.findViewById(R.id.departure_date_textview) ;
        departuerTime       =   (TextView) rootView.findViewById(R.id.departure_time_textview) ;
        toStationName       =   (TextView) rootView.findViewById(R.id.to_station_name_textview) ;
        toStationCode       =   (TextView) rootView.findViewById(R.id.to_station_code_textview) ;
        arrivalDate         =   (TextView) rootView.findViewById(R.id.arrival_date_textview) ;
        arrivalTime         =   (TextView) rootView.findViewById(R.id.arrival_time_textview) ;
        trainName           =   (TextView) rootView.findViewById(R.id.train_name_textview) ;
        trainNu             =   (TextView) rootView.findViewById(R.id.train_number_textview) ;
        classType           =   (TextView) rootView.findViewById(R.id.class_type_textview) ;
        status              =   (TextView) rootView.findViewById(R.id.status_textview) ;
        duration            =   (TextView) rootView.findViewById(R.id.duration_textview) ;
        noOfStops           =   (TextView) rootView.findViewById(R.id.nu_of_stop_textview) ;

        tripOver            =   (TextView) rootView.findViewById(R.id.trip_over_button) ;
        tripCanceled        =   (TextView) rootView.findViewById(R.id.trip_canceled_button) ;
        lastUpdate          =   (TextView) rootView.findViewById(R.id.last_updated_button) ;

        psgrListTitle       =   (TextView) rootView.findViewById(R.id.psgr_list_title) ;
        psgrListPsgr        =   (TextView) rootView.findViewById(R.id.psgr_list_psgr) ;
        psgrListPoss        =   (TextView) rootView.findViewById(R.id.psgr_list_poss_title) ;
        psgrListStatus      =   (TextView) rootView.findViewById(R.id.psgr_list_status) ;
        cross               =   (TextView) rootView.findViewById(R.id.pnr_close_cross) ;


        // listviews
        passengerListView   =   (ListView) rootView.findViewById(R.id.pnr_psgr_list) ;

        // imageviews
        pnrKiStithi         =   (ImageView) rootView.findViewById(R.id.pnr_ki_stithi_top) ;
        stamp               =   (ImageView) rootView.findViewById(R.id.status_image) ;
        timetableIcon       =   (ImageView) rootView.findViewById(R.id.train_timetable_icon) ;
        shareIcon           =   (ImageView) rootView.findViewById(R.id.share_button) ;
        refreshIcon         =   (ImageView) rootView.findViewById(R.id.refresh_button) ;


        // setting typeface
        pnrTitle.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        pnrHeader.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        fromStationName.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        toStationName.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        fromStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        toStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        departuerTime.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        departureDate.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        arrivalDate.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        arrivalTime.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        trainName.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        classType.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        status.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        duration.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        noOfStops.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        psgrListStatus.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        psgrListPoss.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        psgrListPsgr.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        psgrListTitle.setTypeface(AppFonts.getRobotoReguler(getActivity()));

        tripOver.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        tripCanceled.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        lastUpdate.setTypeface(AppFonts.getRobotoMedium(getActivity()));
    }

    public Drawable getSvgDrawable(int imageId) {
        SVG svg = SVGParser.getSVGFromResource(getActivity().getResources(), imageId);
        return svg.createPictureDrawable();
    }

    private void setDataOnViews(final PnrStatusNewBean pnrStatus) {

        try {

            final PnrStatusNewBean.TrainInfo pnrTrainInfo = pnrStatus.getTrainInfo() ;

            pnrTitle.setText(new StringBuffer(pnrStatus.getPnrNumber()).insert(3,"-").insert(7,"-").toString());

            pnrKiStithi.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pnr_ki_stithi));

            if(flag.equals(Constants.UPCOMING)) {
                pnrHeader.setText("Upcoming trip");
            } else if (flag.equals(Constants.COMPLETED)) {
                pnrHeader.setText("Completed trip");
            } else if(flag.equals(Constants.OTHERS)) {
                pnrHeader.setText("Other trip");
            }

            fromStationCode.setText(pnrStatus.getBoardFromCode());
            fromStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
            fromStationName.setText(pnrStatus.getBoardFromName());
            departureDate.setText(DateAndMore.formatDateToString(pnrTrainInfo.srcDepDate, DateAndMore.DAY_DATE_MONTH));
            departuerTime.setText(pnrTrainInfo.srcDepTime);

            toStationCode.setText(pnrStatus.getBoardToCode());
            toStationCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
            toStationName.setText(pnrStatus.getBoardToName());
            arrivalDate.setText(DateAndMore.formatDateToString(pnrTrainInfo.destArrDate, DateAndMore.DAY_DATE_MONTH));
            arrivalTime.setText(pnrTrainInfo.destArrTime);

            trainName.setText(pnrTrainInfo.trainName);
            trainName.setTypeface(AppFonts.getRobotoLight(getActivity()));
            trainNu.setText(pnrTrainInfo.trainNumber);
            classType.setText(pnrStatus.getClassType());
            duration.setText(pnrTrainInfo.duration);

            noOfStops.setText(pnrTrainInfo.totalHalts);

            if(pnrStatus.getChartingStatus().equalsIgnoreCase("charting done") || pnrStatus.getChartingStatus().equalsIgnoreCase("chart prepared")) {
                chartingFlag = true ;
                psgrListPoss.setText("Coach");
            }

            lastUpdate.setText("Last Updated @ "+ DateAndMore.formatDateToString(pnrStatus.getLastTimeCheck(), DateAndMore.SUPER_DATE_FORMAT)+"\n"+pnrStatus.getChartingStatus());

            String statusString = pnrStatus.getMainStatus() ;
            status.setText(statusString) ;

            if(statusString.contains("W/L")) {
                status.setText("WL");
                status.setTextColor(getActivity().getResources().getColor(R.color.white));
                status.setBackground(getActivity().getResources().getDrawable(R.drawable.light_red));
            }
            else if(statusString.contains("CNF") || statusString.contains("Conf")) {
                status.setText("CONFIRM");
                status.setBackground(getActivity().getResources().getDrawable(R.drawable.green));
            }
            else if(statusString.contains("RAC")) {
                status.setText("RAC");
                status.setBackground(getActivity().getResources().getDrawable(R.drawable.yellow));
            }
            else if(statusString.contains("CAN")) {
                status.setText("CANCELED");
                status.setTextColor(getActivity().getResources().getColor(R.color.white)) ;
                status.setBackground(getActivity().getResources().getDrawable(R.drawable.black));
                tripCanceled.setVisibility(View.VISIBLE);
            }
            else {
                status.setTextColor(getActivity().getResources().getColor(R.color.black));
            }


            if(pnrStatus.getChartingStatus().equalsIgnoreCase("charting done") || pnrStatus.getChartingStatus().equalsIgnoreCase("chart prepared")) {
                adapter = new ShowPnrPsgListAdapter(getActivity(), pnrStatus.getPassengerList(), chartingFlag, ShowExistingPnr.this );
            } else {
                adapter = new ShowPnrPsgListAdapter(getActivity(), pnrStatus.getPassengerList(), chartingFlag, ShowExistingPnr.this );
            }

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
                    if(InternetChecking.isNetWorkOn(getActivity())) {
                        refreshAndUpdatePnrStatusTask = new RefreshAndUpdatePnrStatusTask(getActivity(), pnrStatus.getPnrNumber(), ShowExistingPnr.this);
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

                    String trainNumber = pnrTrainInfo.trainNumber ;

                    TimeTableNewBean savedTimeTable = timeTablePreferencesHandler.getTimeTableFromPref(trainNumber);

                    if (savedTimeTable != null) {
                        updateTimeTableUI(savedTimeTable);
                    } else {
                        if (InternetChecking.isNetWorkOn(getActivity())) {

                            getTimeTableTask = new GetTimeTableTaskNew(getActivity(), trainNumber, ShowExistingPnr.this);
                            getTimeTableTask.execute();

                        } else {
                            InternetChecking.noInterNetToast(getActivity());
                        }
                    }
                }
            });

        }
        catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show() ;
        }
    }


    @Override
    public void updateRefreshPnrUI(RefreshPnrBean refreshPnrBean) {

        if(pnrStatus != null) {

            this.pnrStatus.setPassengerList(refreshPnrBean.getPassengerList());
            this.pnrStatus.setChartingStatus(refreshPnrBean.getCharting());
            this.pnrStatus.setPnrNumber(refreshPnrBean.getPnrNumber());
            this.pnrStatus.setLastTimeCheck(refreshPnrBean.getLastTimeCheck());
            String statusString = refreshPnrBean.getPassengerList().get(refreshPnrBean.getPassengerList().size()-1).status ;
            this.pnrStatus.setMainStatus(statusString) ;

            setDataOnViews(pnrStatus);

            PnrPreferenceBean bean = new PnrPreferenceBean() ;
            bean.setPnrNumber(pnrStatus.getPnrNumber());
            bean.setFlag(flag);
            bean.setPnrObject(pnrStatus);

            preferencesHandler.saveToPreferences(bean);
            Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show() ;
        } else {
            Toast.makeText(getActivity(), "Error in refreshing .. try again !", Toast.LENGTH_SHORT).show() ;
        }
    }

    @Override
    public void updateTimeTableUI(TimeTableNewBean bean) {
        comm.respond(new RouteMapTabsFragment(bean));
    }

    @Override
    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show() ;
    }
}
