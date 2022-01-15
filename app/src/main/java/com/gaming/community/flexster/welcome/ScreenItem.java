package com.gaming.community.flexster.welcome;

public class ScreenItem {

    String Title,des;
    int screenImg;

    public ScreenItem(String title, String des, int screenImg) {
        Title = title;
        this.des = des;
        this.screenImg = screenImg;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setScreenImg(int screenImg) {
        this.screenImg = screenImg;
    }


    public String getTitle() {
        return Title;
    }


    public int getScreenImg() {
        return screenImg;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
