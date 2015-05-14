package com.aumum.app.mobile.ui.comment;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Comment;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.SpannableTextView;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 13/05/2015.
 */
public class CommentCard {

    private Activity activity;
    private View view;
    private CommentListener listener;

    public CommentCard(Activity activity,
                       View view,
                       CommentListener listener) {
        this.activity = activity;
        this.view = view;
        this.listener = listener;
    }

    public void refresh(final Comment comment) {
        final UserInfo user = comment.getUser();

        View cardLayout = view.findViewById(R.id.layout_comment_card);
        if (comment.isOwner()) {
            cardLayout.setLongClickable(true);
            cardLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showActionDialog(comment);
                    return false;
                }
            });
        } else {
            cardLayout.setLongClickable(false);
        }

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(user.getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(activity, user.getObjectId()));

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

    private void showActionDialog(final Comment comment) {
        String actions[] = { activity.getString(R.string.label_delete) };
        new ListViewDialog(activity, null, Arrays.asList(actions),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                if (listener != null) {
                                    listener.onDelete(comment);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }
}
