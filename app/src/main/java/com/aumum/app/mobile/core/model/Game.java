package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 26/03/2015.
 */
public class Game extends AggregateRoot {

    private String deletedAt;
    private int seq;
    private String screenName;
    private String avatarUrl;
    private String uri;
    private String description;
    private int orientation;
    private int clicks;

    private final int ORIENTATION_PORTRAIT = 0;
    private final int ORIENTATION_LANDSCAPE = 1;

    public int getSeq() {
        return seq;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUri() {
        return uri;
    }

    public String getDescription() {
        return description;
    }

    public boolean isLandscape() {
        return orientation == ORIENTATION_LANDSCAPE;
    }

    public int getClicks() {
        return clicks;
    }
}
