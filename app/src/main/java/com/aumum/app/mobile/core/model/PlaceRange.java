package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 2/01/2015.
 */
public class PlaceRange {

    private double minLat;
    private double maxLat;
    private double minLng;
    private double maxLng;

    private static final double NEARBY_THRESHOLD = 10.0;

    public PlaceRange(double lat, double lng) {
        double kmInLongitudeDegree = 111.320 * Math.cos( lat / 180.0 * Math.PI);
        double deltaLat = NEARBY_THRESHOLD / 111.1;
        double deltaLng = NEARBY_THRESHOLD / kmInLongitudeDegree;

        minLat = lat - deltaLat;
        maxLat = lat + deltaLat;
        minLng = lng - deltaLng;
        maxLng = lng + deltaLng;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMaxLng() {
        return maxLng;
    }

    public double getMinLng() {
        return minLng;
    }
}
