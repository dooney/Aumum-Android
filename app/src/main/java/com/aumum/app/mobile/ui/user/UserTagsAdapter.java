package com.aumum.app.mobile.ui.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.UserTag;

import java.util.List;

/**
 * Created by Administrator on 1/03/2015.
 */
public class UserTagsAdapter extends ArrayAdapter<UserTag> {

    private UserTagClickListener userTagClickListener;

    public UserTagsAdapter(Context context, List<UserTag> objects,
                           UserTagClickListener userTagClickListener) {
        super(context, 0, objects);
        this.userTagClickListener = userTagClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UserTagCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_tag_listitem_inner, parent, false);
            card = new UserTagCard(convertView, userTagClickListener);
            convertView.setTag(card);
        } else {
            card = (UserTagCard) convertView.getTag();
        }

        UserTag userTag = getItem(position);
        card.refresh(userTag);

        return convertView;
    }
}
