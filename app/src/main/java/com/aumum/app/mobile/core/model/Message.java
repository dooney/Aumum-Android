package com.aumum.app.mobile.core.model;

import com.google.gson.Gson;

/**
 * Created by Administrator on 7/10/2014.
 */
public class Message extends AggregateRoot {
    private String fromUserId;
    private String toUserId;
    private int type;
    private String content;
    private String parent;
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

    public static class SubCategory {
        public static final int PARTY_MEMBERSHIP = 101;
        public static final int PARTY_COMMENTS = 102;
        public static final int PARTY_LIKES = 103;
    }

    public static class Category {
        public static final int PARTY = 1001;
    }

    private static final String ACTION_OPTIONS[] = {
        "该消息已删除",
        "关注了您",
        "报名了聚会",
        "退出了聚会",
        "支持了聚会",
        "发表了评论",
        "回复了评论",
        "删除了聚会"
    };

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

    public void setType(int type) {
        this.type = type;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageParent getParent() {
        if (parent != null) {
            Gson gson = new Gson();
            return gson.fromJson(parent, MessageParent.class);
        }
        return null;
    }

    public void setParent(MessageParent object) {
        if (object != null) {
            Gson gson = new Gson();
            this.parent = gson.toJson(object);
        }
    }

    public static int[] getSubCategoryTypes(int subCategory) {
        switch (subCategory) {
            case SubCategory.PARTY_MEMBERSHIP:
                return new int[]{ Type.PARTY_JOIN, Type.PARTY_QUIT };
            case SubCategory.PARTY_COMMENTS:
                return new int[]{ Type.PARTY_COMMENT, Type.PARTY_REPLY };
            case SubCategory.PARTY_LIKES:
                return new int[]{ Type.PARTY_LIKE };
        }
        return new int[]{};
    }

    public String getActionText() {
        return ACTION_OPTIONS[type];
    }
}
