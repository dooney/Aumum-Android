package com.aumum.app.mobile.core.model;

import com.google.gson.Gson;

/**
 * Created by Administrator on 28/12/2014.
 */
public class CmdMessage {
    private int type;
    private String title;
    private String content;
    private String payload;

    public CmdMessage(int type, String title, String content, String payload) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.payload = payload;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getPayload() {
        return payload;
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static class Type {
        public static final int DELETED = 0;
        public static final int PARTY_NEW = 1;
        public static final int PARTY_JOIN = 2;
        public static final int PARTY_QUIT = 3;
        public static final int PARTY_LIKE = 4;
        public static final int PARTY_COMMENT = 5;
        public static final int PARTY_REPLY = 6;
        public static final int PARTY_CANCEL = 7;
        public static final int PARTY_COMMENT_LIKE = 8;
        public static final int GROUP_JOIN = 11;
        public static final int GROUP_QUIT = 12;
        public static final int USER_NEW = 21;
        public static final int MOMENT_NEW = 31;
        public static final int MOMENT_LIKE = 32;
        public static final int MOMENT_COMMENT = 33;
        public static final int MOMENT_REPLY = 34;
        public static final int MOMENT_COMMENT_LIKE = 35;
    }
}
