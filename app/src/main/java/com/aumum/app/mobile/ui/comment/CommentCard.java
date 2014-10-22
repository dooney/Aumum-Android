package com.aumum.app.mobile.ui.comment;

import android.view.View;
import android.widget.ImageView;
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
    private String currentUserId;
    private DeleteCommentListener.OnActionListener onActionListener;

    public CommentCard(View view, String currentUserId,
                       DeleteCommentListener.OnActionListener onActionListener) {
        this.view = view;
        this.currentUserId = currentUserId;
        this.onActionListener = onActionListener;
    }

    public void refresh(Comment comment) {
        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(comment.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), comment.getUserId()));

        ImageView deleteImage = (ImageView) view.findViewById(R.id.image_delete);

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(comment.getUser().getUsername());

        TextView commentText = (TextView) view.findViewById(R.id.text_content);
        commentText.setText(comment.getContent());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_comment);
        if (comment.getObjectId() == null) {
            createdAtText.setVisibility(View.GONE);
            deleteImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            createdAtText.setText(comment.getCreatedAtFormatted());
            createdAtText.setVisibility(View.VISIBLE);
            if (comment.isOwner(currentUserId)) {
                deleteImage.setVisibility(View.VISIBLE);
                DeleteCommentListener listener = new DeleteCommentListener(comment, currentUserId);
                listener.setOnActionListener(onActionListener);
                deleteImage.setOnClickListener(listener);
            } else {
                deleteImage.setVisibility(View.GONE);
            }
        }
    }
}
