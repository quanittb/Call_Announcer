package com.mobiai.base.basecode.language;

import java.io.Serializable;


public class Language implements Serializable {
    private int flag;
    private String title;
    private String locale;
    private boolean isChoose;


    public void setIndex(int index) {
        this.index = index;
    }

    private int index;

    public Language(int flag, String title, String locale) {
        this.flag = flag;
        this.title = title;
        this.locale = locale;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
