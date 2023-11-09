package com.example.carapp.Model;

public class DashboardLinkModel {
    private String linkText;
    private int iconRes;
    private int navDirection;
    public DashboardLinkModel (String linkText, int iconRes, int navDirection) {
        this.linkText = linkText;
        this.iconRes = iconRes;
        this.navDirection = navDirection;
    }
    public String getLinkText() {
        return linkText;
    }
    public int getIconRes() {
        return iconRes;
    }

    public int getNavDirection() {
        return navDirection;
    }
}
