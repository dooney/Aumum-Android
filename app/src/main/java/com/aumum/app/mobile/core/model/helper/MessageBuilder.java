package com.aumum.app.mobile.core.model.helper;

import com.aumum.app.mobile.core.dao.vm.MessageVM;
import com.aumum.app.mobile.core.dao.vm.UserVM;
import com.aumum.app.mobile.core.model.MessageParent;
import com.aumum.app.mobile.core.model.Party;

/**
 * Created by Administrator on 31/10/2014.
 */
public class MessageBuilder {

    private static MessageVM build(int type, UserVM fromUser, String toUserId, String content, String parentId, String parentTitle) {
        MessageVM message = new MessageVM();
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

    public static MessageVM buildPartyMessage(int type, UserVM fromUser, String toUserId, String content, Party party) {
        return build(type, fromUser, toUserId, content, party.getObjectId(), party.getTitle());
    }

    public static MessageVM buildUserMessage(int type, UserVM fromUser, String toUserId) {
        return build(type, fromUser, toUserId, null, null, null);
    }
}
