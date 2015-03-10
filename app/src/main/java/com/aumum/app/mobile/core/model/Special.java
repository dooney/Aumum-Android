package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 10/03/2015.
 */
public class Special extends AggregateRoot {

    private String name;
    private String vendorLogoUrl;
    private String info;
    private int likes;

    public String getName() {
        return name;
    }

    public String getVendorLogoUrl() {
        return vendorLogoUrl;
    }

    public String getInfo() {
        return info;
    }

    public int getLikes() {
        return likes;
    }
}
