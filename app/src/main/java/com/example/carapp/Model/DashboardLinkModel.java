package com.example.carapp.Model;

import androidx.navigation.NavDirections;

public class DashboardLinkModel {
    private String linkText;
    private int iconRes;
    private NavDirections navAction;
    public DashboardLinkModel (String linkText, int iconRes) {
        this.linkText = linkText;
        this.iconRes = iconRes;
        this.navAction = null;
    }

    public DashboardLinkModel (String linkText, int iconRes, NavDirections navAction) {
        this.linkText = linkText;
        this.iconRes = iconRes;
        this.navAction = navAction;
    }

    public String getLinkText() {
        return linkText;
    }
    public int getIconRes() {
        return iconRes;
    }
    public NavDirections getNavAction() {
        return navAction;
    }
    public void setNavAction(NavDirections navAction) {
        this.navAction = navAction;
    }
}
