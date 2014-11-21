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
        return list;
    }

    public List<EMGroup> getAllPublicGroups() throws EaseMobException {
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

    public void applyJoinToGroup(String groupId) throws EaseMobException {
        EMGroupManager.getInstance().applyJoinToGroup(groupId, "求加入");
    }

    public void joinGroup(String groupId) throws EaseMobException {
        EMGroupManager.getInstance().joinGroup(groupId);
    }

    public void quitGroup(String groupId) throws Exception {
        EMGroupManager.getInstance().exitFromGroup(groupId);
    }

    public void addTextMessage(String from, String receipt, boolean isGroup, String text) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        message.setAttribute("userId", from);
        // 如果是群聊，设置chat type,默认是单聊
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        TextMessageBody txtBody = new TextMessageBody(text);
        // 设置消息body
        message.addBody(txtBody);
        // 设置要发给谁,用户username或者群聊group id
        message.setReceipt(receipt);
        // update conversation
        EMConversation conversation = EMChatManager.getInstance().getConversation(receipt);
        conversation.addMessage(message);
    }

    public EMConversation getConversation(String id) {
        return EMChatManager.getInstance().getConversation(id);
    }

    public void sendMessage(EMMessage message, EMCallBack callBack) {
        // 调用sdk发送异步发送方法
        EMChatManager.getInstance().sendMessage(message, callBack);
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

    public String getAckMessageBroadcastAction() {
        return EMChatManager.getInstance().getAckMessageBroadcastAction();
    }

    public void setAppInitialized() {
        EMChat.getInstance().setAppInited();
    }

    public void acceptInvitation(String userId) throws Exception {
        EMChatManager.getInstance().acceptInvitation(userId);
    }

    public void refuseInvitation(String userId) throws Exception {
        EMChatManager.getInstance().refuseInvitation(userId);
    }

    public void setGroupChangeListener(GroupChangeListener groupChangeListener) {
        EMGroupManager.getInstance().addGroupChangeListener(groupChangeListener);
    }
}
