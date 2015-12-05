package com.railgadi.interfaces;

import com.railgadi.beans.FareEnqInput;
import com.railgadi.beans.SeatFareBeanNew;

public interface ISeatAvlFare {

    public void updateException(String exceptionMsg) ;
    public void updateSearchTrainUi(SeatFareBeanNew seatFareBeanNew, FareEnqInput fei) ;
}
