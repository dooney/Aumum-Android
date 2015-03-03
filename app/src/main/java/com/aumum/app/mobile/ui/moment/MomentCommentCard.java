package com.aumum.app.mobile.ui.moment;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.MomentComment;
import com.aumum.app.mobile.ui.delegate.ActionListener;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.ui.view.SpannableTextView;

import javax.inject.Inject;

/**
 * Created by Administrator on 3/03/2015.
 */
public class MomentCommentCard implements ActionListener {

    @Inject ApiKeyProvider apiKeyProvider;

    private MomentComment comment;

    private AvatarImageView avatarImage;
    private TextView userNameText;
    private SpannableTextView commentText;
    private TextView createdAtText;
    private LikeTextView likeText;
    private ProgressBar progressBar;

    public MomentComment getComment() {
        return comment;
    }

    public MomentCommentCard(View view) {
        Injector.inject(this);
        this.avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.commentText = (SpannableTextView) view.findViewById(R.id.text_content);
        this.createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        this.likeText = (LikeTextView) view.findViewById(R.id.text_like);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void refresh(MomentComment comment) {
        this.comment = comment;

        avatarImage.getFromUrl(comment.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), comment.getUserId()));
        userNameText.setText(comment.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), comment.getUserId()));
        commentText.setSpannableText(comment.getContent());

        likeText.setLikeResId(R.drawable.ic_fa_thumbs_o_up_s);
        likeText.setLikedResId(R.drawable.ic_fa_thumbs_up_s);
        likeText.init(comment.getLikesCount(), comment.isLiked(apiKeyProvider.getAuthUserId()));
        likeText.setLikeListener(new MomentCommentLikeListener(comment));

        if (comment.getObjectId() == null) {
            createdAtText.setVisibility(View.GONE);
            likeText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            likeText.setVisibility(View.VISIBLE);
            createdAtText.setText(comment.getCreatedAtFormatted());
            createdAtText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActionStart() {
        likeText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActionFinish() {
        progressBar.setVisibility(View.GONE);
        likeText.setVisibility(View.VISIBLE);
    }
}
