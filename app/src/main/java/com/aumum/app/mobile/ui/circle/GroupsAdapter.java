package com.aumum.app.mobile.ui.circle;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.easemob.chat.EMGroup;

import java.util.List;

/**
 * Created by Administrator on 10/11/2014.
 */
public class GroupsAdapter extends ArrayAdapter<EMGroup> {

    private Activity activity;
    private List<EMGroup> dataSet;
    private String currentUserId;

    public GroupsAdapter(Activity activity, List<EMGroup> objects, String currentUserId) {
        super(activity, 0, objects);
        this.activity = activity;
        this.dataSet = objects;
        this.currentUserId = currentUserId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GroupCard card;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_listitem_inner, parent, false);
            card = new GroupCard(activity, convertView, currentUserId);
            convertView.setTag(card);
        } else {
            card = (GroupCard) convertView.getTag();
        }

        EMGroup group = dataSet.get(position);
        card.refresh(group);

        return convertView;
    }
}
