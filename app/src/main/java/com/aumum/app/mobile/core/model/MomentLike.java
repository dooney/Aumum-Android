package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 17/05/2015.
 */
public class MomentLike implements RefreshItem {

    private String momentId;
    private String userId;
    private String createdAt;

    private UserInfo user;
    private Moment moment;

    public MomentLike(String momentId,
                      String userId,
                      String createdAt) {
        this.momentId = momentId;
        this.userId = userId;
        this.createdAt = createdAt;
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
}
