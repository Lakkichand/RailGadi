package com.railgadi.beans;

import java.util.ArrayList;

public class ShowTrainScheduleBean {

    private String trainNumber ;
    private String trainName ;
    private String source ;
    private String totalStop ;

    private ArrayList<RouteStationNameTime> routeStationData;



    // setters
    public void setTrainName(String tName) {
        this.trainName = tName;
    }

    public void setTrainNumber(String tNumber) {
        this.trainNumber = tNumber;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTotalStop(String totalStop) {
        this.totalStop = totalStop;
    }

    public void setRouteStationData(ArrayList<RouteStationNameTime> object) {
        this.routeStationData = object;
    }

    //getters
    public String getTrainName() {
        return this.trainName;
    }

    public String getTrainNumber() {
        return this.trainNumber;
    }

    public String getSource() {
        return this.source;
    }

    public String getTotalStop() {
        return this.totalStop;
    }

    public ArrayList<RouteStationNameTime> getRouteStaionData() {
        return this.routeStationData;
    }


    // class for route station listview
    public static class RouteStationNameTime {
        private String stationName, arrTime;

        //setters
        public void setStationName(String stationName) {
            this.stationName = stationName;
        }

        public String getStationName() {
            return this.stationName;
        }

        //getters
        public void setArrivalTime(String arrTime) {
            this.arrTime = arrTime;
        }

        public String getArrivalTime() {
            return this.arrTime;
        }
    }
}
