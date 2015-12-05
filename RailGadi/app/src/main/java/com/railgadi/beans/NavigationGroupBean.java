package com.railgadi.beans;

import java.util.List;

public class NavigationGroupBean {

    private String groupName ;
    private List<NavigationChildBean> childData ;
    private int groupIndicator ;
    private int icon ;
    private boolean isSelected ;
    private boolean isExpandable ;

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean isExpandable) {
        this.isExpandable = isExpandable;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<NavigationChildBean> getChildData() {
        return childData;
    }

    public void setChildData(List<NavigationChildBean> childData) {
        this.childData = childData;
    }

    public int getGroupIndicator() {
        return groupIndicator;
    }

    public void setGroupIndicator(int groupIndicator) {
        this.groupIndicator = groupIndicator;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
