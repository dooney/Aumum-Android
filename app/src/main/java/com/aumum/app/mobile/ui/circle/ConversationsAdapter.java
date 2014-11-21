package com.aumum.app.mobile.ui.circle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Conversation;

import java.util.List;

/**
 * Created by Administrator on 10/11/2014.
 */
public class ConversationsAdapter extends ArrayAdapter<Conversation> {

    private Context context;
    private List<Conversation> dataSet;

    public ConversationsAdapter(Context context, List<Conversation> objects) {
        super(context, 0, objects);
        this.context = context;
        this.dataSet = objects;
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

        Conversation conversation = dataSet.get(position);
        card.refresh(conversation);

        return convertView;
    }
}
