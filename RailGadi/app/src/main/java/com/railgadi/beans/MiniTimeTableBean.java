package com.railgadi.beans;

import java.util.List;

public class MiniTimeTableBean {

    private String trainNumber ;
    private String trainName ;
    private String srcStnCode ;
    private String srcStnName ;
    private String srcDepTime ;
    private String destStnCode ;
    private String destStnName ;
    private String destArrTime ;
    private String totalTravelTime ;
    private boolean [] runningDays ;
    private List<String> avlClasses ;
    private String totalDistance ;
    private String trainType ;
    private List<TrainRoute> trainRouteList ;


    public TrainRoute getRouteStation(String stnCode) {

        for(TrainRoute tr : trainRouteList) {
            if(tr.stationCode.equals(stnCode)) {
                return tr ;
            }
        }
        return null ;
    }


    public static class TrainRoute implements Comparable<TrainRoute> {

        public String stationCode ;
        public String stationName ;
        public String arrTime ;
        public String depTime ;
        public String distance ;
        public String day ;

        @Override
        public int compareTo(TrainRoute another) {
            Integer one = Integer.parseInt(this.distance) ;
            Integer two = Integer.parseInt(another.distance) ;
            return one.compareTo(two) ;
        }
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

    public String getSrcStnCode() {
        return srcStnCode;
    }

    public void setSrcStnCode(String srcStnCode) {
        this.srcStnCode = srcStnCode;
    }

    public String getSrcStnName() {
        return srcStnName;
    }

    public void setSrcStnName(String srcStnName) {
        this.srcStnName = srcStnName;
    }

    public String getSrcDepTime() {
        return srcDepTime;
    }

    public void setSrcDepTime(String srcDepTime) {
        this.srcDepTime = srcDepTime;
    }

    public String getDestStnCode() {
        return destStnCode;
    }

    public void setDestStnCode(String destStnCode) {
        this.destStnCode = destStnCode;
    }

    public String getDestStnName() {
        return destStnName;
    }

    public void setDestStnName(String destStnName) {
        this.destStnName = destStnName;
    }

    public String getDestArrTime() {
        return destArrTime;
    }

    public void setDestArrTime(String destArrTime) {
        this.destArrTime = destArrTime;
    }

    public String getTotalTravelTime() {
        return totalTravelTime;
    }

    public void setTotalTravelTime(String totalTravelTime) {
        this.totalTravelTime = totalTravelTime;
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

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public List<TrainRoute> getTrainRouteList() {
        return trainRouteList;
    }

    public void setTrainRouteList(List<TrainRoute> trainRouteList) {
        this.trainRouteList = trainRouteList;
    }
}
