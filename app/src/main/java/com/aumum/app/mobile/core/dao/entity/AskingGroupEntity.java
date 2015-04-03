package com.aumum.app.mobile.core.dao.entity;

/**
 * Created by Administrator on 3/04/2015.
 */
public class AskingGroupEntity extends BaseEntity {

    /** Not-null value. */
    private String objectId;
    private String avatarUrl;
    private String screenName;
    private String description;
    private Integer seq;

    public AskingGroupEntity() {
    }

    public AskingGroupEntity(String objectId) {
        this.objectId = objectId;
    }

    public AskingGroupEntity(String objectId, String avatarUrl, String screenName, String description, Integer seq) {
        this.objectId = objectId;
        this.avatarUrl = avatarUrl;
        this.screenName = screenName;
        this.description = description;
        this.seq = seq;
    }

    /** Not-null value. */
    public String getObjectId() {
        return objectId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
