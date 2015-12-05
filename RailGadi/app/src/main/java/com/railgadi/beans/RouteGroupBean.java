package com.railgadi.beans;

import java.util.List;

public class RouteGroupBean {

    private String dayGroup ;
    private List<RouteChildBean> childList;

    public String getDayGroup() {
        return dayGroup;
    }

    public void setDayGroup(String dayGroup) {
        this.dayGroup = dayGroup;
    }

    public List<RouteChildBean> getChildList() {
        return childList;
    }

    public void setChildList(List<RouteChildBean> childList) {
        this.childList = childList;
    }
}
