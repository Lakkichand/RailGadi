package com.railgadi.beans;

import com.railgadi.fragments.SearchTrainToNextFrag;
import com.railgadi.utilities.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SearchTrainResultBean {

    private List<SearchTrain> searchTrainList ;

    public List<SearchTrain> getSearchTrainList() {
        return searchTrainList;
    }

    public void setSearchTrainList(List<SearchTrain> searchTrainList) {
        this.searchTrainList = searchTrainList;
    }

    public static class SearchTrain implements Comparable<SearchTrain> {

        public String trainNo ;
        public String trainName ;
        public String trainType ;
        public String srcStnCode ;
        public String destStnCode ;
        public String srcStnName ;
        public String destStnName ;
        public String arrTime ;
        public String deptTime ;
        public String travelTime ;
        public boolean [] runningDays ;
        public List<String> avlClasses ;
        public String activeClass ;

        // for selection in list
        public SeatFareBeanNew seatFareBeanNew ;
        public boolean isSelected ;


        public static SearchTrainToNextFrag.MySorting mySort;

        @Override
        public int compareTo(SearchTrain another) {

            if (mySort != null) {

                if (mySort.getType().equals(Constants.DEP_TYPE)) {

                    if (!mySort.getFlag()) {

                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                        try {
                            Date thisTime = formatter.parse(this.deptTime);
                            Date anotherTime = formatter.parse(another.deptTime);

                            return thisTime.compareTo(anotherTime);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                        try {
                            Date thisTime = formatter.parse(this.deptTime) ;
                            Date anotherTime = formatter.parse(another.deptTime) ;

                            return anotherTime.compareTo(thisTime);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (mySort.getType().equals(Constants.ARR_TYPE)) {

                    if (!mySort.getFlag()) {

                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                        try {
                            Date thisTime = formatter.parse(this.arrTime) ;
                            Date anotherTime = formatter.parse(another.arrTime) ;

                            return thisTime.compareTo(anotherTime);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                        try {
                            Date thisTime = formatter.parse(this.arrTime) ;
                            Date anotherTime = formatter.parse(another.arrTime) ;

                            return anotherTime.compareTo(thisTime);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else if (mySort.getType().equals(Constants.DUR_TYPE)) {
                    if (!mySort.getFlag()) {

                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                        try {
                            String thisT = this.travelTime ;
                            String anotherT = another.travelTime ;

                            Date thisTime = formatter.parse(thisT);
                            Date anotherTime = formatter.parse(anotherT);

                            return thisTime.compareTo(anotherTime);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                        try {
                            String thisT = this.travelTime ;
                            String anotherT = another.travelTime ;

                            Date thisTime = formatter.parse(thisT);
                            Date anotherTime = formatter.parse(anotherT);

                            return anotherTime.compareTo(thisTime);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return 0;
        }
    }
}
