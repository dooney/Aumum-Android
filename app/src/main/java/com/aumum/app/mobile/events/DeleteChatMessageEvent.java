package com.aumum.app.mobile.events;

/**
 * Created by Administrator on 10/04/2015.
 */
public class DeleteChatMessageEvent {

    private String messageId;

    public String getMessageId() {
        return messageId;
    }

    public DeleteChatMessageEvent(String messageId) {
        this.messageId = messageId;
    }
}
