package com.aumum.app.mobile.ui;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Comment;

/**
 * Created by Administrator on 13/10/2014.
 */
public class CommentCard {
    private View view;

    public CommentCard(View view) {
        this.view = view;
    }

    public void updateView(Comment comment) {
        TextView userNameText = (TextView) view.findViewById(R.id.text_comment_user_name);
        userNameText.setText(comment.getUser().getUsername());

        TextView commentText = (TextView) view.findViewById(R.id.text_comment_text);
        commentText.setText(comment.getText());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_comment_createdAt);
        if (comment.getObjectId() == null) {
            createdAtText.setVisibility(View.GONE);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_comment);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            createdAtText.setText("1分钟前");
        }
    }
}
