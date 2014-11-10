package com.aumum.app.mobile.ui.circle;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Group;

import java.util.List;

/**
 * Created by Administrator on 10/11/2014.
 */
public class GroupsAdapter extends ArrayAdapter<Group> {

    private Activity activity;
    private List<Group> dataSet;

    public GroupsAdapter(Activity activity, List<Group> objects) {
        super(activity, 0, objects);
        this.activity = activity;
        this.dataSet = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GroupCard card;
        LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_listitem_inner, parent, false);
            card = new GroupCard(convertView, activity);
            convertView.setTag(card);
        } else {
            card = (GroupCard) convertView.getTag();
        }

        Group group = dataSet.get(position);
        card.refresh(group);

        return convertView;
    }
}
