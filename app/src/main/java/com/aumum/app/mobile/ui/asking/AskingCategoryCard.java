package com.aumum.app.mobile.ui.asking;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.AskingCategory;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 31/03/2015.
 */
public class AskingCategoryCard {

    private View view;

    public AskingCategoryCard(View view) {
        this.view = view;
    }

    public void refresh(final AskingCategory askingCategory) {
        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(askingCategory.getAvatarUrl());

        TextView screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        screenNameText.setText(askingCategory.getScreenName());

        TextView descriptionText = (TextView) view.findViewById(R.id.text_description);
        descriptionText.setText(askingCategory.getDescription());

        ImageView unreadImage = (ImageView) view.findViewById(R.id.image_unread);
        if (askingCategory.isUnread()) {
            unreadImage.setVisibility(View.VISIBLE);
        } else {
            unreadImage.setVisibility(View.INVISIBLE);
        }
    }
}
