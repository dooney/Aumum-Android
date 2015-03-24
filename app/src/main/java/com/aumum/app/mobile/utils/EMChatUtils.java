package com.aumum.app.mobile.utils;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;

/**
 * Created by Administrator on 10/11/2014.
 */
public class EMChatUtils {

    public static void init(Context context) {
        EMChat.getInstance().init(context);
        EMChat.getInstance().setDebugMode(false);

        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.setAcceptInvitationAlways(false);
        Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        options.setNotifyRingUri(ringUri);
        options.setShowNotificationInBackgroud(true);
        options.setUseRoster(true);
    }
}
