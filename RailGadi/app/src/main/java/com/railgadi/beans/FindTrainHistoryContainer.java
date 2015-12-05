package com.railgadi.beans;

public class FindTrainHistoryContainer {

    private String srcCode;
    private String srcName;
    private String destCode;
    private String destName;
    private String dateTime;
    private int srcIcon;
    private int destIcon;

    public String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSrcCode() {
        return this.srcCode;
    }

    public void setSrcCode(String srcCode) {
        this.srcCode = srcCode;
    }

    public String getSrcName() {
        return this.srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getDestCode() {
        return this.destCode;
    }

    public void setDestCode(String destCode) {
        this.destCode = destCode;
    }

    public String getDestName() {
        return this.destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public int getSrcIcon() {
        return this.srcIcon ;
    }

    public void setSrcIcon(int srcIcon) {
        this.srcIcon = srcIcon ;
    }

    public int getDestIcon() {
        return this.destIcon;
    }

    public void setDestIcon(int destIcon) {
        this.destIcon = destIcon ;
    }

    @Override
    public int hashCode() {
        return this.srcCode.hashCode() + this.destCode.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        FindTrainHistoryContainer obj = null;
        if (o != null && o instanceof FindTrainHistoryContainer) {
            obj = (FindTrainHistoryContainer) o;
            if (obj.hashCode() == this.hashCode()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
