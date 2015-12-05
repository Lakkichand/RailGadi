package com.railgadi.beans;

import java.util.List;

public class SpecialTrainNewBean {

    private List<SpecialTrains> specialTrainsList ;

    public List<SpecialTrains> getSpecialTrainsList() {
        return specialTrainsList;
    }

    public void setSpecialTrainsList(List<SpecialTrains> specialTrainsList) {
        this.specialTrainsList = specialTrainsList;
    }

    public static class SpecialTrains {

        public String trainNumber ;
        public String runsFromStn ;
        public String depTime ;
        public String arrTime ;
        public String travelTime ;
        public String validFrom ;
        public String validTo ;
        public String runsOnBin ;
        public String srcStnName ;
        public String destStnName ;
        public String trainName ;
        public boolean [] runningDays ;
    }
}
