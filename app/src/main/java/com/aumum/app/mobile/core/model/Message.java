package com.aumum.app.mobile.core.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 7/10/2014.
 */
public class Message extends AggregateRoot {
    private String fromUserId;
    private String toUserId;
    private int type;
    private String content;
    private String parent;

    private User user;

    public Message() {

    }

    public Message(String objectId,
                   String createdAt,
                   int type,
                   String fromUserId,
                   String toUserId,
                   String content,
                   String parent) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.type = type;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.content = content;
        this.parent = parent;
    }

    public Message(int type,
                   String fromUserId,
                   String toUserId,
                   String content,
                   String parentId,
                   String parentTitle) {
        this.type = type;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.content = content;
        if (parentId != null) {
            MessageParent parent = new MessageParent();
            parent.setObjectId(parentId);
            parent.setContent(parentTitle);
            setParent(parent);
        }
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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class Type {
        public static final int DELETED = 0;
        public static final int USER_FOLLOW = 1;
        public static final int PARTY_NEW = 2;
        public static final int PARTY_JOIN = 3;
        public static final int PARTY_QUIT = 4;
        public static final int PARTY_LIKE = 5;
        public static final int PARTY_COMMENT = 6;
        public static final int PARTY_REPLY = 7;
        public static final int PARTY_DELETE = 8;
        public static final int PARTY_CHECK_IN = 9;
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
            "发布了新聚会",
            "报名了聚会",
            "退出了聚会",
            "支持了聚会",
            "发表了聚会评论",
            "回复了聚会评论",
            "删除了聚会",
            "在聚会签到"
    };

    public MessageParent getMessageParent() {
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

    public static List<Integer> getSubCategoryTypes(int subCategory) {
        ArrayList<Integer> types = new ArrayList<Integer>();
        switch (subCategory) {
            case SubCategory.PARTY_MEMBERSHIP:
                types.add(Type.PARTY_JOIN);
                types.add(Type.PARTY_QUIT);
                return types;
            case SubCategory.PARTY_COMMENTS:
                types.add(Type.PARTY_COMMENT);
                types.add(Type.PARTY_REPLY);
                return types;
            case SubCategory.PARTY_LIKES:
                types.add(Type.PARTY_LIKE);
                return types;
            default:
                return types;
        }
    }

    public String getActionText() {
        return ACTION_OPTIONS[type];
    }
}
