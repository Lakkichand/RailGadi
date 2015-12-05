package com.railgadi.utilities;

import android.content.Context;
import android.location.Location;

import com.javadocmd.simplelatlng.LatLng;
import com.railgadi.beans.CancelledTrainBean;
import com.railgadi.beans.CoachCompositionBean;
import com.railgadi.beans.CurrentBookingAvailabilityBean;
import com.railgadi.beans.DivertedTrainBean;
import com.railgadi.beans.FareEnquiryNewBean;
import com.railgadi.beans.LiveStationNewBean;
import com.railgadi.beans.LiveTrainNewBean;
import com.railgadi.beans.LoginResponseBean;
import com.railgadi.beans.MiniTimeTableBean;
import com.railgadi.beans.NearByAmenitiesBean;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.beans.RefreshPnrBean;
import com.railgadi.beans.RescheduledTrainBean;
import com.railgadi.beans.SearchTrainResultBean;
import com.railgadi.beans.SeatFareBeanNew;
import com.railgadi.beans.SpecialTrainNewBean;
import com.railgadi.beans.StationInformationBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.beans.TrainDataBean;
import com.railgadi.dbhandlers.StationsDBHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonResponseParsing {


    public static LoginResponseBean getSuccessLoginData(JSONObject response) {

        LoginResponseBean bean = null ;

        if(response != null) {

            try {

                bean                =   new LoginResponseBean() ;

                JSONObject data     =   response.getJSONObject("data") ;

                bean.setEmail(data.getString("email")) ;
                bean.setName(data.getString("username"));

                return bean ;

            } catch( Exception e ) {
                bean = null ;
            }
        }

        return bean ;

    }


    public static LiveTrainNewBean getLiveTrainData(MiniTimeTableBean miniTimeTableBean, JSONObject response) {

        LiveTrainNewBean bean = null;

        if (response != null) {

            try {

                bean = new LiveTrainNewBean();

                JSONObject statusJson = response.getJSONObject("liveTrnStatus");

                JSONObject lastJson = statusJson.getJSONObject("lst_stn");
                if (lastJson != null && lastJson.has("code") && lastJson.has("message")) {
                    bean.setLastStation(null);
                    return bean;
                } else {

                    LiveTrainNewBean.LastStation ls = new LiveTrainNewBean.LastStation();

                    ls.stnCode = lastJson.getString("stnCode");
                    ls.stnName = lastJson.getString("stnName");
                    ls.delay = lastJson.getString("delay");
                    ls.depTime = lastJson.getString("deptTime");

                    bean.setLastStation(ls);
                }

                JSONObject nextJson = statusJson.getJSONObject("nxt_stn");
                if (nextJson != null && nextJson.has("code") && nextJson.has("message")) {
                    bean.setNextStation(null);
                } else {

                    LiveTrainNewBean.NextStation ns = new LiveTrainNewBean.NextStation();

                    ns.stnCode = nextJson.getString("stnCode");
                    ns.stnName = nextJson.getString("stnName");
                    ns.nextDistAhead = nextJson.getString("nxt_dst_ahead");

                    bean.setNextStation(ns);
                }

                if (bean.getNextStation() != null) {

                    String nextStnCode = bean.getNextStation().stnCode;

                    for (int i = 0; i < miniTimeTableBean.getTrainRouteList().size(); i++) {

                        MiniTimeTableBean.TrainRoute tr = miniTimeTableBean.getTrainRouteList().get(i);

                        if (tr.stationCode.equals(nextStnCode)) {

                            bean.nextStnPosition = i;
                            bean.prevStnPosition = i - 1;
                            bean.trainPosition = bean.prevStnPosition;
                            break;
                        }
                    }

                    String lastStnCode = bean.getLastStation().stnCode;

                    if (lastStnCode.equals(miniTimeTableBean.getTrainRouteList().get(bean.prevStnPosition).stationCode)) {
                        bean.isMain = true;
                    } else {
                        bean.isMain = false;
                    }

                } else {
                    bean.trainPosition = miniTimeTableBean.getTrainRouteList().size() - 1;
                    bean.isMain = true ;
                }

                bean.setJourneyStation(statusJson.getString("journey_stn"));
                bean.setSchArr(statusJson.getString("sch_arr"));
                bean.setSchDep(statusJson.getString("sch_dept"));
                bean.setActArr(statusJson.getString("act_arr"));
                bean.setActDep(statusJson.getString("act_dept"));
                bean.setLastUpdatedTime(statusJson.getString("lastUpdateTime"));
                bean.setMiniTimeTableBean(miniTimeTableBean);

            } catch (Exception e) {

                bean = null;
            }
        }
        return bean;
    }


    public static PnrStatusNewBean getPnrStatus(Context context, JSONObject response) {

        StationsDBHandler db = new StationsDBHandler(context);

        PnrStatusNewBean bean = null;

        if (response != null) {

            try {
                bean = new PnrStatusNewBean();

                JSONObject status = response.getJSONObject("status");
                JSONObject trainInfo = response.getJSONObject("traininfo");

                bean.setPnrNumber(status.getString("pnr"));
                bean.setClassType(status.getString("class"));

                bean.setChartingStatus(status.getString("chart"));
                //bean.setChartingStatus("CHART PREPARED");

                bean.setTravelDate(status.getString("travel_date"));
                bean.setLastTimeCheck(new Date());
                bean.setFirstVisible(true);

                String boardFromCode = status.getString("from");
                String boardToCode = status.getString("to");

                bean.setBoardFromCode(boardFromCode);
                bean.setBoardFromName(db.getStationNameFromCode(boardFromCode));
                bean.setBoardToCode(boardToCode);
                bean.setBoardToName(db.getStationNameFromCode(boardToCode));

                PnrStatusNewBean.TrainInfo ti = new PnrStatusNewBean.TrainInfo();

                ti.trainName = trainInfo.getString("TrainName");
                ti.trainNumber = trainInfo.getString("TrainNo");
                //ti.srcStnCode       =   trainInfo.getString("SrcStncode") ;
                //ti.srcStnName       =   trainInfo.getString("SrcStnName") ;
                ti.srcDepTime = trainInfo.getString("SrcDeptTime");
                //ti.destStnCode      =   trainInfo.getString("DestStncode") ;
                //ti.destStnName      =   trainInfo.getString("DestStnName") ;
                ti.destArrTime = trainInfo.getString("DestArrTime");

                String travelDate = status.getString("travel_date");

                //Date sDate = DateAndMore.formatStringToDate(travelDate, DateAndMore.DATE_WITH_DASH_TWO);
                Date sDate = UtilsMethods.getDateWithTime(travelDate, ti.srcDepTime);
                ti.srcDepDate = sDate;

                Date dDate = UtilsMethods.addDuration(sDate, UtilsMethods.extractHoursMinutes(trainInfo.getString("TravelTime")));
                ti.destArrDate = dDate;

                ti.duration = trainInfo.getString("TravelTime");
                ti.totalHalts = trainInfo.getString("NumOfHalts");
                ti.distance = trainInfo.getString("Distance");
                ti.trainType = trainInfo.getString("TrainType");

                ti.runningDays = UtilsMethods.getRunningDays(trainInfo.getString("RunningDays"));
                ti.avlClasses = UtilsMethods.getAvailableClasses(trainInfo.getString("AvlClass"));

                ti.pantries = UtilsMethods.getPantries(trainInfo.getString("Pantry"));

                bean.setTrainInfo(ti);

                List<Map.Entry<Integer, String>> possList = null;

                GetConfirmChance gcc = new GetConfirmChance();
                HashMap<Integer, String> possibilityMap = gcc.getPNRConfirmChance(bean.getPnrNumber());
                if (possibilityMap != null) {
                    possList = new ArrayList<>(possibilityMap.entrySet());
                    Collections.sort(possList, new Comparator<Map.Entry<Integer, String>>() {
                        @Override
                        public int compare(Map.Entry<Integer, String> lhs, Map.Entry<Integer, String> rhs) {
                            return lhs.getKey().compareTo(rhs.getKey());
                        }
                    });
                }

                JSONArray pList = status.getJSONArray("passenger");
                List<PnrStatusNewBean.Passenger> passengers = new ArrayList<>(pList.length());
                for (int i = 0; i < pList.length(); i++) {

                    JSONObject json = pList.getJSONObject(i);

                    PnrStatusNewBean.Passenger p = new PnrStatusNewBean.Passenger();

                    p.sNo = "Passenger " + json.getString("sNo");
                    p.coachNumber = json.getString("coach_number");
                    p.seatNumber = json.getString("seat_number");
                    p.quotaType = json.getString("quota_type");
                    p.coachPos = json.getString("coach_pos");
                    p.status = json.getString("status");
                    p.possibility = "-";

                    if (possList != null) {
                        p.possibility = possList.get(i).getValue();
                    }

                    if (i == pList.length() - 1) {
                        bean.setMainStatus(p.status);
                    }

                    passengers.add(p);
                }

                bean.setPassengerList(passengers);

                return bean;

            } catch (Exception e) {
                bean = null;
            }
        }

        return bean;
    }


    public static SearchTrainResultBean getSearchTrain(JSONObject response) {

        SearchTrainResultBean searchTrainResultBean = null;

        if (response != null) {

            try {

                searchTrainResultBean = new SearchTrainResultBean();

                JSONArray trainArray = response.getJSONArray("search_trains");

                if (trainArray != null && trainArray.length() > 0) {

                    List<SearchTrainResultBean.SearchTrain> searchTrainList = new ArrayList<>(trainArray.length());

                    for (int i = 0; i < trainArray.length(); i++) {

                        SearchTrainResultBean.SearchTrain train = new SearchTrainResultBean.SearchTrain();

                        JSONObject trainJson = trainArray.getJSONObject(i);

                        train.trainNo = trainJson.getString("trainNo");
                        train.trainName = trainJson.getString("trainName");
                        train.trainType = trainJson.getString("trainType");
                        train.srcStnCode = trainJson.getString("srcStnCode");
                        train.destStnCode = trainJson.getString("dstnStnCode");
                        train.srcStnName = trainJson.getString("srcStnName");
                        train.destStnName = trainJson.getString("dstnStnName");
                        train.arrTime = trainJson.getString("arrTime");
                        train.deptTime = trainJson.getString("deptTime");
                        train.travelTime = trainJson.getString("travelTime");
                        train.runningDays = UtilsMethods.getRunningDays(trainJson.getString("runningDays"));
                        train.avlClasses = UtilsMethods.getAvailableClasses(trainJson.getString("AvlClass"));

                        searchTrainList.add(train);
                    }

                    searchTrainResultBean.setSearchTrainList(searchTrainList);
                }

            } catch (Exception e) {
                searchTrainResultBean = null;
            }
        }

        return searchTrainResultBean;
    }


    public static LiveStationNewBean getLiveStation(JSONObject response) {

        LiveStationNewBean bean = null;

        if (response != null) {

            try {

                bean = new LiveStationNewBean();

                JSONArray liveArray = response.getJSONArray("liveStn");

                if (liveArray != null && liveArray.length() > 0) {

                    List<LiveStationNewBean.LiveStationData> liveStationDataList = new ArrayList<>(liveArray.length());

                    for (int i = 0; i < liveArray.length(); i++) {

                        JSONObject json = liveArray.getJSONObject(i);

                        LiveStationNewBean.LiveStationData liveStationData = new LiveStationNewBean.LiveStationData();

                        liveStationData.trainNumber = json.getString("trainNo");
                        liveStationData.trainName = json.getString("trainName");
                        liveStationData.trainType = json.getString("trainType");
                        liveStationData.srcStationName = json.getString("srcStnName");
                        liveStationData.destStationName = json.getString("dstnStnName");
                        liveStationData.sta = json.getString("sta");
                        liveStationData.std = json.getString("std");
                        liveStationData.eta = json.getString("eta");
                        liveStationData.etd = json.getString("etd");
                        liveStationData.delay = json.getString("delay");
                        liveStationData.platform = json.getString("pf");

                        liveStationDataList.add(liveStationData);
                    }

                    bean.setLiveStationList(liveStationDataList);
                }

            } catch (Exception e) {
                bean = null;
            }
        }

        return bean;
    }


    public static RefreshPnrBean getRefreshPnr(JSONObject response) {

        RefreshPnrBean bean = null;

        if (response != null) {

            try {

                bean = new RefreshPnrBean();

                JSONObject status = response.getJSONObject("status");

                bean.setPnrNumber(status.getString("pnr"));
                bean.setCharting(status.getString("chart"));
                //bean.setCharting("CHART PREPARED");
                bean.setLastTimeCheck(new Date());

                JSONArray passArray = status.getJSONArray("passenger");

                List<Map.Entry<Integer, String>> possList = null;

                if (passArray != null && passArray.length() > 0) {

                    GetConfirmChance gcc = new GetConfirmChance();
                    HashMap<Integer, String> possibilityMap = gcc.getPNRConfirmChance(bean.getPnrNumber());
                    if (possibilityMap != null) {
                        possList = new ArrayList<>(possibilityMap.entrySet());
                        Collections.sort(possList, new Comparator<Map.Entry<Integer, String>>() {
                            @Override
                            public int compare(Map.Entry<Integer, String> lhs, Map.Entry<Integer, String> rhs) {
                                return lhs.getKey().compareTo(rhs.getKey());
                            }
                        });
                    }


                    JSONArray pList = status.getJSONArray("passenger");
                    List<PnrStatusNewBean.Passenger> passengers = new ArrayList<>(pList.length());
                    for (int i = 0; i < pList.length(); i++) {

                        JSONObject json = pList.getJSONObject(i);

                        PnrStatusNewBean.Passenger p = new PnrStatusNewBean.Passenger();

                        p.sNo = "Passenger " + json.getString("sNo");
                        p.coachNumber = json.getString("coach_number");
                        p.seatNumber = json.getString("seat_number");
                        p.quotaType = json.getString("quota_type");
                        p.coachPos = json.getString("coach_pos");
                        p.status = json.getString("status");
                        p.possibility = "-";

                        if (possList != null) {
                            p.possibility = possList.get(i).getValue();
                        }

                        passengers.add(p);
                    }

                    bean.setPassengerList(passengers);
                }

            } catch (Exception e) {
                bean = null;
            }
        }
        return bean;
    }


    public static FareEnquiryNewBean getFareEnquiry(JSONObject response) {

        FareEnquiryNewBean bean = null;

        if (response != null) {

            try {

                bean = new FareEnquiryNewBean();

                JSONObject mainJson = response.getJSONObject("fare_breakup");

                JSONArray fareDetails = mainJson.getJSONArray("Fare_Details");

                if (fareDetails != null && fareDetails.length() > 0) {

                    List<FareEnquiryNewBean.FareDetails> fareList = new ArrayList<>(fareDetails.length());

                    for (int i = 0; i < fareDetails.length(); i++) {

                        JSONObject json = fareDetails.getJSONObject(i);

                        FareEnquiryNewBean.FareDetails fd = new FareEnquiryNewBean.FareDetails();

                        fd.key = json.getString("key");
                        fd.value = json.getString("value");

                        fareList.add(fd);

                        if (i == (fareDetails.length() - 1)) {

                            FareEnquiryNewBean.FareDetails f = new FareEnquiryNewBean.FareDetails();

                            f.key = "Net Amount";
                            if (mainJson.getString("Net_Amount") == null || mainJson.getString("Net_Amount").isEmpty()) {
                                f.value = mainJson.getString("Total_Amount");
                            } else {
                                f.value = mainJson.getString("Net_Amount");
                            }

                            fareList.add(f);
                        }
                    }

                    bean.setTrainNumber(mainJson.getString("train_number"));
                    bean.setTrainType(mainJson.getString("traintype"));
                    bean.setNetAmount(mainJson.getString("Net_Amount"));
                    bean.setTotalAmount(mainJson.getString("Total_Amount"));
                    bean.setFareList(fareList);
                }

                return bean;

            } catch (Exception e) {
                bean = null;
            }
        }

        return bean;
    }


    public static PnrStatusNewBean getPnrTrainInfoRouteList(Context context, JSONObject object, PnrStatusNewBean runningBean) {

        if (object != null) {

            try {

                JSONObject trainInfoJson = object.getJSONObject("traininfo");
                JSONObject coachJson = object.getJSONObject("CoachComposition");

                JSONArray coaches = coachJson.getJSONArray("Coachs");

                List<CoachCompositionBean.CoachBean> coachesList = UtilsMethods.extractCoachComposition(context, coaches);

                if (coachesList != null) {

                    CoachCompositionBean ccb = new CoachCompositionBean();
                    ccb.setTrainName(coachJson.getString("TrainName"));
                    ccb.setCoachBean(coachesList);

                    runningBean.setCoachCompositionBean(ccb);
                }

                TimeTableNewBean route = new TimeTableNewBean();

                route.setTrainNumber(trainInfoJson.getString("TrainNo"));
                route.setTrainName(trainInfoJson.getString("TrainName"));
                route.setTotalTravelTime(trainInfoJson.getString("TravelTime"));
                route.setAvlClasses(UtilsMethods.getAvailableClasses(trainInfoJson.getString("AvlClass")));
                route.setTrainType(trainInfoJson.getString("TrainType"));
                route.setDistance(trainInfoJson.getString("Distance"));
                route.setTotalHalts(trainInfoJson.getString("NumOfHalts"));

                route.setRunningDays(UtilsMethods.getRunningDays(trainInfoJson.getString("RunningDays")));
                route.setPantries(UtilsMethods.getPantries(trainInfoJson.getString("Pantry")));

                JSONArray routeArray = trainInfoJson.getJSONArray("Route");
                route.setGroupBeans(UtilsMethods.parseRoute(context, routeArray, true));

                List<TimeTableNewBean.AllStations> allStationsList = new ArrayList<>(routeArray.length());

                StationsDBHandler dbHandler = new StationsDBHandler(context) ;

                for (int i = 0; i < routeArray.length(); i++) {

                    JSONObject routeJson = routeArray.getJSONObject(i);

                    TimeTableNewBean.AllStations allStation = new TimeTableNewBean.AllStations();

                    if(routeJson.has("sNo"))
                        allStation.sNo = routeJson.getString("sNo") ;
                    if(routeJson.has("MainOrSubStn")) {
                        String mainOrSub = routeJson.getString("MainOrSubStn") ;
                        if(mainOrSub.toLowerCase().equals("main")) {
                            allStation.isMain = true ;
                        } else {
                            allStation.isMain = false ;
                        }
                    }
                    if (routeJson.has("StnCode")) {
                        allStation.stnCode = routeJson.getString("StnCode");
                        allStation.geoHash = dbHandler.getGeoHashOfStation(allStation.stnCode) ;
                    }
                    if (routeJson.has("StnName"))
                        allStation.stnName = routeJson.getString("StnName");
                    if (routeJson.has("ArrTime"))
                        allStation.arrTime = routeJson.getString("ArrTime");
                    if (routeJson.has("DeptTime"))
                        allStation.deptTime = routeJson.getString("DeptTime");
                    if (routeJson.has("HaltTime"))
                        allStation.halt = routeJson.getString("HaltTime");
                    if (routeJson.has("Platform"))
                        allStation.platForm = routeJson.getString("Platform");
                    if (routeJson.has("Distance"))
                        allStation.distance = routeJson.getString("Distance");
                    if (routeJson.has("Day"))
                        allStation.day = routeJson.getString("Day");
                    if (routeJson.has("adt"))
                        allStation.adt = routeJson.getString("adt");

                    allStationsList.add(allStation);
                }

                route.setAllStationsList(allStationsList);

                runningBean.setTrainDetailRoute(route);

                UtilsMethods.saveTimeTable(context, route, trainInfoJson.getJSONArray("Route"));

                return runningBean;

            } catch (Exception e) {
                runningBean = null;
            }
        }

        return runningBean;
    }


    public static CurrentBookingAvailabilityBean getCurrentBookingAvailability(JSONObject response) {

        CurrentBookingAvailabilityBean bean = new CurrentBookingAvailabilityBean();

        if (response != null) {

            try {

                JSONArray currentBooking = response.getJSONArray("crnt_bkg");

                if (currentBooking != null && currentBooking.length() > 0) {

                    List<CurrentBookingAvailabilityBean.CurrentBooking> bookingList = new ArrayList<>(currentBooking.length());

                    for (int i = 0; i < currentBooking.length(); i++) {

                        CurrentBookingAvailabilityBean.CurrentBooking cBooking = new CurrentBookingAvailabilityBean.CurrentBooking();

                        JSONObject json = currentBooking.getJSONObject(i);

                        List<CurrentBookingAvailabilityBean.SeatAvailability> seatList = new ArrayList<>();

                        JSONArray seatArray = json.getJSONArray("seatAvl");

                        if (seatArray != null && seatArray.length() > 0) {

                            for (int j = 0; j < seatArray.length(); j++) {

                                JSONObject seatJson = seatArray.getJSONObject(j);

                                CurrentBookingAvailabilityBean.SeatAvailability seat = new CurrentBookingAvailabilityBean.SeatAvailability();

                                seat.keyClass = seatJson.getString("key");
                                seat.valueAvl = seatJson.getString("value");

                                seatList.add(seat);
                            }
                        }

                        cBooking.setTrainName(json.getString("train_name"));
                        cBooking.setTrainNumber(json.getString("train_number"));
                        cBooking.setSrcStationName(json.getString("srcStnName"));
                        cBooking.setDeptTime(json.getString("deptTime"));
                        cBooking.setDestStationName(json.getString("destStnName"));
                        cBooking.setSeatAvlList(seatList);

                        bookingList.add(cBooking);
                    }

                    bean.setCurrentBookingList(bookingList);
                }

                return bean;

            } catch (Exception e) {
                bean = null;
            }

        }

        return bean;
    }


    public static PnrStatusNewBean getMessageValidationPnrStatus(Context context, JSONObject response) {

        StationsDBHandler dbHandler = new StationsDBHandler(context);

        PnrStatusNewBean bean = null;

        if (response != null) {

            try {

                bean = new PnrStatusNewBean();

                JSONObject status = response.getJSONObject("status");
                JSONObject trainInfo = response.getJSONObject("traininfo");

                bean.setPnrNumber(status.getString("pnr"));
                bean.setClassType(status.getString("class"));
                bean.setChartingStatus(status.getString("chart"));
                bean.setLastTimeCheck(new Date());
                bean.setFirstVisible(true);

                String boardFromCode = status.getString("from");
                String boardToCode = status.getString("to");

                bean.setBoardFromCode(boardFromCode);
                bean.setBoardFromName(dbHandler.getStationNameFromCode(boardFromCode));
                bean.setBoardToCode(boardToCode);
                bean.setBoardToName(dbHandler.getStationNameFromCode(boardToCode));

                PnrStatusNewBean.TrainInfo ti = new PnrStatusNewBean.TrainInfo();

                ti.trainName = trainInfo.getString("TrainName");
                ti.trainNumber = trainInfo.getString("TrainNo");

                // parsing and saving timetable with off visibility
                TimeTableNewBean timeTableNewBean = new TimeTableNewBean();

                timeTableNewBean.setTrainNumber(trainInfo.getString("TrainNo"));
                timeTableNewBean.setTrainName(trainInfo.getString("TrainName"));
                timeTableNewBean.setTotalTravelTime(trainInfo.getString("TravelTime"));

                timeTableNewBean.setRunningDays(UtilsMethods.getRunningDays(trainInfo.getString("RunningDays")));
                timeTableNewBean.setAvlClasses(UtilsMethods.getAvailableClasses(trainInfo.getString("AvlClass")));
                timeTableNewBean.setTotalHalts(trainInfo.getString("NumOfHalts"));
                timeTableNewBean.setDistance(trainInfo.getString("Distance"));
                timeTableNewBean.setTrainType(trainInfo.getString("TrainType"));

                timeTableNewBean.setPantries(UtilsMethods.getPantries(trainInfo.getString("Pantry")));
                timeTableNewBean.setGroupBeans(UtilsMethods.parseRoute(context, trainInfo.getJSONArray("Route"), true));

                TimeTableNewBean.AllStations source = null;
                TimeTableNewBean.AllStations destination = null;

                JSONArray route = trainInfo.getJSONArray("Route");
                if (route != null && route.length() > 0) {
                    List<TimeTableNewBean.AllStations> allStationsList = new ArrayList<>(route.length());
                    for (int i = 0; i < route.length(); i++) {
                        JSONObject routeJson = route.getJSONObject(i);
                        TimeTableNewBean.AllStations allStation = new TimeTableNewBean.AllStations();
                        if(routeJson.has("sNo"))
                            allStation.sNo = routeJson.getString("sNo") ;
                        if(routeJson.has("MainOrSubStn")) {
                            String mainOrSub = routeJson.getString("MainOrSubStn") ;
                            if(mainOrSub.toLowerCase().equals("main")) {
                                allStation.isMain = true ;
                            } else {
                                allStation.isMain = false ;
                            }
                        }
                        if (routeJson.has("StnCode")) {
                            allStation.stnCode = routeJson.getString("StnCode");
                            allStation.geoHash = dbHandler.getGeoHashOfStation(allStation.stnCode) ;
                        }
                        if (routeJson.has("StnName"))
                            allStation.stnName = routeJson.getString("StnName");
                        if (routeJson.has("ArrTime"))
                            allStation.arrTime = routeJson.getString("ArrTime");
                        if (routeJson.has("DeptTime"))
                            allStation.deptTime = routeJson.getString("DeptTime");
                        if (routeJson.has("HaltTime"))
                            allStation.halt = routeJson.getString("HaltTime");
                        if (routeJson.has("Platform"))
                            allStation.platForm = routeJson.getString("Platform");
                        if (routeJson.has("Distance"))
                            allStation.distance = routeJson.getString("Distance");
                        if (routeJson.has("Day"))
                            allStation.day = routeJson.getString("Day");
                        if (routeJson.has("adt"))
                            allStation.adt = routeJson.getString("adt");

                        if (allStation.stnCode.equals(boardFromCode)) {
                            source = allStation;
                        }

                        if (allStation.stnCode.equals(boardToCode)) {
                            destination = allStation;
                        }
                        allStationsList.add(allStation);
                    }

                    timeTableNewBean.setAllStationsList(allStationsList);
                }

                if (source != null && destination != null) {

                    ti.srcDepTime = source.deptTime;
                    ti.destArrTime = destination.arrTime;
                    int[] d = Utilities.getTimeDiffBtwStation(source.deptTime, source.day, destination.deptTime, destination.day);
                    String duration = d[0] + "h " + d[1] + "m";
                    ti.duration = duration;
                }

                //ti.srcStnCode       =   trainInfo.getString("SrcStncode") ;
                //ti.srcStnName       =   trainInfo.getString("SrcStnName") ;
                //ti.srcDepTime = trainInfo.getString("SrcDeptTime");

                //ti.destStnCode      =   trainInfo.getString("DestStncode") ;
                //ti.destStnName      =   trainInfo.getString("DestStnName") ;
                //ti.destArrTime = trainInfo.getString("DestArrTime");

                String travelDate = status.getString("travel_date");

                bean.setTravelDate(travelDate);

                Date sDate = DateAndMore.formatStringToDate(travelDate, DateAndMore.DATE_WITH_DASH_TWO);
                ti.srcDepDate = sDate;

                Date dDate = UtilsMethods.addDuration(sDate, UtilsMethods.extractHoursMinutes(trainInfo.getString("TravelTime")));
                ti.destArrDate = dDate;

                //ti.duration = trainInfo.getString("TravelTime");
                ti.totalHalts = trainInfo.getString("NumOfHalts");
                ti.distance = trainInfo.getString("Distance");
                ti.trainType = trainInfo.getString("TrainType");

                ti.runningDays = UtilsMethods.getRunningDays(trainInfo.getString("RunningDays"));
                ti.avlClasses = UtilsMethods.getAvailableClasses(trainInfo.getString("AvlClass"));

                ti.pantries = UtilsMethods.getPantries(trainInfo.getString("Pantry"));

                bean.setTrainInfo(ti);

                bean.setTrainDetailRoute(timeTableNewBean);


                UtilsMethods.saveTimeTable(context, timeTableNewBean, trainInfo.getJSONArray("Route"));

                List<Map.Entry<Integer, String>> possList = null;

                try {
                    GetConfirmChance gcc = new GetConfirmChance();
                    HashMap<Integer, String> possibilityMap = gcc.getPNRConfirmChance(bean.getPnrNumber());
                    if (possibilityMap != null) {
                        possList = new ArrayList<>(possibilityMap.entrySet());
                        Collections.sort(possList, new Comparator<Map.Entry<Integer, String>>() {
                            @Override
                            public int compare(Map.Entry<Integer, String> lhs, Map.Entry<Integer, String> rhs) {
                                return lhs.getKey().compareTo(rhs.getKey());
                            }
                        });
                    }
                } catch (Exception e) {
                    possList = null;
                }

                JSONArray pList = status.getJSONArray("passenger");
                List<PnrStatusNewBean.Passenger> passengers = new ArrayList<>(pList.length());

                for (int i = 0; i < pList.length(); i++) {

                    JSONObject json = pList.getJSONObject(i);

                    PnrStatusNewBean.Passenger p = new PnrStatusNewBean.Passenger();

                    p.sNo = "Passenger " + json.getString("sNo");
                    p.coachNumber = json.getString("coach_number");
                    p.seatNumber = json.getString("seat_number");
                    p.quotaType = json.getString("quota_type");
                    p.coachPos = json.getString("coach_pos");
                    p.status = json.getString("status");
                    p.possibility = "-";

                    if (possList != null) {
                        p.possibility = possList.get(i).getValue();
                    } else {
                        p.possibility = "-";
                    }

                    if (i == pList.length() - 1) {
                        bean.setMainStatus(p.status);
                    }

                    passengers.add(p);
                }

                bean.setPassengerList(passengers);


                JSONObject coachCompJson = response.getJSONObject("CoachComposition");
                JSONArray coachBeanArray = coachCompJson.getJSONArray("Coachs");

                if (coachBeanArray != null && coachBeanArray.length() > 0) {

                    CoachCompositionBean coachCompositionBean = new CoachCompositionBean();
                    List<CoachCompositionBean.CoachBean> coachBeanList = new ArrayList<>(coachBeanArray.length());

                    coachBeanList = UtilsMethods.extractCoachComposition(context, coachBeanArray);

                    if (coachCompJson.has("TrainName")) {
                        coachCompositionBean.setTrainName(coachCompJson.getString("TrainName"));
                    }

                    if (coachBeanList != null) {
                        coachCompositionBean.setCoachBean(coachBeanList);
                    } else {
                        return null;
                    }

                    bean.setCoachCompositionBean(coachCompositionBean);
                }

                return bean;

            } catch (Exception ex) {
                bean = null;
            }
        }

        return bean;

    }


    public static StationInformationBean getStationInformation(Context context, JSONObject response) {

        StationInformationBean bean = null;

        StationsDBHandler handler = new StationsDBHandler(context);

        if (response != null) {

            try {

                bean = new StationInformationBean();

                JSONObject stnDetailJson = response.getJSONObject("stn_details");

                if (stnDetailJson != null) {

                    bean.setStationCode(stnDetailJson.getString("stnCode"));
                    bean.setStationName(stnDetailJson.getString("stnName"));
                    bean.setStationNameHindi(stnDetailJson.getString("stnNameHN"));
                    bean.setTrack(stnDetailJson.getString("track"));
                    bean.setNoOfPlatforms(stnDetailJson.getString("numOfPfs"));
                    bean.setElevation(stnDetailJson.getString("elevation"));
                    bean.setZone(stnDetailJson.getString("zone"));
                    bean.setDivision(stnDetailJson.getString("division"));
                    bean.setAddress(stnDetailJson.getString("address"));

                    String[] loc = handler.getLatlngByCode(bean.getStationCode());
                    if (loc != null && loc.length == 2) {
                        bean.setStationLatitude(loc[0]);
                        bean.setStationLongitude(loc[1]);
                    }

                    JSONObject nearbyJson = stnDetailJson.getJSONObject("nearby");

                    if (nearbyJson != null) {

                        StationInformationBean.NearByLocation nbl = new StationInformationBean.NearByLocation();

                        nbl.location = nearbyJson.getString("location");
                        nbl.distance = nearbyJson.getString("dist");

                        bean.setNearByLocation(nbl);
                    }
                }

            } catch (Exception e) {
                bean = null;
            }
        }

        return bean;
    }


    public static SpecialTrainNewBean getSpecialTrains(JSONObject response) {

        SpecialTrainNewBean bean = null;

        if (response != null) {

            try {

                bean = new SpecialTrainNewBean();

                JSONArray spArray = response.getJSONArray("SpecialTrains");

                if (spArray != null && spArray.length() > 0) {

                    List<SpecialTrainNewBean.SpecialTrains> spList = new ArrayList<>(spArray.length());

                    for (int i = 0; i < spArray.length(); i++) {

                        SpecialTrainNewBean.SpecialTrains st = new SpecialTrainNewBean.SpecialTrains();

                        JSONObject json = spArray.getJSONObject(i);

                        st.trainNumber = json.getString("trainNo");
                        st.runsFromStn = json.getString("runsFromStn");
                        st.depTime = json.getString("depTime");
                        st.arrTime = json.getString("arrTime");
                        st.travelTime = json.getString("travelTime");
                        st.validFrom = json.getString("validFrom");
                        st.validTo = json.getString("validTo");
                        st.srcStnName = json.getString("srcStnName");
                        st.destStnName = json.getString("dstnStnName");
                        st.trainName = json.getString("trainName");
                        st.runningDays = UtilsMethods.getRunningDays(json.getString("runsFromStn"));

                        spList.add(st);
                    }

                    bean.setSpecialTrainsList(spList);
                }

            } catch (Exception e) {
                bean = null;
            }

        }

        return bean;
    }


    public static List<TrainDataBean> getAutoSuggestion(String response) {

        List<TrainDataBean> list = null;

        if (response != null) {

            try {
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.has("GetTrains")) {

                    JSONArray array = jsonObject.getJSONArray("GetTrains");

                    if (array != null && array.length() > 0) {

                        list = new ArrayList<>(array.length()) ;

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject json = array.getJSONObject(i);

                            if (json.has("TrainNo") && json.has("TrainName")) {

                                TrainDataBean bean = new TrainDataBean() ;
                                bean.setTrainNumber(json.getString("TrainNo"));
                                bean.setTrainName(json.getString("TrainName"));

                                list.add(bean) ;
                            }
                        }

                        return list;
                    }
                }
            } catch (Exception e) {
                list = null;
            }
        }
        return list;
    }


    public static RescheduledTrainBean getRescheduledTrains(JSONObject response) {

        RescheduledTrainBean bean = null;

        if (response != null) {

            try {

                bean = new RescheduledTrainBean();

                JSONArray yesterdayArr = response.getJSONArray("yesterday");
                JSONArray todayArr = response.getJSONArray("today");
                JSONArray tomorrowArr = response.getJSONArray("tommarrow");

                if (yesterdayArr != null && yesterdayArr.length() > 0) {

                    List<RescheduledTrainBean.RescheduledData> yesterList = new ArrayList<>(yesterdayArr.length());

                    for (int i = 0; i < yesterdayArr.length(); i++) {

                        RescheduledTrainBean.RescheduledData resData = new RescheduledTrainBean.RescheduledData();

                        JSONObject json = yesterdayArr.getJSONObject(i);

                        if (json.has("message") && json.has("code")) {
                            yesterList = null;
                            break;
                        }

                        resData.trainName = json.getString("trainName");
                        resData.trainNo = json.getString("trainNo");
                        resData.trainType = json.getString("trainType");
                        resData.srcStationName = json.getString("srcStnName");
                        resData.destStationName = json.getString("dstnStnName");
                        resData.startDateTime = json.getString("startDateTime");
                        resData.reschDateTime = json.getString("ReschDateTime");
                        resData.delay = json.getString("delay");

                        yesterList.add(resData);
                    }

                    bean.setYesterdayList(yesterList);
                }

                if (todayArr != null && todayArr.length() > 0) {

                    List<RescheduledTrainBean.RescheduledData> todayList = new ArrayList<>(todayArr.length());

                    for (int i = 0; i < todayArr.length(); i++) {

                        RescheduledTrainBean.RescheduledData resData = new RescheduledTrainBean.RescheduledData();

                        JSONObject json = todayArr.getJSONObject(i);

                        if (json.has("message") && json.has("code")) {
                            todayList = null;
                            break;
                        }

                        resData.trainName = json.getString("trainName");
                        resData.trainNo = json.getString("trainNo");
                        resData.trainType = json.getString("trainType");
                        resData.srcStationName = json.getString("srcStnName");
                        resData.destStationName = json.getString("dstnStnName");
                        resData.startDateTime = json.getString("startDateTime");
                        resData.reschDateTime = json.getString("ReschDateTime");
                        resData.delay = json.getString("delay");

                        todayList.add(resData);
                    }

                    bean.setTodayList(todayList);
                }

                if (tomorrowArr != null && tomorrowArr.length() > 0) {

                    List<RescheduledTrainBean.RescheduledData> tomorrowList = new ArrayList<>(tomorrowArr.length());

                    for (int i = 0; i < tomorrowArr.length(); i++) {

                        RescheduledTrainBean.RescheduledData resData = new RescheduledTrainBean.RescheduledData();

                        JSONObject json = tomorrowArr.getJSONObject(i);

                        if (json.has("message") && json.has("code")) {
                            tomorrowList = null;
                            break;
                        }

                        resData.trainName = json.getString("trainName");
                        resData.trainNo = json.getString("trainNo");
                        resData.trainType = json.getString("trainType");
                        resData.srcStationName = json.getString("srcStnName");
                        resData.destStationName = json.getString("dstnStnName");
                        resData.startDateTime = json.getString("startDateTime");
                        resData.reschDateTime = json.getString("ReschDateTime");
                        resData.delay = json.getString("delay");

                        tomorrowList.add(resData);
                    }

                    bean.setTomorrowList(tomorrowList);
                    ;
                }

            } catch (Exception e) {
                bean = null;
            }
        }

        return bean;
    }


    public static DivertedTrainBean getDivertedTrains(JSONObject response) {

        DivertedTrainBean bean = null;

        if (response != null) {

            try {

                bean = new DivertedTrainBean();

                JSONArray yesterdayArr = response.getJSONArray("yesterday");
                JSONArray todayArr = response.getJSONArray("today");
                JSONArray tomorrowArr = response.getJSONArray("tommarrow");

                if (yesterdayArr != null && yesterdayArr.length() > 0) {

                    List<DivertedTrainBean.DivertedData> yesterList = new ArrayList<>(yesterdayArr.length());

                    for (int i = 0; i < yesterdayArr.length(); i++) {

                        DivertedTrainBean.DivertedData divData = new DivertedTrainBean.DivertedData();

                        JSONObject json = yesterdayArr.getJSONObject(i);

                        if (json.has("message") && json.has("code")) {
                            yesterList = null;
                            break;
                        }

                        divData.trainName = json.getString("trainName");
                        divData.trainNo = json.getString("trainNo");
                        divData.trainType = json.getString("trainType");
                        divData.srcStationName = json.getString("srcStnName");
                        divData.destStationName = json.getString("dstnStnName");
                        divData.startTime = json.getString("startTime");
                        divData.divertedFrom = json.getString("dvrtedFrom");
                        divData.divertedTo = json.getString("dvrtedTo");

                        yesterList.add(divData);
                    }

                    bean.setYesterdayList(yesterList);
                }

                if (todayArr != null && todayArr.length() > 0) {

                    List<DivertedTrainBean.DivertedData> todayList = new ArrayList<>(todayArr.length());

                    for (int i = 0; i < todayArr.length(); i++) {

                        DivertedTrainBean.DivertedData divData = new DivertedTrainBean.DivertedData();

                        JSONObject json = todayArr.getJSONObject(i);

                        if (json.has("message") && json.has("code")) {
                            todayList = null;
                            break;
                        }

                        divData.trainName = json.getString("trainName");
                        divData.trainNo = json.getString("trainNo");
                        divData.trainType = json.getString("trainType");
                        divData.srcStationName = json.getString("srcStnName");
                        divData.destStationName = json.getString("dstnStnName");
                        divData.startTime = json.getString("startTime");
                        divData.divertedFrom = json.getString("dvrtedFrom");
                        divData.divertedTo = json.getString("dvrtedTo");

                        todayList.add(divData);
                    }

                    bean.setTodayList(todayList);
                }

                if (tomorrowArr != null && tomorrowArr.length() > 0) {

                    List<DivertedTrainBean.DivertedData> tomorrowList = new ArrayList<>(tomorrowArr.length());

                    for (int i = 0; i < tomorrowArr.length(); i++) {

                        DivertedTrainBean.DivertedData divData = new DivertedTrainBean.DivertedData();

                        JSONObject json = tomorrowArr.getJSONObject(i);

                        if (json.has("message") && json.has("code")) {
                            tomorrowList = null;
                            break;
                        }

                        divData.trainName = json.getString("trainName");
                        divData.trainNo = json.getString("trainNo");
                        divData.trainType = json.getString("trainType");
                        divData.srcStationName = json.getString("srcStnName");
                        divData.destStationName = json.getString("dstnStnName");
                        divData.startTime = json.getString("startTime");
                        divData.divertedFrom = json.getString("dvrtedFrom");
                        divData.divertedTo = json.getString("dvrtedTo");

                        tomorrowList.add(divData);
                    }

                    bean.setTomorrowList(tomorrowList);
                }

            } catch (Exception e) {
                bean = null;
            }

        }

        return bean;
    }


    public static CancelledTrainBean getCancelledTrains(JSONObject response) {

        CancelledTrainBean bean = null;

        if (response != null) {

            try {

                bean = new CancelledTrainBean();

                JSONObject yesterdayJson = response.getJSONObject("yesterday");
                JSONObject todayJson = response.getJSONObject("today");
                JSONObject tomorrowJson = response.getJSONObject("tommarrow");

                if (yesterdayJson != null) {

                    CancelledTrainBean.CancelledData cancelledData = new CancelledTrainBean.CancelledData();

                    JSONArray fully = yesterdayJson.getJSONArray("fully");
                    JSONArray partially = yesterdayJson.getJSONArray("partially");

                    if ((fully != null && fully.length() > 0) && (partially != null && partially.length() > 0)) {

                        List<CancelledTrainBean.FullyCancelled> fullyCancelledList = new ArrayList<>(fully.length());

                        for (int i = 0; i < fully.length(); i++) {

                            JSONObject json = fully.getJSONObject(i);

                            if (json.has("message") && json.has("code")) {
                                fullyCancelledList = null;
                                break;
                            }

                            CancelledTrainBean.FullyCancelled fc = new CancelledTrainBean.FullyCancelled();

                            fc.trainNo = json.getString("trainNo");
                            fc.trainName = json.getString("trainName");
                            fc.trainType = json.getString("trainType");
                            fc.srcStationName = json.getString("srcStnName");
                            fc.destStationName = json.getString("dstnStnName");
                            fc.startTime = json.getString("startTime");

                            fullyCancelledList.add(fc);
                        }

                        cancelledData.fullyCancelledList = fullyCancelledList;

                        List<CancelledTrainBean.PartiallyCancelled> partiallyCancelledList = new ArrayList<>(partially.length());

                        for (int i = 0; i < partially.length(); i++) {

                            JSONObject json = partially.getJSONObject(i);

                            if (json.has("message") && json.has("code")) {
                                partiallyCancelledList = null;
                                break;
                            }

                            CancelledTrainBean.PartiallyCancelled pc = new CancelledTrainBean.PartiallyCancelled();

                            pc.trainNo = json.getString("trainNo");
                            pc.trainName = json.getString("trainName");
                            pc.trainType = json.getString("trainType");
                            pc.srcStationName = json.getString("srcStnName");
                            pc.destStationName = json.getString("dstnStnName");
                            pc.startTime = json.getString("startTime");
                            pc.cancelledFrom = json.getString("cncledFrom");
                            pc.cancelledTo = json.getString("cncledTo");

                            partiallyCancelledList.add(pc);
                        }

                        cancelledData.partiallyCancelledList = partiallyCancelledList;
                    }

                    bean.setYesterday(cancelledData);
                }

                if (todayJson != null) {

                    CancelledTrainBean.CancelledData cancelledData = new CancelledTrainBean.CancelledData();

                    JSONArray fully = todayJson.getJSONArray("fully");
                    JSONArray partially = todayJson.getJSONArray("partially");

                    if ((fully != null && fully.length() > 0) && (partially != null && partially.length() > 0)) {

                        List<CancelledTrainBean.FullyCancelled> fullyCancelledList = new ArrayList<>(fully.length());

                        for (int i = 0; i < fully.length(); i++) {

                            JSONObject json = fully.getJSONObject(i);

                            if (json.has("message") && json.has("code")) {
                                fullyCancelledList = null;
                                break;
                            }

                            CancelledTrainBean.FullyCancelled fc = new CancelledTrainBean.FullyCancelled();

                            fc.trainNo = json.getString("trainNo");
                            fc.trainName = json.getString("trainName");
                            fc.trainType = json.getString("trainType");
                            fc.srcStationName = json.getString("srcStnName");
                            fc.destStationName = json.getString("dstnStnName");
                            fc.startTime = json.getString("startTime");

                            fullyCancelledList.add(fc);
                        }

                        cancelledData.fullyCancelledList = fullyCancelledList;

                        List<CancelledTrainBean.PartiallyCancelled> partiallyCancelledList = new ArrayList<>(partially.length());

                        for (int i = 0; i < partially.length(); i++) {

                            JSONObject json = partially.getJSONObject(i);

                            if (json.has("message") && json.has("code")) {
                                partiallyCancelledList = null;
                                break;
                            }

                            CancelledTrainBean.PartiallyCancelled pc = new CancelledTrainBean.PartiallyCancelled();

                            pc.trainNo = json.getString("trainNo");
                            pc.trainName = json.getString("trainName");
                            pc.trainType = json.getString("trainType");
                            pc.srcStationName = json.getString("srcStnName");
                            pc.destStationName = json.getString("dstnStnName");
                            pc.startTime = json.getString("startTime");
                            pc.cancelledFrom = json.getString("cncledFrom");
                            pc.cancelledTo = json.getString("cncledTo");

                            partiallyCancelledList.add(pc);
                        }

                        cancelledData.partiallyCancelledList = partiallyCancelledList;
                    }

                    bean.setToday(cancelledData);
                    ;
                }

                if (tomorrowJson != null) {

                    CancelledTrainBean.CancelledData cancelledData = new CancelledTrainBean.CancelledData();

                    JSONArray fully = tomorrowJson.getJSONArray("fully");
                    JSONArray partially = tomorrowJson.getJSONArray("partially");

                    if ((fully != null && fully.length() > 0) && (partially != null && partially.length() > 0)) {

                        List<CancelledTrainBean.FullyCancelled> fullyCancelledList = new ArrayList<>(fully.length());

                        for (int i = 0; i < fully.length(); i++) {

                            JSONObject json = fully.getJSONObject(i);

                            if (json.has("message") && json.has("code")) {
                                fullyCancelledList = null;
                                break;
                            }

                            CancelledTrainBean.FullyCancelled fc = new CancelledTrainBean.FullyCancelled();

                            fc.trainNo = json.getString("trainNo");
                            fc.trainName = json.getString("trainName");
                            fc.trainType = json.getString("trainType");
                            fc.srcStationName = json.getString("srcStnName");
                            fc.destStationName = json.getString("dstnStnName");
                            fc.startTime = json.getString("startTime");

                            fullyCancelledList.add(fc);
                        }

                        cancelledData.fullyCancelledList = fullyCancelledList;

                        List<CancelledTrainBean.PartiallyCancelled> partiallyCancelledList = new ArrayList<>(partially.length());

                        for (int i = 0; i < partially.length(); i++) {

                            JSONObject json = partially.getJSONObject(i);

                            if (json.has("message") && json.has("code")) {
                                partiallyCancelledList = null;
                                break;
                            }

                            CancelledTrainBean.PartiallyCancelled pc = new CancelledTrainBean.PartiallyCancelled();

                            pc.trainNo = json.getString("trainNo");
                            pc.trainName = json.getString("trainName");
                            pc.trainType = json.getString("trainType");
                            pc.srcStationName = json.getString("srcStnName");
                            pc.destStationName = json.getString("dstnStnName");
                            pc.startTime = json.getString("startTime");
                            pc.cancelledFrom = json.getString("cncledFrom");
                            pc.cancelledTo = json.getString("cncledTo");

                            partiallyCancelledList.add(pc);
                        }

                        cancelledData.partiallyCancelledList = partiallyCancelledList;
                    }

                    bean.setTomorrow(cancelledData);
                    ;
                }

            } catch (Exception e) {
                bean = null;
            }

        }

        return bean;
    }


    public static MiniTimeTableBean getMiniTimetable(JSONObject response) {

        MiniTimeTableBean bean = null;

        if (response != null) {

            try {

                bean = new MiniTimeTableBean();

                JSONObject main = response.getJSONObject("trainschedule");

                JSONArray routes = main.getJSONArray("Route");

                if (routes != null && routes.length() > 0) {

                    List<MiniTimeTableBean.TrainRoute> routeList = new ArrayList<>(routes.length());

                    for (int i = 0; i < routes.length(); i++) {

                        JSONObject json = routes.getJSONObject(i);

                        MiniTimeTableBean.TrainRoute tr = new MiniTimeTableBean.TrainRoute();

                        tr.stationCode = json.getString("StnCode");
                        tr.stationName = json.getString("StnName");
                        tr.arrTime = json.getString("ArrTime");
                        tr.depTime = json.getString("DeptTime");
                        tr.distance = json.getString("Distance");
                        tr.day = json.getString("Day");

                        routeList.add(tr);
                    }

                    bean.setTrainNumber(main.getString("TrainNo"));
                    bean.setTrainName(main.getString("TrainName"));
                    bean.setSrcStnCode(main.getString("SrcStncode"));
                    bean.setSrcStnName(main.getString("SrcStnName"));
                    bean.setSrcDepTime(main.getString("SrcDeptTime"));
                    bean.setDestStnCode(main.getString("DestStncode"));
                    bean.setDestStnName(main.getString("DestStnName"));
                    bean.setDestArrTime(main.getString("DestArrTime"));
                    bean.setTotalTravelTime(main.getString("TravelTime"));
                    bean.setTotalDistance(main.getString("Distance"));
                    bean.setTrainType(main.getString("TrainType"));
                    bean.setRunningDays(UtilsMethods.getRunningDays(main.getString("RunningDays")));
                    bean.setAvlClasses(UtilsMethods.getAvailableClasses(main.getString("AvlClass")));
                    bean.setTrainRouteList(routeList);
                }

                return bean;

            } catch (Exception e) {
                bean = null;
            }
        }

        return bean;
    }


    public static CoachCompositionBean getCoachComposition(JSONObject response, Context context) {

        CoachCompositionBean bean = null;
        List<CoachCompositionBean.CoachBean> beanList = null;

        if (response != null) {

            try {

                JSONArray array = response.getJSONArray("Coachs");

                bean = new CoachCompositionBean();

                if (array != null && array.length() > 0) {

                    beanList = UtilsMethods.extractCoachComposition(context, array);

                    if (response.has("TrainName")) {
                        bean.setTrainName(response.getString("TrainName"));
                    }

                    if (beanList != null) {
                        bean.setCoachBean(beanList);
                    } else {
                        return null;
                    }
                }

                return bean;

            } catch (Exception ex) {
                bean = null;
            }
        }
        return bean;
    }


    public static TimeTableNewBean getTimeTableData(Context context, JSONObject response) {

        TimeTableNewBean bean = null;

        if (response != null) {

            try {

                bean = new TimeTableNewBean();

                JSONObject json = response.getJSONObject("trainschedule");

                bean.setTrainNumber(json.getString("TrainNo"));
                bean.setTrainName(json.getString("TrainName"));
                bean.setTotalTravelTime(json.getString("TravelTime"));

                bean.setRunningDays(UtilsMethods.getRunningDays(json.getString("RunningDays")));
                bean.setAvlClasses(UtilsMethods.getAvailableClasses(json.getString("AvlClass")));
                bean.setTotalHalts(json.getString("NumOfHalts"));
                bean.setDistance(json.getString("Distance"));
                bean.setTrainType(json.getString("TrainType"));

                bean.setPantries(UtilsMethods.getPantries(json.getString("Pantry")));
                bean.setGroupBeans(UtilsMethods.parseRoute(context, json.getJSONArray("Route"), false));

                JSONArray route = json.getJSONArray("Route");
                if (route != null && route.length() > 0) {
                    List<TimeTableNewBean.AllStations> allStationsList = new ArrayList<>(route.length());
                    for (int i = 0; i < route.length(); i++) {
                        JSONObject routeJson = route.getJSONObject(i);
                        TimeTableNewBean.AllStations allStation = new TimeTableNewBean.AllStations();
                        if (routeJson.has("StnCode"))
                            allStation.stnCode = routeJson.getString("StnCode");
                        if (routeJson.has("StnName"))
                            allStation.stnName = routeJson.getString("StnName");
                        if (routeJson.has("ArrTime"))
                            allStation.arrTime = routeJson.getString("ArrTime");
                        if (routeJson.has("DeptTime"))
                            allStation.deptTime = routeJson.getString("DeptTime");
                        if (routeJson.has("HaltTime"))
                            allStation.halt = routeJson.getString("HaltTime");
                        if (routeJson.has("Platform"))
                            allStation.platForm = routeJson.getString("Platform");
                        if (routeJson.has("Distance"))
                            allStation.distance = routeJson.getString("Distance");
                        if (routeJson.has("Day"))
                            allStation.day = routeJson.getString("Day");
                        if (routeJson.has("adt"))
                            allStation.adt = routeJson.getString("adt");

                        allStationsList.add(allStation);
                    }

                    bean.setAllStationsList(allStationsList);
                }

            } catch (Exception e) {
                bean = null;
            }
        }
        return bean;
    }


    public static SeatFareBeanNew getSeatAvlFareBreakup(JSONObject response) {

        SeatFareBeanNew bean = null;

        if (response != null) {

            try {

                JSONObject seatAvlJson = response.getJSONObject("seat_avl");
                JSONObject fareBreakupJson = response.getJSONObject("fare_breakup");

                bean = new SeatFareBeanNew();

                // seat availability
                SeatFareBeanNew.SeatAvl seatAvl = new SeatFareBeanNew.SeatAvl();

                JSONArray statusChart = seatAvlJson.getJSONArray("StatusChart");

                if (statusChart != null && statusChart.length() > 0) {

                    List<SeatFareBeanNew.SeatAvl.AvlStatus> avlList = new ArrayList<>(statusChart.length());

                    for (int i = 0; i < statusChart.length(); i++) {

                        JSONObject json = statusChart.getJSONObject(i);

                        SeatFareBeanNew.SeatAvl.AvlStatus avlStatus = new SeatFareBeanNew.SeatAvl.AvlStatus();

                        avlStatus.sNo = json.getString("sNo");
                        avlStatus.date = json.getString("date");

                        JSONArray status = json.getJSONArray("status");
                        String[] arr = new String[status.length()];
                        for (int j = 0; j < status.length(); j++) {
                            arr[j] = status.getString(j);
                        }

                        avlStatus.status = arr;

                        avlList.add(avlStatus);
                    }

                    seatAvl.trainNumber = seatAvlJson.getString("train_no");
                    seatAvl.avlStatusList = avlList;

                    JSONObject nextJson = seatAvlJson.getJSONObject("next");
                    SeatFareBeanNew.SeatAvl.NextPrevious np = new SeatFareBeanNew.SeatAvl.NextPrevious();
                    np.nextDay = nextJson.getString("day");
                    np.nextMonth = nextJson.getString("month");

                    seatAvl.next = np;

                    nextJson = seatAvlJson.getJSONObject("prev");
                    np = new SeatFareBeanNew.SeatAvl.NextPrevious();
                    np.nextDay = nextJson.getString("day");
                    np.nextMonth = nextJson.getString("month");

                    seatAvl.previous = np;
                }

                // fare breakup
                SeatFareBeanNew.FareBreakup fareBreakup = new SeatFareBeanNew.FareBreakup();

                JSONArray fareDetails = fareBreakupJson.getJSONArray("Fare_Details");

                if (fareDetails != null && fareDetails.length() > 0) {

                    List<SeatFareBeanNew.FareBreakup.FareDetails> fareDetailList = new ArrayList<>(fareDetails.length());

                    for (int i = 0; i < fareDetails.length(); i++) {

                        SeatFareBeanNew.FareBreakup.FareDetails fd = new SeatFareBeanNew.FareBreakup.FareDetails();

                        JSONObject json = fareDetails.getJSONObject(i);

                        fd.key = json.getString("key");
                        fd.value = json.getString("value");

                        fareDetailList.add(fd);
                    }

                    fareBreakup.trainNumber = fareBreakupJson.getString("train_number");
                    fareBreakup.trainType = fareBreakupJson.getString("traintype");
                    fareBreakup.netAmount = fareBreakupJson.getString("Net_Amount");
                    fareBreakup.totalAmount = fareBreakupJson.getString("Total_Amount");
                    fareBreakup.fareList = fareDetailList;
                }

                bean.setSeatAvl(seatAvl);
                bean.setFareBreakup(fareBreakup);

            } catch (Exception e) {
                bean = null;
            }
        }

        return bean;
    }


    public static List<NearByAmenitiesBean> getNearByAmenities(String response, LatLng source) {

        List<NearByAmenitiesBean> list = null;

        if (response != null) {

            try {

                JSONObject jsonObject = new JSONObject(response);
                JSONArray array = jsonObject.getJSONArray("results");
                list = new ArrayList<>(array.length());

                if (array != null && array.length() > 0) {

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject json = array.getJSONObject(i);

                        NearByAmenitiesBean bean = new NearByAmenitiesBean();

                        if (json.has("geometry")) {
                            JSONObject geometry = json.getJSONObject("geometry");
                            if (geometry.has("location")) {
                                JSONObject location = geometry.getJSONObject("location");
                                if (location.has("lat")) {
                                    bean.setLatitude(location.getString("lat"));
                                }
                                if (location.has("lng")) {
                                    bean.setLongitude(location.getString("lng"));
                                }
                            }

                            float[] arrayOfFloat = new float[3];
                            Location.distanceBetween(source.getLatitude(), source.getLongitude(), Double.parseDouble(bean.getLatitude()), Double.parseDouble(bean.getLongitude()), arrayOfFloat);
                            float distance = Float.parseFloat(String.format("%.2f", (arrayOfFloat[0] / 1000.0F)));

                            bean.setDistance(distance);

                        }

                        if (json.has("name")) {
                            bean.setName(json.getString("name"));
                        }

                        if (json.has("formatted_address")) {
                            bean.setAddress(json.getString("formatted_address"));
                        }

                        list.add(bean);
                    }
                }

                return list;

            } catch (Exception ex) {
                list = null;
            }
        }

        return list;
    }
}
