package com.railgadi.beans;

import com.railgadi.interfaces.IResCanDivMarker;

import java.util.List;

public class CancelledTrainBean implements IResCanDivMarker {

    private CancelledData yesterday ;
    private CancelledData today ;
    private CancelledData tomorrow ;

    public static class CancelledData {

        public List<FullyCancelled> fullyCancelledList ;
        public List<PartiallyCancelled> partiallyCancelledList ;
    }

    public static class FullyCancelled {

        public String trainNo ;
        public String trainName ;
        public String trainType ;
        public String srcStationName ;
        public String destStationName ;
        public String startTime ;
    }

    public static class PartiallyCancelled {

        public String trainNo ;
        public String trainName ;
        public String trainType ;
        public String srcStationName ;
        public String destStationName ;
        public String startTime ;
        public String cancelledFrom ;
        public String cancelledTo ;
    }

    public CancelledData getYesterday() {
        return yesterday;
    }

    public void setYesterday(CancelledData yesterday) {
        this.yesterday = yesterday;
    }

    public CancelledData getToday() {
        return today;
    }

    public void setToday(CancelledData today) {
        this.today = today;
    }

    public CancelledData getTomorrow() {
        return tomorrow;
    }

    public void setTomorrow(CancelledData tomorrow) {
        this.tomorrow = tomorrow;
    }
}
