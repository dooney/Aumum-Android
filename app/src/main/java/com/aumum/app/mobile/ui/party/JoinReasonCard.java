package com.aumum.app.mobile.ui.party;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.PartyJoinReason;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 28/10/2014.
 */
public class JoinReasonCard {
    private AvatarImageView avatarImage;
    private TextView userNameText;
    private TextView reasonText;
    private TextView createdAtText;
    private ProgressBar progressBar;

    public JoinReasonCard(View view) {
        this.avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        this.userNameText = (TextView) view.findViewById(R.id.text_user_name);
        this.reasonText = (TextView) view.findViewById(R.id.text_content);
        this.createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void refresh(PartyJoinReason reason) {
        avatarImage.getFromUrl(reason.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), reason.getUserId()));
        userNameText.setText(reason.getUser().getScreenName());
        reasonText.setText(reason.getContent());
        if (reason.getObjectId() == null) {
            createdAtText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            createdAtText.setText(reason.getCreatedAtFormatted());
            createdAtText.setVisibility(View.VISIBLE);
        }
    }
}
