package com.railgadi.beans;

public class StationInformationBean {

    private String stationCode ;
    private String stationName ;
    private String stationNameHindi ;
    private String track ;
    private String noOfPlatforms ;
    private String elevation ;
    private String zone ;
    private String division ;
    private String address ;
    private String stationLatitude ;
    private String stationLongitude ;
    private NearByLocation nearByLocation ;

    public static class NearByLocation {
        public String location  ;
        public String distance ;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationNameHindi() {
        return stationNameHindi;
    }

    public void setStationNameHindi(String stationNameHindi) {
        this.stationNameHindi = stationNameHindi;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getNoOfPlatforms() {
        return noOfPlatforms;
    }

    public void setNoOfPlatforms(String noOfPlatforms) {
        this.noOfPlatforms = noOfPlatforms;
    }

    public String getElevation() {
        return elevation;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStationLatitude() {
        return stationLatitude;
    }

    public void setStationLatitude(String stationLatitude) {
        this.stationLatitude = stationLatitude;
    }

    public String getStationLongitude() {
        return stationLongitude;
    }

    public void setStationLongitude(String stationLongitude) {
        this.stationLongitude = stationLongitude;
    }

    public NearByLocation getNearByLocation() {
        return nearByLocation;
    }

    public void setNearByLocation(NearByLocation nearByLocation) {
        this.nearByLocation = nearByLocation;
    }
}
