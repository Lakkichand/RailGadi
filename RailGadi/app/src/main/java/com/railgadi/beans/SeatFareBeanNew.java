package com.railgadi.beans;

import java.util.List;

public class SeatFareBeanNew {

    private SeatAvl seatAvl ;
    public FareBreakup fareBreakup ;

    public SeatAvl getSeatAvl() {
        return seatAvl;
    }

    public void setSeatAvl(SeatAvl seatAvl) {
        this.seatAvl = seatAvl;
    }

    public FareBreakup getFareBreakup() {
        return fareBreakup;
    }

    public void setFareBreakup(FareBreakup fareBreakup) {
        this.fareBreakup = fareBreakup;
    }

    public static class SeatAvl {

        public String trainNumber ;
        public List<AvlStatus> avlStatusList ;
        public NextPrevious next ;
        public NextPrevious previous ;

        public static class AvlStatus {
            public String sNo ;
            public String date ;
            public String [] status ;
        }

        public static class NextPrevious {
            public String nextDay ;
            public String nextMonth ;
        }
    }

    public static class FareBreakup {

        public String trainNumber ;
        public String trainType ;
        public String netAmount ;
        public String totalAmount ;
        public List<FareDetails> fareList ;

        public static class FareDetails {
            public String key ;
            public String value ;
        }
    }

}
