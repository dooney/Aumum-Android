package com.aumum.app.mobile.ui.like;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.user.UserListActivity;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.utils.DisplayUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Administrator on 13/11/2014.
 */
public class LikesLayoutListener {

    @Inject UserStore userStore;

    private Activity activity;
    private String currentUserId;

    public LikesLayoutListener(Activity activity, String currentUserId) {
        this.activity = activity;
        this.currentUserId = currentUserId;
        Injector.inject(this);
    }

    public void update(ViewGroup likesLayout, List<String> likeList) {
        ArrayList<String> likes = new ArrayList<String>();
        likes.addAll(likeList);
        Collections.reverse(likes);
        final int size = likes.size();
        if (size > 0) {
            ViewGroup layoutLikingAvatars = (ViewGroup) likesLayout.findViewById(R.id.layout_liking_avatars);
            layoutLikingAvatars.removeAllViews();
            LayoutInflater inflater = activity.getLayoutInflater();
            final int displayWidth = DisplayUtils.getDisplayPixelWidth(activity);
            final float spaceForAvatars = displayWidth - (activity.getResources().getDimensionPixelSize(R.dimen.margin_large) * 2);
            final float avatarSizeWithMargin = activity.getResources().getDimensionPixelSize(R.dimen.avatar_sz_small) +
                    activity.getResources().getDimensionPixelSize(R.dimen.margin_small);
            final int maxAvatars = (int)(spaceForAvatars / avatarSizeWithMargin);
            int count = 0;
            final HashMap<String, AvatarImageView> avatarImages = new HashMap<String, AvatarImageView>();
            for(String userId: likes) {
                if (!userId.equals(currentUserId) && count < maxAvatars) {
                    AvatarImageView avatarImage = (AvatarImageView) inflater.inflate(R.layout.small_avatar, layoutLikingAvatars, false);
                    avatarImage.setOnClickListener(new UserListener(activity, userId));
                    avatarImages.put(userId, avatarImage);
                    layoutLikingAvatars.addView(avatarImage);
                    count++;
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

            final ArrayList<String> userList = new ArrayList<String>();
            userList.addAll(likes);
            TextView likesCountText = (TextView) likesLayout.findViewById(R.id.text_likes_count);
            likesCountText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startUserListActivity(userList);
                }
            });
            if (likes.contains(currentUserId)) {
                if (size == 1) {
                    likesCountText.setText(activity.getString(R.string.label_you_like));
                    layoutLikingAvatars.setVisibility(View.GONE);
                } else {
                    likesCountText.setText(activity.getString(R.string.label_you_and_others_like, size - 1));
                }
            } else {
                likesCountText.setText(activity.getString(R.string.label_others_like, size));
            }

            if (likesLayout.getVisibility() != View.VISIBLE) {
                Animation.fadeIn(likesLayout, Animation.Duration.SHORT);
            }
        } else {
            Animation.fadeOut(likesLayout, Animation.Duration.SHORT);
        }
    }

    private void startUserListActivity(ArrayList<String> userList) {
        final Intent intent = new Intent(activity, UserListActivity.class);
        intent.putExtra(UserListActivity.INTENT_TITLE,
                activity.getString(R.string.label_others_like, userList.size()));
        intent.putStringArrayListExtra(UserListActivity.INTENT_USER_LIST, userList);
        activity.startActivity(intent);
    }
}
