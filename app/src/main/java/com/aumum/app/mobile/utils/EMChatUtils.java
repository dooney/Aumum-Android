package com.aumum.app.mobile.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Administrator on 10/11/2014.
 */
public class EMChatUtils {

    private static ArrayList<Activity> activities = new ArrayList<>();
    private static ArrayList<CmdMessage> cmdMessageQueue = new ArrayList<>();

    private static void notifyOnNewMessage(Context context,
                                           EMMessage message) {
        String content = "";
        if (message.getType() == EMMessage.Type.TXT) {
            TextMessageBody messageBody = (TextMessageBody) message.getBody();
            content += messageBody.getMessage();
        } else if (message.getType() == EMMessage.Type.IMAGE) {
            content += "["+ context.getString(R.string.label_image) +"]";
        }
        String title = message.getStringAttribute("screenName",
                context.getString(R.string.info_you_have_new_messages));
        Intent intent = new Intent();
        intent.putExtra(ChatActivity.INTENT_ID, message.getFrom());
        intent.putExtra(ChatActivity.INTENT_TITLE, title);
        intent.setComponent(new ComponentName(context, ChatActivity.class));
        NotificationUtils.notify(context, title, content, null, intent);
    }

    public static void init(final Context context) {
        EMChat.getInstance().init(context);
        EMChat.getInstance().setDebugMode(false);

        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.setAcceptInvitationAlways(false);
        options.setUseRoster(true);

        EMChatManager.getInstance().registerEventListener(new EMEventListener() {
            @Override
            public void onEvent(EMNotifierEvent event) {
                switch (event.getEvent()) {
                    case EventNewMessage:
                        if (activities.isEmpty()) {
                            EMMessage message = (EMMessage) event.getData();
                            notifyOnNewMessage(context, message);
                            updateAppBadge(context);
                        }
                        break;
                    case EventNewCMDMessage:
                        if (activities.isEmpty()) {
                            EMMessage message = (EMMessage) event.getData();
                            CmdMessage cmdMessage = getCmdMessage(message);
                            if (cmdMessage != null) {
                                cmdMessageQueue.add(cmdMessage);
                            }
                        }
                        break;
                    case EventOfflineMessage:
                        if (activities.isEmpty()) {
                            List<EMMessage> offlineMessages = (List<EMMessage>) event.getData();
                            for (EMMessage message : offlineMessages) {
                                if (message.getType() == EMMessage.Type.CMD) {
                                    CmdMessage cmdMessage = getCmdMessage(message);
                                    cmdMessageQueue.add(cmdMessage);
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public static void pushActivity(Activity activity) {
        activities.add(activity);
    }

    public static void popActivity(Activity activity) {
        activities.remove(activity);
    }

    public static ArrayList<CmdMessage> getCmdMessageQueue() {
        return cmdMessageQueue;
    }

    public static void pushCmdMessageQueue(CmdMessage cmdMessage) {
        cmdMessageQueue.add(cmdMessage);
    }

    public static void clearCmdMessageQueue() {
        cmdMessageQueue.clear();
    }

    public static void updateAppBadge(Context context) {
        ShortcutBadger.with(context).count(
                EMChatManager.getInstance().getUnreadMsgsCount());
    }

    public interface OnAuthenticateListener {
        void onSuccess();
        void onError(String message);
    }

    public static void createAccount(String userName, String password) throws Exception {
        EMChatManager.getInstance().createAccountOnServer(userName, password);
    }

    public static void authenticate(String userName, String password,
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

    public static void loadAllResources() {
        EMChatManager.getInstance().loadAllConversations();
    }

    public static String getChatId() {
        return EMChatManager.getInstance().getCurrentUser();
    }

    public static void logOut() {
        if (EMChat.getInstance().isLoggedIn()) {
            EMChatManager.getInstance().logout();
        }
    }

    private static void sortConversationByLastChatTime(List<EMConversation> conversationList) {
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

    public static List<EMConversation> getAllConversations() {
        List<EMConversation> list = EMChatManager.getInstance()
                .getConversationsByType(EMConversation.EMConversationType.Chat);
        sortConversationByLastChatTime(list);
        return list;
    }

    public static void deleteConversation(String userId) {
        EMChatManager.getInstance().deleteConversation(userId);
    }

    public static void deleteAllConversation() {
        EMChatManager.getInstance().deleteAllConversation();
    }

    public static void clearConversation(String userName) {
        EMChatManager.getInstance().clearConversation(userName);
    }

    private static EMMessage addTextMessage(String receipt, boolean isSystem, String text) {
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

    public static EMMessage addTextMessage(String receipt, String text) {
        return addTextMessage(receipt, false, text);
    }

    public static void sendSystemMessage(String receipt, String text) {
        EMMessage message = addTextMessage(receipt, true, text);
        sendMessage(message, null);
    }

    public static EMMessage addImageMessage(String receipt, String imagePath) {
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        ImageMessageBody body = new ImageMessageBody(new File(imagePath));
        message.addBody(body);
        message.setReceipt(receipt);
        return message;
    }

    public static void sendCmdMessage(String receipt, CmdMessage cmdMessage) {
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        CmdMessageBody body = new CmdMessageBody("cmd");
        message.addBody(body);
        message.setReceipt(receipt);
        message.setAttribute("type", cmdMessage.getType());
        message.setAttribute("userId", cmdMessage.getUserId());
        message.setAttribute("screenName", cmdMessage.getScreenName());
        message.setAttribute("avatarUrl", cmdMessage.getAvatarUrl());
        message.setAttribute("momentId", cmdMessage.getMomentId());
        message.setAttribute("imageUrl", cmdMessage.getImageUrl());
        if (cmdMessage.getContent() != null) {
            message.setAttribute("content", cmdMessage.getContent());
        }
        sendMessage(message, null);
    }

    public static CmdMessage getCmdMessage(EMMessage message) {
        try {
            String payload = message.getStringAttribute("payload");
            Gson gson = new Gson();
            return gson.fromJson(payload, CmdMessage.class);
        } catch (Exception e) {
            Ln.e(e);
        }
        return null;
    }

    public static EMConversation getConversation(String id) {
        return EMChatManager.getInstance()
                .getConversationByType(id, EMConversation.EMConversationType.Chat);
    }

    public static void sendMessage(EMMessage message, EMCallBack callBack) {
        EMChatManager.getInstance().sendMessage(message, callBack);
    }

    public static EMMessage getMessage(String msgId) {
        return EMChatManager.getInstance().getMessage(msgId);
    }

    public static void addContact(String toUserId, String reason) throws Exception {
        EMContactManager.getInstance().addContact(toUserId, reason);
    }

    public static void deleteContact(String userId) throws Exception {
        EMContactManager.getInstance().deleteContact(userId);
    }

    public static void setContactListener(EMContactListener contactListener) {
        EMContactManager.getInstance().setContactListener(contactListener);
    }

    public static void setAppInitialized() {
        EMChat.getInstance().setAppInited();
    }

    public static void acceptInvitation(String userId) throws Exception {
        EMChatManager.getInstance().acceptInvitation(userId);
    }
}