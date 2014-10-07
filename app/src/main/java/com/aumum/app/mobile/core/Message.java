package com.aumum.app.mobile.core;

/**
 * Created by Administrator on 7/10/2014.
 */
public class Message extends AggregateRoot {
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
