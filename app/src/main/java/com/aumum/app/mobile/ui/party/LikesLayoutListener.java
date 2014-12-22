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

    public void update(ViewGroup likesLayout, List<String> likes) throws Exception {
        int count = likes.size();
        if (count > 0) {
            ViewGroup layoutLikingAvatars = (ViewGroup) likesLayout.findViewById(R.id.layout_liking_avatars);
            layoutLikingAvatars.removeAllViews();
            LayoutInflater inflater = activity.getLayoutInflater();
            final HashMap<String, AvatarImageView> avatarImages = new HashMap<String, AvatarImageView>();
            for(String userId: likes) {
                if (!userId.equals(currentUserId)) {
                    AvatarImageView avatarImage = (AvatarImageView) inflater.inflate(R.layout.small_avatar, layoutLikingAvatars, false);
                    avatarImage.setOnClickListener(new UserListener(activity, userId));
                    avatarImages.put(userId, avatarImage);
                    layoutLikingAvatars.addView(avatarImage);
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

            TextView likesCountText = (TextView) likesLayout.findViewById(R.id.text_likes_count);
            if (likes.contains(currentUserId)) {
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
