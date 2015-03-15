package com.aumum.app.mobile.core.dao.entity;

/**
 * Created by Administrator on 16/03/2015.
 */
public class PartyRequestEntity extends BaseEntity {

    private String userId;
    private String city;
    private String area;
    private String type;
    private String subType;

    public PartyRequestEntity() {

    }

    public PartyRequestEntity(String objectId,
                              java.util.Date createdAt,
                              String userId,
                              String city,
                              String area,
                              String type,
                              String subType) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.userId = userId;
        this.city = city;
        this.area = area;
        this.type = type;
        this.subType = subType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }
}
