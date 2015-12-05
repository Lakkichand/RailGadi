package com.railgadi.beans;

import java.util.List;

public class TimeTableNewBean {

    private String trainNumber;
    private String trainName;
    private String totalTravelTime;
    private String travelTimeBtwnStn ;
    private boolean[] runningDays; // starts with monday to sunday
    private List<String> avlClasses; // 1A:2A:3A:3E:CC:FC:SL:2S
    private String totalHalts;
    private String distance;
    private String trainType;
    private boolean[] pantries; // pantry car, catering, e-catering
    private List<RouteGroupBean> groupBeans;
    private boolean isVisible ;
    private List<AllStations> allStationsList ;


    public static class AllStations implements Comparable<AllStations> {

        public String sNo ;
        public String stnCode ;
        public String stnName ;
        public String arrTime ;
        public String deptTime ;
        public String halt ;
        public String platForm ;
        public String distance ;
        public String geoHash ;
        public String day ;
        public String adt ;
        public boolean isMain ;
        public Double distanceFromCurrent ;

        @Override
        public int compareTo(AllStations another) {
            Integer one = Integer.valueOf(this.distance);
            Integer two = Integer.valueOf(another.distance);
            return one.compareTo(two) ;
        }
    }


    public AllStations getSingleStationDetail(String stationCode) {

        for(AllStations as : allStationsList) {

            if(as.stnCode.equals(stationCode)) {
                return as ;
            }
        }

        return null ;
    }


    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getTravelTimeBtwnStn() {
        return travelTimeBtwnStn;
    }

    public void setTravelTimeBtwnStn(String travelTimeBtwnStn) {
        this.travelTimeBtwnStn = travelTimeBtwnStn;
    }

    public String getTotalTravelTime() {
        return totalTravelTime;
    }

    public void setTotalTravelTime(String travelTime) {
        this.totalTravelTime = travelTime;
    }

    public boolean[] getRunningDays() {
        return runningDays;
    }

    public void setRunningDays(boolean[] runningDays) {
        this.runningDays = runningDays;
    }

    public List<String> getAvlClasses() {
        return avlClasses;
    }

    public void setAvlClasses(List<String> avlClasses) {
        this.avlClasses = avlClasses;
    }

    public String getTotalHalts() {
        return totalHalts;
    }

    public void setTotalHalts(String totalHalts) {
        this.totalHalts = totalHalts;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public boolean[] getPantries() {
        return pantries;
    }

    public void setPantries(boolean[] pantries) {
        this.pantries = pantries;
    }

    public List<RouteGroupBean> getGroupBeans() {
        return groupBeans;
    }

    public void setGroupBeans(List<RouteGroupBean> groupBeans) {
        this.groupBeans = groupBeans;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public List<AllStations> getAllStationsList() {
        return allStationsList;
    }

    public void setAllStationsList(List<AllStations> allStationsList) {
        this.allStationsList = allStationsList;
    }
}
