package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Administrator on 13/11/2014.
 */
public class MembersLayoutListener {
    @Inject UserStore userStore;

    private Activity activity;
    private String currentUserId;

    public MembersLayoutListener(Activity activity, String currentUserId) {
        this.activity = activity;
        this.currentUserId = currentUserId;
        Injector.inject(this);
    }

    public void update(ViewGroup membersLayout, List<String> members) throws Exception {
        int count = members.size();
        if (count > 0) {
            ViewGroup layoutMembersAvatars = (ViewGroup) membersLayout.findViewById(R.id.layout_members_avatars);
            layoutMembersAvatars.setVisibility(View.VISIBLE);
            layoutMembersAvatars.removeAllViews();
            LayoutInflater inflater = activity.getLayoutInflater();
            final HashMap<String, AvatarImageView> avatarImages = new HashMap<String, AvatarImageView>();
            for(String userId: members) {
                if (!userId.equals(currentUserId)) {
                    AvatarImageView avatarImage = (AvatarImageView) inflater.inflate(R.layout.small_avatar, layoutMembersAvatars, false);
                    avatarImage.setOnClickListener(new UserListener(activity, userId));
                    avatarImages.put(userId, avatarImage);
                    layoutMembersAvatars.addView(avatarImage);
                }
            }
            final HashMap<String, String> avatarUrls = new HashMap<String, String>();
            new SafeAsyncTask<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Iterator it = avatarImages.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, AvatarImageView> pair = (Map.Entry<String, AvatarImageView>) it.next();
                        String userId = pair.getKey();
                        User user = userStore.getUserById(userId);
                        avatarUrls.put(userId, user.getAvatarUrl());
                    }
                    return true;
                }

                @Override
                protected void onSuccess(Boolean success) throws Exception {
                    Iterator it = avatarImages.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, AvatarImageView> pair = (Map.Entry<String, AvatarImageView>) it.next();
                        String userId = pair.getKey();
                        AvatarImageView avatarImage = pair.getValue();
                        avatarImage.getFromUrl(avatarUrls.get(userId));
                    }
                }
            }.execute();

            TextView membersCountText = (TextView) membersLayout.findViewById(R.id.text_members_count);
            if (members.contains(currentUserId)) {
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
