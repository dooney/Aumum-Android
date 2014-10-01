package com.aumum.app.mobile.core;

import java.util.ArrayList;
import java.util.List;

public class User extends AggregateRoot {
    protected String sessionToken;
    protected Boolean emailVerified;
    protected int area;
    protected List<String> partyList;

    public String getSessionToken() {
        return sessionToken;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public int getArea() {
        return area;
    }

    public List<String> getPartyList() {
        if (partyList == null)
            return new ArrayList<String>();
        return partyList;
    }
}
