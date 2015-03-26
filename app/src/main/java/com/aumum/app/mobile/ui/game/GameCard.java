package com.aumum.app.mobile.ui.game;

import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Game;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 26/03/2015.
 */
public class GameCard {

    private View view;

    public GameCard(View view) {
        this.view = view;
    }

    public void refresh(Game game) {
        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(game.getAvatarUrl());

        TextView screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        screenNameText.setText(game.getScreenName());

        TextView descriptionText = (TextView) view.findViewById(R.id.text_description);
        descriptionText.setText(game.getDescription());
    }
}
