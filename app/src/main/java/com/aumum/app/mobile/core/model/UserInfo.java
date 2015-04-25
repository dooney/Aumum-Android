package com.aumum.app.mobile.core.model;

import com.aumum.app.mobile.ui.view.sort.InitialSortable;
import com.aumum.app.mobile.utils.Strings;

/**
 * Created by Administrator on 25/04/2015.
 */
public class UserInfo extends AggregateRoot implements InitialSortable {

    protected String chatId;
    protected String screenName;
    protected String avatarUrl;

    public UserInfo() {

    }

    public UserInfo(String objectId,
                    String chatId,
                    String createdAt,
                    String screenName,
                    String avatarUrl) {
        this.objectId = objectId;
        this.chatId = chatId;
        this.createdAt = createdAt;
        this.screenName = screenName;
        this.avatarUrl = avatarUrl;
    }

    public String getChatId() {
        if (chatId != null) {
            return chatId;
        } else if (objectId != null) {
            return objectId.toLowerCase();
        }
        return null;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String getSortLetters() {
        String pinyin = Strings.getSelling(screenName);
        String sortString = pinyin.substring(0, 1).toUpperCase();
        if (sortString.matches("[A-Z]")) {
            return sortString.toUpperCase();
        } else {
            return "#";
        }
    }
}
