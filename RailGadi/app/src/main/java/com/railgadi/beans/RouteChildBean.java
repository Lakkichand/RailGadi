package com.railgadi.beans;

public class RouteChildBean {

    private String serialNumber ;
    private boolean isMain ;
    private String arrival ;
    private String departure ;
    private String stationName ;
    private String stationCode ;
    private String adt ;
    private String day ;
    private String haltTime ;
    private String distance ;
    private String platform ;
    private String latitude ;
    private String longitude ;
    private int adtColor ;
    private boolean isArrivalSelected ;
    private boolean isDepartureSelected ;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getAdt() {
        return adt;
    }

    public void setAdt(String adt) {
        this.adt = adt;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHaltTime() {
        return haltTime;
    }

    public void setHaltTime(String haltTime) {
        this.haltTime = haltTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getAdtColor() {
        return this.adtColor ;
    }

    public void setAdtColor(int adtColor) {
        this.adtColor = adtColor ;
    }

    public boolean isArrivalSelected() {
        return isArrivalSelected;
    }

    public void setArrivalSelected(boolean isArrivalSelected) {
        this.isArrivalSelected = isArrivalSelected;
    }

    public boolean isDepartureSelected() {
        return isDepartureSelected;
    }

    public void setDepartureSelected(boolean isDepartureSelected) {
        this.isDepartureSelected = isDepartureSelected;
    }
}
