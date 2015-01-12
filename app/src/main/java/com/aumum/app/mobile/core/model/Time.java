package com.aumum.app.mobile.core.model;

import java.io.Serializable;

/**
 * Created by Administrator on 25/09/2014.
 */
public class Time implements Serializable {
    protected int hour;
    protected int minute;

    public Time() {

    }

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    private String getHeadingZeroString(int value) {
        String ret = String.valueOf(value);
        if (value < 10) {
            return "0" + ret;
        }
        return ret;
    }

    public String getTimeText() {
        String prefix = "";
        int h = hour;
        if (h < 5) {
            prefix = "凌晨";
        } else if (h <= 12) {
            prefix = "上午";
        } else if (h < 19) {
            h -= 12;
            prefix = "下午";
        } else {
            h -= 12;
            prefix = "晚上";
        }
        return prefix + h + "点" + getHeadingZeroString(minute);
    }
}
