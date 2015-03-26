package com.aumum.app.mobile.ui.group;

import android.app.Activity;
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

    private Activity activity;

    public GroupsAdapter(Activity activity, List<GroupDetails> objects) {
        super(activity, 0, objects);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GroupCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.group_listitem_inner, parent, false);
            card = new GroupCard(activity, convertView);
            convertView.setTag(card);
        } else {
            card = (GroupCard) convertView.getTag();
        }

        GroupDetails group = getItem(position);
        card.refresh(group);

        return convertView;
    }
}
