package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 2/11/2014.
 */
public class Place {
    private String location;
    private double latitude;
    private double longitude;

    public Place(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
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
