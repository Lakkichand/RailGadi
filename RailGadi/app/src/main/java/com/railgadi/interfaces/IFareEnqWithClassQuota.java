package com.railgadi.interfaces;

import com.railgadi.beans.FareEnqInput;
import com.railgadi.beans.FareEnquiryNewBean;

public interface IFareEnqWithClassQuota {

    public void updateFareOnClassAndQuota(FareEnquiryNewBean bean, FareEnqInput fei) ;
    public void updateException(String exceptionType) ;
}
