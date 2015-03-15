package com.aumum.app.mobile.ui.feed;

import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Feed;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 15/03/2015.
 */
public class FeedCard {

    private View view;

    public FeedCard(View view) {
        this.view = view;
    }

    public void refresh(Feed feed) {
        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(feed.getAvatarUrl());

        TextView screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        screenNameText.setText(feed.getScreenName());

        TextView descriptionText = (TextView) view.findViewById(R.id.text_description);
        descriptionText.setText(feed.getDescription());
    }
}
