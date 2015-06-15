package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.CmdMessage;
import com.easemob.EMCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.google.gson.Gson;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
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
        EMChatManager.getInstance().loadAllConversations();
    }

    public String getChatId() {
        return EMChatManager.getInstance().getCurrentUser();
    }

    public void logOut() {
        if (EMChat.getInstance().isLoggedIn()) {
            EMChatManager.getInstance().logout();
        }
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
        List<EMConversation> list = EMChatManager.getInstance()
                .getConversationsByType(EMConversation.EMConversationType.Chat);
        sortConversationByLastChatTime(list);
        return list;
    }

    public void deleteConversation(String userId) {
        EMChatManager.getInstance().deleteConversation(userId);
    }

    public void deleteAllConversation() {
        EMChatManager.getInstance().deleteAllConversation();
    }

    public void clearConversation(String userName) {
        EMChatManager.getInstance().clearConversation(userName);
    }

    private EMMessage addTextMessage(String receipt, boolean isSystem, String text) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
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

    public EMMessage addTextMessage(String receipt, String text) {
        return addTextMessage(receipt, false, text);
    }

    public void sendSystemMessage(String receipt, String text) {
        EMMessage message = addTextMessage(receipt, true, text);
        sendMessage(message, null);
    }

    public EMMessage addImageMessage(String receipt, String imagePath) {
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        ImageMessageBody body = new ImageMessageBody(new File(imagePath));
        message.addBody(body);
        message.setReceipt(receipt);
        return message;
    }

    public void sendCmdMessage(String receipt, CmdMessage cmdMessage) {
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        CmdMessageBody body = new CmdMessageBody("cmd");
        message.addBody(body);
        message.setReceipt(receipt);
        message.setAttribute("payload", cmdMessage.toString());
        sendMessage(message, null);
    }

    public CmdMessage getCmdMessage(EMMessage message) throws Exception {
        String payload = message.getStringAttribute("payload");
        Gson gson = new Gson();
        return gson.fromJson(payload, CmdMessage.class);
    }

    public EMConversation getConversation(String id) {
        return EMChatManager.getInstance()
                .getConversationByType(id, EMConversation.EMConversationType.Chat);
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

    public void setAppInitialized() {
        EMChat.getInstance().setAppInited();
    }

    public void acceptInvitation(String userId) throws Exception {
        EMChatManager.getInstance().acceptInvitation(userId);
    }
}
