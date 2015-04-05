package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 5/04/2015.
 */
public class CreditRule extends AggregateRoot {

    private int seq;
    private int credit;
    private String description;

    // positive credit
    public static int ADD_AVATAR = 1;
    public static int ADD_TAGS = 2;
    public static int ADD_ABOUT = 3;
    public static int ADD_CONTACT = 4;

    // negative credit
    public static int REMOVE_CONTACT = 101;

    public CreditRule(String objectId,
                      int seq,
                      int credit,
                      String description) {
        this.objectId = objectId;
        this.seq = seq;
        this.credit = credit;
        this.description = description;
    }

    public int getSeq() {
        return seq;
    }

    public int getCredit() {
        return credit;
    }

    public String getDescription() {
        return description;
    }
}
