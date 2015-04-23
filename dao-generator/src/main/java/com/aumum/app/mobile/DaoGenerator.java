package com.aumum.app.mobile;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {

    private static final int SCHEMA_VERSION = 19;

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(SCHEMA_VERSION, "com.aumum.app.mobile.core.dao.gen");

        addUser(schema);
        addContactRequest(schema);
        addGroupRequest(schema);
        addCreditRule(schema);

        new de.greenrobot.daogenerator.DaoGenerator().generateAll(schema, args[0]);
    }

    private static void addUser(Schema schema) {
        Entity user = schema.addEntity("UserEntity");
        user.setSuperclass("BaseEntity");
        user.addStringProperty("objectId").notNull().primaryKey();
        user.addStringProperty("username").notNull();
        user.addStringProperty("chatId").notNull();
        user.addDateProperty("createdAt").notNull();
        user.addStringProperty("screenName");
        user.addStringProperty("email");
        user.addStringProperty("city");
        user.addStringProperty("area");
        user.addStringProperty("avatarUrl");
        user.addStringProperty("about");
        user.addStringProperty("contacts");
        user.addStringProperty("parties");
        user.addStringProperty("askings");
        user.addStringProperty("favParties");
        user.addStringProperty("favAskings");
        user.addStringProperty("tags");
        user.addStringProperty("moments");
        user.addStringProperty("favSpecials");
        user.addStringProperty("askingGroups");
        user.addIntProperty("credit");
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
}