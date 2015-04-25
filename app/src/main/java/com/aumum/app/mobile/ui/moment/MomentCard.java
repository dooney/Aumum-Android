package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 24/04/2015.
 */
public class MomentCard {

    private Activity activity;
    private View view;

    public MomentCard(Activity activity,
                      View view) {
        this.activity = activity;
        this.view = view;
    }

    public void refresh(Moment moment) {
        User user = moment.getUser();

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(user.getAvatarUrl());

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(user.getScreenName());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(moment.getCreatedAtFormatted());
    }
}
