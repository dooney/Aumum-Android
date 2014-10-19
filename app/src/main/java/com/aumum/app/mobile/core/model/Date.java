package com.aumum.app.mobile.core.model;

import java.io.Serializable;

/**
 * Created by Administrator on 25/09/2014.
 */
public class Date implements Serializable {
    protected int year;
    protected int month;
    protected int day;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    private String getHeadingZeroString(int value) {
        String ret = String.valueOf(value);
        if (value < 10) {
            return "0" + ret;
        }
        return ret;
    }

    public String getDateString() {
        return getHeadingZeroString(year) + "-" +
                getHeadingZeroString(month) + "-" +
                getHeadingZeroString(day);
    }
}
