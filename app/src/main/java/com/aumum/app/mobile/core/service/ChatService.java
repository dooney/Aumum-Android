package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.CmdMessage;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Administrator on 10/11/2014.
 */
public class ChatService {

    public interface OnAuthenticateListener {
        void onSuccess();
        void onError(String message);
    }

    public void createAccount(String userName, String password) throws Exception {
        EMChatManager.getInstance().createAccountOnServer(userName, password);
    }

    public void authenticate(String userName, String password,
                             final OnAuthenticateListener listener) {
        EMChatManager.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                loadAllResources();
                listener.onSuccess();
            }

            @Override
            public void onError(int code, String message) {
                listener.onError(message);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    public void loadAllResources() {
        EMGroupManager.getInstance().loadAllGroups();
        EMChatManager.getInstance().loadAllConversations();
    }

    public void setConnectionListener(EMConnectionListener emConnectionListener) {
        EMChatManager.getInstance().addConnectionListener(emConnectionListener);
    }

    public void logOut() {
        EMChatManager.getInstance().logout();
    }

    private void sortConversationByLastChatTime(List<EMConversation> conversationList) {
        Collections.sort(conversationList, new Comparator<EMConversation>() {
            @Override
            public int compare(final EMConversation con1, final EMConversation con2) {
                EMMessage con2LastMessage = con2.getLastMessage();
                EMMessage con1LastMessage = con1.getLastMessage();
                if (con1LastMessage == null) {
                    return 1;
                }
                if (con2LastMessage == null) {
                    return -1;
                }
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
            list.add(conversation);
        }
        sortConversationByLastChatTime(list);
        return list;
    }

    public void deleteConversation(String userId) {
        EMChatManager.getInstance().deleteConversation(userId);
    }

    public boolean deleteGroupConversation(String groupId) {
        return EMChatManager.getInstance().deleteConversation(groupId, true);
    }

    public void clearConversation(String userName) {
        EMChatManager.getInstance().clearConversation(userName);
    }

    public EMGroup createGroup(String groupName) throws Exception {
        String members[] = {};
        EMGroup emGroup = EMGroupManager.getInstance().createPublicGroup(groupName, "", members, false);
        return EMGroupManager.getInstance().createOrUpdateLocalGroup(emGroup);
    }

    public void deleteGroup(String groupId) throws Exception {
        EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
    }

    public EMGroup getGroupById(String groupId) throws Exception {
        EMGroup emGroup = EMGroupManager.getInstance().getGroup(groupId);
        if (emGroup == null) {
            emGroup = EMGroupManager.getInstance().getGroupFromServer(groupId);
            return EMGroupManager.getInstance().createOrUpdateLocalGroup(emGroup);
        }
        return emGroup;
    }

    public void joinGroup(String groupId, String userId) throws Exception {
        EMGroupManager.getInstance().joinGroup(groupId);
        addGroupMember(groupId, userId);
    }

    public void quitGroup(String groupId, String userId) throws Exception {
        EMGroupManager.getInstance().exitFromGroup(groupId);
        removeGroupMember(groupId, userId);
    }

    public void addGroupMember(String groupId, String userId) {
        EMGroup emGroup = EMGroupManager.getInstance().getGroup(groupId);
        if (emGroup != null && !emGroup.getMembers().contains(userId)) {
            emGroup.addMember(userId);
        }
    }

    public void removeGroupMember(String groupId, String userId) {
        EMGroup emGroup = EMGroupManager.getInstance().getGroup(groupId);
        if (emGroup != null && emGroup.getMembers().contains(userId)) {
            emGroup.removeMember(userId);
        }
    }

    public void setGroupChangeListener(GroupChangeListener listener) {
        EMGroupManager.getInstance().addGroupChangeListener(listener);
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

    public EMMessage addTextMessage(String receipt, boolean isGroup, String text) {
        return addTextMessage(receipt, isGroup, false, text);
    }

    public void sendSystemMessage(String receipt, boolean isGroup, String text, EMCallBack callBack) {
        EMMessage message = addTextMessage(receipt, isGroup, true, text);
        sendMessage(message, callBack);
    }

    public EMMessage addVoiceMessage(String receipt, boolean isGroup, String filePath, int length) {
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        VoiceMessageBody body = new VoiceMessageBody(new File(filePath), length);
        message.addBody(body);
        message.setReceipt(receipt);
        return message;
    }

    public EMMessage addImageMessage(String receipt, boolean isGroup, String imagePath) {
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        ImageMessageBody body = new ImageMessageBody(new File(imagePath));
        message.addBody(body);
        message.setReceipt(receipt);
        return message;
    }

    public void sendCmdMessage(String receipt, CmdMessage cmdMessage, boolean isGroup, EMCallBack callBack) {
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        if (isGroup) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        CmdMessageBody body = new CmdMessageBody("cmd");
        message.addBody(body);
        message.setReceipt(receipt);
        message.setAttribute("payload", cmdMessage.toString());
        sendMessage(message, callBack);
    }

    public CmdMessage getCmdMessage(EMMessage message) throws Exception {
        String payload = message.getStringAttribute("payload");
        Gson gson = new Gson();
        return gson.fromJson(payload, CmdMessage.class);
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

    public String getCmdMessageBroadcastAction() {
        return EMChatManager.getInstance().getCmdMessageBroadcastAction();
    }

    public void setAppInitialized() {
        EMChat.getInstance().setAppInited();
    }

    public void acceptInvitation(String userId) throws Exception {
        EMChatManager.getInstance().acceptInvitation(userId);
    }

    public void setMessageNotifyListener(OnMessageNotifyListener messageNotifyListener) {
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.setNotifyText(messageNotifyListener);
    }

    public void setNotificationClickListener(OnNotificationClickListener notificationClickListener) {
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.setOnNotificationClickListener(notificationClickListener);
    }
}
