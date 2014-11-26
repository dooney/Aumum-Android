package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

/**
 * Created by Administrator on 11/11/2014.
 */
public class ChatMessagesAdapter extends BaseAdapter {

    private Activity activity;
    private EMConversation conversation;

    private static final int MESSAGE_TYPE_SYSTEM = 0;
    private static final int MESSAGE_TYPE_RECV_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_TXT = 2;

    public ChatMessagesAdapter(Activity activity, EMConversation conversation) {
        this.activity = activity;
        this.conversation = conversation;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = conversation.getMessage(position);
        if (message.getBooleanAttribute("isSystem", false)) {
            return MESSAGE_TYPE_SYSTEM;
        } else if (message.getType() == EMMessage.Type.TXT) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
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
        final CardRefreshListener card;

        int type = getItemViewType(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int viewId;
            switch (type) {
                case MESSAGE_TYPE_SYSTEM:
                    viewId = R.layout.chat_system_msg_listitem_inner;
                    convertView = inflater.inflate(viewId, parent, false);
                    card = new SystemMessageCard(convertView);
                    break;
                case MESSAGE_TYPE_RECV_TXT:
                    if (conversation.isGroup()) {
                        viewId = R.layout.group_chat_text_received_listitem_inner;
                    } else {
                        viewId = R.layout.chat_text_received_listitem_inner;
                    }
                    convertView = inflater.inflate(viewId, parent, false);
                    card = new TextMessageCard(activity, convertView);
                    break;
                case MESSAGE_TYPE_SENT_TXT:
                    viewId = R.layout.chat_text_sent_listitem_inner;
                    convertView = inflater.inflate(viewId, parent, false);
                    card = new TextMessageCard(activity, convertView);
                    break;
                default:
                    Ln.e(String.format("Invalid type: %d", type));
                    return null;
            }
            convertView.setTag(card);
        } else {
            switch (type) {
                case MESSAGE_TYPE_SYSTEM:
                    card = (SystemMessageCard) convertView.getTag();
                    break;
                case MESSAGE_TYPE_RECV_TXT:
                    card = (TextMessageCard) convertView.getTag();
                    break;
                case MESSAGE_TYPE_SENT_TXT:
                    card = (TextMessageCard) convertView.getTag();
                    break;
                default:
                    Ln.e(String.format("Invalid type: %d", type));
                    return null;
            }
        }
        card.refresh(conversation, position);

        return convertView;
    }
}
