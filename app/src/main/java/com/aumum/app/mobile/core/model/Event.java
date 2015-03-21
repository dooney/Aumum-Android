package com.aumum.app.mobile.core.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Administrator on 21/03/2015.
 */
public class Event {

    class Point {
        Double lat;
        Double lng;
    }

    private String url;
    private String name;
    private String address;
    private Point point;
    private String datetime_start;
    private String description;

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return point != null ? point.lat : null;
    }

    public Double getLongitude() {
        return point != null ? point.lng : null;
    }

    public String getDescription() {
        return description;
    }

    private DateTime getDateTime() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return dtf.parseDateTime(datetime_start);
    }

    public Date getDate() {
        DateTime dt = getDateTime();
        return new Date(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth());
    }

    public Time getTime() {
        DateTime dt = getDateTime();
        return new Time(dt.getHourOfDay(), dt.getMinuteOfHour());
    }

    public String getDateTimeText() {
        return getDate().getDateText() + " " + getTime().getTimeText();
    }

    public String getDetails() {
        return getDescription() + " " + "查看详情" + getUrl();
    }
}
