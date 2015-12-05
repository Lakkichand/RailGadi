package com.railgadi.interfaces;

import com.railgadi.beans.LiveTrainNewBean;
import com.railgadi.beans.TimeTableNewBean;

public interface ILiveTrainInterface {
    public void updateException(String exception) ;
    public void updateLiveTrainData(LiveTrainNewBean bean, TimeTableNewBean timeTableNewBean) ;
}
