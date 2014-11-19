package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 7/10/2014.
 */
public class Message extends AggregateRoot {
    private String fromUserId;
    private String toUserId;
    private int type;
    private String content;
    private String parentId;

    private User user;

    public Message() {

    }

    public Message(String objectId,
                   String createdAt,
                   int type,
                   String fromUserId,
                   String toUserId,
                   String content,
                   String parentId) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.type = type;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.content = content;
        this.parentId = parentId;
    }

    public Message(int type,
                   String fromUserId,
                   String toUserId,
                   String content,
                   String parentId) {
        this.type = type;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.content = content;
        this.parentId = parentId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class Type {
        public static final int DELETED = 0;
        public static final int PARTY_NEW = 1;
        public static final int PARTY_JOIN = 2;
        public static final int PARTY_QUIT = 3;
        public static final int PARTY_LIKE = 4;
        public static final int PARTY_COMMENT = 5;
        public static final int PARTY_REPLY = 6;
        public static final int PARTY_DELETE = 7;
    }
}
