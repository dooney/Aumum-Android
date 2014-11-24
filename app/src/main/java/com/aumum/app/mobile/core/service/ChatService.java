package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.utils.Ln;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Administrator on 10/11/2014.
 */
public class ChatService {

    public void createAccount(String userName, String password) throws Exception {
        EMChatManager.getInstance().createAccountOnServer(userName, password);
    }

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
        if (getCurrentUser() != null) {
            EMChatManager.getInstance().logout();
        }
    }

    public String getCurrentUser() {
        try {
            return EMChatManager.getInstance().getCurrentUser();
        } catch (Exception e) {
            Ln.e(e);
        }
        return null;
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

    public List<EMConversation> getAllConversations() {
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (EMConversation conversation : conversations.values()) {
            if (conversation.getAllMessages().size() > 0) {
                list.add(conversation);
            }
        }
        // 排序
        sortConversationByLastChatTime(list);
        return list;
    }

    public void deleteGroupConversation(String groupId) {
        EMChatManager.getInstance().deleteConversation(groupId, true);
    }

    public List<EMGroup> getAllPublicGroups() throws Exception {
        ArrayList<EMGroup> result = new ArrayList<EMGroup>();
        List<EMGroupInfo> list = EMGroupManager.getInstance().getAllPublicGroupsFromServer();
        for (EMGroupInfo groupInfo: list) {
            result.add(EMGroupManager.getInstance().getGroupFromServer(groupInfo.getGroupId()));
        }
        return result;
    }

    public EMGroup getGroupById(String groupId) {
        return EMGroupManager.getInstance().getGroup(groupId);
    }

    public void applyJoinToGroup(String groupId) throws Exception {
        EMGroupManager.getInstance().applyJoinToGroup(groupId, "求加入");
    }

    public void joinGroup(String groupId) throws Exception {
        EMGroupManager.getInstance().joinGroup(groupId);
    }

    public void quitGroup(String groupId) throws Exception {
        EMGroupManager.getInstance().exitFromGroup(groupId);
    }

    private EMMessage addTextMessage(String receipt, boolean isGroup, boolean isSystem, String text) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        if (isSystem) {
            message.setAttribute("isSystem", true);
        }
        TextMessageBody txtBody = new TextMessageBody(text);
        message.addBody(txtBody);
        message.setReceipt(receipt);
        EMConversation conversation = EMChatManager.getInstance().getConversation(receipt);
        conversation.addMessage(message);
        return message;
    }

    public void addTextMessage(String receipt, boolean isGroup, String text) {
        addTextMessage(receipt, isGroup, false, text);
    }

    public void sendSystemMessage(String receipt, boolean isGroup, String text, EMCallBack callBack) {
        EMMessage message = addTextMessage(receipt, isGroup, true, text);
        EMChatManager.getInstance().sendMessage(message, callBack);
    }

    public EMConversation getConversation(String id) {
        return EMChatManager.getInstance().getConversation(id);
    }

    public void sendMessage(EMMessage message, EMCallBack callBack) {
        EMChatManager.getInstance().sendMessage(message, callBack);
    }

    public EMMessage getMessage(String msgId) {
        return EMChatManager.getInstance().getMessage(msgId);
    }

    public void addContact(String toUserId, String reason) throws Exception {
        EMContactManager.getInstance().addContact(toUserId, reason);
    }

    public void deleteContact(String userId) throws Exception {
        EMContactManager.getInstance().deleteContact(userId);
    }

    public void setContactListener(EMContactListener contactListener) {
        EMContactManager.getInstance().setContactListener(contactListener);
    }

    public String getNewMessageBroadcastAction() {
        return EMChatManager.getInstance().getNewMessageBroadcastAction();
    }

    public void setAppInitialized() {
        EMChat.getInstance().setAppInited();
    }

    public void acceptInvitation(String userId) throws Exception {
        EMChatManager.getInstance().acceptInvitation(userId);
    }

    public void setGroupChangeListener(GroupChangeListener groupChangeListener) {
        EMGroupManager.getInstance().addGroupChangeListener(groupChangeListener);
    }
}
