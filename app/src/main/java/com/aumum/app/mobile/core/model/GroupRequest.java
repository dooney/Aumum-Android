package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 25/03/2015.
 */
public class GroupRequest {

    private Long id;
    private String groupId;
    private String userId;
    private String reason;
    private int status;

    private String groupName;
    private UserInfo user;

    public static final int STATUS_NONE = 0;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_REJECTED = 2;

    public GroupRequest(Long id,
                        String groupId,
                        String userId,
                        String reason,
                        int status) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.reason = reason;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }

    public String getReason() {
        return reason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }
}
