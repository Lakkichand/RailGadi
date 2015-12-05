package com.railgadi.beans;

import java.util.List;

public class StationListDataHolder {

    private String name ;
    private List<StationNameCodeBean> nameCodeData ;

    public StationListDataHolder() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StationNameCodeBean> getNameCodeData() {
        return nameCodeData;
    }

    public void setNameCodeData(List<StationNameCodeBean> nameCodeData) {
        this.nameCodeData = nameCodeData;
    }
}
