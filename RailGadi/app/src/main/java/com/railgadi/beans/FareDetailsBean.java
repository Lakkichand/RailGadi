package com.railgadi.beans;

import java.util.Map;

public class FareDetailsBean {

    private String classType;
    private String totalFare;
    private Map<String, String> subFares;

    public FareDetailsBean(String classType, String totalFare, Map<String, String> subFares) {
        this.classType = classType;
        this.totalFare = totalFare;
        this.subFares = subFares;
    }

    public String getClassType() {
        return this.classType;
    }

    public String getTotalFare() {
        return this.totalFare;
    }

    public Map<String, String> getSubFare() {
        return this.subFares;
    }
}