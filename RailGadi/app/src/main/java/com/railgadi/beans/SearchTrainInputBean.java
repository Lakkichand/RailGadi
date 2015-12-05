package com.railgadi.beans;

public class SearchTrainInputBean {

    private String fromStationCode ;
    private String toStationCode ;
    private String viaStationCode ;
    private String fromStationName ;
    private String toStationName ;
    private String viaStationName ;
    private String startRange ;
    private String endRange ;
    private String day ;
    private String month ;
    private String year ;
    private String quota ;
    private boolean includeWeekly ;

    public String getFromStationCode() {
        return fromStationCode;
    }

    public void setFromStationCode(String fromStationCode) {
        this.fromStationCode = fromStationCode;
    }

    public String getToStationCode() {
        return toStationCode;
    }

    public void setToStationCode(String toStationCode) {
        this.toStationCode = toStationCode;
    }

    public String getViaStationCode() {
        return viaStationCode;
    }

    public void setViaStationCode(String viaStationCode) {
        this.viaStationCode = viaStationCode;
    }

    public String getFromStationName() {
        return fromStationName;
    }

    public void setFromStationName(String fromStationName) {
        this.fromStationName = fromStationName;
    }

    public String getToStationName() {
        return toStationName;
    }

    public void setToStationName(String toStationName) {
        this.toStationName = toStationName;
    }

    public String getViaStationName() {
        return viaStationName;
    }

    public void setViaStationName(String viaStationName) {
        this.viaStationName = viaStationName;
    }

    public String getStartRange() {
        return startRange;
    }

    public void setStartRange(String startRange) {
        this.startRange = startRange;
    }

    public String getEndRange() {
        return endRange;
    }

    public void setEndRange(String endRange) {
        this.endRange = endRange;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public boolean isIncludeWeekly() {
        return includeWeekly;
    }

    public void setIncludeWeekly(boolean includeWeekly) {
        this.includeWeekly = includeWeekly;
    }
}
