package com.railgadi.beans;

import java.util.Date;
import java.util.List;

public class RefreshPnrBean {

    private String pnrNumber ;
    private List<PnrStatusNewBean.Passenger> passengerList ;
    private String charting ;
    private Date lastTimeCheck ;

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public List<PnrStatusNewBean.Passenger> getPassengerList() {
        return passengerList;
    }

    public void setPassengerList(List<PnrStatusNewBean.Passenger> passengerList) {
        this.passengerList = passengerList;
    }

    public String getCharting() {
        return charting;
    }

    public void setCharting(String charting) {
        this.charting = charting;
    }

    public Date getLastTimeCheck() {
        return lastTimeCheck;
    }

    public void setLastTimeCheck(Date lastTimeCheck) {
        this.lastTimeCheck = lastTimeCheck;
    }
}
