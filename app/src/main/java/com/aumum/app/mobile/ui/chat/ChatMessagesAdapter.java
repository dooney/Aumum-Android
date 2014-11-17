package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

/**
 * Created by Administrator on 11/11/2014.
 */
public class ChatMessagesAdapter extends BaseAdapter {

    private Activity activity;
    private EMConversation conversation;
    private UserStore userStore;

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;

    public ChatMessagesAdapter(Activity activity, EMConversation conversation, UserStore userStore) {
        this.activity = activity;
        this.conversation = conversation;
        this.userStore = userStore;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = conversation.getMessage(position);
        if (message.getType() == EMMessage.Type.TXT) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return conversation.getMsgCount();
    }

    @Override
    public EMMessage getItem(int position) {
        return conversation.getMessage(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatMessageCard card;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int type = getItemViewType(position);
            int viewId;
            switch (type) {
                case MESSAGE_TYPE_RECV_TXT:
                    viewId = R.layout.chat_text_received_listitem_inner;
                    break;
                case MESSAGE_TYPE_SENT_TXT:
                    viewId = R.layout.chat_text_sent_listitem_inner;
                    break;
                default:
                    Ln.e(String.format("Invalid type: %d", type));
                    return null;
            }
            convertView = inflater.inflate(viewId, parent, false);
            card = new ChatMessageCard(activity, convertView);
            convertView.setTag(card);
        } else {
            card = (ChatMessageCard) convertView.getTag();
        }

        String userName = activity.getString(R.string.label_unknown_user);
        String avatarUrl = null;
        try {
            EMMessage message = conversation.getMessage(position);
            String userId = message.getStringAttribute("userId");
            User user = userStore.getUserById(userId);
            userName = user.getScreenName();
            avatarUrl = user.getAvatarUrl();
        } catch (Exception e) {
            Ln.d(e);
        }
        card.refresh(conversation, position, userName, avatarUrl);

        return convertView;
    }
}
