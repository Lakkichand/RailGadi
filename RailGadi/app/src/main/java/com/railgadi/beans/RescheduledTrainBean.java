package com.railgadi.beans;

import com.railgadi.interfaces.IResCanDivMarker;

import java.util.List;

public class RescheduledTrainBean implements IResCanDivMarker {

    private List<RescheduledData> yesterdayList ;
    private List<RescheduledData> todayList ;
    private List<RescheduledData> tomorrowList ;

    public static class RescheduledData {

        public String trainNo ;
        public String trainName ;
        public String trainType ;
        public String srcStationName ;
        public String destStationName ;
        public String startDateTime ;
        public String reschDateTime ;
        public String delay ;
    }

    public List<RescheduledData> getYesterdayList() {
        return yesterdayList;
    }

    public void setYesterdayList(List<RescheduledData> yesterdayList) {
        this.yesterdayList = yesterdayList;
    }

    public List<RescheduledData> getTodayList() {
        return todayList;
    }

    public void setTodayList(List<RescheduledData> todayList) {
        this.todayList = todayList;
    }

    public List<RescheduledData> getTomorrowList() {
        return tomorrowList;
    }

    public void setTomorrowList(List<RescheduledData> tomorrowList) {
        this.tomorrowList = tomorrowList;
    }
}
