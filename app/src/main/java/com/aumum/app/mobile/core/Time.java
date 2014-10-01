package com.aumum.app.mobile.core;

import java.io.Serializable;

/**
 * Created by Administrator on 25/09/2014.
 */
public class Time implements Serializable {
    protected int hour;
    protected int minute;

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

    public String getTimeString() {
        return getHeadingZeroString(hour) + ":" +
                getHeadingZeroString(minute);
    }
}
