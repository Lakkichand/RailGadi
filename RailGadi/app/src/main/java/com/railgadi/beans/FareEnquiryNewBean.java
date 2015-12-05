package com.railgadi.beans;

import java.util.List;

public class FareEnquiryNewBean {

    private String trainNumber ;
    private String trainType ;
    private String netAmount ;
    private String totalAmount ;
    private List<FareDetails> fareList ;

    public static class FareDetails {
        public String key ;
        public String value ;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<FareDetails> getFareList() {
        return fareList;
    }

    public void setFareList(List<FareDetails> fareList) {
        this.fareList = fareList;
    }
}
