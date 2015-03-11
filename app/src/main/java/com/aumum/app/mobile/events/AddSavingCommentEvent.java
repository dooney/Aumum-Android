package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 12/03/2015.
 */
public class AddSavingCommentEvent {

    private String repliedId;
    private String content;

    public AddSavingCommentEvent(String repliedId, String content) {
        this.repliedId = repliedId;
        this.content = content;
    }

    public String getRepliedId() {
        return repliedId;
    }

    public String getContent() {
        return content;
    }
}
