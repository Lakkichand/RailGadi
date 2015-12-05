package com.railgadi.comparators;

import com.railgadi.beans.TrainDataBean;

import java.util.Comparator;

/**
 * Created by Vijay on 03-12-2015.
 */
public class TrainNumberComparator implements Comparator<TrainDataBean> {
    @Override
    public int compare(TrainDataBean lhs, TrainDataBean rhs) {
        return lhs.getTrainNumber().compareTo(rhs.getTrainNumber()) ;
    }
}
