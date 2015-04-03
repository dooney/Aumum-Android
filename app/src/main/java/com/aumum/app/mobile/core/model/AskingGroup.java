package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 31/03/2015.
 */
public class AskingGroup extends AggregateRoot {

    private String deletedAt;
    private String avatarUrl;
    private String screenName;
    private String description;
    private int seq;
    private String boardId;
    private boolean unread;
    private boolean isMember;

    public AskingGroup(String objectId,
                       String avatarUrl,
                       String screenName,
                       String description,
                       int seq,
                       String boardId) {
        this.objectId = objectId;
        this.avatarUrl = avatarUrl;
        this.screenName = screenName;
        this.description = description;
        this.seq = seq;
        this.boardId = boardId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getDescription() {
        return description;
    }

    public int getSeq() {
        return seq;
    }

    public String getBoardId() {
        return boardId;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean isMember) {
        this.isMember = isMember;
    }
}
