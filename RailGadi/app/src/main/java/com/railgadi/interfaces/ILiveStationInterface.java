package com.railgadi.interfaces;

import com.railgadi.beans.LiveStationNewBean;

public interface ILiveStationInterface {

    public void updateLiveStationUI(LiveStationNewBean bean);

    public void updateException(String exception);
}
