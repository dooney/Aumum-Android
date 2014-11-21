package com.aumum.app.mobile.utils;

import android.content.Context;
import android.util.Log;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;

/**
 * Created by Administrator on 10/11/2014.
 */
public class EMChatUtils {

    public static void init(Context context) {
        EMChat.getInstance().init(context);
        EMChat.getInstance().setDebugMode(true);

        // 获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
        options.setUseRoster(true);
        // 设置收到消息是否有新消息通知(声音和震动提示)，默认为true
        options.setNotifyBySoundAndVibrate(false);
        // 设置收到消息是否有声音提示，默认为true
        options.setNoticeBySound(false);
        // 设置收到消息是否震动 默认为true
        options.setNoticedByVibrate(false);
        // 设置语音消息播放是否设置为扬声器播放 默认为true
        options.setUseSpeaker(false);
    }
}
