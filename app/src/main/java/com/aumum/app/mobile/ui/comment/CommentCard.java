package com.aumum.app.mobile.ui.comment;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Comment;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.SpannableTextView;

/**
 * Created by Administrator on 13/05/2015.
 */
public class CommentCard {

    private View view;

    public CommentCard(View view) {
        this.view = view;
    }

    public void refresh(Comment comment) {
        final UserInfo user = comment.getUser();

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(user.getAvatarUrl());

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(user.getScreenName());

        SpannableTextView contentText = (SpannableTextView) view.findViewById(R.id.text_content);
        contentText.setSpannableText(comment.getContent());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        createdAtText.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        if (comment.getObjectId() != null) {
            createdAtText.setText(comment.getCreatedAtFormatted());
            createdAtText.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
