package com.aumum.app.mobile.ui.moment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.MomentComment;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

/**
 * Created by Administrator on 18/05/2015.
 */
public class MomentCommentCard {

    private View view;

    public MomentCommentCard(View view) {
        this.view = view;
    }

    public void refresh(MomentComment momentComment) {
        final UserInfo user = momentComment.getUser();

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(user.getAvatarUrl());

        TextView screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        screenNameText.setText(user.getScreenName());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(momentComment.getCreatedAt());

        final Moment moment = momentComment.getMoment();
        ImageView image = (ImageView) view.findViewById(R.id.image_moment);
        ImageLoaderUtils.displayImage(moment.getImageUrl(), image);

        TextView commentText = (TextView) view.findViewById(R.id.text_comment);
        commentText.setText(momentComment.getContent());
    }
}
