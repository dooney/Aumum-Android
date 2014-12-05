package com.aumum.app.mobile.ui.asking;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.AskingReply;
import com.aumum.app.mobile.ui.delegate.ActionListener;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.ui.view.SpannableTextView;

import javax.inject.Inject;

/**
 * Created by Administrator on 30/11/2014.
 */
public class AskingReplyCard implements ActionListener {

    @Inject ApiKeyProvider apiKeyProvider;

    private AskingReply askingReply;

    private AvatarImageView avatarImage;
    private TextView userNameText;
    private SpannableTextView replyText;
    private TextView createdAtText;
    private LikeTextView likeText;
    private ProgressBar progressBar;

    public AskingReplyCard(View view) {
        Injector.inject(this);
        this.avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.replyText = (SpannableTextView) view.findViewById(R.id.text_content);
        this.createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        this.likeText = (LikeTextView) view.findViewById(R.id.text_like);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public AskingReply getAskingReply() {
        return askingReply;
    }

    public void refresh(AskingReply askingReply) {
        this.askingReply = askingReply;

        avatarImage.getFromUrl(askingReply.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), askingReply.getUserId()));
        userNameText.setText(askingReply.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), askingReply.getUserId()));

        likeText.setLikeResId(R.drawable.ic_fa_thumbs_o_up_s);
        likeText.setLikedResId(R.drawable.ic_fa_thumbs_up_s);
        likeText.init(askingReply.getLikesCount(), askingReply.isLiked(apiKeyProvider.getAuthUserId()));
        likeText.setLikeListener(new AskingReplyLikeListener(askingReply));

        replyText.setSpannableText(askingReply.getContent());
        if (askingReply.getObjectId() == null) {
            createdAtText.setVisibility(View.GONE);
            likeText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            likeText.setVisibility(View.VISIBLE);
            createdAtText.setText(askingReply.getCreatedAtFormatted());
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
