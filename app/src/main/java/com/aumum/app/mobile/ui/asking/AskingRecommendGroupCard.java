package com.aumum.app.mobile.ui.asking;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.AskingGroup;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 2/04/2015.
 */
public class AskingRecommendGroupCard implements AskingGroupJoinListener.OnActionListener {

    private Activity activity;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private TextView descriptionText;
    private Button joinButton;
    private ProgressBar progressBar;
    private AskingRecommendGroupJoinListener listener;

    public AskingRecommendGroupCard(Activity activity,
                                    View view,
                                    AskingRecommendGroupJoinListener listener) {
        this.activity = activity;
        this.listener = listener;
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        descriptionText = (TextView) view.findViewById(R.id.text_description);
        joinButton = (Button) view.findViewById(R.id.b_join);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void refresh(final AskingGroup askingGroup) {
        avatarImage.getFromUrl(askingGroup.getAvatarUrl());
        screenNameText.setText(askingGroup.getScreenName());
        descriptionText.setText(askingGroup.getDescription());
        if (askingGroup.isMember()) {
            joinButton.setVisibility(View.GONE);
        } else {
            final AskingGroupJoinListener listener = new AskingGroupJoinListener(activity);
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onJoin(activity, askingGroup, AskingRecommendGroupCard.this);
                }
            });
            joinButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        joinButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onException(Exception e) {
        progressBar.setVisibility(View.GONE);
        joinButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess() {
        joinButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        if (listener != null) {
            listener.onSuccess();
        }
    }
}
