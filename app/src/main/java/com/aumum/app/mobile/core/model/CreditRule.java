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
    public static int ADD_ASKING_GROUP = 5;
    public static int ADD_ASKING = 6;
    public static int ADD_ASKING_REPLY = 7;
    public static int ADD_PARTY = 8;
    public static int ADD_PARTY_MEMBER = 9;
    public static int ADD_PARTY_COMMENT = 10;

    // negative credit
    public static int DELETE_CONTACT = 101;
    public static int DELETE_ASKING_GROUP = 102;
    public static int DELETE_ASKING = 103;
    public static int DELETE_ASKING_REPLY = 104;
    public static int DELETE_PARTY = 105;
    public static int DELETE_PARTY_MEMBER = 106;
    public static int DELETE_PARTY_COMMENT = 107;

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
