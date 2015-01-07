package com.aumum.app.mobile.ui.party;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.PartyReason;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.SpannableTextView;

/**
 * Created by Administrator on 28/10/2014.
 */
public class PartyReasonCard {
    private AvatarImageView avatarImage;
    private TextView userNameText;
    private SpannableTextView reasonText;
    private TextView createdAtText;
    private TextView actionText;
    private ProgressBar progressBar;

    public PartyReasonCard(View view) {
        this.avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.reasonText = (SpannableTextView) view.findViewById(R.id.text_content);
        this.createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        this.actionText = (TextView) view.findViewById(R.id.text_action);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void refresh(PartyReason reason) {
        avatarImage.getFromUrl(reason.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), reason.getUserId()));
        userNameText.setText(reason.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), reason.getUserId()));

        if (reason.getContent() != null && reason.getContent().length() > 0) {
            reasonText.setSpannableText(reason.getContent());
            reasonText.setVisibility(View.VISIBLE);
        } else {
            reasonText.setVisibility(View.GONE);
        }
        actionText.setText(reason.getActionText());
        if (reason.getObjectId() == null) {
            actionText.setVisibility(View.GONE);
            createdAtText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            actionText.setVisibility(View.VISIBLE);
            createdAtText.setText(reason.getCreatedAtFormatted());
            createdAtText.setVisibility(View.VISIBLE);
        }
    }
}
