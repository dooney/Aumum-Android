package com.aumum.app.mobile.ui.moment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.MomentLike;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.TimeUtils;

import org.joda.time.DateTime;

/**
 * Created by Administrator on 17/05/2015.
 */
public class MomentLikeCard {

    private View view;

    public MomentLikeCard(View view) {
        this.view = view;
    }

    public void refresh(MomentLike momentLike) {
        final UserInfo user = momentLike.getUser();

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(user.getAvatarUrl());

        TextView screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        screenNameText.setText(user.getScreenName());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        String createdAt = TimeUtils.getFormattedTimeString(
                new DateTime(momentLike.getCreatedAt()));
        createdAtText.setText(createdAt);

        final Moment moment = momentLike.getMoment();
        ImageView image = (ImageView) view.findViewById(R.id.image_moment);
        ImageLoaderUtils.displayImage(moment.getImageUrl(), image);
    }
}
