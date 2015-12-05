package com.railgadi.beans;

public class StationNameCodeBean {

    private String stationCode, stationName ;
    private int stationIcon ;

    public StationNameCodeBean(String stationCode, String stationName, int stationIcon) {
        this.stationCode = stationCode ;
        this.stationName = stationName ;
        this.stationIcon = stationIcon ;
    }

    public String getStationCode() {
        return this.stationCode ;
    }
    public String getStationName() {
        return this.stationName ;
    }
    public int getStationIcon() {
        return this.stationIcon ;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public void setStationIcon(int stationIcon) {
        this.stationIcon = stationIcon;
    }

    /*@Override
    public int hashCode() {
        return this.getStationCode().hashCode()+this.getStationName().hashCode() ;
    }

    @Override
    public boolean equals(Object o) {

        StationNameCodeBean obj = null ;
        if( o != null && o instanceof StationNameCodeBean ) {
            obj = (StationNameCodeBean) o ;

            return obj.hashCode() == this.hashCode() ;
        } else {
            return false ;
        }
    }*/
}
