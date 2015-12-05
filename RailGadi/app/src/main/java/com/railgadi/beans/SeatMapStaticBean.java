package com.railgadi.beans;

public class SeatMapStaticBean  {

    private String title ;
    private String total ;
    private String coachNu ;
    private String footer ;

    public SeatMapStaticBean(String title, String total, String coachNu, String footer) {
        this.title = title;
        this.total = total;
        this.coachNu = coachNu;
        this.footer = footer;
    }

    public String getTitle() {
        return title;
    }

    public String getTotal() {
        return total;
    }

    public String getCoachNu() {
        return coachNu;
    }

    public String getFooter() {
        return footer;
    }
}
