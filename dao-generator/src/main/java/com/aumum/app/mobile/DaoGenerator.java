package com.aumum.app.mobile;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {

    private static final int SCHEMA_VERSION = 1;

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(SCHEMA_VERSION, "com.aumum.app.mobile.core.dao.gen");

        addMessage(schema);
        addUser(schema);

        new de.greenrobot.daogenerator.DaoGenerator().generateAll(schema, args[0]);
    }

    private static void addMessage(Schema schema) {
        Entity message = schema.addEntity("MessageVM");
        message.setSuperclass("BaseVM");
        message.addIdProperty();
        message.addStringProperty("objectId").notNull();
        message.addDateProperty("createdAt").notNull();
        message.addStringProperty("fromUserId");
        message.addStringProperty("toUserId");
        message.addIntProperty("type");
        message.addStringProperty("content");
        message.addStringProperty("parent");
    }

    private static void addUser(Schema schema) {
        Entity user = schema.addEntity("UserVM");
        user.setSuperclass("BaseVM");
        user.addIdProperty();
        user.addStringProperty("objectId").notNull();
        user.addDateProperty("createdAt").notNull();
        user.addStringProperty("screenName");
        user.addIntProperty("area");
        user.addStringProperty("avatarUrl");
        user.addStringProperty("about");
        user.addStringProperty("followers");
        user.addStringProperty("followings");
        user.addStringProperty("comments");
        user.addStringProperty("messages");
        user.addStringProperty("parties");
        user.addStringProperty("partyPosts");
        user.addStringProperty("moments");
        user.addStringProperty("momentPosts");
    }
}