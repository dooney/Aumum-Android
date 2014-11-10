package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.Conversation;
import com.aumum.app.mobile.core.model.Group;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Administrator on 10/11/2014.
 */
public class ChatService {

    public void authenticate(String userName, String password) {
        EMChatManager.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
            }

            @Override
            public void onError(int code, String message) {
                Ln.d(message);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    public void logOut() {
        EMChatManager.getInstance().logout();
    }

    /**
     * 根据最后一条消息的时间排序
     */
    private void sortConversationByLastChatTime(List<EMConversation> conversationList) {
        Collections.sort(conversationList, new Comparator<EMConversation>() {
            @Override
            public int compare(final EMConversation con1, final EMConversation con2) {
                EMMessage con2LastMessage = con2.getLastMessage();
                EMMessage con1LastMessage = con1.getLastMessage();
                if (con2LastMessage.getMsgTime() == con1LastMessage.getMsgTime()) {
                    return 0;
                } else if (con2LastMessage.getMsgTime() > con1LastMessage.getMsgTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    public List<Conversation> getAllConversations() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        List<EMConversation> list = new ArrayList<EMConversation>();
        // 过滤掉messages size为0的conversation
        for (EMConversation conversation : conversations.values()) {
            if (conversation.getAllMessages().size() != 0)
                list.add(conversation);
        }
        // 排序
        sortConversationByLastChatTime(list);
        ArrayList<Conversation> result = new ArrayList<Conversation>();
        for (EMConversation conversation: list) {
            result.add(buildConversation(conversation));
        }
        return result;
    }

    private Conversation buildConversation(EMConversation emConversation) {
        Conversation conversation = new Conversation();
        conversation.setScreenName(emConversation.getUserName());
        return conversation;
    }

    public List<Group> getAllPublicGroups(String userId) throws EaseMobException {
        ArrayList<Group> result = new ArrayList<Group>();
        List<EMGroupInfo> list = EMGroupManager.getInstance().getAllPublicGroupsFromServer();
        for (EMGroupInfo groupInfo: list) {
            result.add(buildGroupInfo(groupInfo, userId));
        }
        return result;
    }

    private Group buildGroupInfo(EMGroupInfo groupInfo, String userId) throws EaseMobException {
        EMGroup groupDetails = EMGroupManager.getInstance().getGroupFromServer(groupInfo.getGroupId());
        Group group = new Group();
        group.setScreenName(groupInfo.getGroupName());
        group.setObjectId(groupInfo.getGroupId());
        group.setCurrentSize(groupDetails.getMembers().size());
        group.setMember(groupDetails.getMembers().contains(userId.toLowerCase()));
        group.setMembersOnly(groupDetails.isMembersOnly());
        return group;
    }

    public void applyJoinToGroup(String groupId) throws EaseMobException {
        EMGroupManager.getInstance().applyJoinToGroup(groupId, "求加入");
    }

    public void joinGroup(String groupId) throws EaseMobException {
        EMGroupManager.getInstance().joinGroup(groupId);
    }

    public void sendText(String receipt, boolean isGroup, String text) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        // 如果是群聊，设置chat type,默认是单聊
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        TextMessageBody txtBody = new TextMessageBody(text);
        // 设置消息body
        message.addBody(txtBody);
        // 设置要发给谁,用户username或者群聊group id
        message.setReceipt(receipt);
    }
}
