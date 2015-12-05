package com.railgadi.interfaces;

import com.railgadi.beans.NearByAmenitiesBean;

import java.util.List;

public interface IAmenitiesConnector {
    public void updateException(String exception) ;
    public void updateAmenities(List<NearByAmenitiesBean> amenitiesList) ;
}
