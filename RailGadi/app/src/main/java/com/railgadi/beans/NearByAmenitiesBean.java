package com.railgadi.beans;

public class NearByAmenitiesBean implements Comparable<NearByAmenitiesBean> {

    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private int rating;
    private float distance ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public void setDistance(float distance) {
        this.distance = distance ;
    }

    public float getDistance() {
        return this.distance ;
    }

    @Override
    public int compareTo(NearByAmenitiesBean another) {

        Float one = new Float(this.getDistance()) ;
        Float two = new Float(another.getDistance()) ;

        return one.compareTo(two) ;
    }
}
