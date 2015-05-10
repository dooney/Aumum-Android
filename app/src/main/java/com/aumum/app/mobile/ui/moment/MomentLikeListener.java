package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.user.UserListActivity;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.LikeTextView;
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
 * Created by Administrator on 10/05/2015.
 */
public class MomentLikeListener implements LikeTextView.LikeListener {

    private Activity activity;
    private Moment moment;
    private SafeAsyncTask<Boolean> task;

    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject MomentStore momentStore;
    @Inject ChatService chatService;

    public MomentLikeListener(Activity activity,
                              Moment moment) {
        this.activity = activity;
        this.moment = moment;
        Injector.inject(this);
    }

    @Override
    public void onUnLike(LikeTextView view) {
        if (task != null) {
            return;
        }
        final View rootView = view.getRootView();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                restService.removeMomentLike(moment.getObjectId(), currentUser.getObjectId());
                moment.removeLike(currentUser.getObjectId());
                momentStore.save(moment);
                return true;
            }

            @Override
            public void onSuccess(final Boolean success) {
                ViewGroup likesLayout = (ViewGroup) rootView.findViewById(R.id.layout_likes);
                updateLikesLayout(likesLayout, moment.getLikes());
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    @Override
    public void onLike(LikeTextView view) {
        if (task != null) {
            return;
        }
        final View rootView = view.getRootView();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                restService.addMomentLike(moment.getObjectId(), currentUser.getObjectId());
                moment.addLike(currentUser.getObjectId());
                momentStore.save(moment);
                sendLikeMessage(rootView.getContext(), currentUser);
                return true;
            }

            @Override
            public void onSuccess(final Boolean success) {
                ViewGroup likesLayout = (ViewGroup) rootView.findViewById(R.id.layout_likes);
                updateLikesLayout(likesLayout, moment.getLikes());
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    private void sendLikeMessage(Context context, User currentUser) throws Exception {
        if (!moment.getUserId().equals(currentUser.getObjectId())) {
            String content = context.getString(R.string.label_like_moment);
            CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.MOMENT_LIKE,
                    currentUser.getScreenName(), content, moment.getObjectId());
            UserInfo user = userStore.getUserInfoById(moment.getUserId());
            chatService.sendCmdMessage(user.getChatId(), cmdMessage, false, null);
        }
    }

    public void updateLikesLayout(ViewGroup likesLayout, List<String> likeList) {
        final ArrayList<String> likes = new ArrayList<>();
        likes.addAll(likeList);
        Collections.reverse(likes);
        final int size = likes.size();
        if (size > 0) {
            ViewGroup layoutLikingAvatars =
                    (ViewGroup) likesLayout.findViewById(R.id.layout_avatars);
            layoutLikingAvatars.removeAllViews();
            LayoutInflater inflater = activity.getLayoutInflater();
            final int displayWidth = DisplayUtils.getDisplayPixelWidth(activity);
            final float spaceForAvatars = displayWidth -
                    (activity.getResources().getDimensionPixelSize(R.dimen.margin_large) * 2);
            final float avatarSizeWithMargin =
                    activity.getResources().getDimensionPixelSize(R.dimen.avatar_sz_small) +
                    activity.getResources().getDimensionPixelSize(R.dimen.margin_small);
            final int maxAvatars = (int)(spaceForAvatars / avatarSizeWithMargin) - 1;
            int count = 0;
            final HashMap<String, AvatarImageView> avatarImages = new HashMap<>();
            TextView likesText = (TextView) likesLayout.findViewById(R.id.text_likes);
            likesText.setVisibility(View.INVISIBLE);
            for(String userId: likes) {
                if (count < maxAvatars) {
                    AvatarImageView avatarImage = (AvatarImageView) inflater
                            .inflate(R.layout.avatar_thumbnail, layoutLikingAvatars, false);
                    avatarImage.setOnClickListener(new UserListener(activity, userId));
                    avatarImages.put(userId, avatarImage);
                    layoutLikingAvatars.addView(avatarImage);
                    count++;
                } else {
                    likesText.setVisibility(View.VISIBLE);
                    likesText.setText(String.valueOf(size));
                    likesText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Intent intent = new Intent(activity, UserListActivity.class);
                            intent.putExtra(UserListActivity.INTENT_TITLE,
                                    activity.getString(R.string.label_users_like, size));
                            intent.putStringArrayListExtra(UserListActivity.INTENT_USER_LIST, likes);
                            activity.startActivity(intent);
                        }
                    });
                    break;
                }
            }
            final HashMap<String, String> avatarUrls = new HashMap<>();
            new SafeAsyncTask<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Iterator it = avatarImages.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, AvatarImageView> pair =
                                (Map.Entry<String, AvatarImageView>) it.next();
                        String userId = pair.getKey();
                        UserInfo user = userStore.getUserInfoById(userId);
                        avatarUrls.put(userId, user.getAvatarUrl());
                    }
                    return true;
                }

                @Override
                protected void onSuccess(Boolean success) throws Exception {
                    Iterator it = avatarImages.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, AvatarImageView> pair =
                                (Map.Entry<String, AvatarImageView>) it.next();
                        String userId = pair.getKey();
                        AvatarImageView avatarImage = pair.getValue();
                        avatarImage.getFromUrl(avatarUrls.get(userId));
                    }
                }
            }.execute();

            if (likesLayout.getVisibility() != View.VISIBLE) {
                Animation.fadeIn(likesLayout, Animation.Duration.SHORT);
            }
        } else {
            Animation.fadeOut(likesLayout, Animation.Duration.SHORT);
        }
    }
}