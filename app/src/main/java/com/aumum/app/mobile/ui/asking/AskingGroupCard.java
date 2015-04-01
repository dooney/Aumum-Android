package com.aumum.app.mobile.ui.asking;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.AskingGroup;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 31/03/2015.
 */
public class AskingGroupCard implements AskingGroupJoinListener.OnActionListener {

    private Activity activity;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private TextView descriptionText;
    private ImageView unreadImage;
    private Button joinButton;
    private ImageView deleteImage;
    private ProgressBar progressBar;
    private AskingGroupQuitListener quitListener;

    public AskingGroupCard(Activity activity,
                           View view,
                           AskingGroupQuitListener quitListener) {
        this.activity = activity;
        this.quitListener = quitListener;
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        descriptionText = (TextView) view.findViewById(R.id.text_description);
        unreadImage = (ImageView) view.findViewById(R.id.image_unread);
        joinButton = (Button) view.findViewById(R.id.b_join);
        deleteImage = (ImageView) view.findViewById(R.id.image_delete);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void refresh(final AskingGroup askingGroup) {
        avatarImage.getFromUrl(askingGroup.getAvatarUrl());
        screenNameText.setText(askingGroup.getScreenName());
        descriptionText.setText(askingGroup.getDescription());
        if (askingGroup.isUnread()) {
            unreadImage.setVisibility(View.VISIBLE);
        } else {
            unreadImage.setVisibility(View.INVISIBLE);
        }
        final AskingGroupJoinListener joinListener = new AskingGroupJoinListener();
        if (askingGroup.isMember()) {
            joinButton.setVisibility(View.GONE);
            deleteImage.setVisibility(View.VISIBLE);
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    quitListener.onQuit(askingGroup);
                }
            });
        } else {
            deleteImage.setVisibility(View.GONE);
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    joinListener.onJoin(activity, askingGroup, AskingGroupCard.this);
                }
            });
            joinButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        joinButton.setVisibility(View.GONE);
        deleteImage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onException(Exception e) {
        progressBar.setVisibility(View.GONE);
        deleteImage.setVisibility(View.GONE);
        joinButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess() {
        joinButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        deleteImage.setVisibility(View.VISIBLE);
    }
}
