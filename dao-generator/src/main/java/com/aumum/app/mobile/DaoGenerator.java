package com.aumum.app.mobile;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {

    private static final int SCHEMA_VERSION = 35;

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(SCHEMA_VERSION, "com.aumum.app.mobile.core.dao.gen");

        addUser(schema);
        addUserInfo(schema);
        addMoment(schema);
        addContactRequest(schema);
        addMomentLike(schema);
        addMomentComment(schema);

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

    private static void addContactRequest(Schema schema) {
        Entity request = schema.addEntity("ContactRequestEntity");
        request.setSuperclass("MessageEntity");
        request.addStringProperty("userId").notNull().primaryKey();
        request.addDateProperty("createdAt").notNull();
        request.addStringProperty("info");
        request.addBooleanProperty("isRead");
    }

    private static void addMomentLike(Schema schema) {
        Entity request = schema.addEntity("MomentLikeEntity");
        request.setSuperclass("MessageEntity");
        request.addIdProperty();
        request.addStringProperty("userId").notNull();
        request.addDateProperty("createdAt").notNull();
        request.addStringProperty("momentId").notNull();
        request.addBooleanProperty("isRead");
    }

    private static void addMomentComment(Schema schema) {
        Entity request = schema.addEntity("MomentCommentEntity");
        request.setSuperclass("MessageEntity");
        request.addIdProperty();
        request.addStringProperty("userId").notNull();
        request.addDateProperty("createdAt").notNull();
        request.addStringProperty("momentId").notNull();
        request.addStringProperty("content");
        request.addBooleanProperty("isRead");
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
}