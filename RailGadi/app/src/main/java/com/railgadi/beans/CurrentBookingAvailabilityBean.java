package com.railgadi.beans;

import java.util.List;

public class CurrentBookingAvailabilityBean {

    private List<CurrentBooking> currentBookingList ;

    public List<CurrentBooking> getCurrentBookingList() {
        return currentBookingList;
    }

    public void setCurrentBookingList(List<CurrentBooking> currentBookingList) {
        this.currentBookingList = currentBookingList;
    }

    public static class CurrentBooking {

        private String trainNumber ;
        private String trainName ;
        private String srcStationName ;
        private String deptTime ;
        private String destStationName ;
        private List<SeatAvailability> seatAvlList ;

        public String getTrainNumber() {
            return trainNumber;
        }

        public void setTrainNumber(String trainNumber) {
            this.trainNumber = trainNumber;
        }

        public String getTrainName() {
            return trainName;
        }

        public void setTrainName(String trainName) {
            this.trainName = trainName;
        }

        public String getSrcStationName() {
            return srcStationName;
        }

        public void setSrcStationName(String srcStationName) {
            this.srcStationName = srcStationName;
        }

        public String getDeptTime() {
            return deptTime;
        }

        public void setDeptTime(String deptTime) {
            this.deptTime = deptTime;
        }

        public String getDestStationName() {
            return destStationName;
        }

        public void setDestStationName(String destStationName) {
            this.destStationName = destStationName;
        }

        public List<SeatAvailability> getSeatAvlList() {
            return seatAvlList;
        }

        public void setSeatAvlList(List<SeatAvailability> seatAvlList) {
            this.seatAvlList = seatAvlList;
        }
    }

    public static class SeatAvailability {
        public String keyClass ;
        public String valueAvl ;
    }
}
