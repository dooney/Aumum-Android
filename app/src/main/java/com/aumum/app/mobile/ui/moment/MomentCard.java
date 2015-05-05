package com.aumum.app.mobile.ui.moment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

/**
 * Created by Administrator on 24/04/2015.
 */
public class MomentCard {

    private View view;

    public MomentCard(View view) {
        this.view = view;
    }

    public void refresh(Moment moment) {
        UserInfo user = moment.getUser();

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(user.getAvatarUrl());

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(user.getScreenName());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(moment.getCreatedAtFormatted());

        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        ImageLoaderUtils.displayImage(moment.getImageUrl(), imageView);

        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(moment.getText());
    }
}
