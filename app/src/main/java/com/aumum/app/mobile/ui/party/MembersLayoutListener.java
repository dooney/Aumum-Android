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
public class MembersLayoutListener {

    private Activity activity;
    private String currentUserId;

    public MembersLayoutListener(Activity activity, String currentUserId) {
        this.activity = activity;
        this.currentUserId = currentUserId;
    }

    public void update(ViewGroup membersLayout, List<User> members) throws Exception {
        int count = members.size();
        if (count > 0) {
            ViewGroup layoutMembersAvatars = (ViewGroup) membersLayout.findViewById(R.id.layout_members_avatars);
            layoutMembersAvatars.setVisibility(View.VISIBLE);
            layoutMembersAvatars.removeAllViews();
            LayoutInflater inflater = activity.getLayoutInflater();
            boolean hasCurrentUser = false;
            for (User user: members) {
                String userId = user.getObjectId();
                if (!userId.equals(currentUserId)) {
                    AvatarImageView imgAvatar = (AvatarImageView) inflater.inflate(R.layout.small_avatar, layoutMembersAvatars, false);
                    imgAvatar.setOnClickListener(new UserListener(activity, userId));
                    imgAvatar.getFromUrl(user.getAvatarUrl());
                    layoutMembersAvatars.addView(imgAvatar);
                } else {
                    hasCurrentUser = true;
                }
            }

            TextView membersCountText = (TextView) membersLayout.findViewById(R.id.text_members_count);
            if (hasCurrentUser) {
                if (count == 1) {
                    membersCountText.setText(activity.getString(R.string.label_you_join_the_party));
                    layoutMembersAvatars.setVisibility(View.GONE);
                } else {
                    membersCountText.setText(activity.getString(R.string.label_you_and_others_join_the_party, count - 1));
                }
            } else {
                membersCountText.setText(activity.getString(R.string.label_others_join_the_party, count));
            }

            if (membersLayout.getVisibility() != View.VISIBLE) {
                Animation.fadeIn(membersLayout, Animation.Duration.SHORT);
            }
        } else {
            Animation.fadeOut(membersLayout, Animation.Duration.SHORT);
        }
    }
}
