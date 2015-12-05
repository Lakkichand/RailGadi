package com.railgadi.beans;

import java.io.Serializable;

public class FareEnqInput implements Serializable {

    private String trainNo = "";
    private String srcCode = "";
    private String destCode = "";
    private String day = "";
    private String month = "";
    private String classCode = "";
    private String age = "";
    private String concCode = "";
    private String quota = "";

    public FareEnqInput() {
        /*age = "30";
        concCode = "ZZZZZZ";
        quota = "GN";*/
    }

    public void assign(FareEnqInput fei){

        this.age            =   fei.age ;
        this.classCode      =   fei.classCode ;
        this.trainNo        =   fei.trainNo ;
        this.day            =   fei.day ;
        this.month          =   fei.month ;
        this.quota          =   fei.quota ;
        this.concCode       =   fei.concCode ;
        this.srcCode        =   fei.srcCode ;
        this.destCode       =   fei.destCode ;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getSrcCode() {
        return srcCode;
    }

    public void setSrcCode(String srcCode) {
        this.srcCode = srcCode;
    }

    public String getDestCode() {
        return destCode;
    }

    public void setDestCode(String destCode) {
        this.destCode = destCode;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getConcCode() {
        return concCode;
    }

    public void setConcCode(String concCode) {
        this.concCode = concCode;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public boolean isSetConcCode() {
        if (this.concCode == null || this.concCode.equalsIgnoreCase("ZZZZZZ")) {
            return false;
        }
        return true;
    }

    public String getNoneConcCode() {
        return "ZZZZZZ";
    }

}
