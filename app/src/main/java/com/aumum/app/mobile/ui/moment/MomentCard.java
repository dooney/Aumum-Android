package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.ui.image.ImageCard;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.ui.view.SpannableTextView;

/**
 * Created by Administrator on 2/03/2015.
 */
public class MomentCard extends ImageCard {

    private Moment moment;
    private String currentUserId;
    private MomentLikeListener likeListener;
    private MomentCommentClickListener momentCommentClickListener;

    public MomentCard(Activity activity, Moment moment, String currentUserId,
                      MomentCommentClickListener momentCommentClickListener) {
        super(activity, R.layout.moment_listitem_inner, moment.getImages());
        this.moment = moment;
        this.currentUserId = currentUserId;
        likeListener = new MomentLikeListener(moment);
        this.momentCommentClickListener = momentCommentClickListener;
    }

    public Moment getMoment() {
        return moment;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(moment.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), moment.getUserId()));

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(moment.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), moment.getUserId()));

        TextView cityText = (TextView) view.findViewById(R.id.text_city);
        cityText.setText(moment.getUser().getCity());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(moment.getCreatedAtFormatted());

        SpannableTextView detailsText = (SpannableTextView) view.findViewById(R.id.text_details);
        if (moment.getDetails() != null && moment.getDetails().length() > 0) {
            detailsText.setSpannableText(moment.getDetails());
        } else {
            detailsText.setVisibility(View.GONE);
        }

        TextView commentText = (TextView) view.findViewById(R.id.text_comment);
        int comments = moment.getCommentsCount();
        commentText.setText(comments > 0 ? String.valueOf(comments) : view.getResources().getString(R.string.label_comment));
        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (momentCommentClickListener != null) {
                    momentCommentClickListener.OnClick(moment.getObjectId());
                }
            }
        });

        LikeTextView likeText = (LikeTextView) view.findViewById(R.id.text_like);
        likeText.setTextResId(R.string.label_like);
        likeText.setLikeResId(R.drawable.ic_fa_thumbs_o_up);
        likeText.setLikedResId(R.drawable.ic_fa_thumbs_up);
        likeText.init(moment.getLikesCount(), moment.isLiked(currentUserId));
        likeText.setLikeListener(likeListener);
    }
}