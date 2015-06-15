package com.aumum.app.mobile.utils;

import android.app.Activity;
import android.content.Context;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;

import java.util.ArrayList;

/**
 * Created by Administrator on 10/11/2014.
 */
public class EMChatUtils {

    private static ArrayList<Activity> activities = new ArrayList<>();

    public static void init(Context context) {
        EMChat.getInstance().init(context);
        EMChat.getInstance().setDebugMode(false);

        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.setAcceptInvitationAlways(true);
        options.setUseRoster(true);

        EMChatManager.getInstance().registerEventListener(new EMEventListener() {
            @Override
            public void onEvent(EMNotifierEvent event) {
                switch (event.getEvent()) {
                    case EventNewMessage:
                        if (activities.isEmpty()) {

                        }
                        break;
                    case EventNewCMDMessage:
                        if (activities.isEmpty()) {

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
}
