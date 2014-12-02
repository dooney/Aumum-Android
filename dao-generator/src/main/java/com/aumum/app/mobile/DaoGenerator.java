package com.aumum.app.mobile;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {

    private static final int SCHEMA_VERSION = 1;

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(SCHEMA_VERSION, "com.aumum.app.mobile.core.dao.gen");

        addMessage(schema);
        addUser(schema);
        addParty(schema);
        addContactRequest(schema);
        addAsking(schema);

        new de.greenrobot.daogenerator.DaoGenerator().generateAll(schema, args[0]);
    }

    private static void addMessage(Schema schema) {
        Entity message = schema.addEntity("MessageEntity");
        message.setSuperclass("BaseEntity");
        message.addStringProperty("objectId").notNull().primaryKey();
        message.addDateProperty("createdAt").notNull();
    }

    private static void addUser(Schema schema) {
        Entity user = schema.addEntity("UserEntity");
        user.setSuperclass("BaseEntity");
        user.addStringProperty("objectId").notNull().primaryKey();
        user.addStringProperty("chatId").notNull();
        user.addDateProperty("createdAt").notNull();
        user.addStringProperty("screenName");
        user.addIntProperty("area");
        user.addStringProperty("avatarUrl");
        user.addStringProperty("about");
        user.addStringProperty("contacts");
        user.addStringProperty("messages");
        user.addStringProperty("parties");
        user.addStringProperty("askings");
        user.addStringProperty("favParties");
        user.addStringProperty("favAskings");
    }

    private static void addParty(Schema schema) {
        Entity party = schema.addEntity("PartyEntity");
        party.setSuperclass("BaseEntity");
        party.addStringProperty("objectId").notNull().primaryKey();
        party.addDateProperty("createdAt").notNull();
        party.addStringProperty("userId");
        party.addStringProperty("date");
        party.addStringProperty("time");
        party.addIntProperty("age");
        party.addIntProperty("gender");
        party.addStringProperty("title");
        party.addStringProperty("place");
        party.addStringProperty("details");
        party.addStringProperty("members");
        party.addStringProperty("fans");
        party.addStringProperty("comments");
        party.addStringProperty("reasons");
        party.addStringProperty("favorites");
    }

    private static void addContactRequest(Schema schema) {
        Entity request = schema.addEntity("ContactRequestEntity");
        request.addIdProperty();
        request.addStringProperty("userId").notNull();
        request.addStringProperty("intro");
    }

    private static void addAsking(Schema schema) {
        Entity asking = schema.addEntity("AskingEntity");
        asking.setSuperclass("BaseEntity");
        asking.addStringProperty("objectId").notNull().primaryKey();
        asking.addDateProperty("createdAt").notNull();
        asking.addDateProperty("updatedAt").notNull();
        asking.addStringProperty("userId");
        asking.addIntProperty("category");
        asking.addStringProperty("question");
        asking.addStringProperty("replies");
        asking.addStringProperty("favorites");
    }
}