package com.aumum.app.mobile.ui.chat;

import android.content.Context;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.TextMessageBody;

import javax.inject.Inject;

/**
 * Created by Administrator on 19/12/2014.
 */
public class MessageNotifyListener implements OnMessageNotifyListener {

    @Inject UserStore userStore;
    private Context context;

    public MessageNotifyListener(Context context) {
        Injector.inject(this);
        this.context = context;
    }

    @Override
    public String onNewMessageNotify(EMMessage emMessage) {
        try {
            User user = userStore.getUserByChatId(emMessage.getFrom());
            String screenName = user.getScreenName();
            String message = screenName + "：";
            if (emMessage.getType() == EMMessage.Type.TXT) {
                TextMessageBody messageBody = (TextMessageBody) emMessage.getBody();
                message += messageBody.getMessage();
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                message += "["+ context.getString(R.string.label_voice) +"]";
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                message += "["+ context.getString(R.string.label_image) +"]";
            }
            return message;
        } catch (Exception e) {
            Ln.e(e);
        }
        return null;
    }

    @Override
    public String onLatestMessageNotify(EMMessage emMessage, int fromUsersNum, int messageNum) {
        return context.getString(R.string.info_chat_messages_arrived, fromUsersNum, messageNum);
    }

    @Override
    public String onSetNotificationTitle(EMMessage emMessage) {
        return context.getString(R.string.app_name);
    }

    @Override
    public int onSetSmallIcon(EMMessage emMessage) {
        return R.drawable.icon;
    }
}
