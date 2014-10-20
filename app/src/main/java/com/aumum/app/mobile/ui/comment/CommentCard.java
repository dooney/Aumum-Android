package com.aumum.app.mobile.ui.comment;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Comment;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 13/10/2014.
 */
public class CommentCard {
    private View view;

    public CommentCard(View view) {
        this.view = view;
    }

    public void updateView(Comment comment) {
        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(comment.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), comment.getUserId()));

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(comment.getUser().getUsername());

        TextView commentText = (TextView) view.findViewById(R.id.text_content);
        commentText.setText(comment.getContent());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_comment);
        if (comment.getObjectId() == null) {
            createdAtText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            createdAtText.setText(comment.getCreatedAtFormatted());
            createdAtText.setVisibility(View.VISIBLE);
        }
    }
}
