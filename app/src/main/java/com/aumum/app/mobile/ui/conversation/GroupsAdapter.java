package com.aumum.app.mobile.ui.conversation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.GroupDetails;

import java.util.List;

/**
 * Created by Administrator on 24/03/2015.
 */
public class GroupsAdapter extends ArrayAdapter<GroupDetails> {

    public GroupsAdapter(Context context, List<GroupDetails> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GroupCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.group_listitem_inner, parent, false);
            card = new GroupCard(convertView);
            convertView.setTag(card);
        } else {
            card = (GroupCard) convertView.getTag();
        }

        GroupDetails group = getItem(position);
        card.refresh(group);

        return convertView;
    }
}
