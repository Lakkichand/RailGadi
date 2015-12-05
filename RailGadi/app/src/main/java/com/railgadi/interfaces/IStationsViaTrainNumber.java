package com.railgadi.interfaces;

import com.railgadi.beans.MiniTimeTableBean;

public interface IStationsViaTrainNumber {
    public void updateMiniTimetable(MiniTimeTableBean bean) ;
    public void updateException(String exceptionType) ;
}
