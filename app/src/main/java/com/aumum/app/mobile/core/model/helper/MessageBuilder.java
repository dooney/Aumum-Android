package com.aumum.app.mobile.core.model.helper;

import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.MessageParent;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;

/**
 * Created by Administrator on 31/10/2014.
 */
public class MessageBuilder {

    private static Message build(int type, User fromUser, String toUserId, String content, String parentId, String parentTitle) {
        Message message = new Message();
        message.setType(type);
        message.setFromUserId(fromUser.getObjectId());
        message.setFromUser(fromUser);
        message.setToUserId(toUserId);
        message.setContent(content);
        if (parentId != null) {
            MessageParent parent = new MessageParent();
            parent.setObjectId(parentId);
            parent.setContent(parentTitle);
            message.setParent(parent);
        }

        return message;
    }

    public static Message buildPartyMessage(int type, User fromUser, String toUserId, String content, Party party) {
        return build(type, fromUser, toUserId, content, party.getObjectId(), party.getTitle());
    }

    public static Message buildUserMessage(int type, User fromUser, String toUserId) {
        return build(type, fromUser, toUserId, null, null, null);
    }
}
