package com.railgadi.beans;

import java.util.Date;

public class LiveTrainNewBean {

    private MiniTimeTableBean miniTimeTableBean;
    private Date inputDateInstance ;
    private String journeyStation ;
    private String schArr ;
    private String actArr ;
    private String delayArr ;
    private String schDep ;
    private String actDep ;
    private String delayDep ;
    private String lastUpdatedTime ;
    private LastStation lastStation ;
    private NextStation nextStation ;
    private boolean isIntermediate ;
    private int interMediateCount ;
    private boolean isTrainNotDeparted ;
    private boolean isTrainArrived;

    public int trainPosition ;
    public boolean isMain ;
    public int nextStnPosition ;
    public int prevStnPosition ;

    public static class LastStation {
        public String stnCode ;
        public String stnName ;
        public String delay ;
        public String depTime ;
    }

    public static class NextStation {
        public String stnCode ;
        public String stnName ;
        public String nextDistAhead ;
    }

    public Date getInputDateInstance() {
        return inputDateInstance;
    }

    public void setInputDateInstance(Date inputDateInstance) {
        this.inputDateInstance = inputDateInstance;
    }

    public MiniTimeTableBean getMiniTimeTableBean() {
        return miniTimeTableBean;
    }

    public void setMiniTimeTableBean(MiniTimeTableBean miniTimeTableBean) {
        this.miniTimeTableBean = miniTimeTableBean;
    }

    public String getJourneyStation() {
        return journeyStation;
    }

    public void setJourneyStation(String journeyStation) {
        this.journeyStation = journeyStation;
    }

    public String getSchArr() {
        return schArr;
    }

    public void setSchArr(String schArr) {
        this.schArr = schArr;
    }

    public String getActArr() {
        return actArr;
    }

    public void setActArr(String actArr) {
        this.actArr = actArr;
    }

    public String getDelayArr() {
        return delayArr;
    }

    public void setDelayArr(String delayArr) {
        this.delayArr = delayArr;
    }

    public String getSchDep() {
        return schDep;
    }

    public void setSchDep(String schDep) {
        this.schDep = schDep;
    }

    public String getActDep() {
        return actDep;
    }

    public void setActDep(String actDep) {
        this.actDep = actDep;
    }

    public String getDelayDep() {
        return delayDep;
    }

    public void setDelayDep(String delayDep) {
        this.delayDep = delayDep;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public LastStation getLastStation() {
        return lastStation;
    }

    public void setLastStation(LastStation lastStation) {
        this.lastStation = lastStation;
    }

    public NextStation getNextStation() {
        return nextStation;
    }

    public void setNextStation(NextStation nextStation) {
        this.nextStation = nextStation;
    }

    public boolean isIntermediate() {
        return isIntermediate;
    }

    public void setIntermediate(boolean isIntermediate) {
        this.isIntermediate = isIntermediate;
    }

    public int getInterMediateCount() {
        return interMediateCount;
    }

    public void setInterMediateCount(int interMediateCount) {
        this.interMediateCount = interMediateCount;
    }

    public boolean isTrainNotDeparted() {
        return isTrainNotDeparted;
    }

    public void setTrainNotDeparted(boolean isTrainDeparted) {
        this.isTrainNotDeparted = isTrainDeparted;
    }

    public boolean isTrainArrived() {
        return isTrainArrived;
    }

    public void setTrainArrived(boolean isTrainArrived) {
        this.isTrainArrived = isTrainArrived;
    }
}
