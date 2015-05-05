package com.aumum.app.mobile;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {

    private static final int SCHEMA_VERSION = 23;

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(SCHEMA_VERSION, "com.aumum.app.mobile.core.dao.gen");

        addUser(schema);
        addUserInfo(schema);
        addContactRequest(schema);
        addGroupRequest(schema);
        addCreditRule(schema);
        addMoment(schema);

        new de.greenrobot.daogenerator.DaoGenerator().generateAll(schema, args[0]);
    }

    private static void addUser(Schema schema) {
        Entity user = schema.addEntity("UserEntity");
        user.setSuperclass("BaseEntity");
        user.addStringProperty("objectId").notNull().primaryKey();
        user.addStringProperty("username");
        user.addStringProperty("chatId").notNull();
        user.addDateProperty("createdAt").notNull();
        user.addStringProperty("screenName");
        user.addStringProperty("email");
        user.addStringProperty("city");
        user.addStringProperty("area");
        user.addStringProperty("avatarUrl");
        user.addStringProperty("about");
        user.addStringProperty("contacts");
        user.addStringProperty("tags");
        user.addIntProperty("credit");
    }

    private static void addUserInfo(Schema schema) {
        Entity user = schema.addEntity("UserInfoEntity");
        user.setSuperclass("BaseEntity");
        user.addStringProperty("objectId").notNull().primaryKey();
        user.addStringProperty("chatId").notNull();
        user.addDateProperty("createdAt").notNull();
        user.addStringProperty("screenName");
        user.addStringProperty("avatarUrl");
    }

    private static void addContactRequest(Schema schema) {
        Entity request = schema.addEntity("ContactRequestEntity");
        request.addIdProperty();
        request.addStringProperty("userId").notNull();
        request.addStringProperty("intro");
    }

    private static void addGroupRequest(Schema schema) {
        Entity request = schema.addEntity("GroupRequestEntity");
        request.addIdProperty();
        request.addStringProperty("groupId").notNull();
        request.addStringProperty("userId").notNull();
        request.addStringProperty("reason");
        request.addIntProperty("status");
    }

    private static void addCreditRule(Schema schema) {
        Entity creditRule = schema.addEntity("CreditRuleEntity");
        creditRule.setSuperclass("BaseEntity");
        creditRule.addStringProperty("objectId").notNull().primaryKey();
        creditRule.addIntProperty("seq");
        creditRule.addIntProperty("credit");
        creditRule.addStringProperty("description");
    }

    private static void addMoment(Schema schema) {
        Entity moment = schema.addEntity("MomentEntity");
        moment.setSuperclass("BaseEntity");
        moment.addStringProperty("objectId").notNull().primaryKey();
        moment.addDateProperty("createdAt").notNull();
        moment.addStringProperty("userId");
        moment.addStringProperty("likes");
        moment.addStringProperty("comments");
        moment.addStringProperty("text");
        moment.addStringProperty("imageUrl");
    }
}