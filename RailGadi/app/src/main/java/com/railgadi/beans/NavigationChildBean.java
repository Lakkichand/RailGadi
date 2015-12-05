package com.railgadi.beans;

public class NavigationChildBean {

    private String menu ;
    private int unselectedIcon;

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public int getIcon() {
        return unselectedIcon;
    }

    public void setIcon(int unSelectedImageId) {
        this.unselectedIcon = unSelectedImageId;
    }
}
