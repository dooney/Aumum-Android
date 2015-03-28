package com.aumum.app.mobile;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {

    private static final int SCHEMA_VERSION = 12;

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(SCHEMA_VERSION, "com.aumum.app.mobile.core.dao.gen");

        addUser(schema);
        addParty(schema);
        addContactRequest(schema);
        addAsking(schema);
        addMoment(schema);
        addPartyRequest(schema);
        addGroupRequest(schema);

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
        user.addIntProperty("credit");
    }

    private static void addParty(Schema schema) {
        Entity party = schema.addEntity("PartyEntity");
        party.setSuperclass("BaseEntity");
        party.addStringProperty("objectId").notNull().primaryKey();
        party.addDateProperty("createdAt").notNull();
        party.addStringProperty("userId");
        party.addStringProperty("title");
        party.addStringProperty("date");
        party.addStringProperty("time");
        party.addStringProperty("address");
        party.addDoubleProperty("latitude");
        party.addDoubleProperty("longitude");
        party.addStringProperty("location");
        party.addStringProperty("details");
        party.addStringProperty("groupId");
        party.addStringProperty("members");
        party.addStringProperty("likes");
        party.addStringProperty("comments");
        party.addStringProperty("reasons");
        party.addStringProperty("favorites");
        party.addStringProperty("images");
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

    private static void addAsking(Schema schema) {
        Entity asking = schema.addEntity("AskingEntity");
        asking.setSuperclass("BaseEntity");
        asking.addStringProperty("objectId").notNull().primaryKey();
        asking.addDateProperty("createdAt").notNull();
        asking.addDateProperty("updatedAt").notNull();
        asking.addStringProperty("userId");
        asking.addIntProperty("category");
        asking.addBooleanProperty("isAnonymous");
        asking.addStringProperty("title");
        asking.addStringProperty("details");
        asking.addStringProperty("replies");
        asking.addStringProperty("likes");
        asking.addStringProperty("favorites");
        asking.addStringProperty("images");
    }

    private static void addMoment(Schema schema) {
        Entity moment = schema.addEntity("MomentEntity");
        moment.setSuperclass("BaseEntity");
        moment.addStringProperty("objectId").notNull().primaryKey();
        moment.addDateProperty("createdAt").notNull();
        moment.addStringProperty("userId");
        moment.addStringProperty("details");
        moment.addStringProperty("images");
        moment.addStringProperty("likes");
        moment.addStringProperty("comments");
    }

    private static void addPartyRequest(Schema schema) {
        Entity partyRequest = schema.addEntity("PartyRequestEntity");
        partyRequest.setSuperclass("BaseEntity");
        partyRequest.addStringProperty("objectId").notNull().primaryKey();
        partyRequest.addDateProperty("createdAt").notNull();
        partyRequest.addStringProperty("userId");
        partyRequest.addStringProperty("city");
        partyRequest.addStringProperty("area");
        partyRequest.addStringProperty("type");
        partyRequest.addStringProperty("subType");
    }
}