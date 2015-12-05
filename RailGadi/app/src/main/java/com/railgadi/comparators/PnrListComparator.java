package com.railgadi.comparators;

import com.railgadi.beans.PnrStatusNewBean;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class PnrListComparator implements Comparator<Map.Entry<String,PnrStatusNewBean>> {

    @Override
    public int compare(Map.Entry<String, PnrStatusNewBean> first, Map.Entry<String, PnrStatusNewBean> second) {
        int returnValue = 0 ;

        try {
            Date dateOne = first.getValue().getTrainInfo().srcDepDate ;
            Date dateTwo = second.getValue().getTrainInfo().destArrDate ;

            returnValue = dateOne.compareTo(dateTwo) ;

        } catch(Exception e) {

        }
        return returnValue ;
    }
}
