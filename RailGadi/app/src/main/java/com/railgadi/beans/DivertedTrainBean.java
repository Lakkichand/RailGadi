package com.railgadi.beans;

import com.railgadi.interfaces.IResCanDivMarker;

import java.util.List;

public class DivertedTrainBean implements IResCanDivMarker {

    private List<DivertedData> yesterdayList ;
    private List<DivertedData> todayList ;
    private List<DivertedData> tomorrowList ;

    public static class DivertedData {

        public String trainNo ;
        public String trainName ;
        public String trainType ;
        public String srcStationName ;
        public String destStationName ;
        public String startTime ;
        public String divertedTo ;
        public String divertedFrom ;
    }

    public List<DivertedData> getYesterdayList() {
        return yesterdayList;
    }

    public void setYesterdayList(List<DivertedData> yesterdayList) {
        this.yesterdayList = yesterdayList;
    }

    public List<DivertedData> getTodayList() {
        return todayList;
    }

    public void setTodayList(List<DivertedData> todayList) {
        this.todayList = todayList;
    }

    public List<DivertedData> getTomorrowList() {
        return tomorrowList;
    }

    public void setTomorrowList(List<DivertedData> tomorrowList) {
        this.tomorrowList = tomorrowList;
    }
}
