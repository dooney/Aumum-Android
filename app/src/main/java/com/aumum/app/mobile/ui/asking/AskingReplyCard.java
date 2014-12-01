package com.aumum.app.mobile.ui.asking;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.AskingReply;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 30/11/2014.
 */
public class AskingReplyCard {

    private AvatarImageView avatarImage;
    private TextView userNameText;
    private TextView replyText;
    private TextView createdAtText;
    private ProgressBar progressBar;

    public AskingReplyCard(View view) {
        this.avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.replyText = (TextView) view.findViewById(R.id.text_content);
        this.createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void refresh(AskingReply askingReply) {
        avatarImage.getFromUrl(askingReply.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), askingReply.getUserId()));
        userNameText.setText(askingReply.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), askingReply.getUserId()));
        replyText.setText(askingReply.getContent());
        if (askingReply.getObjectId() == null) {
            createdAtText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            createdAtText.setText(askingReply.getCreatedAtFormatted());
            createdAtText.setVisibility(View.VISIBLE);
        }
    }
}
