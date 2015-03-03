package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 3/03/2015.
 */
public class AddMomentCommentEvent {

    private String repliedId;
    private String content;

    public AddMomentCommentEvent(String repliedId, String content) {
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
