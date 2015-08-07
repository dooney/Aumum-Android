package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 28/12/2014.
 */
public class CmdMessage {
    private int type;
    private String userId;
    private String screenName;
    private String avatarUrl;
    private String momentId;
    private String imageUrl;
    private String content;

    public CmdMessage(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMomentId() {
        return momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static class Type {
        public static final int NEW_MOMENT = 1;
        public static final int MOMENT_LIKE = 2;
        public static final int MOMENT_COMMENT = 3;
        public static final int NEW_CONTACT = 11;
    }
}
