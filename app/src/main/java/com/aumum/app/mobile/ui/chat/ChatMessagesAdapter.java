package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aumum.app.mobile.R;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

/**
 * Created by Administrator on 11/11/2014.
 */
public class ChatMessagesAdapter extends BaseAdapter {

    private Activity activity;
    private EMConversation conversation;

    public ChatMessagesAdapter(Activity activity, EMConversation conversation) {
        this.activity = activity;
        this.conversation = conversation;
    }

    @Override
    public int getCount() {
        return conversation.getMsgCount();
    }

    @Override
    public Object getItem(int position) {
        return conversation.getMessage(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatMessageCard card;
        EMMessage message = conversation.getMessage(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (message.direct == EMMessage.Direct.SEND) {
                convertView = inflater.inflate(R.layout.chat_sent_listitem_inner, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.chat_received_listitem_inner, parent, false);
            }
            card = new ChatMessageCard(activity, convertView);
            convertView.setTag(card);
        } else {
            card = (ChatMessageCard) convertView.getTag();
        }
        card.refresh(conversation, position);

        return convertView;
    }
}
