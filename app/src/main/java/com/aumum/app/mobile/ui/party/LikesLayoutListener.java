package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;

import java.util.List;

/**
 * Created by Administrator on 13/11/2014.
 */
public class LikesLayoutListener {

    private Activity activity;
    private String currentUserId;

    public LikesLayoutListener(Activity activity, String currentUserId) {
        this.activity = activity;
        this.currentUserId = currentUserId;
    }

    public void update(ViewGroup likesLayout, List<User> likes) throws Exception {
        int count = likes.size();
        if (count > 0) {
            ViewGroup layoutLikingAvatars = (ViewGroup) likesLayout.findViewById(R.id.layout_liking_avatars);
            layoutLikingAvatars.removeAllViews();
            LayoutInflater inflater = activity.getLayoutInflater();
            boolean hasCurrentUser = false;
            for (User user: likes) {
                String userId = user.getObjectId();
                if (!userId.equals(currentUserId)) {
                    AvatarImageView imgAvatar = (AvatarImageView) inflater.inflate(R.layout.small_avatar, layoutLikingAvatars, false);
                    imgAvatar.setOnClickListener(new UserListener(activity, userId));
                    imgAvatar.getFromUrl(user.getAvatarUrl());
                    layoutLikingAvatars.addView(imgAvatar);
                } else {
                    hasCurrentUser = true;
                }
            }

            TextView likesCountText = (TextView) likesLayout.findViewById(R.id.text_likes_count);
            if (hasCurrentUser) {
                if (count == 1) {
                    likesCountText.setText(activity.getString(R.string.label_you_like_the_party));
                    layoutLikingAvatars.setVisibility(View.GONE);
                } else {
                    likesCountText.setText(activity.getString(R.string.label_you_and_others_like_the_party, count - 1));
                }
            } else {
                likesCountText.setText(activity.getString(R.string.label_others_like_the_party, count));
            }

            if (likesLayout.getVisibility() != View.VISIBLE) {
                Animation.fadeIn(likesLayout, Animation.Duration.SHORT);
            }
        } else {
            Animation.fadeOut(likesLayout, Animation.Duration.SHORT);
        }
    }
}
