package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 31/12/2014.
 */
public class Report extends AggregateRoot {
    private String entityType;
    private String entityId;
    private String type;
    private String details;

    public Report(String entityType,
                  String entityId,
                  String type,
                  String details) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.type = type;
        this.details = details;
    }
}
