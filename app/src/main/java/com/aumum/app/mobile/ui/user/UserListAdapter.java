package com.aumum.app.mobile.ui.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;

import java.util.List;

/**
 * Created by Administrator on 26/12/2014.
 */
public class UserListAdapter extends ArrayAdapter<UserInfo> {

    private Context context;

    public UserListAdapter(Context context,
                           List<UserInfo> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UserCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_listitem_inner, parent, false);
            card = new UserCard(context, convertView);
            convertView.setTag(card);
        } else {
            card = (UserCard) convertView.getTag();
        }

        UserInfo user = getItem(position);
        card.refresh(user);

        return convertView;
    }
}
