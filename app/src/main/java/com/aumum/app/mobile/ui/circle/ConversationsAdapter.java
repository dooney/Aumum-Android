package com.aumum.app.mobile.ui.circle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.ChatService;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;

import java.util.List;

/**
 * Created by Administrator on 10/11/2014.
 */
public class ConversationsAdapter extends ArrayAdapter<EMContact> {

    private Context context;
    private List<EMContact> dataSet;
    private ChatService chatService;

    public ConversationsAdapter(Context context, List<EMContact> objects, ChatService chatService) {
        super(context, 0, objects);
        this.context = context;
        this.dataSet = objects;
        this.chatService = chatService;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ConversationCard card;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.conversation_listitem_inner, parent, false);
            card = new ConversationCard(context, convertView);
            convertView.setTag(card);
        } else {
            card = (ConversationCard) convertView.getTag();
        }

        EMContact contact = dataSet.get(position);
        EMConversation conversation = chatService.getConversation(contact.getUsername());
        card.refresh(contact, conversation);

        return convertView;
    }
}
