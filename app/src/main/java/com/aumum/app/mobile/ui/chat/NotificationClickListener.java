package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.content.Intent;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnNotificationClickListener;

import javax.inject.Inject;

/**
 * Created by Administrator on 19/12/2014.
 */
public class NotificationClickListener implements OnNotificationClickListener {

    @Inject UserStore userStore;
    @Inject ChatService chatService;
    private Activity activity;

    public NotificationClickListener(Activity activity) {
        Injector.inject(this);
        this.activity = activity;
    }

    @Override
    public Intent onNotificationClick(EMMessage emMessage) {
        Intent intent = new Intent(activity, ChatActivity.class);
        try {
            if (emMessage.getChatType() == EMMessage.ChatType.Chat) {
                UserInfo user = userStore.getUserInfoByChatId(emMessage.getFrom());
                String screenName = user.getScreenName();
                intent.putExtra(ChatActivity.INTENT_ID, emMessage.getFrom());
                intent.putExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_SINGLE);
                intent.putExtra(ChatActivity.INTENT_TITLE, screenName);
            } else if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
                EMGroup emGroup = chatService.getGroupById(emMessage.getTo());
                intent.putExtra(ChatActivity.INTENT_ID, emMessage.getTo());
                intent.putExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_GROUP);
                intent.putExtra(ChatActivity.INTENT_TITLE, emGroup.getGroupName());
            }
        } catch (Exception e) {
            Ln.e(e);
        }
        return intent;
    }
}
