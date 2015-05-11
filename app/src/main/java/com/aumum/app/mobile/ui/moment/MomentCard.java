package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.Share;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.ShareUtils;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

/**
 * Created by Administrator on 24/04/2015.
 */
public class MomentCard {

    private Activity activity;
    private View view;

    public MomentCard(Activity activity,
                      View view) {
        this.activity = activity;
        this.view = view;
    }

    public void refresh(final Moment moment) {
        final UserInfo user = moment.getUser();

        UserListener userListener = new UserListener(activity, user.getObjectId());
        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(user.getAvatarUrl());
        avatarImage.setOnClickListener(userListener);

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(user.getScreenName());
        userNameText.setOnClickListener(userListener);

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(moment.getCreatedAtFormatted());

        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        ImageLoaderUtils.displayImage(moment.getImageUrl(), imageView, R.drawable.photo_placeholder);

        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(moment.getText());

        LikeTextView likeText = (LikeTextView) view.findViewById(R.id.text_like);
        likeText.init(moment.isLiked());
        MomentLikeListener likeListener = new MomentLikeListener(activity, moment);
        ViewGroup likesLayout = (ViewGroup) view.findViewById(R.id.layout_likes);
        likeListener.updateLikesLayout(likesLayout, moment.getLikesInfo());
        likeText.setLikeListener(likeListener);

        TextView chatText = (TextView) view.findViewById(R.id.text_chat);
        if (moment.isOwner()) {
            chatText.setVisibility(View.GONE);
        } else {
            chatText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(activity, ChatActivity.class);
                    intent.putExtra(ChatActivity.INTENT_TITLE, user.getScreenName());
                    intent.putExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_SINGLE);
                    intent.putExtra(ChatActivity.INTENT_ID, user.getChatId());
                    activity.startActivity(intent);
                }
            });
            chatText.setVisibility(View.VISIBLE);
        }

        TextView shareText = (TextView) view.findViewById(R.id.text_share);
        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = activity.getString(R.string.label_share_content);
                Share share = new Share(moment.getText(), content, moment.getImageUrl());
                ShareUtils.show(activity, share);
            }
        });
    }
}
