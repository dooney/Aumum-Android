package com.aumum.app.mobile.ui.vendor;

import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.EventCategory;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 21/03/2015.
 */
public class EventCategoryCard {

    private View view;

    public EventCategoryCard(View view) {
        this.view = view;
    }

    public void refresh(EventCategory eventCategory) {
        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(eventCategory.getAvatarUrl());

        TextView nameText = (TextView) view.findViewById(R.id.text_name);
        nameText.setText(eventCategory.getName());
    }
}
