package com.aumum.app.mobile.ui.game;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Game;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 26/03/2015.
 */
public class GameCard {

    private View view;
    private GameClickListener listener;

    public GameCard(View view, GameClickListener listener) {
        this.view = view;
        this.listener = listener;
    }

    public void refresh(final Game game) {
        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(game.getAvatarUrl());

        TextView screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        screenNameText.setText(game.getScreenName());

        TextView descriptionText = (TextView) view.findViewById(R.id.text_description);
        descriptionText.setText(game.getDescription());

        ViewGroup gameCardLayout = (ViewGroup) view.findViewById(R.id.layout_game_card);
        gameCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(game);
                }
            }
        });
        Button playButton = (Button) view.findViewById(R.id.b_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(game);
                }
            }
        });

        TextView countText = (TextView) view.findViewById(R.id.text_count);
        countText.setText(view.getContext().getString(R.string.label_play_count, game.getClicks()));
    }
}
