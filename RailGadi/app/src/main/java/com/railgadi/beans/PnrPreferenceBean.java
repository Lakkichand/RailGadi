package com.railgadi.beans;

public class PnrPreferenceBean {

    private String flag;
    private String pnrNumber;
    private PnrStatusNewBean object;

    public PnrPreferenceBean() {

    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public PnrStatusNewBean getPnrObject() {
        return object;
    }

    public void setPnrObject(PnrStatusNewBean object) {
        this.object = object;
    }
}
