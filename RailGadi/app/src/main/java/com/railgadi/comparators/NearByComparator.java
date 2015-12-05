package com.railgadi.comparators;

import com.railgadi.beans.NearByStationBean;

import java.util.Comparator;
import java.util.Map;

public class NearByComparator implements Comparator<Map.Entry<String, NearByStationBean>> {

    @Override
    public int compare(Map.Entry<String, NearByStationBean> lhs, Map.Entry<String, NearByStationBean> rhs) {

        Double distanceOne = lhs.getValue().getDistance() ;
        Double distanceTwo = rhs.getValue().getDistance() ;

        return distanceOne.compareTo(distanceTwo) ;
    }
}
