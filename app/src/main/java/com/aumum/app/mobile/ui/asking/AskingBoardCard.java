package com.aumum.app.mobile.ui.asking;

import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.AskingBoard;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 2/04/2015.
 */
public class AskingBoardCard {

    private View view;

    public AskingBoardCard(View view) {
        this.view = view;
    }

    public void refresh(final AskingBoard askingBoard) {
        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(askingBoard.getAvatarUrl());

        TextView screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        screenNameText.setText(askingBoard.getScreenName());

        TextView descriptionText = (TextView) view.findViewById(R.id.text_description);
        descriptionText.setText(askingBoard.getDescription());
    }
}
