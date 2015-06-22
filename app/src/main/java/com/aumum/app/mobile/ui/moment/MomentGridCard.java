package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.LikeCountTextView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

/**
 * Created by Administrator on 20/06/2015.
 */
public class MomentGridCard {

    private Activity activity;
    private View view;

    public MomentGridCard(Activity activity,
                          View view) {
        this.activity = activity;
        this.view = view;
    }

    public void refresh(Moment moment) {
        final UserInfo user = moment.getUser();

        UserListener userListener = new UserListener(activity, user.getObjectId());
        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(user.getAvatarUrl());
        avatarImage.setOnClickListener(userListener);

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(user.getScreenName());
        userNameText.setOnClickListener(userListener);

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(moment.getCreatedAtFormatted());

        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        ImageLoaderUtils.displayImage(moment.getImageUrl(), imageView);

        LikeCountTextView likeText = (LikeCountTextView) view.findViewById(R.id.text_like);
        likeText.init(moment.isLiked(), moment.getLikes().size());
        MomentLikeListener likeListener = new MomentLikeListener(activity, moment);
        likeText.setLikeListener(likeListener);
    }
}
