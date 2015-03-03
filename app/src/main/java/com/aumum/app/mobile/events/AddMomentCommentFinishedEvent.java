package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 3/03/2015.
 */
public class AddMomentCommentFinishedEvent {

    private String content;

    public AddMomentCommentFinishedEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
