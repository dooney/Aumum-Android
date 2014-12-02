package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 3/12/2014.
 */
public class ReplyAskingReplyEvent {

    private String replyHint;

    public ReplyAskingReplyEvent(String replyHint) {
        this.replyHint = replyHint;
    }

    public String getReplyHint() {
        return replyHint;
    }
}
