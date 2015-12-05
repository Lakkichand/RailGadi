package com.railgadi.interfaces;

import com.railgadi.beans.NearByStationBean;

import java.util.Map;

public interface INearByStations {

    public void updateUI(Map<String, NearByStationBean> map) ;
    public void updateException(String exception) ;
}
