package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 13/01/2015.
 */
public class Feedback extends AggregateRoot {

    private String details;
    private String userId;

    public Feedback(String details, String userId) {
        this.details = details;
        this.userId = userId;
    }
}
