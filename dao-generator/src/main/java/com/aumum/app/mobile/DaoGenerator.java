package com.aumum.app.mobile;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {

    private static final int SCHEMA_VERSION = 1;

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(SCHEMA_VERSION, "com.aumum.app.mobile.core.dao.gen");

        addMessage(schema);

        new de.greenrobot.daogenerator.DaoGenerator().generateAll(schema, args[0]);
    }

    private static void addMessage(Schema schema) {
        Entity note = schema.addEntity("MessageVM");
        note.setSuperclass("BaseVM");
        note.addIdProperty();
        note.addStringProperty("objectId").notNull();
        note.addDateProperty("createdAt").notNull();
        note.addStringProperty("fromUserId");
        note.addStringProperty("toUserId");
        note.addIntProperty("type");
        note.addStringProperty("content");
        note.addStringProperty("parent");
    }
}