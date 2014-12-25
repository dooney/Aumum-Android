package com.aumum.app.mobile.ui.user;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 26/12/2014.
 */
public class UserCard {

    private Context context;
    private View view;

    public UserCard(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public void refresh(User user) {
        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(user.getAvatarUrl());

        TextView screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        screenNameText.setText(user.getScreenName());

        view.setOnClickListener(new UserListener(context, user.getObjectId()));
    }
}
