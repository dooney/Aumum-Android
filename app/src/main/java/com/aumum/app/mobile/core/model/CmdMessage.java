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
        public static final int GROUP_JOIN = 11;
        public static final int GROUP_QUIT = 12;
        public static final int MOMENT_LIKE = 21;
        public static final int MOMENT_COMMENT = 22;
    }
}
