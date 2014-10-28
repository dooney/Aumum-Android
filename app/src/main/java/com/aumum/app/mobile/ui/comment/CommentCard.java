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
public class CommentCard implements DeleteCommentListener.OnProgressListener {
    private AvatarImageView avatarImage;
    private ImageView deleteImage;
    private TextView userNameText;
    private TextView commentText;
    private TextView createdAtText;
    private ProgressBar progressBar;
    private String currentUserId;
    private DeleteCommentListener.OnActionListener onActionListener;

    public CommentCard(View view, String currentUserId,
                       DeleteCommentListener.OnActionListener onActionListener) {
        this.avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        this.deleteImage = (ImageView) view.findViewById(R.id.image_delete);
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.commentText = (TextView) view.findViewById(R.id.text_content);
        this.createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
        this.currentUserId = currentUserId;
        this.onActionListener = onActionListener;
    }

    public void refresh(Comment comment) {
        avatarImage.getFromUrl(comment.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), comment.getUserId()));
        userNameText.setText(comment.getUser().getScreenName());
        commentText.setText(comment.getContent());
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
                listener.setOnProgressListener(this);
                deleteImage.setOnClickListener(listener);
            } else {
                deleteImage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDeleteCommentStart() {
        deleteImage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeleteCommentFinish() {
        progressBar.setVisibility(View.GONE);
        deleteImage.setVisibility(View.VISIBLE);
    }
}
