package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 27/11/2014.
 */
public class Asking extends AggregateRoot {

    private String userId;
    private int category;
    private String question;

    public Asking(String userId,
                  int category,
                  String question) {
        this.userId = userId;
        this.category = category;
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }


}
