package com.railgadi.beans;

import java.util.List;


public class CoachCompositionBean {

    private String trainName ;
    private List<CoachBean> coachBean;

    public void setTrainName(String trainName) {
        this.trainName = trainName ;
    }
    public String getTrainName() {
        return this.trainName;
    }

    public void setCoachBean(List<CoachBean> coachBean) {
        this.coachBean = coachBean;
    }

    public List<CoachBean> getCoachBean() {
        return this.coachBean;
    }

    public static class CoachBean {

        private String serialNo;
        private String code;
        private String description;
        private int backgroundColor;
        private int textColor;

        public int getBackground() {
            return this.backgroundColor;
        }

        public void setBackground(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public int getTextColor() {
            return this.textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
