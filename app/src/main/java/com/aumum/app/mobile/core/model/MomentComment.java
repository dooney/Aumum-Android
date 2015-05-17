package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 18/05/2015.
 */
public class MomentComment implements RefreshItem {

    private String momentId;
    private String userId;
    private String createdAt;
    private String content;

    private UserInfo user;
    private Moment moment;

    public MomentComment(String momentId,
                         String userId,
                         String createdAt,
                         String content) {
        this.momentId = momentId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.content = content;
    }

    @Override
    public String getCreatedAt() {
        return createdAt;
    }

    public String getMomentId() {
        return momentId;
    }

    public String getUserId() {
        return userId;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public Moment getMoment() {
        return moment;
    }

    public void setMoment(Moment moment) {
        this.moment = moment;
    }

    public String getContent() {
        return content;
    }
}
