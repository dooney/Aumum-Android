package com.aumum.app.mobile.ui.comment;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Comment;
import com.aumum.app.mobile.ui.delegate.ActionListener;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.SpannableTextView;

/**
 * Created by Administrator on 13/10/2014.
 */
public class CommentCard implements ActionListener {

    private Comment comment;

    private AvatarImageView avatarImage;
    private TextView userNameText;
    private SpannableTextView commentText;
    private TextView createdAtText;
    private ProgressBar progressBar;

    public Comment getComment() {
        return comment;
    }

    public CommentCard(View view) {
        this.avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.commentText = (SpannableTextView) view.findViewById(R.id.text_content);
        this.createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void refresh(Comment comment) {
        this.comment = comment;

        avatarImage.getFromUrl(comment.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), comment.getUserId()));
        userNameText.setText(comment.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), comment.getUserId()));
        commentText.setSpannableText(comment.getContent());
        if (comment.getObjectId() == null) {
            createdAtText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            createdAtText.setText(comment.getCreatedAtFormatted());
            createdAtText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActionStart() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActionFinish() {
        progressBar.setVisibility(View.GONE);
    }
}
