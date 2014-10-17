package com.aumum.app.mobile.ui.comment;

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
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_comment);
        if (comment.getObjectId() == null) {
            createdAtText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            createdAtText.setText("1分钟前");
            createdAtText.setVisibility(View.VISIBLE);
        }
    }
}
