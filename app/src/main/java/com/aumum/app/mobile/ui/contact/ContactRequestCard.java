package com.aumum.app.mobile.ui.contact;

import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 20/11/2014.
 */
public class ContactRequestCard {

    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private TextView introText;

    public ContactRequestCard(View view) {
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        introText = (TextView) view.findViewById(R.id.text_intro);
    }

    public void refresh(ContactRequest request) {
        User user = request.getUser();
        avatarImage.getFromUrl(user.getAvatarUrl());
        screenNameText.setText(user.getScreenName());
        introText.setText(request.getIntro());
    }
}
