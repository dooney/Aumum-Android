package com.aumum.app.mobile.ui.asking;

import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 27/11/2014.
 */
public class AskingCard {

    private AvatarImageView avatarImage;
    private TextView userNameText;
    private TextView createdAtText;
    private TextView questionText;

    public AskingCard(View view) {
        this.avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        this.questionText = (TextView) view.findViewById(R.id.text_question);
    }

    public void refresh(Asking asking) {
        avatarImage.getFromUrl(asking.getUser().getAvatarUrl());
        userNameText.setText(asking.getUser().getScreenName());
        createdAtText.setText(asking.getCreatedAtFormatted());
        questionText.setText(asking.getQuestion());
    }
}
