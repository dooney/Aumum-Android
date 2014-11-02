package com.aumum.app.mobile.core.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2/11/2014.
 */
public class Place implements Serializable {
    private String location;
    private double latitude;
    private double longitude;

    public Place() {}

    public Place(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
