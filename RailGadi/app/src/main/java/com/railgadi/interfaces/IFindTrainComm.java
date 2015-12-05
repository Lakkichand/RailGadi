package com.railgadi.interfaces;

import com.railgadi.beans.SearchTrainInputBean;
import com.railgadi.beans.SearchTrainResultBean;

public interface IFindTrainComm {

    public void updateSearchTrainListUI(SearchTrainResultBean searchTrainResult, SearchTrainInputBean input) ;
    public void updateException(String exceptionType) ;
}
