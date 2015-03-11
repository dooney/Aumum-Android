package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 12/03/2015.
 */
public class AddSavingCommentFinishedEvent {

    private String content;

    public AddSavingCommentFinishedEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
