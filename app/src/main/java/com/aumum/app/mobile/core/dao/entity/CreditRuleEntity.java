package com.aumum.app.mobile.core.dao.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CREDIT_RULE_ENTITY.
 */
public class CreditRuleEntity extends BaseEntity  {

    /** Not-null value. */
    private String objectId;
    private Integer seq;
    private Integer credit;
    private String description;

    public CreditRuleEntity() {
    }

    public CreditRuleEntity(String objectId) {
        this.objectId = objectId;
    }

    public CreditRuleEntity(String objectId, Integer seq, Integer credit, String description) {
        this.objectId = objectId;
        this.seq = seq;
        this.credit = credit;
        this.description = description;
    }

    /** Not-null value. */
    public String getObjectId() {
        return objectId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
