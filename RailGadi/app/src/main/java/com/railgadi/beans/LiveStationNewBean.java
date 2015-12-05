package com.railgadi.beans;

import java.util.List;

public class LiveStationNewBean {

    private List<LiveStationData> liveStationList ;

    public List<LiveStationData> getLiveStationList() {
        return liveStationList;
    }

    public void setLiveStationList(List<LiveStationData> liveStationList) {
        this.liveStationList = liveStationList;
    }

    public static class LiveStationData {
        public String trainNumber ;
        public String trainName ;
        public String trainType ;
        public String srcStationName ;
        public String destStationName ;
        public String sta ;
        public String std ;
        public String eta ;
        public String etd ;
        public String delay ;
        public String platform ;
    }
}
