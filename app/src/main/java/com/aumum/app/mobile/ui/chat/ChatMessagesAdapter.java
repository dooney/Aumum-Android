package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.chat.EMMessage;
import com.easemob.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 11/11/2014.
 */
public class ChatMessagesAdapter extends BaseAdapter {

    private Activity activity;
    private boolean isGroup;
    private ArrayList<EMMessage> data;

    private static final int MESSAGE_TYPE_SYSTEM = 0;
    private static final int MESSAGE_TYPE_RECV_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_TXT = 2;
    private static final int MESSAGE_TYPE_RECV_VOICE = 3;
    private static final int MESSAGE_TYPE_SENT_VOICE = 4;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 6;

    public ChatMessagesAdapter(Activity activity, boolean isGroup) {
        this.activity = activity;
        this.isGroup = isGroup;
        data = new ArrayList<EMMessage>();
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        if (message.getBooleanAttribute("isSystem", false)) {
            return MESSAGE_TYPE_SYSTEM;
        } else if (message.getType() == EMMessage.Type.TXT) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        } else if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        } else if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public EMMessage getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatMessageListener card;

        int type = getItemViewType(position);
        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            int viewId;
            switch (type) {
                case MESSAGE_TYPE_SYSTEM:
                    viewId = R.layout.chat_system_msg_listitem_inner;
                    convertView = inflater.inflate(viewId, parent, false);
                    card = new SystemMessageCard(convertView);
                    break;
                case MESSAGE_TYPE_RECV_TXT:
                    if (isGroup) {
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
                case MESSAGE_TYPE_RECV_VOICE:
                    if (isGroup) {
                        viewId = R.layout.group_chat_voice_received_listitem_inner;
                    } else {
                        viewId = R.layout.chat_voice_received_listitem_inner;
                    }
                    convertView = inflater.inflate(viewId, parent, false);
                    card = new VoiceMessageCard(activity, convertView);
                    break;
                case MESSAGE_TYPE_SENT_VOICE:
                    viewId = R.layout.chat_voice_sent_listitem_inner;
                    convertView = inflater.inflate(viewId, parent, false);
                    card = new VoiceMessageCard(activity, convertView);
                    break;
                case MESSAGE_TYPE_RECV_IMAGE:
                    if (isGroup) {
                        viewId = R.layout.group_chat_image_received_listitem_inner;
                    } else {
                        viewId = R.layout.chat_image_received_listitem_inner;
                    }
                    convertView = inflater.inflate(viewId, parent, false);
                    card = new ImageMessageCard(activity, convertView);
                    break;
                case MESSAGE_TYPE_SENT_IMAGE:
                    viewId = R.layout.chat_image_sent_listitem_inner;
                    convertView = inflater.inflate(viewId, parent, false);
                    card = new ImageMessageCard(activity, convertView);
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
                case MESSAGE_TYPE_SENT_TXT:
                    card = (TextMessageCard) convertView.getTag();
                    break;
                case MESSAGE_TYPE_RECV_VOICE:
                case MESSAGE_TYPE_SENT_VOICE:
                    card = (VoiceMessageCard) convertView.getTag();
                    break;
                case MESSAGE_TYPE_RECV_IMAGE:
                case MESSAGE_TYPE_SENT_IMAGE:
                    card = (ImageMessageCard) convertView.getTag();
                    break;
                default:
                    Ln.e(String.format("Invalid type: %d", type));
                    return null;
            }
        }

        EMMessage message = getItem(position);
        boolean showTimestamp = true;
        if (position > 0) {
            EMMessage lastMessage = getItem(position - 1);
            showTimestamp = !DateUtils.isCloseEnough(message.getMsgTime(), lastMessage.getMsgTime());
        }
        card.refresh(getItem(position), showTimestamp, position);

        return convertView;
    }

    public void addAll(List<EMMessage> messageList) {
        data.clear();
        data.addAll(messageList);
        notifyDataSetChanged();
    }
}
