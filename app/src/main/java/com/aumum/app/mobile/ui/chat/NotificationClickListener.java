package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.content.Intent;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnNotificationClickListener;

import javax.inject.Inject;

/**
 * Created by Administrator on 19/12/2014.
 */
public class NotificationClickListener implements OnNotificationClickListener {

    @Inject UserStore userStore;
    private Activity activity;

    public NotificationClickListener(Activity activity) {
        Injector.inject(this);
        this.activity = activity;
    }

    @Override
    public Intent onNotificationClick(EMMessage emMessage) {
        Intent intent = new Intent(activity, ChatActivity.class);
        try {
            UserInfo user = userStore.getUserInfoByChatId(emMessage.getFrom());
            String screenName = user.getScreenName();
            intent.putExtra(ChatActivity.INTENT_ID, emMessage.getFrom());
            intent.putExtra(ChatActivity.INTENT_TITLE, screenName);
        } catch (Exception e) {
            Ln.e(e);
        }
        return intent;
    }
}