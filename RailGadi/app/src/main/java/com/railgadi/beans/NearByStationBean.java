package com.railgadi.beans;

public class NearByStationBean implements Comparable<NearByStationBean> {

    private String stationName ;
    private String stationCode ;
    private double latitude ;
    private double longitude ;
    private double distance ;

    @Override
    public int compareTo(NearByStationBean another) {

        Double thisDistance = this.getDistance() ;
        Double anotherDistance = another.getDistance() ;

        return thisDistance.compareTo(anotherDistance) ;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode ;
    }

    public String getStationCode() {
        return this.stationCode ;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
