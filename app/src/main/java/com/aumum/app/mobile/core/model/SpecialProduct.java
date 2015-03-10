package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 10/03/2015.
 */
public class SpecialProduct extends AggregateRoot {

    private String previewUrl;
    private String name;
    private double was;
    private double now;

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getName() {
        return name;
    }

    public double getWas() {
        return was;
    }

    public double getNow() {
        return now;
    }
}
