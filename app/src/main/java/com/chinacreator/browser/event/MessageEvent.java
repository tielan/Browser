package com.chinacreator.browser.event;

public class MessageEvent {
    public static final String C_url = "url";
    public static final String C_screenOrientation = "screenOrientation";
    public static final String C_showBack = "showBack";

    private String url;
    private String orientation;
    private String showBack;

    public String getShowBack() {
        return showBack;
    }

    public void setShowBack(String showBack) {
        this.showBack = showBack;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }


}
