package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 7/10/2014.
 */
public class Message extends AggregateRoot {
    private String fromUserId;
    private String toUserId;
    private int type;
    private User fromUser;

    public static class Type {
        public static final int DELETED = 0;
        public static final int USER_FOLLOW = 1;
        public static final int PARTY_JOIN = 2;
        public static final int PARTY_QUIT = 3;
        public static final int PARTY_LIKE = 4;
        public static final int PARTY_COMMENT = 5;
        public static final int PARTY_REPLY = 6;
        public static final int PARTY_DELETE = 7;
    }

    public static class Category {
        public static final int PARTY_MEMBERSHIP = 101;
        public static final int PARTY_COMMENTS = 102;
        public static final int PARTY_LIKES = 103;
    }

    private static final String MESSAGE_BODY_OPTIONS[] = {
        "该消息已删除",
        "关注了您",
        "报名了宝妈活动",
        "取消了报名宝妈活动",
        "支持了宝妈活动",
        "发表了评论",
        "回复了您的评论",
        "删除了宝妈活动"
    };

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    public String getBody() {
        return MESSAGE_BODY_OPTIONS[type];
    }

    public static int[] getTypesByCategory(int category) {
        switch (category) {
            case Category.PARTY_MEMBERSHIP:
                return new int[]{ Type.PARTY_JOIN, Type.PARTY_QUIT };
            case Category.PARTY_COMMENTS:
                return new int[]{ Type.PARTY_COMMENT, Type.PARTY_REPLY };
            case Category.PARTY_LIKES:
                return new int[]{ Type.PARTY_LIKE };
        }
        return new int[]{};
    }
}
