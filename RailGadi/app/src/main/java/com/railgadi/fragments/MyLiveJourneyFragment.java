package com.railgadi.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javadocmd.simplelatlng.Geohasher;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.beans.NearByStationBean;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IBaseGpsListener;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.INearByStations;
import com.railgadi.utilities.CLocation;
import com.railgadi.utilities.DateAndMore;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyLiveJourneyFragment extends Fragment implements IBaseGpsListener, INearByStations {

    private View rootView;

    private PnrStatusNewBean livePnrBean;

    private LocationManager locationManager;

    private IFragReplaceCommunicator comm;

    private TimeTableNewBean.AllStations myNearestStation, nextStation, prevStation;

    //views
    private ImageView arrowImage;
    private TextView trainName, trainNumber, srcName, destName, srcCode, destCode, srcDate, destDate, srcTime, destTime, srcPlat, destPlat,
            travelTime, trainRescheduled, yourCoachPos, liveStatusText, speedTextView, ticketSrc, ticketDest, ticketSrcTime, ticketDestTime,
            pasOne, pasTwo, pasThree, pasFour, pasFive, pasSix, currentTimeDiff, prevTimeDiff, nextTimeDiff,
            nearStationText, nearStationName, ticketDetailsText, pnrText, pnrNumber, seatMap, boogieOrder, moreInfoTitle, moreInformation;

    private boolean locationFlag;


    public MyLiveJourneyFragment(PnrStatusNewBean livePnrBean) {

        this.livePnrBean = livePnrBean;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.toolbar.setVisibility(View.VISIBLE);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.live_journey_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem call = menu.getItem(0);
        MenuItem alarm = menu.getItem(1);

        alarm.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                comm.respond(new AlarmFragment());
                return true;
            }
        });

        call.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                comm.respond(new CustomerCareFragment());
                return true;
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onResume() {

        super.onResume();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.my_live_journey, container, false);

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.my_live_journey).toUpperCase());

        comm = (IFragReplaceCommunicator) getActivity();

        initializeAllViews();
        setTypeface();
        setDataOnViews();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity(), "Please keep GPS on", Toast.LENGTH_LONG).show();
        } else {
            setUp();
        }

        return rootView;
    }


    private void initializeAllViews() {

        arrowImage          = (ImageView) rootView.findViewById(R.id.arrow_image);

        trainName           = (TextView) rootView.findViewById(R.id.train_name);
        trainNumber         = (TextView) rootView.findViewById(R.id.train_number);
        srcName             = (TextView) rootView.findViewById(R.id.src_name);
        destName            = (TextView) rootView.findViewById(R.id.dest_name);
        srcCode             = (TextView) rootView.findViewById(R.id.src_code);
        destCode            = (TextView) rootView.findViewById(R.id.dest_code);
        srcDate             = (TextView) rootView.findViewById(R.id.src_date);
        destDate            = (TextView) rootView.findViewById(R.id.dest_date);
        srcTime             = (TextView) rootView.findViewById(R.id.src_time);
        destTime            = (TextView) rootView.findViewById(R.id.dest_time);
        srcPlat             = (TextView) rootView.findViewById(R.id.src_platform);
        destPlat            = (TextView) rootView.findViewById(R.id.dest_platform);
        travelTime          = (TextView) rootView.findViewById(R.id.travel_time);
        trainRescheduled    = (TextView) rootView.findViewById(R.id.train_rescheduled);
        yourCoachPos        = (TextView) rootView.findViewById(R.id.coach_position_from_engine);
        liveStatusText      = (TextView) rootView.findViewById(R.id.live_status_text);
        speedTextView       = (TextView) rootView.findViewById(R.id.speed_textview);
        prevTimeDiff        = (TextView) rootView.findViewById(R.id.prev_time_diff);
        currentTimeDiff     = (TextView) rootView.findViewById(R.id.current_time_diff);
        nextTimeDiff        = (TextView) rootView.findViewById(R.id.next_time_diff);
        ticketSrc           = (TextView) rootView.findViewById(R.id.ticket_src_name);
        ticketDest          = (TextView) rootView.findViewById(R.id.ticket_dest_name);
        ticketSrcTime       = (TextView) rootView.findViewById(R.id.ticket_src_time);
        ticketDestTime      = (TextView) rootView.findViewById(R.id.ticket_dest_time);
        nearStationText     = (TextView) rootView.findViewById(R.id.near_station_text);
        nearStationName     = (TextView) rootView.findViewById(R.id.near_station_name);
        ticketDetailsText   = (TextView) rootView.findViewById(R.id.ticket_details_text);
        pnrText             = (TextView) rootView.findViewById(R.id.pnr_text);
        pnrNumber           = (TextView) rootView.findViewById(R.id.pnr_number);
        pasOne              = (TextView) rootView.findViewById(R.id.passenger_one);
        pasTwo              = (TextView) rootView.findViewById(R.id.passenger_two);
        pasThree            = (TextView) rootView.findViewById(R.id.passenger_three);
        pasFour             = (TextView) rootView.findViewById(R.id.passenger_four);
        pasFive             = (TextView) rootView.findViewById(R.id.passenger_five);
        pasSix              = (TextView) rootView.findViewById(R.id.passenger_six);
        seatMap             = (TextView) rootView.findViewById(R.id.seat_map);
        boogieOrder         = (TextView) rootView.findViewById(R.id.boogie_order);
        moreInfoTitle       = (TextView) rootView.findViewById(R.id.more_info_title);
        moreInformation     = (TextView) rootView.findViewById(R.id.more_information);

    }


    private void setTypeface() {

        arrowImage.setImageAlpha(140);

        trainName.setTypeface(AppFonts.getRobotoLight(getActivity()));
        trainNumber.setTypeface(AppFonts.getRobotoLight(getActivity()));
        srcName.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        destName.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        srcCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        destCode.setTypeface(AppFonts.getRobotoLight(getActivity()));
        srcDate.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        destDate.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        srcTime.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        destTime.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        srcPlat.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        destPlat.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        travelTime.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        trainRescheduled.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        yourCoachPos.setTypeface(AppFonts.getRobotoThin(getActivity()));
        liveStatusText.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        speedTextView.setTypeface(AppFonts.getRobotoLight(getActivity()));
        ticketSrc.setTypeface(AppFonts.getRobotoLight(getActivity()));
        ticketDest.setTypeface(AppFonts.getRobotoLight(getActivity()));
        ticketSrcTime.setTypeface(AppFonts.getRobotoLight(getActivity()));
        ticketDestTime.setTypeface(AppFonts.getRobotoLight(getActivity()));
        nearStationText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        nearStationName.setTypeface(AppFonts.getRobotoLight(getActivity()));
        ticketDetailsText.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        pnrText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        pnrNumber.setTypeface(AppFonts.getRobotoLight(getActivity()));
        seatMap.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        boogieOrder.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        moreInfoTitle.setTypeface(AppFonts.getRobotoReguler(getActivity()));
        moreInformation.setTypeface(AppFonts.getRobotoLight(getActivity()));

    }


    private void setDataOnViews() {

        PnrStatusNewBean.TrainInfo ti = livePnrBean.getTrainInfo();

        trainName.setText(ti.trainName);
        trainNumber.setText(ti.trainNumber);
        srcCode.setText(livePnrBean.getBoardFromCode());
        destCode.setText(livePnrBean.getBoardToCode());
        srcName.setText(livePnrBean.getBoardFromName());
        destName.setText(livePnrBean.getBoardToName());
        srcDate.setText(DateAndMore.formatDateToString(ti.srcDepDate, DateAndMore.DATE_WITHOUT_TIME));
        destDate.setText(DateAndMore.formatDateToString(ti.destArrDate, DateAndMore.DATE_WITHOUT_TIME));
        srcTime.setText(ti.srcDepTime);
        destTime.setText(ti.destArrTime);
        travelTime.setText("Travel Time : " + ti.duration);
        pnrNumber.setText(new StringBuffer(livePnrBean.getPnrNumber()).insert(3, "-").insert(7, "-").toString());

        TimeTableNewBean.AllStations sourceDetails = livePnrBean.getTrainDetailRoute().getSingleStationDetail(livePnrBean.getBoardFromCode());
        TimeTableNewBean.AllStations destDetails = livePnrBean.getTrainDetailRoute().getSingleStationDetail(livePnrBean.getBoardFromCode());

        if (sourceDetails != null) {
            srcPlat.setText("Platform : " + sourceDetails.platForm);
        }
        if (destDetails != null) {
            destPlat.setText("Platform : " + destDetails.platForm);
        }


        List<PnrStatusNewBean.Passenger> passengerList = livePnrBean.getPassengerList();
        if (passengerList != null && passengerList.size() > 0) {
            TextView[] pasArray = {pasOne, pasTwo, pasThree, pasFour, pasFive, pasSix};
            for (int i = 0; i < passengerList.size(); i++) {
                PnrStatusNewBean.Passenger ps = passengerList.get(i);
                pasArray[i].setText(ps.coachNumber + "-" + ps.seatNumber);
                pasArray[i].setVisibility(View.VISIBLE);
            }
        }


        boogieOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comm.respond(new CoachCompToNextFrag(livePnrBean.getCoachCompositionBean()));
            }
        });

        seatMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openSeatMap(livePnrBean.getClassType());
            }
        });


        locateNearestStation();

    }


    private void locateNearestStation() {

        Location l = UtilsMethods.checkLocation(getActivity());

        if (l != null) {

            try {

                String myGeo = Geohasher.hash(new com.javadocmd.simplelatlng.LatLng(l.getLatitude(), l.getLongitude()));
                /*//String myGeo = "tgt597qrgfx8"; // MCS
                //String myGeo = "tgth6txr8j0m"; // CTC
                //String myGeo = "tggk994upurs"; // RIG
                //String myGeo = "tgth11v7nn8q"; // BRAG*/

                String initials = myGeo.substring(0, 3);

                List<TimeTableNewBean.AllStations> allStationsList = livePnrBean.getTrainDetailRoute().getAllStationsList();

                List<TimeTableNewBean.AllStations> nearestStationList = new ArrayList<>();

                TimeTableNewBean.AllStations pnrStation = null ;

                for (TimeTableNewBean.AllStations as : allStationsList) {

                    if (as.geoHash != null) {

                        if (as.geoHash.startsWith(initials)) {

                            Double d = LatLngTool.distance(Geohasher.decode(as.geoHash), Geohasher.decode(myGeo), LengthUnit.METER);
                            as.distanceFromCurrent = d;

                            nearestStationList.add(as);
                        }

                        if(as.stnCode.equals(livePnrBean.getBoardFromCode())) {
                            pnrStation = as ;
                        }
                    }
                }

                Collections.sort(nearestStationList, new Comparator<TimeTableNewBean.AllStations>() {
                    @Override
                    public int compare(TimeTableNewBean.AllStations lhs, TimeTableNewBean.AllStations rhs) {
                        return lhs.distanceFromCurrent.compareTo(rhs.distanceFromCurrent);
                    }
                });


                if (nearestStationList.size() > 0) {

                    myNearestStation = nearestStationList.get(0);
                    nearStationName.setText(myNearestStation.stnName);

                    String delay = UtilsMethods.getDelayFromCurrent(livePnrBean.getTravelDate(), pnrStation.day, myNearestStation.arrTime, myNearestStation.day);

                    String[] arr = delay.split(" ");
                    if (arr[0].contains("00") && arr[1].contains("00")) {
                        currentTimeDiff.setText("Train running on time");
                        currentTimeDiff.setTextColor(getActivity().getResources().getColor(R.color.green));
                    } else {
                        currentTimeDiff.setText("Train running " + delay + " delay");
                        currentTimeDiff.setTextColor(getActivity().getResources().getColor(R.color.red));
                    }
                }

                String sNo = myNearestStation.sNo;

                if (myNearestStation.isMain) {

                    int prevSno = (Integer.parseInt(sNo) - 1);
                    int nextSno = (Integer.parseInt(sNo) + 1);

                    for (TimeTableNewBean.AllStations as : allStationsList) {

                        if (as.sNo.equals(prevSno + "")) {
                            prevStation = as;
                        }
                        if (as.sNo.equals(nextSno + "")) {
                            nextStation = as;
                        }
                    }

                } else {

                    int prevSno = (int) Math.floor(Double.parseDouble(sNo));
                    int nextSno = (int) Math.ceil(Double.parseDouble(sNo));

                    for (TimeTableNewBean.AllStations as : allStationsList) {

                        if (as.sNo.equals(prevSno + "")) {
                            prevStation = as;
                        }
                        if (as.sNo.equals(nextSno + "")) {
                            nextStation = as;
                        }
                    }
                }

                if (nextStation != null) {
                    ticketDest.setText(nextStation.stnName);
                    ticketDestTime.setText(nextStation.arrTime + ", PF no : " + nextStation.platForm);

                    double aheadDist = Double.parseDouble(nextStation.distance) - Double.parseDouble(myNearestStation.distance);

                    aheadDist = Math.round(aheadDist * 100);
                    aheadDist = aheadDist / 100;

                    nextTimeDiff.setText(aheadDist + " Km");
                }

                if (prevStation != null) {
                    ticketSrc.setText(prevStation.stnName);
                    ticketSrcTime.setText(prevStation.arrTime + ", PF no : " + prevStation.platForm);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
        }
    }


    private void alertToOnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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


    private void setUp() {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }


    public void openSeatMap(String classType) {

        int defaultSelection = -1;

        switch (classType.toUpperCase()) {

            case "1A": {
                defaultSelection = 0;
                break;
            }
            case "2A": {
                defaultSelection = 1;
                break;
            }
            case "3A": {
                defaultSelection = 2;
                break;
            }
            case "SL": {
                defaultSelection = 3;
                break;
            }
            case "3E": {
                defaultSelection = 10;
                break;
            }
        }

        if (defaultSelection != -1) {
            comm.respond(new SeatMapFragment(defaultSelection));
        } else {
            Toast.makeText(getActivity(), "No Seat Map Found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void updateUI(Map<String, NearByStationBean> map) {

        List<Map.Entry<String, NearByStationBean>> entryList = new ArrayList<>(map.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<String, NearByStationBean>>() {
            @Override
            public int compare(Map.Entry<String, NearByStationBean> lhs, Map.Entry<String, NearByStationBean> rhs) {

                Double distanceOne = lhs.getValue().getDistance();
                Double distanceTwo = rhs.getValue().getDistance();

                return distanceOne.compareTo(distanceTwo);
            }
        });

        NearByStationBean nearestStation = entryList.get(0).getValue();

        nearStationName.setText(nearestStation.getStationName());
    }


    @Override
    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show();
    }


    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        float nCurrentSpeed = 0;

        if (location != null) {
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');
        double speed = Double.parseDouble(strCurrentSpeed);
        speed = speed * 1.60934;
        String strUnits = "miles/hour";
        int count = (int) speed;
        String s = String.valueOf(count);
        speedTextView.setText("Train Speed : " + s + " Km/h");
    }


    @Override
    public void onLocationChanged(Location location) {

        try {

            /*if (locationFlag == false) {
                Toast.makeText(getActivity(), "Location Detected", Toast.LENGTH_SHORT).show();
                locationFlag = true;
            }*/

            if (location != null) {
                CLocation myLocation = new CLocation(location);
                updateSpeed(myLocation);
            }

        } catch (Exception e) {

        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        alertToOnGPS();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }

}
