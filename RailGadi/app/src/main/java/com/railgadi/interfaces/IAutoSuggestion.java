package com.railgadi.interfaces;

import com.railgadi.beans.TrainDataBean;

import java.util.List;

public interface IAutoSuggestion {

    public void updateAutoSuggestion(List<TrainDataBean> suggestionEntries) ;
    public void updateAutoSuggestionException(String exception) ;
}
