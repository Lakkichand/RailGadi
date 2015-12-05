package com.railgadi.interfaces;

import com.railgadi.beans.PnrStatusNewBean;

public interface IPnrStatusComm {

    public void updatePnrUI(PnrStatusNewBean ps) ;
    public void updateException(String exception) ;
}
