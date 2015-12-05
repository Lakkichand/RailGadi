package com.railgadi.beans;

import java.util.Date;
import java.util.List;

public class PnrStatusNewBean {

    private String pnrNumber ;
    private String classType ;
    private String mainStatus ;
    private String chartingStatus ;
    private String boardFromCode ;
    private String boardFromName ;
    private String boardToCode ;
    private String boardToName ;
    private Date lastTimeCheck ;
    private String travelDate ;
    private TimeTableNewBean trainDetailRoute ;
    private List<Passenger> passengerList;
    private TrainInfo trainInfo;
    private CoachCompositionBean coachCompositionBean ;

    private String flag ;
    private boolean isFirstVisible ;
    private boolean checkMark ;


    public static class Passenger {
        public String sNo;
        public String coachNumber;
        public String seatNumber;
        public String quotaType;
        public String coachPos ;
        public String possibility ;
        public String status;
    }


    public static class TrainInfo {

        public String trainNumber ;
        public String trainName ;

        //public String srcStnCode ;
        //public String srcStnName ;
        public Date srcDepDate ;
        public String srcDepTime ;

        //public String destStnCode ;
        //public String destStnName ;
        public Date destArrDate ;
        public String destArrTime ;

        public String duration ;
        public String totalHalts ;
        public String distance ;
        public String trainType ;

        public List<String> avlClasses ;
        public boolean[] runningDays ;
        public boolean[] pantries ;
    }

    public CoachCompositionBean getCoachCompositionBean() {
        return coachCompositionBean;
    }

    public void setCoachCompositionBean(CoachCompositionBean coachCompositionBean) {
        this.coachCompositionBean = coachCompositionBean;
    }

    public TimeTableNewBean getTrainDetailRoute() {
        return trainDetailRoute;
    }

    public void setTrainDetailRoute(TimeTableNewBean trainDetailRoute) {
        this.trainDetailRoute = trainDetailRoute;
    }

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getMainStatus() {
        return mainStatus;
    }

    public void setMainStatus(String mainStatus) {
        this.mainStatus = mainStatus;
    }

    public String getChartingStatus() {
        return this.chartingStatus ;
    }

    public void setChartingStatus(String chartingStatus) {
        this.chartingStatus = chartingStatus ;
    }

    public String getBoardFromCode() {
        return boardFromCode;
    }

    public void setBoardFromCode(String boardFromCode) {
        this.boardFromCode = boardFromCode;
    }

    public String getBoardFromName() {
        return boardFromName;
    }

    public void setBoardFromName(String boardFromName) {
        this.boardFromName = boardFromName;
    }

    public String getBoardToCode() {
        return boardToCode;
    }

    public void setBoardToCode(String boardToCode) {
        this.boardToCode = boardToCode;
    }

    public String getBoardToName() {
        return boardToName;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public void setBoardToName(String boardToName) {
        this.boardToName = boardToName;
    }

    public boolean isCheckMark() {
        return checkMark;
    }

    public Date getLastTimeCheck() {
        return this.lastTimeCheck ;
    }

    public void setLastTimeCheck(Date lastTimeCheck) {
        this.lastTimeCheck = lastTimeCheck ;
    }

    public String getFlag() {
        return this.flag ;
    }

    public void setFlag(String flag) {
        this.flag   =   flag ;
    }

    public boolean isFirstVisible() {
        return this.isFirstVisible ;
    }

    public void setFirstVisible(boolean isFirstVisible) {
        this.isFirstVisible = isFirstVisible ;
    }

    public boolean getCheckMark() {
        return this.checkMark ;
    }

    public void setCheckMark(boolean checkMark) {
        this.checkMark = checkMark ;
    }

    public List<Passenger> getPassengerList() {
        return passengerList;
    }

    public void setPassengerList(List<Passenger> passengerList) {
        this.passengerList = passengerList;
    }

    public TrainInfo getTrainInfo() {
        return trainInfo;
    }

    public void setTrainInfo(TrainInfo trainInfo) {
        this.trainInfo = trainInfo;
    }
}
