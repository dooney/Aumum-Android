package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 30/11/2014.
 */
public class AddAskingReplyEvent {

    private String reply;

    public AddAskingReplyEvent(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }
}
