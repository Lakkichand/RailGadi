package com.railgadi.beans;

import java.util.List;

public class SeatMapDataBean {

    private String name ;
    private String noOfPage ;
    private List<Integer> pages ;
    private List<SeatMapStaticBean> headerList ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoOfPage() {
        return noOfPage;
    }

    public void setNoOfPage(String noOfPage) {
        this.noOfPage = noOfPage;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public List<SeatMapStaticBean> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<SeatMapStaticBean> headerList) {
        this.headerList = headerList;
    }

}
