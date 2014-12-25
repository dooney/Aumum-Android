package com.aumum.app.mobile.ui.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;

import java.util.List;

/**
 * Created by Administrator on 26/12/2014.
 */
public class UserListAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> dataSet;

    public UserListAdapter(Context context, List<User> objects) {
        super(context, 0, objects);
        this.context = context;
        this.dataSet = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UserCard card;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_listitem_inner, parent, false);
            card = new UserCard(context, convertView);
            convertView.setTag(card);
        } else {
            card = (UserCard) convertView.getTag();
        }

        User user = dataSet.get(position);
        card.refresh(user);

        return convertView;
    }
}
