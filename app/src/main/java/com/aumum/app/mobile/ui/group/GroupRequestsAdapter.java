package com.aumum.app.mobile.ui.group;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.GroupRequest;

import java.util.List;

/**
 * Created by Administrator on 25/03/2015.
 */
public class GroupRequestsAdapter extends ArrayAdapter<GroupRequest> {

    private Activity activity;

    public GroupRequestsAdapter(Activity activity,
                                List<GroupRequest> objects) {
        super(activity, 0, objects);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GroupRequestCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.group_request_listitem_inner, parent, false);
            card = new GroupRequestCard(activity, convertView);
            convertView.setTag(card);
        } else {
            card = (GroupRequestCard) convertView.getTag();
        }

        GroupRequest application = getItem(position);
        card.refresh(application);

        return convertView;
    }
}
