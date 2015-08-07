package com.aumum.app.mobile;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {

    private static final int SCHEMA_VERSION = 36;

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(SCHEMA_VERSION, "com.aumum.app.mobile.core.dao.gen");

        addUser(schema);
        addUserInfo(schema);
        addMoment(schema);
        addMessage(schema);

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
        user.addStringProperty("country");
        user.addStringProperty("city");
        user.addStringProperty("area");
        user.addStringProperty("avatarUrl");
        user.addStringProperty("about");
        user.addStringProperty("contacts");
    }

    private static void addUserInfo(Schema schema) {
        Entity user = schema.addEntity("UserInfoEntity");
        user.setSuperclass("BaseEntity");
        user.addStringProperty("objectId").notNull().primaryKey();
        user.addStringProperty("chatId").notNull();
        user.addDateProperty("createdAt").notNull();
        user.addStringProperty("screenName");
        user.addStringProperty("avatarUrl");
        user.addStringProperty("city");
        user.addIntProperty("credit");
    }

    private static void addMoment(Schema schema) {
        Entity moment = schema.addEntity("MomentEntity");
        moment.setSuperclass("BaseEntity");
        moment.addStringProperty("objectId").notNull().primaryKey();
        moment.addDateProperty("createdAt").notNull();
        moment.addStringProperty("userId").notNull();
        moment.addStringProperty("likes");
        moment.addStringProperty("text");
        moment.addStringProperty("imageUrl");
        moment.addIntProperty("hot");
    }

    private static void addMessage(Schema schema) {
        Entity message = schema.addEntity("MessageEntity");
        message.addIdProperty();
        message.addIntProperty("type");
        message.addStringProperty("userId").notNull();
        message.addDateProperty("createdAt").notNull();
        message.addStringProperty("screenName");
        message.addStringProperty("avatarUrl");
        message.addStringProperty("momentId");
        message.addStringProperty("imageUrl");
        message.addStringProperty("content");
        message.addBooleanProperty("isRead");
    }
}