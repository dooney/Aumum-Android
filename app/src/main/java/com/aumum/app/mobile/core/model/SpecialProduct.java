package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 10/03/2015.
 */
public class SpecialProduct extends AggregateRoot {

    private String specialId;
    private String previewUrl;
    private String name;
    private double was;
    private double now;
    private List<String> likes = new ArrayList<String>();
    private List<String> favorites = new ArrayList<String>();

    public String getSpecialId() {
        return specialId;
    }

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

    public int getLikesCount() {
        if (likes != null) {
            return likes.size();
        }
        return 0;
    }

    public int getFavoritesCount() {
        if (favorites != null) {
            return favorites.size();
        }
        return 0;
    }

    public boolean isLiked(String userId) {
        if (likes != null) {
            return likes.contains(userId);
        }
        return false;
    }

    public boolean isFavorited(String userId) {
        if (favorites != null) {
            return favorites.contains(userId);
        }
        return false;
    }
}
